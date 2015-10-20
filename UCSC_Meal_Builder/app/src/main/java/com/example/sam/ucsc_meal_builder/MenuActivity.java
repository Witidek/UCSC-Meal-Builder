package com.example.sam.ucsc_meal_builder;

import android.app.ListActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MenuActivity extends ListActivity {

    //LIST OF Restaurants WHICH WILL SERVE AS LIST ITEMS
    ArrayList<Item> itemList = new ArrayList<Item>();
    //DEFINING A Restaurant ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<Item> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        //restaurantList = (ListView) findViewById(R.id.list);

        adapter = new ArrayAdapter<Item>(this,
                android.R.layout.simple_list_item_1,
                itemList);
        setListAdapter(adapter);

        //Declare and add two restaurants.
        Item burger = new Item("burger", 5);
        Item fries = new Item("fries", 2);
        adapter.add(burger);
        adapter.add(fries);

    }
}
