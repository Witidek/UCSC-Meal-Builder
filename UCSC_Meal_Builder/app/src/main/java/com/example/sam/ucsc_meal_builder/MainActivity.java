package com.example.sam.ucsc_meal_builder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load layout
        setContentView(R.layout.activity_main);

        /*
            This should really be in its own thread, but i focused on getting it working first.
         */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);



        // Manually set title so it is not highlighted with the home button
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setCustomView(R.layout.ab_title);
        TextView title = (TextView) findViewById(android.R.id.text1);
        title.setText("UCSC Meal Builder");

        // Initialize DBHelper singleton and clear all carts
        DBHelper db = DBHelper.getInstance(this);
        for (int rid = 0; rid < 10; rid++) {
            db.clearCart(rid);
        }

        //load data from url database.
        //GetJSONForDB.execute()
        //db.getJSONFromURL("http://ucscmealbuilder.com/manage/api.php?type=r", 0);
        //db.getJSONFromURL("http://ucscmealbuilder.com/manage/api.php?type=i", 1);
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
