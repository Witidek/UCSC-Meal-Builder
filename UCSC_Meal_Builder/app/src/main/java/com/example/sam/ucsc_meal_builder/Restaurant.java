package com.example.sam.ucsc_meal_builder;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Sam on 10/19/2015.
 */
public class Restaurant implements Parcelable {

    // DB table name
    public static final String TABLE = "Restaurant";

    // DB table column names
    public static final String KEY_restaurant_id = "restaurant_id";
    public static final String KEY_name = "name";

    // Fields
    private int restaurantID;
    private String name;

    // Constructor
    public Restaurant(int id, String n) {
        restaurantID = id;
        name = n;
    }

    // Methods
    public int getRestaurantID() {
        return restaurantID;
    }

    public String getName(){
        return name;
    }

    public String toString() {
        return name;
    }

    // Parcelable method implementations

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(restaurantID);
        out.writeString(name);
    }

    public static final Parcelable.Creator<Restaurant> CREATOR = new Parcelable.Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };

    private Restaurant(Parcel in) {
        restaurantID = in.readInt();
        name = in.readString();
    }
}
