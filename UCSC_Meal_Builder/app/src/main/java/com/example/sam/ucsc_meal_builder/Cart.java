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

    public double getTotal() {
        double total = 0.0;
        for (int i = 0; i < items.size(); i++){
            total += items.get(i).getPrice();
        }
        return total;
    }

    public void addItem(Item i){
        items.add(i);
    }

}
