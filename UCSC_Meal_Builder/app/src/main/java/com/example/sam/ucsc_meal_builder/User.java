package com.example.sam.ucsc_meal_builder;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

/**
 * Created by Sam on 10/21/2015.
 */
public class User {

    ArrayList<Item> cart;

    int mealsBalance;
    float flexisBalance;

    int mealsBudget;
    float flexisBudget;

    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editPrefs;

    public User(Context context){

        //balance is a Preference that contains values 'meals' and 'flexis'
        sharedPrefs = context.getSharedPreferences("balance", 0);
        editPrefs = sharedPrefs.edit();

        //If balances and budget shave never been saved yet, save them as 0.
        if(!sharedPrefs.contains("mealsBalance")) {
            editPrefs.putInt("mealsBalance", 0);
        }
        if(!sharedPrefs.contains("flexisBalance")) {
            editPrefs.putFloat("flexisBalance", 0);
        }

        if(!sharedPrefs.contains("mealsBudget")) {
            editPrefs.putInt("mealsBudget", 0);
        }
        if(!sharedPrefs.contains("flexisBudget")) {
            editPrefs.putFloat("flexisBudget", 0);
        }

        // Save shared preferences
        editPrefs.commit();

        // Open shared preferences, getting the values if they're already there
        mealsBalance = sharedPrefs.getInt("mealsBalance", 0);
        flexisBalance = sharedPrefs.getFloat("flexisBalance", 0);
        mealsBudget = sharedPrefs.getInt("mealsBudget", 0);
        flexisBudget = sharedPrefs.getFloat("flexisBudget", 0);

        //Initialize cart
        cart = new ArrayList<Item>();

    }

    public void addItemToCart(Item i){
        this.cart.add(i);
    }

    public void storeNewMealBalance(int i){
        editPrefs.putInt("mealsBalance", i);
        editPrefs.commit();
    }

    public void storeNewFlexiBalance(float f){
        editPrefs.putFloat("flexisBalance", f);
        editPrefs.commit();
    }

}
