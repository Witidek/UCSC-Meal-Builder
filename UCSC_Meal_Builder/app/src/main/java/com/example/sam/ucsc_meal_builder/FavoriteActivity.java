package com.example.sam.ucsc_meal_builder;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class FavoriteActivity extends ListActivity {

    private ArrayList<String> favList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        DBHelper db = new DBHelper(this);
        favList = db.getFavorites();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, favList);
        setListAdapter(adapter);
    }

    public void goToCart(View view) {
        Intent intent = new Intent(FavoriteActivity.this, CartActivity.class);
        startActivity(intent);
    }
}
