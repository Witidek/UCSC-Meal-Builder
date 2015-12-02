package com.example.sam.ucsc_meal_builder;

/**
 * A favorite is an object that contains IDs corresponding to a saved cart within the Favorite
 * table in the local database. Each Favorite also has a respective Budget and is linked in the
 * database by favoriteID.
 *
 * Immutable upon creation with constructor.
 */
public class Favorite {

    /** Name of related table in local database */
    public static final String TABLE = "Favorite";

    /** Names of each column in Favorite table */
    public static final String KEY_favorite_id = "favorite_id";
    public static final String KEY_restaurant_id = "restaurant_id";
    public static final String KEY_name = "name";
    public static final String KEY_item_id = "item_id";
    public static final String KEY_quantity = "quantity";

    /** Unique ID to identify a favorite */
    private int favoriteID;
    /** Unique denoting which restaurant the favorited cart belongs to */
    private int restaurantID;
    /** User inputted name for a favorite that appears on FavoriteActivity */
    private String name;

    /** Constructor for Favorite */
    public Favorite(int fid, int rid, String name) {
        this.favoriteID = fid;
        this.restaurantID = rid;
        this.name = name;
    }

    /**
     * @return favoriteID  should match a Budget row in database table
     */
    public int getFavoriteID() {
        return favoriteID;
    }

    /**
     * @return restaurantID  which restaurant the cart belongs to
     */
    public int getRestaurantID() {
        return restaurantID;
    }

    /**
     * @return name  user inputted name of favorite
     */
    public String getName() {
        return name;
    }

    /**
     * @return name  Favorite is represented as a String by its name
     */
    @Override
    public String toString() {
        return name;
    }
}
