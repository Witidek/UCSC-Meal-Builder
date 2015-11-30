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

        /*
            This should really be in its own thread, but i focused on getting it working first.
         */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setCustomView(R.layout.ab_title);
        TextView title = (TextView) findViewById(android.R.id.text1);
        title.setText("UCSC Meal Builder");
        //load data from url database.
        //GetJSONForDB.execute()
        new DBHelper(getApplicationContext()).getJSONFromURL("http://ucscmealbuilder.com/manage/api.php?type=r", 0);
        new DBHelper(getApplicationContext()).getJSONFromURL("http://ucscmealbuilder.com/manage/api.php?type=i", 1);
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
