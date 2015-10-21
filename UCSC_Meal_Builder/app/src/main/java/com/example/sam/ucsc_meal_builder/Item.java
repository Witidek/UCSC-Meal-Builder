package com.example.sam.ucsc_meal_builder;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sam on 10/19/2015.
 */
public class Item implements Parcelable {

    // Fields
    String name;
    double price;

    // Constructor
    public Item(String n, double p){
        name = n;
        price = p;

    }

    // Methods
    public double getPrice(){
        return price;
    }

    public String getName(){
        return name;
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
        out.writeString(name);
        out.writeDouble(price);
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
        name = in.readString();
        price = in.readDouble();
    }
}
