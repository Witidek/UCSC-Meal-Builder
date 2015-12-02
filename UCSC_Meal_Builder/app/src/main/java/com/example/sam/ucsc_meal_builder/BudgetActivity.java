package com.example.sam.ucsc_meal_builder;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.app.AlertDialog;

import java.math.BigDecimal;

/**
 * This activity can be started from RestaurantActivity, or returned to through android back button
 * from MenuActivity. It contains three fields where the user can input numerical values for their
 * meals, flexis, and cash budget to build a meal. This activity is chosen when the restaurant
 * selected accepts 55-meal equivalency.
 */
public class BudgetActivity extends Activity {

    private int meals = 0;
    private BigDecimal flexis = new BigDecimal(0);
    private BigDecimal cash = new BigDecimal(0);

    private TextView mealText;
    private TextView flexiText;
    private TextView cashText;

    private Intent intent;
    private int rid;
    private SharedPreferences sharedPrefs;

    /**
     * Actionbar is loaded and setup, TextViews are filled, and listeners are attached to each
     * TextView to capture changes for meals, flexis, and cash.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load layout
        setContentView(R.layout.activity_budget);

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
        mealText = (TextView) findViewById(R.id.mealBudgetText);
        flexiText = (TextView) findViewById(R.id.flexiBudgetText);
        cashText = (TextView) findViewById(R.id.cashBudgetText);
        TextView mealBalanceText = (TextView) findViewById(R.id.mealBalanceText);
        TextView flexiBalanceText = (TextView) findViewById(R.id.flexiBalanceText);

        int mealBalance = sharedPrefs.getInt("meals", 0);
        BigDecimal flexiBalance = new BigDecimal(sharedPrefs.getString("flexis", ""));

        mealBalanceText.setText(String.format("Meals: %d", mealBalance));
        flexiBalanceText.setText(String.format("Flexis: $%.2f", flexiBalance));

        // Save number values to local variable when EditText focus is lost (done editing)
        mealText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    try {
                        meals = Integer.parseInt(mealText.getText().toString());
                    }catch (NumberFormatException e) {
                        // Empty..
                    }
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
        cashText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    cash = new BigDecimal(cashText.getText().toString());
                }
            }
        });

        // Display initial values
        mealText.setText(String.format("%d", meals));
        flexiText.setText(String.format("%.2f", flexis));
        cashText.setText(String.format("%.2f", cash));
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
     * Increments meals value by one and updates the related TextView.
     */
    public void incrementMeals (View view) {
        meals = Integer.valueOf(mealText.getText().toString());
        meals += 1;
        mealText.setText(String.format("%d", meals));
    }

    /**
     * Decrements meals value by one and updates the related TextView. Does not allow value to
     * drop below zero.
     */
    public void decrementMeals(View view) {
        meals = Integer.valueOf(mealText.getText().toString());
        if (meals > 0) meals -= 1;
        mealText.setText(String.format("%d", meals));
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
     * Check user inputted meals and flexis values with current balance. If not over balance,
     * construct a Budget object filled with captured meals, flexis, and cash values from TextViews,
     * and send it in an intent to MenuActivity.
     */
    public void onClickArrow(View view) {
        BigDecimal totalMeals = new BigDecimal(sharedPrefs.getInt("meals", 0));
        BigDecimal totalFlexies = new BigDecimal(sharedPrefs.getString("flexis", "0"));

        if (totalMeals.compareTo(new BigDecimal(meals)) >= 0 && totalFlexies.compareTo(flexis) >= 0) {

            Intent intent = new Intent(BudgetActivity.this, MenuActivity.class);
            intent.putExtra("previous", "BudgetActivity");

            //Send off budget values
            meals = Integer.valueOf(mealText.getText().toString());
            flexis = new BigDecimal(flexiText.getText().toString());
            cash = new BigDecimal(cashText.getText().toString());
            Budget budget = new Budget(rid, meals, flexis, cash);
            intent.putExtra("budget", budget);

            startActivity(intent);
        }else {
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
