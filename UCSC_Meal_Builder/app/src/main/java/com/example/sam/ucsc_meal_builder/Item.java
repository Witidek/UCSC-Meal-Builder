package com.example.sam.ucsc_meal_builder;

import java.math.BigDecimal;

/**
 * An Item is any single food or drink that can be ordered from a restaurant menu. Item can be
 * found used in two states. One is an Item displayed in a restaurant menu. The second is an Item
 * being ordered and that is displayed in the cart which uses the quantity field.
 */
public class Item {

    /** Name of related table in local database */
    public static final String TABLE = "Item";

    /** Names of each column in Item table */
    public static final String KEY_item_id = "item_id";
    public static final String KEY_name = "name";
    public static final String KEY_price = "price";
    public static final String KEY_restaurant_id = "restaurant_id";
    public static final String KEY_course = "course";

    /** Unique ID number to identify an item */
    private int itemID;
    /** Name of item as it appears on the restaurant menu */
    private String name;
    /** Price of the item represented in BigDecimal for currency precision */
    private BigDecimal price;
    /** ID denoting which restaurant the item belongs to */
    private int restaurantID;
    /** One word which denotes what category an item falls under (entree, drink, side, etc.) */
    private String course;
    /** Optional field, only used when Item is stored in cart to have one unique item with multiple quantities */
    private int quantity;

    /** Constructor for Item without quantity, used to display menu items */
    public Item(int id, String n, BigDecimal p, int rid, String c){
        this.itemID = id;
        this.name = n;
        this.price = p;
        this.restaurantID = rid;
        this.course = c;
        this.quantity = 0;
    }

    /** Constructor for Item with quantity, used for cart and favorites interaction */
    public Item(int id, String n, BigDecimal p, int rid, String c, int quantity){
        this.itemID = id;
        this.name = n;
        this.price = p;
        this.restaurantID = rid;
        this.course = c;
        this.quantity = quantity;
    }

    /**
     * @return itemID  unique ID of the item
     */
    public int getItemID() {
        return this.itemID;
    }

    /**
     * @return name  name of item as it appears on restaurant menu represented as a String
     */
    public String getName(){
        return this.name;
    }

    /**
     * @return price  price of item represented as a BigDecimal for precision
     */
    public BigDecimal getPrice(){
        return this.price;
    }

    /**
     * @return restaurantID  unique ID of restaurant which the item belongs to
     */
    public int getRestaurantID() {
        return this.restaurantID;
    }

    /**
     * @return course  category this item falls under (entree, drink, side, etc.)
     */
    public String getCourse(){return this.course;}

    /**
     * @return quantity  quantity of item to be used in cart for how many of this item ordered
     */
    public int getQuantity() {
        return this.quantity;
    }

    /**
     * @param price  price to set in BigDecimal in dollars
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * @param quantity  quantity to set for how many ordered
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * @return name  Item is represented as a String by its name
     */
    @Override
    public String toString() {
        return this.name;
    }
}
