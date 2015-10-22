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

    private DBHelper db;
    private ArrayList<Item> itemList = new ArrayList<Item>();
    private ArrayAdapter<Item> adapter;
    private Cart myCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        db = new DBHelper(this);
        Intent intent = getIntent();
        Restaurant restaurant = intent.getExtras().getParcelable("restaurant");
        itemList = db.getMenu(restaurant.getRestaurantID());

        adapter = new ArrayAdapter<Item>(this,
                android.R.layout.simple_list_item_1,
                itemList);
        setListAdapter(adapter);

        ListView listView = getListView();

        myCart = new Cart();

        // When an item is clicked, the corresponding item is added to the cart.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item foundSelection = adapter.getItem(position);
                myCart.addItem(foundSelection);
                Toast.makeText(getApplicationContext(), adapter.getItem(position).getName(), Toast.LENGTH_SHORT).show();
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
