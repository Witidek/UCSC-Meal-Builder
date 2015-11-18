package com.example.sam.ucsc_meal_builder;

import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Kevin on 11/17/2015.
 */
public class SortHelper {

    private static ArrayList<Item> merge(ArrayList<Item> left, ArrayList<Item> right) {

        // Combined result list
        ArrayList<Item> result = new ArrayList<>();

        int leftIndex = 0;
        int rightIndex = 0;
        // While neither are empty, pop least greatest value, append to result
        while (leftIndex < left.size() && rightIndex < right.size()) {

            if (left.get(leftIndex).getPrice().compareTo(right.get(rightIndex).getPrice()) <= 0) {
                result.add(left.get(leftIndex));
                leftIndex++;
            } else {
                result.add(right.get(rightIndex));
                rightIndex++;
            }
        }

        // Add leftover odd item from either half
        while (leftIndex < left.size()) {
            result.add(left.get(leftIndex));
            leftIndex++;
        }
        while (rightIndex < right.size()) {
            result.add(right.get(rightIndex));
            rightIndex++;
        }

        return result;
    }

    public static ArrayList<Item> mergeSort(ArrayList<Item> itemList) {

        // Two half arrays to split
        ArrayList<Item> left = new ArrayList<>();
        ArrayList<Item> right = new ArrayList<>();

        // Return self if list only has one item
        if (itemList.size() == 1) {
            return itemList;
        }

        // Calculate middle index and split list into left and right halves
        int middle = itemList.size()/2;
        for (int i = 0; i < itemList.size(); i++) {
            if (i < middle) {
                left.add(itemList.get(i));
            }else {
                right.add(itemList.get(i));
            }
        }

        // Call mergeSort on left and right recursively
        left = mergeSort(left);
        right = mergeSort(right);

        // Combine using merge
        return merge(left, right);

    }
}


