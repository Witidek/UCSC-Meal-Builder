package com.example.sam.ucsc_meal_builder;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Sam on 10/19/2015.
 */
public class Restaurant {

    // DB table name
    public static final String TABLE = "Restaurant";

    // DB table column names
    public static final String KEY_restaurant_id = "restaurant_id";
    public static final String KEY_name = "name";
    public static final String KEY_accepts_meals = "accepts_meals";

    // Fields
    private int restaurantID;
    private String name;
    private boolean acceptsMeals;

    // Constructor
    public Restaurant(int id, String n, boolean b) {
        this.restaurantID = id;
        this.name = n;
        this.acceptsMeals = b;
    }

    // Methods
    public int getRestaurantID() {
        return restaurantID;
    }

    public String getName(){
        return name;
    }

    public boolean getAcceptsMeals() {
        return acceptsMeals;
    }

    public String toString() {
        return name;
    }
}
