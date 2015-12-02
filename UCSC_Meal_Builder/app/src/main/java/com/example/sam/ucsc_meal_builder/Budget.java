package com.example.sam.ucsc_meal_builder;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;

/**
 * A Budget is a Parcelable object containing user inputted values for the three types of currency
 * able to be used to purchase items. This object is passed through as an Intent extra from its
 * creation in BudgetActivity to all following activities.
 *
 * Immutable upon creation using one of many constructors.
 */
public class Budget implements Parcelable {

    /** Name of related table in local database */
    public static final String TABLE = "Budget";

    /** Names of each column in Budget table */
    public static final String KEY_favorite_id = "favorite_id";
    public static final String KEY_meals = "meals";
    public static final String KEY_flexis = "flexis";
    public static final String KEY_cash = "cash";

    /** Unique ID to reference which restaurant this Budget is for */
    private int restaurantID;
    /** User inputted number of meals as upper bound */
    private int meals;
    /** User inputted flexis as upper bound */
    private BigDecimal flexis;
    /** User inputted cash as upper bound */
    private BigDecimal cash;

    /** Constructor for a Budget from a restaurant not accepting 55-meal equivalency */
    public Budget(int rid, BigDecimal flexis, BigDecimal cash) {
        this.restaurantID = rid;
        this.flexis = flexis;
        this.cash = cash;
    }

    /** Constructor for a Budget from a restaurant accepting 55-meal equivalency */
    public Budget(int rid, int meals, BigDecimal flexis, BigDecimal cash) {
        this.restaurantID = rid;
        this.meals = meals;
        this.flexis = flexis;
        this.cash = cash;
    }

    /**
     * @return rid  unique ID for which restaurant the Budget was created for
     */
    public int getRestaurantID() {
        return restaurantID;
    }

    /**
     * @return meals  how many meals user inputted
     */
    public int getMeals() {
        return meals;
    }

    /**
     * @return flexis  how many flexis user inputted
     */
    public BigDecimal getFlexis() {
        return flexis;
    }

    /**
     * @return cash  how much cash user inputted
     */
    public BigDecimal getCash() {
        return cash;
    }

    /**
     * Parcelable interface implementation
     */
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(restaurantID);
        out.writeInt(meals);
        out.writeInt(flexis.intValue());
        out.writeInt(cash.intValue());
    }

    public static final Parcelable.Creator<Budget> CREATOR
            = new Parcelable.Creator<Budget>() {
        public Budget createFromParcel(Parcel in) {
            return new Budget(in);
        }

        public Budget[] newArray(int size) {
            return new Budget[size];
        }
    };

    private Budget(Parcel in) {
        restaurantID = in.readInt();
        meals = in.readInt();
        flexis = new BigDecimal(in.readInt());
        cash = new BigDecimal(in.readInt());
    }
}
