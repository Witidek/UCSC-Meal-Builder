package com.example.sam.ucsc_meal_builder;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class FavoriteActivity extends ListActivity {

    private DBHelper db = DBHelper.getInstance(this);
    private ArrayList<Favorite> favList = new ArrayList<>();
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load layout
        setContentView(R.layout.activity_favorite);

        // Set home button and title
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setCustomView(R.layout.ab_title);
        TextView title = (TextView) findViewById(android.R.id.text1);
        title.setText("Favorite");

        // Grab list of favorites and construct ListView adapter
        favList = db.getFavorites();
        final ArrayAdapter<Favorite> adapter = new ArrayAdapter<Favorite>(this, android.R.layout.simple_list_item_1, favList);
        setListAdapter(adapter);

        // onClick brings up alert dialogue prompting for either deletion or loading of the favorite
        listView = getListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(FavoriteActivity.this);
                alertDialog.setTitle(adapter.getItem(position).toString());
                alertDialog.setMessage("Would you like to load or delete this favorite?");
                alertDialog.setPositiveButton("Load", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Budget budget = db.loadFavorite(adapter.getItem(position).getFID());
                        Intent intent = new Intent(FavoriteActivity.this, CartActivity.class);
                        intent.putExtra("previous", "FavoriteActivity");
                        intent.putExtra("budget", budget);
                        startActivity(intent);
                    }
                });
                alertDialog.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteFavorite(adapter.getItem(position));
                        adapter.remove(adapter.getItem(position));
                        adapter.notifyDataSetChanged();
                    }
                });
                alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                alertDialog.show();

            }
        });
    }
}
