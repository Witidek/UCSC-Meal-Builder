package com.example.sam.ucsc_meal_builder;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;

public class MenuActivity extends ListActivity {

    private static final int mealValue = 8;

    private DBHelper db;
    private Budget budget;
    private ArrayList<Item> itemList;
    private MenuAdapter adapter;
    private ListView listView;
    private Cart cart;

    private Intent intent;
    private int rid;

    private TextView budgetText;
    private TextView subtotalText;

    private BigDecimal budgetTotal;
    private BigDecimal budgetRemaining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load layout
        setContentView(R.layout.activity_menu);

        // Set home button and title
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setCustomView(R.layout.ab_title);

        //Unpack budget from intent
        intent = getIntent();
        String previous = intent.getStringExtra("previous");
        budget = intent.getParcelableExtra("budget");

        rid = budget.getRID();
        int meals = budget.getMeals();
        BigDecimal flexis = budget.getFlexis();
        BigDecimal cash = budget.getCash();

        // Sum up total budget and set as budgetRemaining
        budgetTotal = cash.add(flexis.add(new BigDecimal(meals * mealValue)));
        budgetRemaining = budgetTotal;

        // Get cart from DB and set TextViews with budget and subtotal values
        db = DBHelper.getInstance(this);
        cart = db.getCart(rid);
        budgetText = (TextView) findViewById(R.id.budgetText);
        subtotalText = (TextView) findViewById(R.id.subtotalText);
        budgetText.setText(String.format("Budget: %.2f", budgetTotal));
        subtotalText.setText(String.format("Subtotal: %.2f", cart.getTotal()));

        // Grab menu from database
        itemList = db.getMenu(rid);
        TextView title = (TextView) findViewById(android.R.id.text1);
        title.setText(db.getRestaurantName(rid));

        // Build MenuAdapter
        adapter = new MenuAdapter(this, itemList);
        setListAdapter(adapter);
        adapter.setBudgetRemaining(budgetRemaining);

        // When an item is clicked, prompt for quantity to add to cart
        listView = getListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final Item selectedItem = adapter.getItem(position);
                if (selectedItem.getPrice().compareTo(budgetRemaining) > 0) {
                    // Prompt over budget alert, ask to change budget
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MenuActivity.this);
                    alertDialog.setTitle("Adding this would put you over budget!");
                    alertDialog.setMessage("Would you like to change your budget?");

                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Leaving Blank
                        }
                    });
                    alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                    alertDialog.show();
                }else {
                    // Creates AlertDialog to ask user for desired quantity
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MenuActivity.this);
                    alertDialog.setTitle("How much of this item would you like to add?");
                    alertDialog.setMessage("Enter the amount here: ");

                    final EditText editQuantity = new EditText(MenuActivity.this);
                    editQuantity.setInputType(InputType.TYPE_CLASS_NUMBER);
                    alertDialog.setView(editQuantity);

                    alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Parse user quantity input, calculate added price and check if over budget
                            String quantityString = editQuantity.getText().toString();
                            if (!quantityString.isEmpty()) {
                                int quantity = Integer.valueOf(quantityString);
                                if (selectedItem.getPrice().multiply(new BigDecimal(quantity)).compareTo(budgetRemaining) < 0) {
                                    // Not over budget, add requested quantity of the item
                                    for (int i = 0; i < quantity; i++) {
                                        cart.addItem(selectedItem);
                                        db.addToCart(selectedItem);

                                        // Recalculate budgetRemaining and pass to adapter as well
                                        budgetRemaining = budgetTotal.subtract(cart.getTotal());
                                        adapter.setBudgetRemaining(budgetRemaining);

                                        // Update subtotal TextView and toast item addition
                                        subtotalText.setText(String.format("Subtotal: %.2f", cart.getTotal()));
                                        String message = "Added " + adapter.getItem(position).getName() + " to cart";
                                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                                    }
                                }else {
                                    // Would go over budget, alert user
                                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MenuActivity.this);
                                    alertDialog.setTitle("You Are Over Your Budget!");
                                    alertDialog.setMessage("Please Enter a Different Amount");
                                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Empty
                                        }
                                    });
                                    alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                                    alertDialog.show();
                                }
                            }
                        }


                    });
                    alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Leaving Blank
                        }
                    });
                    alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                    alertDialog.show();

                    // Update MenuAdapter
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.cart_button:
                onCartPressed();
                return true;


            case R.id.sort_name:
                // Sort itemList alphabetically by name
                itemList = SortHelper.sortAlpha(itemList);
                adapter = new MenuAdapter(this, itemList);
                adapter.setBudgetRemaining(budgetRemaining);
                setListAdapter(adapter);
                return true;

            case R.id.sort_high:
                // Sort itemList by price
                itemList = SortHelper.sortHigh(itemList);
                adapter = new MenuAdapter(this, itemList);
                adapter.setBudgetRemaining(budgetRemaining);
                setListAdapter(adapter);
                return true;

            case R.id.sort_low:
                // Sort itemList by price
                itemList = SortHelper.sortLow(itemList);
                adapter = new MenuAdapter(this, itemList);
                adapter.setBudgetRemaining(budgetRemaining);
                setListAdapter(adapter);
                return true;

            case R.id.sort_course:
                // Sort itemList by course
                itemList = SortHelper.sortCourse(itemList);
                adapter = new MenuAdapter(this, itemList);
                adapter.setBudgetRemaining(budgetRemaining);
                setListAdapter(adapter);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        // Always call the superclass method first
        super.onResume();

        // Refresh local cart
        cart = db.getCart(rid);

        // Recalculate budgetRemaining and pass to adapter as well
        budgetRemaining = budgetTotal.subtract(cart.getTotal());
        adapter.setBudgetRemaining(budgetRemaining);

        // Update subtotal TextView
        subtotalText.setText(String.format("Subtotal: %.2f", cart.getTotal()));

        // Redraw ListView
        listView.invalidateViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.actionbar_menu, menu);
        return (super.onCreateOptionsMenu(menu));
    }

    public void onCartPressed(){
        Intent intent = new Intent(MenuActivity.this, CartActivity.class);
        intent.putExtra("previous", "MenuActivity");
        intent.putExtra("budget", budget);
        startActivity(intent);
    }

}

