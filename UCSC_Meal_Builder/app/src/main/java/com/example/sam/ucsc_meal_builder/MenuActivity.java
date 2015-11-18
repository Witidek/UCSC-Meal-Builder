package com.example.sam.ucsc_meal_builder;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;

public class MenuActivity extends ListActivity {

    private DBHelper db;
    private ArrayList<Item> itemList;
    private ListAdapter adapter;
    private ListView listView;
    private Cart cart;

    private Intent intent;
    private int rid;
    private String flexisString;
    private String cashString;
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
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

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
            cashString = intent.getStringExtra("cash");
            numCash = new BigDecimal(cashString);

        }
        //If we are coming from BudgetActivity2, we are
        //working with cash and not meals.
        else {
            numMeals = 0;
            cashString = intent.getStringExtra("cash");
            numCash = new BigDecimal(cashString);
        }

        //Here comes the money
        budgetTotal = numCash.add(numFlexis.add(new BigDecimal(numMeals * 8)));
        Toast.makeText(getApplicationContext(), budgetTotal.toString(), Toast.LENGTH_SHORT).show();
        budgetRemaining = budgetTotal;


        //set budgetText and subtotalText with approp. values
        cart = db.getCart(rid);
        budgetText = (TextView) findViewById(R.id.budgetText);
        subtotalText = (TextView) findViewById(R.id.subtotalText);
        budgetText.setText(String.format("Budget: %s", budgetTotal.toString()));
        subtotalText.setText(String.format("Subtotal: %s", cart.getTotal().toString()));

        // Grab menu from database
        itemList = db.getMenu(rid);

        // Build ListAdapter
        adapter = new ListAdapter(this, itemList);
        setListAdapter(adapter);
        adapter.setBudgetRemaining(budgetRemaining);

        listView = getListView();

        // When an item is clicked, the corresponding item is added to the cart.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item selectedItem = adapter.getItem(position);
                if (selectedItem.getPrice().compareTo(budgetRemaining) > 0) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MenuActivity.this);
                    alertDialog.setTitle("You Are Over Your Budget!");
                    alertDialog.setMessage("Would you like to change your budget?");
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Leaving Blank
                        }
                    });
                    alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                    alertDialog.show();
                }
                //Need to do check with total cart $$$ and totalDollars before add

                // Add item to local cart and database cart (combine these two?)
                cart.addItem(selectedItem);
                db.addToCart(selectedItem);

                // Recalculate budgetRemaining and pass to adapter as well
                budgetRemaining = budgetTotal.subtract(cart.getTotal());
                adapter.setBudgetRemaining(budgetRemaining);

                // Update subtotal TextView and toast item addition
                subtotalText.setText(String.format("Subtotal: %s", cart.getTotal().toString()));
                String message = "Added " + adapter.getItem(position).getName() + " to cart";
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                // Update ListAdapter
                adapter.notifyDataSetChanged();
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

            case R.id.sort_name:
                // Sort itemList alphabetically by name

                return true;

            case R.id.sort_high:
                // Sort itemList by price

                return true;

            case R.id.sort_low:
                // Sort itemList by price
                itemList = SortHelper.mergeSort(itemList);
                adapter = new ListAdapter(this, itemList);
                adapter.setBudgetRemaining(budgetRemaining);
                setListAdapter(adapter);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        // Refresh local cart
        cart = db.getCart(rid);

        // Recalculate budgetRemaining and pass to adapter as well
        budgetRemaining = budgetTotal.subtract(cart.getTotal());
        adapter.setBudgetRemaining(budgetRemaining);

        // Update subtotal TextView
        subtotalText.setText(String.format("Subtotal: %s", cart.getTotal().toString()));

        // Redraw ListView
        listView.invalidateViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.actionbar_menu, menu);
        return (super.onCreateOptionsMenu(menu));
    }

    public void onCartPressed(View view){
        Intent intent = new Intent(MenuActivity.this, CartActivity.class);
        intent.putExtra("budgetTotal", budgetTotal.toString());
        intent.putExtra("meals",numMeals);
        intent.putExtra("cash",cashString);
        intent.putExtra("flexies",flexisString);
        intent.putExtra("rid", rid);
        startActivity(intent);
    }

}

