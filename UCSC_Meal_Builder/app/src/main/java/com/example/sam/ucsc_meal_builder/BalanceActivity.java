package com.example.sam.ucsc_meal_builder;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;

public class BalanceActivity extends Activity {

    // Current meals and flexis value for this session (not auto-saved)
    private int meals;
    private BigDecimal flexis;

    // Variables for the EditText fields
    private TextView mealBalanceText;
    private TextView flexiBalanceText;

    // SharedPreferences allows saving variables in AppData so data is not lost when the app
    // is closed. Each value is saved with a tag serving as its key.
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor editPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load layout
        setContentView(R.layout.activity_balance);

        // Set home button and title
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setCustomView(R.layout.ab_title);
        TextView title = (TextView) findViewById(android.R.id.text1);
        title.setText("Balance");

        // Balance is a preference that contains values 'meals' and 'flexis'
        sharedPrefs = getSharedPreferences("balance", MODE_PRIVATE);
        editPrefs = sharedPrefs.edit();

        // Create shared preferences for meals and flexis if no existing ones
        if(!sharedPrefs.contains("meals")) {
            editPrefs.putInt("meals", 0);
        }
        if(!sharedPrefs.contains("flexis")) {
            editPrefs.putString("flexis", "0");
        }
        // Save shared preferences
        editPrefs.apply();

        // Open shared preferences and fill EditText fields
        meals = sharedPrefs.getInt("meals", 0);
        flexis = new BigDecimal(sharedPrefs.getString("flexis", "0"));

        // Select the EditText fields
        mealBalanceText = (TextView) findViewById(R.id.mealBalanceText);
        flexiBalanceText = (TextView) findViewById(R.id.flexiBalanceText);

        // Save number values to local variable when EditText focus is lost (done editing)
        mealBalanceText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    try {
                        meals = Integer.parseInt(mealBalanceText.getText().toString());
                    }catch (NumberFormatException e) {
                        // Empty..
                    }
                }

            }
        });
        flexiBalanceText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    flexis = new BigDecimal(flexiBalanceText.getText().toString());
                }
            }
        });

        // Set fields to data read from SharedPreferences
        mealBalanceText.setText(String.format("%d", meals));
        flexiBalanceText.setText(String.format("%.2f", flexis));

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

    // Save SharedPreference changes when android back button is pressed
    @Override
    public void onBackPressed() {
        meals = Integer.valueOf(mealBalanceText.getText().toString());
        flexis = new BigDecimal(flexiBalanceText.getText().toString());
        if (meals < 0 || flexis.compareTo(new BigDecimal(0))==-1) {
            Toast.makeText(getApplicationContext(), "Cannot use negative.", Toast.LENGTH_SHORT).show();
        }
        else {
            editPrefs.putInt("meals", meals);
            editPrefs.putString("flexis", flexis.toString());
            editPrefs.commit();
            super.onBackPressed();
        }
    }

    public void incrementMeals (View view) {
        meals = Integer.valueOf(mealBalanceText.getText().toString());
        meals += 1;
        mealBalanceText.setText(String.format("%d", meals));
        editPrefs.putInt("meals", meals);
        editPrefs.commit();
    }

    public void decrementMeals(View view) {
        meals = Integer.valueOf(mealBalanceText.getText().toString());
        if (meals > 0) meals -= 1;
        mealBalanceText.setText(String.format("%d", meals));
        editPrefs.putInt("meals", meals);
        editPrefs.commit();
    }

    public void incrementFlexis (View view) {
        flexis = new BigDecimal(flexiBalanceText.getText().toString());
        flexis = flexis.add(new BigDecimal(1));
        flexiBalanceText.setText(String.format("%.2f", flexis));
        editPrefs.putString("flexis", flexis.toString());
        editPrefs.commit();
    }

    public void decrementFlexis(View view) {
        flexis = new BigDecimal(flexiBalanceText.getText().toString());
        if (flexis.compareTo(new BigDecimal(1)) != -1) {
            flexis = flexis.subtract(new BigDecimal(1));
        }
        flexiBalanceText.setText(String.format("%.2f", flexis));
        editPrefs.putString("flexis", flexis.toString());
        editPrefs.commit();
    }
}
