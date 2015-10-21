package com.example.sam.ucsc_meal_builder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class BudgetActivity extends AppCompatActivity {

    /* TODO: fix id naming, comment code, add aesthetics */

    int meals = 0;
    double flexis = 0;

    TextView mealText;
    TextView flexiText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        // Grab TextViews
        mealText = (TextView) findViewById(R.id.mealBudgetText);
        flexiText = (TextView) findViewById(R.id.flexiBudgetText);

        // Save number values to local variable when EditText focus is lost (done editing)
        mealText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    meals = Integer.valueOf(mealText.getText().toString());
                }
            }
        });
        flexiText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    flexis = Double.valueOf(flexiText.getText().toString());
                }
            }
        });

        // Display initial values
        mealText.setText(String.format("%d", meals));
        flexiText.setText(String.format("%.2f", flexis));
    }

    public void incrementMeals (View view) {
        meals = Integer.valueOf(mealText.getText().toString());
        meals += 1;
        mealText.setText(String.format("%d", meals));
    }

    public void decrementMeals(View view) {
        meals = Integer.valueOf(mealText.getText().toString());
        meals -= 1;
        mealText.setText(String.format("%d", meals));
    }

    public void incrementFlexis (View view) {
        flexis = Double.valueOf(flexiText.getText().toString());
        flexis += 1;
        flexiText.setText(String.format("%.2f", flexis));
    }

    public void decrementFlexis(View view) {
        flexis = Double.valueOf(flexiText.getText().toString());
        flexis -= 1;
        flexiText.setText(String.format("%.2f", flexis));
    }

    public void onClickArrow(View view) {
        Intent intent = new Intent(BudgetActivity.this, RestaurantActivity.class);
        startActivity(intent);
    }
}
