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

    private DBHelper db;
    private Budget budget;
    private ArrayList<Item> itemList;
    private ListAdapter adapter;
    private ListView listView;
    private Cart cart;

    private Intent intent;
    private int rid;
    private String flexisString;
    private String cashString;
    private String misBuffer;

    private int meals;
    private BigDecimal flexis;
    private BigDecimal cash;

    private String amount;
    private int numMeals;
    private int numAmount;

    private BigDecimal numFlexis;
    private BigDecimal numCash;
    private BigDecimal newItem;



    private TextView budgetText;
    private TextView subtotalText;

    private BigDecimal budgetTotal;
    private BigDecimal budgetRemaining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setCustomView(R.layout.ab_title);

        db = new DBHelper(this);

        //Unpack extras
        intent = getIntent();
        String previous = intent.getStringExtra("previous");
        budget = intent.getParcelableExtra("budget");

        rid = budget.getRID();
        meals = budget.getMeals();
        flexis = budget.getFlexis();
        cash = budget.getCash();

        //Here comes the money
        budgetTotal = cash.add(flexis.add(new BigDecimal(meals * 8)));
        Toast.makeText(getApplicationContext(), budgetTotal.toString(), Toast.LENGTH_SHORT).show();
        budgetRemaining = budgetTotal;


        //set budgetText and subtotalText with approp. values
        cart = db.getCart(rid);
        budgetText = (TextView) findViewById(R.id.budgetText);
        subtotalText = (TextView) findViewById(R.id.subtotalText);
        budgetText.setText(String.format("Budget: %s", budgetTotal.toString()));
        subtotalText.setText(String.format("Subtotal: %s", cart.getTotal().toString()));

        // Grab menu from database
        itemList = db.getMenu(rid);
        TextView title = (TextView) findViewById(android.R.id.text1);
        title.setText(db.getRestaurantName(rid));


        // Build ListAdapter
        adapter = new ListAdapter(this, itemList);
        setListAdapter(adapter);
        adapter.setBudgetRemaining(budgetRemaining);

        listView = getListView();

        // When an item is clicked, the corresponding item is added to the cart.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final Item selectedItem = adapter.getItem(position);
                numAmount = 0;
                if (selectedItem.getPrice().compareTo(budgetRemaining) > 0) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MenuActivity.this);
                    alertDialog.setTitle("You Are Over Your Budget!");
                    alertDialog.setMessage("Would you like to change your budget?");
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Leaving Blank
                        }
                    });
                    alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                    alertDialog.show();
                }else {
                    // Creates AlertDialog to ask user how much of an item they want
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MenuActivity.this);
                    final EditText input = new EditText(MenuActivity.this);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    alertDialog.setView(input);
                    alertDialog.setTitle("How much of this item would you like to add?");
                    alertDialog.setMessage("Enter the amount here: ");
                    alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            amount = input.getText().toString();
                            if (!amount.isEmpty()) {
                                numAmount = Integer.valueOf(amount);
                                if (selectedItem.getPrice().multiply(new BigDecimal(numAmount)).compareTo(budgetRemaining) < 0) {
                                    for (int i = 0; i < numAmount; i++) {

                                        cart.addItem(selectedItem);
                                        db.addToCart(selectedItem);
                                        // Recalculate budgetRemaining and pass to adapter as well
                                        budgetRemaining = budgetTotal.subtract(cart.getTotal());
                                        adapter.setBudgetRemaining(budgetRemaining);

                                        // Update subtotal TextView and toast item addition
                                        subtotalText.setText(String.format("Subtotal: %s", cart.getTotal().toString()));
                                        String message = "Added " + adapter.getItem(position).getName() + " to cart";
                                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                                    }
                                } else {
                                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MenuActivity.this);
                                    alertDialog.setTitle("You Are Over Your Budget!");
                                    alertDialog.setMessage("Please Enter a Different Amount");
                                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

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



                    // Update ListAdapter
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }
    // Adding Misscellaneous Items to the total
    public void onClickNewItem (View view){
        // Pop the alert dialog on click
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MenuActivity.this);
        alertDialog.setTitle("Adding Miscellaneous Item");
        // Gets the input from the user and stores here
        final EditText input = new EditText(this);
        // Gets a number value in decimal form
        // Note: Can go over two decimal places need to look into that
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        alertDialog.setView(input);
        alertDialog.setMessage("How much is the item you would like to add?");
        alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                misBuffer = input.getText().toString();
                newItem = new BigDecimal(misBuffer);
                Toast.makeText(getApplicationContext(), newItem.toString(), Toast.LENGTH_SHORT).show();

            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.show();

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
                adapter = new ListAdapter(this, itemList);
                adapter.setBudgetRemaining(budgetRemaining);
                setListAdapter(adapter);
                return true;

            case R.id.sort_high:
                // Sort itemList by price
                itemList = SortHelper.sortHigh(itemList);
                adapter = new ListAdapter(this, itemList);
                adapter.setBudgetRemaining(budgetRemaining);
                setListAdapter(adapter);
                return true;

            case R.id.sort_low:
                // Sort itemList by price
                itemList = SortHelper.sortLow(itemList);
                adapter = new ListAdapter(this, itemList);
                adapter.setBudgetRemaining(budgetRemaining);
                setListAdapter(adapter);
                return true;

            case R.id.sort_course:
                // Sort itemList by course
                itemList = SortHelper.sortCourse(itemList);
                adapter = new ListAdapter(this, itemList);
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
        super.onResume();  // Always call the superclass method first

        // Refresh local cart
        cart = db.getCart(rid);

        // Recalculate budgetRemaining and pass to adapter as well
        budgetRemaining = budgetTotal.subtract(cart.getTotal());
        adapter.setBudgetRemaining(budgetRemaining);

        // Update subtotal TextView
        subtotalText.setText(String.format("Subtotal: %s", cart.getTotal().toString()));

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

