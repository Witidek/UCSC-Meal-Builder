package com.example.sam.ucsc_meal_builder;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * This activity can be started from MainActivity, or returned to through android back button from
 * CartActivity when CartActivity was reached from this activity. It contains a ListView of the
 * names of user saved favorites.
 */
public class FavoriteActivity extends ListActivity {

    private DBHelper db = DBHelper.getInstance(this);
    private ArrayList<Favorite> favList = new ArrayList<>();
    private ListView listView;
    private SharedPreferences sharedPrefs;

    /**
     * Actionbar is loaded and setup, the database is queried for a list of favorites,
     * a basic ArrayAdapter is constructed to display names of favorites in a ListView, and an
     * on click listener for the ListView prompts the user to load the cart and start the
     * CartActivity or delete the favorite.
     */
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

        sharedPrefs = getSharedPreferences("balance", MODE_PRIVATE);

        // onClick brings up alert dialogue prompting for either deletion or loading of the favorite
        listView = getListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                // Load budget for this favorite
                final Budget budget = db.loadFavorite(adapter.getItem(position).getFavoriteID());
                int mealsBudget = budget.getMeals();
                BigDecimal flexisBudget = budget.getFlexis();

                // Load balance
                BigDecimal mealsBalance = new BigDecimal(sharedPrefs.getInt("meals", 0));
                BigDecimal flexisBalance = new BigDecimal(sharedPrefs.getString("flexis", "0"));

                // Only continue with prompt if balance is greater than the budget
                if (mealsBalance.compareTo(new BigDecimal(mealsBudget)) >= 0 && flexisBalance.compareTo(flexisBudget) >= 0) {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(FavoriteActivity.this);
                    alertDialog.setTitle(adapter.getItem(position).toString());
                    alertDialog.setMessage("Would you like to load or delete this favorite?");
                    alertDialog.setPositiveButton("Load", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Budget budget = db.loadFavorite(adapter.getItem(position).getFavoriteID());
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
                }else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(FavoriteActivity.this);
                    alertDialog.setTitle("Oh No....!");
                    alertDialog.setMessage("Your balance is too low to use this favorite!");
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Empty
                        }
                    });
                    alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                    alertDialog.show();
                }

            }
        });
    }
}
