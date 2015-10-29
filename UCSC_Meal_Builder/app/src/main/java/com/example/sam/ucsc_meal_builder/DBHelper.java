package com.example.sam.ucsc_meal_builder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.math.BigDecimal;
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
        String[] sqlSelect = new String[]{Item.KEY_item_id, Item.KEY_name, Item.KEY_price};
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
            Item item = new Item(id, name, price, rid);
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
        // SELECT Item.item_id, Item.name, Item.price, Item.restaurant_id, Cart.quantity
        // FROM Item, Cart
        // WHERE Cart.restaurant_id = rid AND Cart.item_id = Item.item_id;
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(Item.TABLE + ", " + Cart.TABLE);
        String[] sqlSelect = new String[]{"Item.item_id", "Item.name", "Item.price", "Item.restaurant_id", "Cart.quantity"};
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
            Item item = new Item(id, name, price, rid, quantity);
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
}
