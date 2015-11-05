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
    private Cart cart;

    private Intent intent;
    private int rid;
    private String flexisString;
    private int numMeals;
    private BigDecimal numFlexis;
    private BigDecimal numCash;

    private int whichBudgetActivity;

    private TextView budgetText;
    private TextView subtotalText;

    private BigDecimal budgetTotal;
    private BigDecimal budgetRemaining;

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

        //Here comes the money
        budgetTotal = numFlexis.add(new BigDecimal(numMeals * 8));
        Toast.makeText(getApplicationContext(), budgetTotal.toString(), Toast.LENGTH_SHORT).show();
        budgetRemaining = budgetTotal;

        //set budgetText and subtotalText with approp. values
        cart = db.getCart(rid);
        budgetText = (TextView) findViewById(R.id.budgetText);
        subtotalText = (TextView) findViewById(R.id.subtotalText);
        budgetText.setText(String.format("Budget: %s", budgetTotal.toString()));
        subtotalText.setText(String.format("Subtotal: %s", cart.getTotal().toString()));

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
                cart.addItem(selectedItem);
                db.addToCart(selectedItem);
                subtotalText.setText(String.format("Subtotal: %s", cart.getTotal().toString()));
                String message = "Added " + adapter.getItem(position).getName() + " to cart";
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        cart = db.getCart(rid);
        budgetRemaining = budgetTotal.subtract(cart.getTotal());
        subtotalText.setText(String.format("Subtotal: %s", cart.getTotal().toString()));
        Toast.makeText(this, "RESUMED FROM CART!", Toast.LENGTH_SHORT).show();
        
        // USE CUSTOM LISTADAPTER HERE, COMPARING BUDGETREMAINING TO EACH ITEM IN LISTVIEW
        // DO COLORING/HIGHLIGHTING HERE
    }

    public void onCartPressed(View view){
        Intent intent = new Intent(MenuActivity.this, CartActivity.class);
        intent.putExtra("budgetTotal", budgetTotal.toString());
        intent.putExtra("rid", rid);
        startActivity(intent);
    }

}
