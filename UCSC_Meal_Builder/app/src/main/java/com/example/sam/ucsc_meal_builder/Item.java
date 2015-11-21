package com.example.sam.ucsc_meal_builder;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;

/**
 * Created by Sam on 10/19/2015.
 */
public class Item {

    public static final String TABLE = "Item";

    public static final String KEY_item_id = "item_id";
    public static final String KEY_name = "name";
    public static final String KEY_price = "price";
    public static final String KEY_restaurant_id = "restaurant_id";
    public static final String KEY_course = "course";

    // Fields
    private int itemID;
    private String name;
    private BigDecimal price;
    private int restaurantID;
    private String course;
    private int quantity;

    // Constructor
    public Item(int id, String n, BigDecimal p, int rid, String c){
        this.itemID = id;
        this.name = n;
        this.price = p;
        this.restaurantID = rid;
        this.course = c;
        this.quantity = 0;
    }

    // Cart item with quantity constructor
    public Item(int id, String n, BigDecimal p, int rid, String c, int quantity){
        this.itemID = id;
        this.name = n;
        this.price = p;
        this.restaurantID = rid;
        this.course = c;
        this.quantity = quantity;
    }

    // Methods
    public int getItemID() {
        return this.itemID;
    }

    public String getName(){
        return this.name;
    }

    public BigDecimal getPrice(){
        return this.price;
    }

    public int getRestaurantID() {
        return this.restaurantID;
    }

    public String getCourse(){return this.course;}

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int q) {
        this.quantity = q;
    }

    public String toString() {
        if (this.quantity > 0) {
            return String.format("%dx %s : %s", this.quantity, this.name, this.price.toString());
        } else {
            return String.format("%s : %s", this.name, this.price.toString());
        }
    }
}
