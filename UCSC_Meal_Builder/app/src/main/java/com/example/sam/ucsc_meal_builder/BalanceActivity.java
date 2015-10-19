package com.example.sam.ucsc_meal_builder;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class BalanceActivity extends AppCompatActivity {

    /* TODO: Tidy up code with a helper method, fix id naming, add save/cancel button, aesthetics */

    // Current meals and flexis value for this session (not auto-saved)
    int meals;
    float flexis;

    // Variables for the EditText fields
    TextView mealBalanceText;
    TextView flexiBalanceText;

    // SharedPreferences allows saving variables in AppData so data is not lost when the app
    // is closed. Each value is saved with a tag serving as its key.
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);

        sharedPrefs = getSharedPreferences("balance", MODE_PRIVATE);
        editPrefs = sharedPrefs.edit();

        // Create shared preferences for meals and flexis if no existing ones
        if(!sharedPrefs.contains("meals")) {
            editPrefs.putInt("meals", 0);
        }
        if(!sharedPrefs.contains("flexis")) {
            editPrefs.putFloat("flexis", 0);
        }
        // Save shared preferences
        editPrefs.commit();

        // Open shared preferences and fill EditText fields
        meals = sharedPrefs.getInt("meals", 0);
        flexis = sharedPrefs.getFloat("flexis", 0);

        // Select the EditText fields
        mealBalanceText = (TextView) findViewById(R.id.mealBalanceText);
        flexiBalanceText = (TextView) findViewById(R.id.flexiBalanceText);

        // Set fields to data read from SharedPreferences
        mealBalanceText.setText(Integer.toString(meals));
        flexiBalanceText.setText(String.format("%.2f", flexis));

    }

    // Save SharedPreference changes when android back button is pressed
    @Override
    public void onBackPressed() {
        meals = Integer.parseInt(mealBalanceText.getText().toString());
        flexis = Float.valueOf(flexiBalanceText.getText().toString());
        editPrefs.putInt("meals", meals);
        editPrefs.putFloat("flexis", flexis);
        editPrefs.commit();
        super.onBackPressed();
    }

    public void incrementMeals (View view) {
        meals += 1;
        mealBalanceText.setText(Integer.toString(meals));
        editPrefs.putInt("meals", meals);
        editPrefs.commit();
    }

    public void decrementMeals(View view) {
        meals -= 1;
        mealBalanceText.setText(Integer.toString(meals));
        editPrefs.putInt("meals", meals);
        editPrefs.commit();
    }

    public void incrementFlexis (View view) {
        flexis += 1;
        flexiBalanceText.setText(String.format("%.2f", flexis));
        editPrefs.putFloat("flexis", flexis);
        editPrefs.commit();
    }

    public void decrementFlexis(View view) {
        flexis -= 1;
        flexiBalanceText.setText(String.format("%.2f", flexis));
        editPrefs.putFloat("flexis", flexis);
        editPrefs.commit();
    }
}
