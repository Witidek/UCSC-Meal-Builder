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
 * https://github.com/jgilfelt/android-sqlite-asset-helper
 *
 * A helper class to manage a local database. It clones an existing database from a file into the
 * app, allows SQL queries to be performed on the app database, and provides methods for updating
 * the database either through SQL upgrade scripts, or through a complete overwrite cloning a
 * database file. All interactions with the database using SQL or JSON can be found in the methods
 * of this class. This class only exists as a singleton which is instantiated at app startup.
 */
public class DBHelper extends SQLiteAssetHelper {

    /** DB file that is cloned, found in app/src/main/assets/databases */
    private static final String DATABASE_NAME = "mealbuilder.db";
    /** Version number when incremented signals SQLiteAssetHelper to clone new database from file */
    private static final int DATABASE_VERSION = 3;
    /** Singleton instance of this class */
    private static DBHelper instance;

    /** Private constructor for singleton */
    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade();
    }

    /** Access method for the singleton instance */
    public static DBHelper getInstance(Context context){
        if (instance == null) {
            instance = new DBHelper(context);
        }
        return instance;
    }

    /**
     * Returns a list of all restaurants from DB as Restaurant objects.
     *
     * SELECT restaurant_id, name
     * FROM Restaurant;
     *
     * @return restaurantList  ArrayList of Restaurant objects
     */
    public ArrayList<Restaurant> getRestaurants() {
        // Create empty restaurant list
        ArrayList<Restaurant> restaurantList = new ArrayList<Restaurant>();

        // Connect to readable DB, grab restaurants from Restaurant table
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String[] sqlSelect = new String[]{Restaurant.KEY_restaurant_id, Restaurant.KEY_name, Restaurant.KEY_accepts_meals};
        qb.setTables(Restaurant.TABLE);
        Cursor cursor = qb.query(db, sqlSelect, null, null, null, null, null);

        // Fill restaurant list
        while (cursor.moveToNext()) {
            int rid = cursor.getInt(cursor.getColumnIndex(Restaurant.KEY_restaurant_id));
            String name = cursor.getString(cursor.getColumnIndex(Restaurant.KEY_name));
            boolean acceptsMeals = cursor.getInt(cursor.getColumnIndex(Restaurant.KEY_accepts_meals)) == 1;
            Restaurant restaurant = new Restaurant(rid, name, acceptsMeals);
            restaurantList.add(restaurant);
        }

        // Close stuff
        cursor.close();
        db.close();

        return restaurantList;
    }

    /**
     * Matches a name to the restaurantID given. Only used to set title in MenuActivity.
     *
     * SELECT name
     * FROM Restaurant
     * WHERE restaurant_id = rid;
     *
     * @param rid    restaurantID to look up
     * @return name  name of restaurant
     */
    public String getRestaurantName(int rid) {
        String name;

        // Connect to readable DB, look up name with rid
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(Restaurant.TABLE);
        String[] sqlSelect = new String[]{Restaurant.KEY_name};
        String sqlWhere = "restaurant_id = ?";
        String[] sqlWhereArgs = new String[]{String.valueOf(rid)};
        Cursor cursor = qb.query(db, sqlSelect, sqlWhere, sqlWhereArgs, null, null, null);

        // Only one should exist, so only take first in cursor
        cursor.moveToFirst();
        name = cursor.getString(cursor.getColumnIndex(Restaurant.KEY_name));

        return name;
    }

    /**
     * Grabs all rows from Item table with corresponding rid and returns an ArrayList of Item
     * objects. Used to fill and display a menu in MenuActivity so Item.quantity is disregarded.
     *
     * SELECT item_id, name, price
     * FROM Item
     * WHERE restaurant_id = rid;
     *
     * @param rid        restaurantID to look up
     * @return itemList  ArrayList of items from restaurant
     */
    public ArrayList<Item> getMenu(int rid) {
        // Create empty item list
        ArrayList<Item> itemList = new ArrayList<Item>();

        // Connect to readable DB, grab restaurants from Restaurant table
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

    /**
     * Loads a cart of items from the database into a Cart object from the restaurantID. Called
     * each time the Cart object needs to be in sync with the database. Quantity is recognized.
     *
     * SELECT Item.item_id, Item.name, Item.price, Item.restaurant_id, Item.course, Cart.quantity
     * FROM Item, Cart
     * WHERE Cart.restaurant_id = rid AND Cart.item_id = Item.item_id;
     *
     * @param rid    unique ID denoting which restaurant
     * @return cart  cart object correpsonding to rid
     */
    // Returns a cart object with list of items from a specific restaurant cart
    public Cart getCart(int rid) {
        // Create empty stuff
        Cart cart;
        ArrayList<Item> itemList = new ArrayList<Item>();

        // Connect to readable DB, grab items from Cart table with matching restaurant_id
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

    /**
     * To add an item to the cart database, a check must be made to see if the item already exists.
     *
     * SELECT quantity
     * FROM Cart
     * WHERE item_id = item.getItemID();
     *
     * If a duplicate item is already found, it will be updated with quantity = quantity + 1
     *
     * UPDATE Cart
     * SET quantity = quantity + 1
     * WHERE item_id = item.getItemID();
     *
     * If no duplicate is found then add the item with starting quantity of one.
     *
     * INSERT INTO Cart
     * VALUES (item.getRestaurantID(), item.getItemID(), 1);
     *
     * @param item  item object to insert or update in DB
     */
    public void addToCart(Item item) {
        // Connect to writable DB
        SQLiteDatabase db = getWritableDatabase();

        // Query to check if item exists in cart
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(Cart.TABLE);
        String[] sqlSelect = new String[]{Cart.KEY_quantity};
        String sqlWhere = "item_id = ?";
        String[] sqlWhereArgs = new String[]{String.valueOf(item.getItemID())};
        Cursor cursor = qb.query(db, sqlSelect, sqlWhere, sqlWhereArgs, null, null, null);

        if (cursor.moveToFirst()) {
            // Duplicate found, update row with quantity + 1 using UPDATE query
            int quantity = cursor.getInt(cursor.getColumnIndex(Cart.KEY_quantity));
            ContentValues values = new ContentValues();
            values.put("quantity", quantity + 1);
            db.update(Cart.TABLE, values, sqlWhere, sqlWhereArgs);
        } else {
            // No duplicate item found in cart, enter new one with quantity 1 using INSERT query
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

    /**
     * Saves a cart as a favorite by entering each item with quantity into the Favorite table. A
     * row is inserted into Budget table with matching favoriteID to keep track of currency values.
     * FavoriteID is unique for each favorite, but is not unique in the Favorite table because
     * each row corresponds to one unique item. Due to this, favoriteID must be checked and
     * incremented manually here.
     *
     * SELECT favorite_id
     * FROM Favorite
     * ORDER BY favorite_id DESC
     * LIMIT 1
     *
     * After the greatest favorite_id is found, increment by one for the new favorite. Loop through
     * the list of items given and insert each item as a row.
     *
     * INSERT INTO Favorite
     * VALUES (favID, favName, item.getRestaurantID(), item.getItemID(), item.getQuantity());
     *
     * @param itemList  cart ArrayList of items
     * @param favName   user inputted name for favorite
     * @param budget    currency values to be saved in Budget table
     */
    public void addToFavorites(ArrayList<Item> itemList, String favName, Budget budget) {
        int favID;
        int meals = budget.getMeals();
        BigDecimal flexis = budget.getFlexis();
        BigDecimal cash = budget.getCash();

        // Connect to writable DB
        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT favorite_id FROM Favorite ORDER BY favorite_id DESC LIMIT 1", null);
        cursor.moveToFirst();

        // Case: Empty favorites table, start with favID 0
        if (cursor.getCount() == 0) {
            favID = 0;
        }else {
            favID = cursor.getInt(cursor.getColumnIndex("favorite_id"));
            favID++;
        }

        // Add each item
        for (Item item: itemList) {
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

    /**
     * Deletes the row in Cart corresponding to the item given.
     *
     * DELETE FROM Cart
     * WHERE item_id = item.getItemID();
     *
     * @param item  object to delete in DB
     */
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

    /**
     * Deletes all rows from Cart for a single restaurant
     *
     * DELETE FROM Cart
     * WHERE restaurant_id = rid;
     *
     * @param rid  restaurantID denoting which restaurant's cart to delete
     */
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

    /**
     * Queries for all unique favorites in Favorite table. This is only used to populate the
     * favorite list in FavoriteActivity in which only the name is displayed. No cart items are
     * returned or used.
     *
     * SELECT name
     * FROM Favorite
     * GROUP BY favorite_id;
     *
     * @return favList  ArrayList of all favorites
     */
    public ArrayList<Favorite> getFavorites() {
        ArrayList<Favorite> favList = new ArrayList<>();

        // Connect to database
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM Favorite GROUP BY favorite_id", null);

        while (cursor.moveToNext()) {
            int fid = cursor.getInt((cursor.getColumnIndex(Favorite.KEY_favorite_id)));
            int rid = cursor.getInt((cursor.getColumnIndex(Favorite.KEY_restaurant_id)));
            String name = cursor.getString(cursor.getColumnIndex(Favorite.KEY_name));
            Favorite fav = new Favorite(fid, rid, name);
            favList.add(fav);
        }

        // Close stuff
        cursor.close();
        db.close();

        return favList;
    }

    /**
     * Look up favorite with favID, clear the cart of the restaurant the favorite belongs to, and
     * then copy the cart items from Favorite to Cart.
     *
     * SELECT *
     * FROM Favorite
     * WHERE favorite_id = favID;
     *
     * Also looks up corresponding row in Budget table and returns a budget object.
     *
     * SELECT *
     * FROM Budget
     * WHERE favorite_id = favID;
     *
     * @param favID    unique ID for favorite to look up
     * @return budget  currency values linked to the saved cart (favorite)
     */
    public Budget loadFavorite(int favID) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT DISTINCT restaurant_id FROM Favorite WHERE favorite_id = ?", new String[]{Integer.toString(favID)});
        cursor.moveToFirst();
        int rid = cursor.getInt(cursor.getColumnIndex("restaurant_id"));
        clearCart(rid);

        // Copy item rows from Favorite to Cart
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

        // Look up budget
        db = getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM Budget WHERE favorite_id = ?", new String[]{Integer.toString(favID)});
        cursor.moveToFirst();

        int meals = cursor.getInt(cursor.getColumnIndex("meals"));
        BigDecimal flexis = new BigDecimal(cursor.getInt(cursor.getColumnIndex("flexis"))).scaleByPowerOfTen(-2);
        BigDecimal cash = new BigDecimal(cursor.getInt(cursor.getColumnIndex("cash"))).scaleByPowerOfTen(-2);
        Budget budget = new Budget(rid, meals, flexis, cash);

        cursor.close();
        db.close();

        return budget;
    }

    /**
     * Deletes all rows in Favorite table corresponding to the favorite parameter as well as the
     * matching row in Budget table.
     *
     * DELETE FROM Favorite
     * WHERE favorite_id = fav.getFavoriteID();
     *
     * DELETE FROM Budget
     * WHERE favorite_id = fav.getFavoriteID();
     *
     * @param fav  object to delete in DB
     */
    public void deleteFavorite(Favorite fav) {
        // Connect to database
        SQLiteDatabase db = getWritableDatabase();

        // Construct DELETE query
        String sqlWhere = "favorite_id = ?";
        String[] sqlWhereArgs = new String[]{String.valueOf(fav.getFavoriteID())};
        db.delete(Favorite.TABLE, sqlWhere, sqlWhereArgs);

        // Construct DELETE query
        db = getWritableDatabase();
        sqlWhere = "favorite_id = ?";
        sqlWhereArgs = new String[]{String.valueOf(fav.getFavoriteID())};
        db.delete(Budget.TABLE, sqlWhere, sqlWhereArgs);

        // Close stuff
        db.close();
    }

    /**
     * This method pulls the right JSON file and populates the database.
     *
     * @param urlp  grabs the json file from this address
     * @param type  specifies if this is a item or a restaurant
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
