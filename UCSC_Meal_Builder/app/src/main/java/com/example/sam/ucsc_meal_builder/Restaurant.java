package com.example.sam.ucsc_meal_builder;

/**
 * A Restaurant refers to any on-campus eatery where one can order Items, but here is only
 * described with its unique ID, name, and whether the restaurant accepts 55-meal equivalency. A
 * menu of Items are not loaded into this object because the menu will only be requested from the
 * database if the restaurant is selected for view.
 *
 * Immutable upon creation using constructor.
 */
public class Restaurant {

    /** Name of related table in local database */
    public static final String TABLE = "Restaurant";

    /** Names of each column in Restaurant table */
    public static final String KEY_restaurant_id = "restaurant_id";
    public static final String KEY_name = "name";
    public static final String KEY_accepts_meals = "accepts_meals";

    /** Unique ID number to identify an restaurant */
    private int restaurantID;
    /** Name of restaurant corresponding to real on-campus eateries */
    private String name;
    /** Flag for if restaurant accepts 55-meal equivalency */
    private boolean acceptsMeals;

    /** Constructor for Restaurant */
    public Restaurant(int id, String n, boolean b) {
        this.restaurantID = id;
        this.name = n;
        this.acceptsMeals = b;
    }

    /**
     * @return restaurantID  unique ID of restaurant
     */
    public int getRestaurantID() {
        return restaurantID;
    }

    /**
     * @return name  name of restaurant represented as a String
     */
    public String getName(){
        return name;
    }

    /**
     * @return acceptsMeals  true if restaurant accepts 55-meal equivalency, false otherwise
     */
    public boolean getAcceptsMeals() {
        return acceptsMeals;
    }

    /**
     * @return name  Restaurant is represented as a String by its name
     */
    @Override
    public String toString() {
        return name;
    }
}
