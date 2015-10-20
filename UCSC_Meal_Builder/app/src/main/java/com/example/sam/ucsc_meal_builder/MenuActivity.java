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

        //Declare and add items currently Banana Joe's menu minus the drinks
        Item burger = new Item("Basic Burger", 5.50);
        Item fries = new Item("House Steak Fries", 2.95);
        Item chickenSandwich = new Item("Santa Cruz Chicken Sandwich", 6.95);
        Item turkeyClub = new Item("Turkey Avocado Club", 6.95);
        Item blat = new Item("BLAT Sandwich", 4.25);
        Item chickenStrips = new Item("Three Chicken Strips", 4.75);
        Item calamari = new Item ("Calamari & Fries", 5.95);
        Item mozzarella = new Item ("Mozzarella Sticks", 4.19);
        Item friesChimi = new Item("Chimi Fries", 3.75);
        Item friesMonster = new Item("Monster Fries", 3.95);
        Item onionRings = new Item("Onion Rings", 3.95);
        Item fruitCup = new Item ("Seasonal Frest Fruit Cup", 3.95);
        Item breakfastBurrito = new Item ("Big Wave Breakfast Burrito", 4.95);
        Item breakfastBurritoBaconAvocado = new Item("Big Wave Breakfast Burrito plus Bacon and Avocado", 7.70);
        Item riceBowl = new Item("Big Island Rice Bowl", 5.75);
        Item bigIslandChickenSalad = new Item("Big Island Chicken Salad", 6.95);
        Item pizzaCheese = new Item ("Cheese Pizza", 4.50);
        Item pizzaPepperoni = new Item ("Pepperoni Pizza", 4.95);
        Item pizzaVegan = new Item ("Supreme Veggie", 4.95);
        Item buffaloChickenRoyale = new Item("Buffalo Chicken Royale",6.95);
        Item hailCaesar = new Item ("Hail Caesar", 6.95);
        Item chimiPizala = new Item("Chimichurri Pizala", 6.75);
        Item acaiBowl = new Item ("Acai Bowl", 5.95);

        //add item adapter
        adapter.add(burger);
        adapter.add(fries);
        adapter.add(chickenSandwich);
        adapter.add(turkeyClub);
        adapter.add(blat);
        adapter.add(chickenStrips);
        adapter.add(calamari);
        adapter.add(mozzarella);
        adapter.add(friesChimi);
        adapter.add(friesMonster);
        adapter.add(onionRings);
        adapter.add(fruitCup);
        adapter.add(breakfastBurrito);
        adapter.add(breakfastBurritoBaconAvocado);
        adapter.add(riceBowl);
        adapter.add(bigIslandChickenSalad);
        adapter.add(pizzaCheese);
        adapter.add(pizzaPepperoni);
        adapter.add(pizzaVegan);
        adapter.add(buffaloChickenRoyale);
        adapter.add(hailCaesar);
        adapter.add(chimiPizala);
        adapter.add(acaiBowl);

    }
}
