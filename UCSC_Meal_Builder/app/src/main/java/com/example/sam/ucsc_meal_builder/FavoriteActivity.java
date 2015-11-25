package com.example.sam.ucsc_meal_builder;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class FavoriteActivity extends ListActivity {

    private ArrayList<String> favList = new ArrayList<>();
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setCustomView(R.layout.ab_title);
        TextView title = (TextView) findViewById(android.R.id.text1);
        title.setText("Favorite");

        final DBHelper db = new DBHelper(this);
        favList = db.getFavorites();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, favList);
        setListAdapter(adapter);

        listView = getListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Budget budget = db.loadFavorite(position);
                Intent intent = new Intent(FavoriteActivity.this, CartActivity.class);
                intent.putExtra("previous", "FavoriteActivity");
                intent.putExtra("budget", budget);
                startActivity(intent);
            }
        });
    }

    public void goToCart(View view) {
        Intent intent = new Intent(FavoriteActivity.this, CartActivity.class);
        startActivity(intent);
    }
}
