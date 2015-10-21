package com.example.sam.ucsc_meal_builder;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class CartActivity extends AppCompatActivity {

    ArrayAdapter<Item> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        /*adapter = new ArrayAdapter<Item>(this,
                android.R.layout.simple_list_item_1,
                MenuActivity.myCart.items);
        setListAdapter(adapter);*/

        //Intent intent = getIntent();
        //Cart checkoutCart = new Cart();
        //String message = intent.getStringExtra("numItems");

        Toast.makeText(getApplicationContext(), Integer.toString(MenuActivity.myCart.getSize()), Toast.LENGTH_SHORT).show();


    }
}
