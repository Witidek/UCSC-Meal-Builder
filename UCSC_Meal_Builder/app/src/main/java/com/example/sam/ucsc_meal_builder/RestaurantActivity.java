package com.example.sam.ucsc_meal_builder;

import android.app.ListActivity;
import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RestaurantActivity extends ListActivity {

    //LIST OF Restaurants WHICH WILL SERVE AS LIST ITEMS
    ArrayList<Restaurant> listItems = new ArrayList<Restaurant>();
    //DEFINING A Restaurant ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<Restaurant> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        //restaurantList = (ListView) findViewById(R.id.list);

        adapter = new ArrayAdapter<Restaurant>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        setListAdapter(adapter);

        //Declare and add two restaurants.
        Restaurant bananaJoes = new Restaurant("Banana Joe's");
        Restaurant owlsNest = new Restaurant("Owl's Nest");
        Restaurant porterCafe = new Restaurant("Porter Slug Grill and Cafe");
        Restaurant eightCafe = new Restaurant("College Eight Cafe");
        Restaurant cowellCoffeeShop = new Restaurant("Cowell Coffee Shop");
        Restaurant stevesonCoffeeHouse = new Restaurant("Stevenson Coffee House");
        Restaurant globalCafe = new Restaurant("Global Village Cafe");
        Restaurant terraFresca = new Restaurant("Terra Fresca");
        Restaurant oakesCafe = new Restaurant("Oakes Cafe");
        Restaurant vivas = new Restaurant("Vivas Restaurant");

        //Add restaurant to adapter
        adapter.add(bananaJoes);
        adapter.add(owlsNest);
        adapter.add(porterCafe);
        adapter.add(eightCafe);
        adapter.add(cowellCoffeeShop);
        adapter.add(stevesonCoffeeHouse);
        adapter.add(globalCafe);
        adapter.add(terraFresca);
        adapter.add(oakesCafe);
        adapter.add(vivas);

        ListView listView = getListView();
        // click listener example
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(RestaurantActivity.this, MenuActivity.class);
                // fill intent with restaurant object
                startActivity(intent);
                // When clicked, show a toast with the TextView text
                Toast.makeText(getApplicationContext(), ((TextView) view).getText(), Toast.LENGTH_SHORT).show();

            }
        });
    }

}
