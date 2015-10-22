package com.example.sam.ucsc_meal_builder;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.widget.Toast;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

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
        setForcedUpgrade(2);
    }

    // Returns a list of all restaurants from DB
    public ArrayList<Restaurant> getRestaurants() {
        // Create empty restaurant list
        ArrayList<Restaurant> restaurantList = new ArrayList<Restaurant>();

        // Connect to readable DB, grab restaurants from Restaurant table
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String[] sqlSelect = {Restaurant.KEY_restaurant_id, Restaurant.KEY_name};
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
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String[] sqlSelect = {Item.KEY_item_id, Item.KEY_name, Item.KEY_price};
        String sqlWhere = "restaurant_id = ?";
        String[] sqlWhereArgs = {String.valueOf(rid)};
        qb.setTables(Item.TABLE);
        Cursor cursor = qb.query(db, sqlSelect, sqlWhere, sqlWhereArgs, null, null, null);

        // Fill item list
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(Item.KEY_item_id));
            String name = cursor.getString(cursor.getColumnIndex(Item.KEY_name));
            double price = (double)cursor.getInt(cursor.getColumnIndex(Item.KEY_price)) / 100;
            Item item = new Item(id, name, price, 0);
            itemList.add(item);
        }

        // Close stuff
        cursor.close();
        db.close();

        return itemList;
    }
}
