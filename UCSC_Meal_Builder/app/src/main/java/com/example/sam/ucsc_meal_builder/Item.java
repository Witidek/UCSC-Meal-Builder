package com.example.sam.ucsc_meal_builder;

/**
 * Created by Sam on 10/19/2015.
 */
public class Item {

    String name;
    double price;

    public Item(String n, double p){
        name = n;
        price = p;

    }

    public String getName(){
        return name;
    }

    public String toString(){
        return name + " " + Double.toString(price);
    }

}
