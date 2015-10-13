package com.example.sam.ucsc_meal_builder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class BudgetActivity extends AppCompatActivity {

    int meals = 0;
    double flexidollars = 0;

    TextView mealText;
    TextView flexiText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);
        mealText = (TextView) findViewById(R.id.mealText);
        flexiText = (TextView) findViewById(R.id.flexiText);

        mealText.setText(Integer.toString(meals));
        flexiText.setText(Double.toString(flexidollars));
    }

    public void incrementMeals (View view) {
        meals += 1;
        mealText.setText(Integer.toString(meals));
    }

    public void decrementMeals(View view) {
        meals -= 1;
        mealText.setText(Integer.toString(meals));
    }

    public void incrementFlexis (View view) {
        flexidollars += 1;
        flexiText.setText(Double.toString(flexidollars));
    }

    public void decrementFlexis(View view) {
        flexidollars -= 1;
        flexiText.setText(Double.toString(flexidollars));
    }


}
