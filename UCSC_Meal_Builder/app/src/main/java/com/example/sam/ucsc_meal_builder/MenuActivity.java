package com.example.sam.ucsc_meal_builder;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

public class MenuActivity extends ListActivity {

    //LIST OF Restaurants WHICH WILL SERVE AS LIST ITEMS
    ArrayList<Item> itemList = new ArrayList<Item>();
    //DEFINING A Restaurant ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<Item> adapter;
    Cart myCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

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

        ListView listView = getListView();

        myCart = new Cart();

        // When an item is clicked, the corresponding item is added to the cart.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                /* Old code that uses getText to grab name and search through ArrayList
                //Take the name of the thing they selected, but cut off the price
                String nameOfSelection = ((TextView) view).getText().toString();
                int indexOfColon = nameOfSelection.indexOf(" : ");
                nameOfSelection = nameOfSelection.substring(0, indexOfColon);


                Toast.makeText(getApplicationContext(), nameOfSelection, Toast.LENGTH_SHORT).show();
                //Iterate through all items in adapter
                for (int i = 0 ; i < adapter.getCount(); i++){
                    //If the name matches, that's the selection
                    if(adapter.getItem(i).name.equals(nameOfSelection)){
                        Item foundSelection = adapter.getItem(i);
                        myCart.addItem(foundSelection);
                    }
                }*/

                // New code that grabs item based on position in ArrayList
                Item foundSelection = adapter.getItem(position);
                myCart.addItem(foundSelection);
            }
        });

    }

    public void onCartPressed(View view){
        Intent intent = new Intent(MenuActivity.this, CartActivity.class);
        // Pack cart object into intent
        intent.putExtra("cart", myCart);
        startActivity(intent);
    }

}
