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

    private DBHelper db;
    private ArrayList<Restaurant> restaurantList;
    private ArrayAdapter<Restaurant> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        db = new DBHelper(this);
        restaurantList = db.getRestaurants();

        adapter = new ArrayAdapter<Restaurant>(this,
                android.R.layout.simple_list_item_1,
                restaurantList);
        setListAdapter(adapter);

        ListView listView = getListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //rid will be passed to the next (Budget)Activity
                int rid = adapter.getItem(position).getRestaurantID();
                //If the restaurant is one of those that only accepts
                //flexis and cash, open up the other budget activity
                if (rid == 2 || rid == 4 || rid == 5 || rid ==9){
                    Intent intent = new Intent(RestaurantActivity.this, BudgetActivity2.class);
                    intent.putExtra("rid", rid);
                    startActivity(intent);
                }
                //Otherwise open the reg budget activity
                else {
                    Intent intent = new Intent(RestaurantActivity.this, BudgetActivity.class);
                    intent.putExtra("rid", rid);
                    startActivity(intent);
                }

                // When clicked, show a toast with restaurant name
                Toast.makeText(getApplicationContext(), adapter.getItem(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
