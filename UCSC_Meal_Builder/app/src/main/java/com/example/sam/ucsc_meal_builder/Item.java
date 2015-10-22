package com.example.sam.ucsc_meal_builder;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sam on 10/19/2015.
 */
public class Item implements Parcelable {

    public static final String TABLE = "Item";

    public static final String KEY_item_id = "item_id";
    public static final String KEY_name = "name";
    public static final String KEY_price = "price";
    public static final String KEY_restaurant_id = "restaurant_id";

    // Fields
    private int itemID;
    private String name;
    private double price;
    private int restaurantID;

    // Constructor
    public Item(int id, String n, double p, int rid){
        itemID = id;
        name = n;
        price = p;
        restaurantID = rid;
    }

    // Methods
    public int getItemID() {
        return itemID;
    }

    public String getName(){
        return name;
    }

    public double getPrice(){
        return price;
    }

    public int getRestaurantID() {
        return restaurantID;
    }

    public String toString(){
        return name + " : " + Double.toString(price);
    }

    // Parcelable method implementations

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(itemID);
        out.writeString(name);
        out.writeDouble(price);
        out.writeInt(restaurantID);
    }

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    private Item(Parcel in) {
        itemID = in.readInt();
        name = in.readString();
        price = in.readDouble();
        restaurantID = in.readInt();
    }
}
