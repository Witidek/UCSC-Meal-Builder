package com.example.sam.ucsc_meal_builder;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;

/**
 * Created by Jason on 11/23/2015.
 */
public class Budget implements Parcelable {
    private int rid;
    private int meals;
    private BigDecimal flexis;
    private BigDecimal cash;

    public Budget() {
        rid = 0;
        meals = 0;
        flexis = new BigDecimal(0);
        cash = new BigDecimal(0);
    }

    public int getRID() {
        return rid;
    }

    public int getMeals() {
        return meals;
    }

    public BigDecimal getFlexis() {
        return flexis;
    }

    public BigDecimal getCash() {
        return cash;
    }

    public void setRID(int rid) {
        this.rid = rid;
    }

    public void setMeals(int meals) {
        this.meals = meals;
    }

    public void setFlexis(BigDecimal flexis) {
        this.flexis = flexis;
    }

    public void setCash(BigDecimal cash) {
        this.cash = cash;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(rid);
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
        rid = in.readInt();
        meals = in.readInt();
        flexis = new BigDecimal(in.readInt());
        cash = new BigDecimal(in.readInt());
    }
}
