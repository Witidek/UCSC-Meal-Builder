package com.example.sam.ucsc_meal_builder;

import java.util.ArrayList;

/**
 * Created by Sam on 10/19/2015.
 */
public class Restaurant {

    String name;
    ArrayList<Item> menu;

    public Restaurant(String n) {
        name = n;
    }

    public void addMenuItem(Item i){
        menu.add(i);
    }

    public String getName(){
        return name;
    }

    public String toString() {
        return name;
    }

}
