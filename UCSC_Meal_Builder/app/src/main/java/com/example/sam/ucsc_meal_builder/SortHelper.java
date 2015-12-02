package com.example.sam.ucsc_meal_builder;

import java.util.ArrayList;

/**
 * A helper class that contains sorting methods. No constructor and no instances of this class
 * can be made. Used for sorting ArrayLists of items for organization in MenuActivity.
 */
public class SortHelper {

    /** Enumerator for sorting types */
    private enum SortType {LOW, HIGH, ALPHA, COURSE};

    /**
     * Public sorting method to order the ArrayList starting from item with lowest price to highest.
     *
     * @param itemList      ArrayList to be sorted
     * @return mergeSort()  resulting ArrayList sorted by mergeSort
     */
    public static ArrayList<Item> sortLow(ArrayList<Item> itemList) {
        return mergeSort(itemList, SortType.LOW);
    }

    /**
     * Public sorting method to order the ArrayList starting from item with highest price to lowest.
     *
     * @param itemList      ArrayList to be sorted
     * @return mergeSort()  resulting ArrayList sorted by mergeSort
     */
    public static ArrayList<Item> sortHigh(ArrayList<Item> itemList) {
        return mergeSort(itemList, SortType.HIGH);
    }

    /**
     * Public sorting method to sort the ArrayList by alphabetical order of item name.
     *
     * @param itemList      ArrayList to be sorted
     * @return mergeSort()  resulting ArrayList sorted by mergeSort
     */
    public static ArrayList<Item> sortAlpha(ArrayList<Item> itemList) {
        return mergeSort(itemList, SortType.ALPHA);
    }

    /**
     * Public sorting method to sort the ArrayList by alphabetical order of item's course.
     *
     * @param itemList      ArrayList to be sorted
     * @return mergeSort()  resulting ArrayList sorted by mergeSort
     */
    public static ArrayList<Item> sortCourse(ArrayList<Item> itemList) {
        return mergeSort(itemList, SortType.COURSE);
    }

    /**
     * Switch case comparison to compare two items. Uses item object information to compare
     * depending on what type of sorting is given.
     *
     * @param left   left item to compare
     * @param right  right item to compare
     * @param type   what to sort by
     */
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

    /**
     * Sort and merge two half lists together using sortCompare
     *
     * @param left     left half of ArrayList
     * @param right    right half of ArrayList
     * @param type     what to sort by
     * @return result  combined left and right into one sorted ArrayList
     */
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

    /**
     * Main recursive sorting method, splits list into many smaller lists, then use merge and
     * sortCompare. Implementation of MergeSort.
     *
     * @param itemList  ArrayList to be sorted
     * @param type      what to sort by
     */
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


