package com.example.sam.ucsc_meal_builder;

/**
 * Created by Jason on 11/29/2015.
 */
public class Favorite {

    public static final String TABLE = "Favorite";

    public static final String KEY_favorite_id = "favorite_id";
    public static final String KEY_restaurant_id = "restaurant_id";
    public static final String KEY_name = "name";

    private int fid;
    private int rid;
    private String name;

    public Favorite(int fid, int rid, String name) {
        this.fid = fid;
        this.rid = rid;
        this.name = name;
    }

    public int getFID() {
        return fid;
    }

    public int getRID() {
        return rid;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
