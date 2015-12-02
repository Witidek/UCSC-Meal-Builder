package com.example.sam.ucsc_meal_builder;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * A Cart is a list of Items (including Item.quantity field) which is filled by pulling Cart
 * information from local database. Various methods to act upon the list of Items are kept here.
 */
public class Cart {

    /** Name of related table in local database */
    public static final String TABLE = "Cart";

    /** Names of each column in Cart table */
    public static final String KEY_restaurant_id = "restaurant_id";
    public static final String KEY_item_id = "item_id";
    public static final String KEY_quantity = "quantity";

    /** List of unique Items with quantities */
    private ArrayList<Item> items;

    /** Constructor for Cart */
    public Cart(ArrayList<Item> itemList) {
        this.items = itemList;
    }

    /**
     * Calculates the sum of prices of all items in itemList with quantity in consideration
     * by multiplying the singular price of each item by its quantity before adding to total.
     *
     * @return total  total price in dollars represented as a BigDecimal
     */
    public BigDecimal getTotal() {
        BigDecimal total = new BigDecimal(0);
        for (Item item : items){
            int quantity = item.getQuantity();
            BigDecimal subtotal = item.getPrice().multiply(new BigDecimal(quantity));
            total = total.add(subtotal);
        }
        return total;
    }

    /**
     * Call isEmpty() on itemList, used to check if no items exist in cart
     *
     * @return items.isEmpty()  true if ArrayList has size of 0
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Return full ArrayList of items, used to construct ListView adapters
     *
     * @return this.items  ArrayList of items
     */
    public ArrayList<Item> getItemList() {
        return this.items;
    }

    /**
     * Add Item to cart by checking if item exists already by matching item ID. If item already
     * exists then increment quantity by one, otherwise append the item to list of items with
     * quantity of one.
     *
     * @param newItem  singular item to be added to cart
     */
    public void addItem(Item newItem){
        // check for duplicate item ID
        for (Item item: items) {
            if (item.getItemID() == newItem.getItemID()) {
                item.setQuantity(item.getQuantity() + 1);
                return;
            }
        }
        newItem.setQuantity(1);
        this.items.add(newItem);
    }

    /**
     * Add a miscellaneous item with quantity one where the user defines the price in dollars.
     * Can only exist with quantity of one, and if one already exists then its price is overwritten
     * by the new input.
     *
     * @param rid    unique ID denoting which restaurant the misc item belongs to
     * @param price  user inputted price represented as BigDecimal for precision
     */
    public void addMisc(int rid, BigDecimal price){
        // check for existing misc item
        for (Item item: items) {
            if (item.getItemID() == 9999) {
                item.setPrice(price);
                return;
            }
        }
        Item item = new Item(9999, "Miscellaneous", price, rid, "Misc", 1);
        this.items.add(item);
    }


    /**
     * Delete an item from the ArrayList based on its index which should always match the position
     * of the displayed row in the ListView. This is only used for CartActivity ListView and not
     * MenuActivity. This will delete all quantities of an unique item.
     *
     * @param index  position within ArrayList to remove
     */
    public void deleteItem(int index) {
        this.items.remove(index);
    }

    /**
     * Calls the clear() method for the item ArrayList
     */
    public void clearCart() {
        this.items.clear();
    }
}
