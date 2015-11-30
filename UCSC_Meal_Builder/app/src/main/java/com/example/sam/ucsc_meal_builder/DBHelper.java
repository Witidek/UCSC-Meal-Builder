package com.example.sam.ucsc_meal_builder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Jason on 10/20/2015.
 */
public class DBHelper extends SQLiteAssetHelper{

    // DB file found in app/src/main/assets/databases
    private static final String DATABASE_NAME = "mealbuilder.db";
    private static final int DATABASE_VERSION = 1;

    // Constructor
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //setForcedUpgrade(2);
    }

    // Returns a list of all restaurants from DB
    public ArrayList<Restaurant> getRestaurants() {
        // Create empty restaurant list
        ArrayList<Restaurant> restaurantList = new ArrayList<Restaurant>();

        // Connect to readable DB, grab restaurants from Restaurant table
        // SELECT restaurant_id, name
        // FROM Restaurant;
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String[] sqlSelect = new String[]{Restaurant.KEY_restaurant_id, Restaurant.KEY_name};
        qb.setTables(Restaurant.TABLE);
        Cursor cursor = qb.query(db, sqlSelect, null, null, null, null, null);

        // Fill restaurant list
        while (cursor.moveToNext()) {
            int rid = cursor.getInt(cursor.getColumnIndex(Restaurant.KEY_restaurant_id));
            String name = cursor.getString(cursor.getColumnIndex(Restaurant.KEY_name));
            Restaurant restaurant = new Restaurant(rid, name);
            restaurantList.add(restaurant);
        }

        // Close stuff
        cursor.close();
        db.close();

        return restaurantList;
    }

    public String getRestaurantName(int rid) {
        String name;

        // SELECT name
        // FROM Restaurant
        // WHERE restaurant_id = rid;
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(Restaurant.TABLE);
        String[] sqlSelect = new String[]{Restaurant.KEY_name};
        String sqlWhere = "restaurant_id = ?";
        String[] sqlWhereArgs = new String[]{String.valueOf(rid)};
        Cursor cursor = qb.query(db, sqlSelect, sqlWhere, sqlWhereArgs, null, null, null);

        cursor.moveToFirst();
        name = cursor.getString(cursor.getColumnIndex(Restaurant.KEY_name));

        return name;
    }

    // Returns a list of items from a restaurant (menu), given a restaurant id
    public ArrayList<Item> getMenu(int rid) {
        // Create empty item list
        ArrayList<Item> itemList = new ArrayList<Item>();

        // Connect to readable DB, grab restaurants from Restaurant table
        // SELECT item_id, name, price
        // FROM Item
        // WHERE restaurant_id = rid;
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(Item.TABLE);
        String[] sqlSelect = new String[]{Item.KEY_item_id, Item.KEY_name, Item.KEY_price, Item.KEY_course};
        String sqlWhere = "restaurant_id = ?";
        String[] sqlWhereArgs = new String[]{String.valueOf(rid)};
        Cursor cursor = qb.query(db, sqlSelect, sqlWhere, sqlWhereArgs, null, null, null);

        // Fill item list
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(Item.KEY_item_id));
            String name = cursor.getString(cursor.getColumnIndex(Item.KEY_name));
            // Price in database is int, make it BigDecimal, set 2 decimal places, divide by 100
            BigDecimal price = new BigDecimal(cursor.getInt(cursor.getColumnIndex(Item.KEY_price)));
            price = price.setScale(2, BigDecimal.ROUND_HALF_UP);
            price = price.divide(new BigDecimal(100), BigDecimal.ROUND_HALF_UP);
            String course = cursor.getString(cursor.getColumnIndex(Item.KEY_course));
            Item item = new Item(id, name, price, rid, course);
            itemList.add(item);
        }

        // Close stuff
        cursor.close();
        db.close();

        return itemList;
    }

    // Returns a cart object with list of items from a specific restaurant cart
    public Cart getCart(int rid) {
        // Create empty stuff
        Cart cart;
        ArrayList<Item> itemList = new ArrayList<Item>();

        // Connect to readable DB, grab items from Cart table with matching restaurant_id
        // SELECT Item.item_id, Item.name, Item.price, Item.restaurant_id, Item.course, Cart.quantity
        // FROM Item, Cart
        // WHERE Cart.restaurant_id = rid AND Cart.item_id = Item.item_id;
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(Item.TABLE + ", " + Cart.TABLE);
        String[] sqlSelect = new String[]{"Item.item_id", "Item.name", "Item.price", "Item.restaurant_id", "Item.course", "Cart.quantity"};
        String sqlWhere = "Cart.restaurant_id = ? AND Cart.item_id = Item.item_id";
        String[] sqlWhereArgs = new String[]{String.valueOf(rid)};
        Cursor cursor = qb.query(db, sqlSelect, sqlWhere, sqlWhereArgs, null, null, null);

        // Fill item and quantity lists
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(Item.KEY_item_id));
            String name = cursor.getString(cursor.getColumnIndex(Item.KEY_name));
            // Price in database is int, make it BigDecimal, set 2 decimal places, divide by 100
            BigDecimal price = new BigDecimal(cursor.getInt(cursor.getColumnIndex(Item.KEY_price)));
            price = price.setScale(2, BigDecimal.ROUND_HALF_UP);
            price = price.divide(new BigDecimal(100), BigDecimal.ROUND_HALF_UP);
            int quantity = cursor.getInt(cursor.getColumnIndex(Cart.KEY_quantity));
            String course = cursor.getString(cursor.getColumnIndex(Item.KEY_course));
            Item item = new Item(id, name, price, rid, course, quantity);
            itemList.add(item);

        }
        cart = new Cart(itemList);

        // Close stuff
        cursor.close();
        db.close();

        return cart;
    }

    // Add an item to cart
    public void addToCart(Item item) {
        // Connect to writable DB
        SQLiteDatabase db = getWritableDatabase();

        // Query to check if item exists in cart
        // SELECT quantity
        // FROM Cart
        // WHERE item_id = item.getItemID();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(Cart.TABLE);
        String[] sqlSelect = new String[]{Cart.KEY_quantity};
        String sqlWhere = "item_id = ?";
        String[] sqlWhereArgs = new String[]{String.valueOf(item.getItemID())};
        Cursor cursor = qb.query(db, sqlSelect, sqlWhere, sqlWhereArgs, null, null, null);

        if (cursor.moveToFirst()) {
            // Duplicate found, update row with quantity + 1 using UPDATE query
            // UPDATE Cart
            // SET quantity = quantity + 1
            // WHERE item_id = item.getItemID();
            int quantity = cursor.getInt(cursor.getColumnIndex(Cart.KEY_quantity));
            ContentValues values = new ContentValues();
            values.put("quantity", quantity + 1);
            db.update(Cart.TABLE, values, sqlWhere, sqlWhereArgs);
        } else {
            // No duplicate item found in cart, enter new one with quantity 1 using INSERT query
            // INSERT INTO Cart VALUES (item.getRestaurantID(), item.getItemID(), 1);
            ContentValues values = new ContentValues();
            values.put("restaurant_id", item.getRestaurantID());
            values.put("item_id", item.getItemID());
            values.put("quantity", 1);
            db.insert(Cart.TABLE, null, values);
        }

        // Close stuff
        cursor.close();
        db.close();
    }

    //Add a "cart" to favorites
    public void addToFavorites(ArrayList<Item> itemList, String favName, Budget budget) {
        int favID;
        int meals = budget.getMeals();
        BigDecimal flexis = budget.getFlexis();
        BigDecimal cash = budget.getCash();

        // Connect to writable DB
        SQLiteDatabase db = getWritableDatabase();

        // SELECT favorite_id
        // FROM Favorite
        // ORDER BY favorite_id DESC
        // LIMIT 1

        Cursor cursor = db.rawQuery("SELECT favorite_id FROM Favorite ORDER BY favorite_id DESC LIMIT 1", null);
        cursor.moveToFirst();

        // Case: Empty favorites table, start with favID 0
        if (cursor.getCount() == 0) {
            favID = 0;
        }else {
            favID = cursor.getInt(cursor.getColumnIndex("favorite_id"));
            favID++;
        }

        for (Item item: itemList) {
            // INSERT INTO Favorite VALUES (null, item.getItemID(), 1);
            ContentValues values = new ContentValues();
            values.put("favorite_id", favID);
            values.put("name", favName);
            values.put("restaurant_id", item.getRestaurantID());
            values.put("item_id", item.getItemID());
            values.put("quantity", item.getQuantity());
            db.insert("Favorite", null, values);
        }

        // Insert budget info with corresponding favorite_id in Budget table
        ContentValues values = new ContentValues();
        values.put("favorite_id", favID);
        values.put("meals", meals);
        values.put("flexis", flexis.scaleByPowerOfTen(2).intValue());
        values.put("cash", cash.scaleByPowerOfTen(2).intValue());
        db.insert("Budget", null, values);

        // Close stuff
        cursor.close();
        db.close();

    }

    public void deleteItem(Item item) {
        // Connect to database
        SQLiteDatabase db = getWritableDatabase();

        // Construct DELETE query
        String sqlWhere = "item_id = ?";
        String[] sqlWhereArgs = new String[]{String.valueOf(item.getItemID())};
        db.delete(Cart.TABLE, sqlWhere, sqlWhereArgs);

        // Close stuff
        db.close();
    }

    // Delete all Cart row items with matching restaurant_id
    public void clearCart(int rid) {
        // Connect to database
        SQLiteDatabase db = getWritableDatabase();

        // Construct DELETE query
        String sqlWhere = "restaurant_id = ?";
        String[] sqlWhereArgs = new String[]{String.valueOf(rid)};
        db.delete(Cart.TABLE, sqlWhere, sqlWhereArgs);

        // Close stuff
        db.close();
    }

    public ArrayList<String> getFavorites() {
        ArrayList<String> favList = new ArrayList<>();

        // Connect to database
        SQLiteDatabase db = getReadableDatabase();

        // SELECT name
        // FROM Favorite
        Cursor cursor = db.rawQuery("SELECT * FROM Favorite GROUP BY favorite_id", null);

        while (cursor.moveToNext()) {
            favList.add(cursor.getString(cursor.getColumnIndex("name")));
        }

        // Close stuff
        cursor.close();
        db.close();

        return favList;
    }

    public Budget loadFavorite(int favID) {
        Budget budget = new Budget();

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT DISTINCT restaurant_id FROM Favorite WHERE favorite_id = ?", new String[]{Integer.toString(favID)});
        cursor.moveToFirst();
        int rid = cursor.getInt(cursor.getColumnIndex("restaurant_id"));
        clearCart(rid);

        // SELECT *
        // FROM Favorite
        // WHERE favorite_id = ?
        db = getWritableDatabase();
        cursor = db.rawQuery("SELECT * FROM Favorite WHERE favorite_id = ?", new String[]{Integer.toString(favID)});
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("item_id"));
            int quantity = cursor.getInt(cursor.getColumnIndex("quantity"));
            ContentValues values = new ContentValues();
            values.put("restaurant_id", rid);
            values.put("item_id", id);
            values.put("quantity", quantity);
            db.insert("Cart", null, values);
        }

        db = getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM Budget WHERE favorite_id = ?", new String[]{Integer.toString(favID)});
        cursor.moveToFirst();
        budget.setRID(rid);
        budget.setMeals(cursor.getInt(cursor.getColumnIndex("meals")));
        budget.setFlexis(new BigDecimal(cursor.getInt(cursor.getColumnIndex("flexis"))).scaleByPowerOfTen(-2));
        budget.setCash(new BigDecimal(cursor.getInt(cursor.getColumnIndex("cash"))).scaleByPowerOfTen(-2));

        cursor.close();
        db.close();

        return budget;
    }

    /*
        This method pulls the right JSON file and populates the database.
        urlp = grabs the json file from this address
        type = specifies if this is a item or a restaurant
     */
    public void getJSONFromURL(String urlp, int type){
        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(urlp);

            urlConnection = (HttpURLConnection) url
                    .openConnection();

            InputStream in = urlConnection.getInputStream();

            InputStreamReader isw = new InputStreamReader(in);

            int data = isw.read();
            String dataString = "";

            while (data != -1) {
                char current = (char) data;
                data = isw.read();
                //System.out.print(current);
                dataString += current;
            }
            JSONArray jo = new JSONArray(dataString);

            switch(type) {
                case 0: jsonDBModifierRest(jo); break;
                case 1: jsonDBModifierItem(jo); break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                urlConnection.disconnect();
            } catch (Exception e) {
                e.printStackTrace(); //If you want further info on failure...
            }
        }
    }

    public void jsonDBModifierRest(JSONArray ja){
        SQLiteDatabase db = getReadableDatabase();

        db.delete(Restaurant.TABLE, null, null);
        //db.insert(Restaurant.TABLE, null, )
        for(int i = 0;i < ja.length(); i++){
            try {
                JSONObject inside = ja.getJSONObject(i);
                //System.out.println("Restarunt");
                //System.out.println("name = "+inside.get("name"));
                ContentValues valuesp = new ContentValues();
                valuesp.put("restaurant_id", (String)inside.get("id"));
                valuesp.put("name", (String)inside.get("name"));

                db.insert(Restaurant.TABLE, null, valuesp);
            }catch(JSONException je){
                System.out.println("JE problem");
            }
        }

        db.close();
    }

    public void jsonDBModifierItem(JSONArray ja){
        SQLiteDatabase db = getReadableDatabase();

        db.delete(Item.TABLE, null, null);
        //db.insert(Restaurant.TABLE, null, )
        for(int i = 0;i < ja.length(); i++){
            try {
                JSONObject inside = ja.getJSONObject(i);
                //System.out.println("Restarunt");
                //System.out.println("name = "+inside.get("name"));
                ContentValues valuesp = new ContentValues();
                valuesp.put("item_id", (String)inside.get("id"));
                valuesp.put("restaurant_id", (String)inside.get("restaurant_id"));
                valuesp.put("name", (String)inside.get("name"));
                valuesp.put("price", (String)inside.get("price"));

                db.insert(Item.TABLE, null, valuesp);
            }catch(JSONException je){
                System.out.println("JE problem");
            }
        }

        db.close();
    }
}
