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
    ArrayList<Item> items;

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

    public void addItem(Item i){
        this.items.add(i);
    }

    public void deleteItem(int i) {
        this.items.remove(i);
    }

    public void clearCart() {
        this.items.clear();
    }
}
