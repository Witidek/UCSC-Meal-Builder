package com.example.sam.ucsc_meal_builder;

import android.app.ListActivity;
import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;

public class RestaurantActivity extends ListActivity {

    private DBHelper db;
    private ArrayList<Restaurant> restaurantList;
    private ArrayAdapter<Restaurant> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load layout
        setContentView(R.layout.activity_restaurant);

        // Enable home button and manually set title
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setCustomView(R.layout.ab_title);
        TextView title = (TextView) findViewById(android.R.id.text1);
        title.setText("Restaurants");

        // Get DBHelper instance and grab restaurant list
        db = DBHelper.getInstance(this);
        restaurantList = db.getRestaurants();

        // Construct basic ArrayAdapter for ListView of restaurants
        adapter = new ArrayAdapter<Restaurant>(this,
                android.R.layout.simple_list_item_1,
                restaurantList);
        setListAdapter(adapter);

        // onClick listener changes activity to MenuActivity and passes restaurant_id
        ListView listView = getListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int rid = adapter.getItem(position).getRestaurantID();
                boolean acceptsMeals = adapter.getItem(position).getAcceptsMeals();

                // Toast for closed restaurants, and start new activity appropriately depending on acceptsMeals
                if (rid == 3 || rid == 6) {
                    //Prompts that the restaurants are closed
                    Toast.makeText(getApplicationContext(),"Currently Closed", Toast.LENGTH_SHORT).show();
                }else if (acceptsMeals) {
                    Intent intent = new Intent(RestaurantActivity.this, BudgetActivity.class);
                    intent.putExtra("previous", "RestaurantActivity");
                    intent.putExtra("rid", rid);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(RestaurantActivity.this, BudgetActivity2.class);
                    intent.putExtra("previous", "RestaurantActivity");
                    intent.putExtra("rid", rid);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
