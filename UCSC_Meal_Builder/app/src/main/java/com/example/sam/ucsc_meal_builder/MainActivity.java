package com.example.sam.ucsc_meal_builder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.TextView;

/**
 * Home activity for the app that serves as the starting point for the user as well as being
 * the first screen shown upon starting the app. The DBHelper singleton is first instantiated on
 * startup of app, and carts for all restaurants are cleared in the local database. The home button
 * in the top left of the actionbar which displays for every activity but this one will lead the
 * user back to this activity. Displays three large buttons leading to other activities.
 */
public class MainActivity extends Activity {

    /**
     * Actionbar is loaded and setup, DBHelper singleton is instantiated, and carts for all
     * restaurants are cleared.
     */
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

    /**
     * Executed when user presses "Build a Meal" button and simply sends an intent to start the
     * RestaurantActivity.
     *
     * @param view  View of the button pressed
     */
    public void onClickBuildMeal (View view) {
        Intent intent = new Intent(MainActivity.this, RestaurantActivity.class);
        intent.putExtra("previous", "MainActivity");
        startActivity(intent);
    }

    /**
     * Executed when user presses "Balance" button and simply sends an intent to start the
     * BalanceActivity.
     *
     * @param view  View of the button pressed
     */
    public void onClickBalance(View view) {
        Intent intent = new Intent(MainActivity.this, BalanceActivity.class);
        intent.putExtra("previous", "MainActivity");
        startActivity(intent);
    }

    /**
     * Executed when user presses "Favorites" button and simply sends an intent to start the
     * FavoriteActivity.
     *
     * @param view  View of the button pressed
     */
    public void onClickFavorites(View view) {
        Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
        intent.putExtra("previous", "MainActivity");
        startActivity(intent);
    }
}
