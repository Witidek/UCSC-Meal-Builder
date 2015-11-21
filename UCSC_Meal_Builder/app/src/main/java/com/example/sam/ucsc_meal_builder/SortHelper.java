package com.example.sam.ucsc_meal_builder;

import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Kevin on 11/17/2015.
 */
public class SortHelper {

    // Enumerator for sorting types
    private enum SortType {LOW, HIGH, ALPHA, COURSE};

    // Public methods to initiate sorting
    public static ArrayList<Item> sortLow(ArrayList<Item> itemList) {
        return mergeSort(itemList, SortType.LOW);
    }

    public static ArrayList<Item> sortHigh(ArrayList<Item> itemList) {
        return mergeSort(itemList, SortType.HIGH);
    }

    public static ArrayList<Item> sortAlpha(ArrayList<Item> itemList) {
        return mergeSort(itemList, SortType.ALPHA);
    }

    public static ArrayList<Item> sortCourse(ArrayList<Item> itemList) {
        return mergeSort(itemList, SortType.COURSE);
    }

    // Switch case comparison to compare two items
    private static boolean sortCompare(Item left, Item right, SortType type) {
        switch(type) {
            case LOW:
                return (left.getPrice().compareTo(right.getPrice()) <= 0);
            case HIGH:
                return (left.getPrice().compareTo(right.getPrice()) >= 0);
            case ALPHA:
                return (left.getName().compareToIgnoreCase(right.getName()) <= 0);
            case COURSE:
                return (left.getCourse().compareToIgnoreCase(right.getCourse()) <= 0);
            default:
                // Should never reach here
                return true;
        }
    }

    // Merges two lists together using sortCompare
    private static ArrayList<Item> merge(ArrayList<Item> left, ArrayList<Item> right, SortType type) {

        // Combined result list
        ArrayList<Item> result = new ArrayList<>();

        int leftIndex = 0;
        int rightIndex = 0;
        // While neither are empty, pop least greatest value, append to result
        while (leftIndex < left.size() && rightIndex < right.size()) {

            if (sortCompare(left.get(leftIndex), right.get(rightIndex), type)) {
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

    // Main sorting method, splits list into many smaller lists, then use merge and sortCompare
    private static ArrayList<Item> mergeSort(ArrayList<Item> itemList, SortType type) {

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
        left = mergeSort(left, type);
        right = mergeSort(right, type);

        // Combine using merge
        return merge(left, right, type);

    }
}


