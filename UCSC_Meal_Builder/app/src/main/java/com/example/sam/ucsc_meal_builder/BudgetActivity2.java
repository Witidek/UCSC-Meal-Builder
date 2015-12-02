package com.example.sam.ucsc_meal_builder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.content.SharedPreferences;

import java.math.BigDecimal;

/**
 * This activity can be started from RestaurantActivity, or returned to through android back button
 * from MenuActivity. It contains two fields where the user can input numerical values for their
 * flexis, and cash budget to build a meal. This activity is chosen when the restaurant selected
 * does not accept 55-meal equivalency.
 */
public class BudgetActivity2 extends Activity {

    BigDecimal cash = new BigDecimal(0);
    BigDecimal flexis = new BigDecimal(0);

    TextView cashText;
    TextView flexiText;
    private SharedPreferences sharedPrefs;

    private Intent intent;
    private int rid;

    /**
     * Actionbar is loaded and setup, TextViews are filled, and listeners are attached to each
     * TextView to capture changes for meals, flexis, and cash.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load layout
        setContentView(R.layout.activity_budget2);

        // Set home button and title
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setCustomView(R.layout.ab_title);
        TextView title = (TextView) findViewById(android.R.id.text1);
        title.setText("Budget");

        //Receive rid
        intent = getIntent();
        rid = intent.getIntExtra("rid", 0);

        // Load SharedPreferences to get balance
        sharedPrefs = getSharedPreferences("balance", MODE_PRIVATE);

        // Grab TextViews
        cashText = (TextView) findViewById(R.id.cashBudgetText);
        flexiText = (TextView) findViewById(R.id.flexiBudgetText);
        TextView mealBalanceText = (TextView) findViewById(R.id.mealBalanceText);
        TextView flexiBalanceText = (TextView) findViewById(R.id.flexiBalanceText);

        int mealBalance = sharedPrefs.getInt("meals", 0);
        BigDecimal flexiBalance = new BigDecimal(sharedPrefs.getString("flexis", ""));

        mealBalanceText.setText(String.format("Meals: %d", mealBalance));
        flexiBalanceText.setText(String.format("Flexis: $%.2f", flexiBalance));

        // Save number values to local variable when EditText focus is lost (done editing)
        cashText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    cash = new BigDecimal(cashText.getText().toString());
                }
            }
        });
        flexiText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    flexis = new BigDecimal(flexiText.getText().toString());
                }
            }
        });

        // Display initial values
        cashText.setText(String.format("%.2f", cash));
        flexiText.setText(String.format("%.2f", flexis));
    }

    /**
     * Enable the home button in top left of actionbar to return to MainActivity when pressed
     */
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

    /**
     * Increments flexis value by one and updates the related TextView
     */
    public void incrementFlexis (View view) {
        flexis = new BigDecimal(flexiText.getText().toString());
        flexis = flexis.add(new BigDecimal(1));
        flexiText.setText(String.format("%.2f", flexis));
    }

    /**
     * Decrements flexis value by one and updates the related TextView. Does not allow value to
     * drop below zero.
     */
    public void decrementFlexis(View view) {
        flexis = new BigDecimal(flexiText.getText().toString());
        if (flexis.compareTo(new BigDecimal(1)) != -1) {
            flexis = flexis.subtract(new BigDecimal(1));
        }
        flexiText.setText(String.format("%.2f", flexis));
    }

    /**
     * Increments cash value by one and updates the related TextView
     */
    public void incrementCash (View view) {
        cash = new BigDecimal(cashText.getText().toString());
        cash = cash.add(new BigDecimal(1));
        cashText.setText(String.format("%.2f", cash));
    }

    /**
     * Decrements cash value by one and updates the related TextView. Does not allow value to
     * drop below zero.
     */
    public void decrementCash(View view) {
        cash = new BigDecimal(cashText.getText().toString());
        if (cash.compareTo(new BigDecimal(1)) != -1) {
            cash = cash.subtract(new BigDecimal(1));
        }
        cashText.setText(String.format("%.2f", cash));
    }

    /**
     * Check user inputted flexis value with current balance. If not over balance,
     * construct a Budget object filled with captured flexis and cash values from TextViews,
     * and send it in an intent to MenuActivity.
     */
    public void onClickArrow(View view) {
        BigDecimal totalFlexies = new BigDecimal(sharedPrefs.getString("flexis", "0"));
        if( totalFlexies.compareTo(flexis) >= 0) {
            Intent intent = new Intent(BudgetActivity2.this, MenuActivity.class);
            intent.putExtra("previous", "BudgetActivity2");

            //Send off budget values
            flexis = new BigDecimal(flexiText.getText().toString());
            cash = new BigDecimal(cashText.getText().toString());
            Budget budget = new Budget(rid, flexis, cash);
            intent.putExtra("budget", budget);

            startActivity(intent);
        }else {
            //Toast.makeText(getApplicationContext(), "Exceeds actual Balance!", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Oh No....!");
            alertDialog.setMessage("You are over your Balance!");
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // here you can add functions
                }
            });
            alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
            alertDialog.show();
        }
    }
}
