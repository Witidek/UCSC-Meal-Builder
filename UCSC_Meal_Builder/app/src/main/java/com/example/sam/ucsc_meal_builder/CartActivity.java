package com.example.sam.ucsc_meal_builder;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;

public class CartActivity extends ListActivity {

    private Intent intent;
    private int rid;
    private BigDecimal budgetTotal;
    private BigDecimal meals;
    private BigDecimal cash;
    private BigDecimal flexies;
    private DBHelper db;
    private ArrayAdapter<Item> adapter;


    private Cart cart;
    private TextView textView;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor editPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // Get restaurant_id from intent
        intent = getIntent();
        rid = intent.getIntExtra("rid", 0);
        budgetTotal = new BigDecimal(intent.getStringExtra("budgetTotal"));
        meals = new BigDecimal(intent.getIntExtra("meals", 0));
        cash = new BigDecimal(intent.getStringExtra("cash"));
        flexies = new BigDecimal(intent.getStringExtra("flexies"));

        // Load SharedPreferences to get balance
        sharedPrefs = getSharedPreferences("balance", MODE_PRIVATE);
        editPrefs = sharedPrefs.edit();

        // Grab cart items for specified restaurant from database
        db = new DBHelper(this);
        cart = db.getCart(rid);

        // Create ListView adapter to display items in cart
        adapter = new ArrayAdapter<Item>(this,
                android.R.layout.simple_list_item_1,
                cart.items);
        setListAdapter(adapter);

        // TextView for total
        textView = (TextView) findViewById(R.id.totalText);
        textView.setText(String.format("Total: $%s", cart.getTotal().toString()));

        // onClick event for ListView items
        ListView listView = getListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // PROMPT WARNING FOR DELETE FROM CART--------------------------
                db.deleteItem(adapter.getItem(position));
                cart.deleteItem(position);
                adapter.notifyDataSetChanged();
                textView.setText(String.format("Total: $%s", cart.getTotal().toString()));
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

    public void onClickClearCart(View view) {
        // PROMPT WARNING FOR CLEAR CART------------------------------
        // Clear local cart and database cart

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Clear Cart");
        alertDialog.setMessage("Are you sure?");
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                cart.clearCart();
                db.clearCart(rid);

                // Update ListView and total text
                adapter.notifyDataSetChanged();
                textView.setText(String.format("Total: $%s", cart.getTotal().toString()));
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.show();


    }

    public void onClickCheckout(View view) {
        // PROMPT CONFIRMATION FOR CHECKOUT----------------------------
        // Subtract total from meals and flexis balance
        BigDecimal totalMeals = new BigDecimal(sharedPrefs.getInt("meals", 0));
        BigDecimal totalFlexies = new BigDecimal(sharedPrefs.getString("flexis", "0"));
        BigDecimal valueMeal = new BigDecimal(8);
        BigDecimal total = cart.getTotal();
        BigDecimal amountMeals = total.divideToIntegralValue(valueMeal);
        BigDecimal compareMeal = amountMeals.min(meals);
        BigDecimal remainAmount = total.subtract(compareMeal.multiply(valueMeal));
        BigDecimal resultflexies;
        //Toast.makeText(getApplicationContext(), compareMeal.toString(), Toast.LENGTH_SHORT).show();
        BigDecimal resultMeals = totalMeals.subtract(compareMeal);
        if(remainAmount.compareTo(flexies) >= 0) {
            resultflexies = totalFlexies.subtract(flexies);
        }else{
            resultflexies = totalFlexies.subtract(remainAmount);
        }

        editPrefs.putInt("meals", resultMeals.intValue());
        // If only using cash or meals don't change flexies or if cash covers it
        if(flexies.compareTo(new BigDecimal(0)) > 0) {
            editPrefs.putString("flexis", resultflexies.toString());
        }
        editPrefs.commit();

        // Clear local cart and database cart
        cart.clearCart();
        db.clearCart(rid);

        // Update ListView and total text
        adapter.notifyDataSetChanged();
        textView.setText(String.format("Total: $%s", cart.getTotal().toString()));
    }
}
