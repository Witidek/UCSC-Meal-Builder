package com.example.sam.ucsc_meal_builder;

import java.util.ArrayList;

/**
 * Created by Sam on 10/20/2015.
 */
public class Cart {

    ArrayList<Item> items;

    public Cart(){
        items = new ArrayList<Item>();
    }

    //Returns sum worth of items in cart
    public double getTotal() {
        double total = 0.0;
        for (int i = 0; i < items.size(); i++){
            total += items.get(i).getPrice();
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
        items.add(i);
    }

}
