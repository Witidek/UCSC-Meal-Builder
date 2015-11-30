package com.example.sam.ucsc_meal_builder;


import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by Sam on 10/20/2015.
 */
public class Cart {

    public static final String TABLE = "Cart";

    public static final String KEY_restaurant_id = "restaurant_id";
    public static final String KEY_item_id = "item_id";
    public static final String KEY_quantity = "quantity";


    // Fields
    private ArrayList<Item> items;
    private int meals;
    private BigDecimal flexis;
    private BigDecimal cash;

    // Constructor
    public Cart(ArrayList<Item> itemList) {
        this.items = itemList;
    }

    //Returns sum worth of items in cart
    public BigDecimal getTotal() {
        BigDecimal total = new BigDecimal(0);
        for (Item item : items){
            int quantity = item.getQuantity();
            BigDecimal subtotal = item.getPrice().multiply(new BigDecimal(quantity));
            total = total.add(subtotal);
        }
        return total;
    }

    //Returns number of items in cart
    public int getSize() {
        return items.size();
    }

    //Returns item at a specified index
    public Item getItem(int index){
        return items.get(index);
    }

    public ArrayList<Item> getItemList() {
        return this.items;
    }

    public void addItem(Item i){
        // check for duplicate item ID
        for (Item item: items) {
            if (item.getItemID() == i.getItemID()) {
                item.setQuantity(item.getQuantity() + 1);
                return;
            }
        }
        i.setQuantity(1);
        this.items.add(i);
    }

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

    public void deleteItem(int i) {
        this.items.remove(i);
    }

    public void clearCart() {
        this.items.clear();
    }
}
