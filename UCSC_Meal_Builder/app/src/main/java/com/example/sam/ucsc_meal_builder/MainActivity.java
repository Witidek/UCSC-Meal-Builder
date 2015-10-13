package com.example.sam.ucsc_meal_builder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("UCSC Meal Builder - Home");
    }

    public void onClickBuildMeal (View view) {
        Intent intent = new Intent(MainActivity.this, BudgetActivity.class);
        startActivity(intent);
    }
}
