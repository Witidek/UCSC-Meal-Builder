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
import java.math.BigDecimal;
import java.util.ArrayList;

public class MenuActivity extends ListActivity {

    private DBHelper db;
    private ArrayList<Item> itemList;
    private ArrayAdapter<Item> adapter;

    private Intent intent;
    private int rid;
    private String flexisString;
    private int numMeals;
    private BigDecimal numFlexis;
    private BigDecimal numCash;

    private int whichBudgetActivity;

    private TextView mealText;
    private TextView flexiText;

    private BigDecimal totalDollars;

    private BigDecimal totalSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        db = new DBHelper(this);

        //Unpack extras
        intent = getIntent();
        whichBudgetActivity = intent.getIntExtra("whichBudgetActivity",1);
        rid = intent.getIntExtra("rid", 0);

        flexisString = intent.getStringExtra("flexis");
        numFlexis = new BigDecimal(flexisString);

        //If we are coming from BudgetActivity, we are
        //working with meals and not cash.
        if (whichBudgetActivity == 1) {
            numMeals = intent.getIntExtra("meals", 0);
            numCash = new BigDecimal(0);
        }
        //If we are coming from BudgetActivity2, we are
        //working with cash and not meals.
        else {
            numMeals = 0;
            numCash = new BigDecimal(intent.getStringExtra("cash"));
        }

        //set mealText and flexiText with approp. values
        mealText = (TextView) findViewById(R.id.mealText);
        flexiText = (TextView) findViewById(R.id.flexiText);
        mealText.setText(String.format("Meals: %s", Integer.toString(numMeals)));
        flexiText.setText(String.format("Flexis: %s", numFlexis.toString()));

        //Here comes the money
        totalDollars = numFlexis.add(new BigDecimal(numMeals));
        Toast.makeText(getApplicationContext(), totalDollars.toString(), Toast.LENGTH_SHORT).show();
        totalSelection = new BigDecimal(0);

        itemList = db.getMenu(rid);

        adapter = new ArrayAdapter<Item>(this,
                android.R.layout.simple_list_item_1,
                itemList);
        setListAdapter(adapter);

        ListView listView = getListView();

        // When an item is clicked, the corresponding item is added to the cart.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item selectedItem = adapter.getItem(position);
                //Need to do check with total cart $$$ and totalDollars before add
                db.addToCart(selectedItem);
                String message = "Added " + adapter.getItem(position).getName() + " to cart";
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void onCartPressed(View view){
        Intent intent = new Intent(MenuActivity.this, CartActivity.class);
        intent.putExtra("rid", rid);
        startActivity(intent);
    }

}
