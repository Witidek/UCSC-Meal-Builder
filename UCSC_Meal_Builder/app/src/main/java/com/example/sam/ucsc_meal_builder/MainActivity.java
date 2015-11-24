package com.example.sam.ucsc_meal_builder;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void onClickBuildMeal (View view) {
        Intent intent = new Intent(MainActivity.this, RestaurantActivity.class);
        intent.putExtra("previous", "MainActivity");
        startActivity(intent);
    }

    public void onClickBalance(View view) {
        Intent intent = new Intent(MainActivity.this, BalanceActivity.class);
        intent.putExtra("previous", "MainActivity");
        startActivity(intent);
    }

    public void onClickFavorites(View view) {
        Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
        intent.putExtra("previous", "MainActivity");
        startActivity(intent);
    }
}
