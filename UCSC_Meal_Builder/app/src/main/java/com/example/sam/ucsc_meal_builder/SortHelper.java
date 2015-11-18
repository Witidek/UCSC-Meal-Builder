package com.example.sam.ucsc_meal_builder;

import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Kevin on 11/17/2015.
 */
public class SortHelper {

    public static int repeat = 0;

    public static ArrayList<Item> merge(ArrayList<Item> left, ArrayList<Item> right) {
        ArrayList<Item> result = new ArrayList<>();
        while (!left.isEmpty() && !right.isEmpty()) {
            if (left.get(0).getPrice().compareTo(right.get(0).getPrice()) <= 0) {
                Item temp = left.get(0);
                left.remove(0);
                result.add(temp);
            } else {
                Item temp = right.get(0);
                right.remove(0);
                result.add(temp);
            }
        }
        if (!left.isEmpty()) {
            Item temp = left.get(0);
            left.remove(0);
            result.add(temp);
        }
        if (!right.isEmpty()) {
            Item temp = right.get(0);
            right.remove(0);
            result.add(temp);
        }
        return result;
    }

    public static ArrayList<Item> mergeSort(ArrayList<Item> itemList) {
        repeat++;
        System.out.println(repeat);
        ArrayList<Item> left = new ArrayList<>();
        ArrayList<Item> right = new ArrayList<>();
        ArrayList<Item> result = new ArrayList<>();
        if (itemList.size() == 1) {
            return itemList;
        }
        int middle = itemList.size()/2;
        for (int i = 0; i < middle; i++) {
            left.add(itemList.get(i));
        }
        for (int i = middle; i < itemList.size(); i++) {
            right.add(itemList.get(i));
        }
        result = merge(mergeSort(left), mergeSort(right));

        return result;
    }
}


