package com.example.sam.ucsc_meal_builder;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Sam on 10/20/2015.
 */
public class Cart implements Parcelable {

    // Fields
    ArrayList<Item> items;

    // Constructor
    public Cart(){
        this.items = new ArrayList<Item>();
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
        this.items.add(i);
    }

    public void deleteItem(int i) {
        this.items.remove(i);
    }

    // Parcelable method implementations
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeTypedList(items);
    }

    public static final Parcelable.Creator<Cart> CREATOR = new Parcelable.Creator<Cart>() {
        @Override
        public Cart createFromParcel(Parcel in) {
            return new Cart(in);
        }

        @Override
        public Cart[] newArray(int size) {
            return new Cart[size];
        }
    };

    private Cart(Parcel in) {
        items = new ArrayList<Item>();
        in.readTypedList(items, Item.CREATOR);
    }
}
