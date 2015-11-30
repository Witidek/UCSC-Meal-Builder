package com.example.sam.ucsc_meal_builder;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
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

public class CartActivity extends ListActivity {

    private Intent intent;
    private Budget budget;
    private int rid;
    private String favBuffer;
    private BigDecimal budgetTotal;
    private int meals;
    private BigDecimal cash;
    private BigDecimal flexis;
    private DBHelper db;
    private CartAdapter adapter;


    private Cart cart;
    private TextView textView;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor editPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setCustomView(R.layout.ab_title);
        TextView title = (TextView) findViewById(android.R.id.text1);
        title.setText("Cart");

        // Get restaurant_id from intent
        intent = getIntent();
        String previous = intent.getStringExtra("previous");
        if (previous.equals("FavoriteActivity")) {
            View button = findViewById(R.id.clearCartButton);
            button.setVisibility(View.GONE);
        }

        budget = intent.getParcelableExtra("budget");

        rid = budget.getRID();
        meals = budget.getMeals();
        flexis = budget.getFlexis();
        cash = budget.getCash();

        // Load SharedPreferences to get balance
        sharedPrefs = getSharedPreferences("balance", MODE_PRIVATE);
        editPrefs = sharedPrefs.edit();

        // Grab cart items for specified restaurant from database
        db = DBHelper.getInstance(this);
        cart = db.getCart(rid);

        // Create ListView adapter to display items in cart
        adapter = new CartAdapter(this, cart.getItemList());
        setListAdapter(adapter);

        // TextView for total
        textView = (TextView) findViewById(R.id.totalText);
        textView.setText(String.format("Total: $%.2f", cart.getTotal()));

        // onClick event for ListView items
        if (previous.equals("MenuActivity")) {
            ListView listView = getListView();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // PROMPT WARNING FOR DELETE FROM CART--------------------------
                    db.deleteItem(adapter.getItem(position));
                    cart.deleteItem(position);
                    adapter.notifyDataSetChanged();
                    textView.setText(String.format("Total: $%.2f", cart.getTotal()));
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.favorite_button:
                // prompt to name favorite
                // Pop the alert dialog on click
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(CartActivity.this);
                alertDialog.setTitle("Adding Favorite Item");
                // Gets the input from the user and stores here
                final EditText input = new EditText(this);
                // Gets a number value in decimal form
                // Note: Can go over two decimal places need to look into that
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                alertDialog.setView(input);
                alertDialog.setMessage("What would you like to name the favorite?");
                alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        favBuffer = input.getText().toString();
                        //newItem = new BigDecimal(misBuffer);
                        if(!favBuffer.isEmpty()) {
                            Toast.makeText(getApplicationContext(), favBuffer, Toast.LENGTH_SHORT).show();
                            //Actually put it in favorites:
                            db.addToFavorites(cart.getItemList(), favBuffer, budget);
                        }
                    }
                });
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });
                alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                alertDialog.show();
                // save restaurant cart to Favorite table in db
                Toast.makeText(CartActivity.this, "favorited", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.add_misc_button:
                onClickNewItem();
                return true;

            default:
                return super.onOptionsItemSelected(item);


        }

    }

    // Adding Miscellaneous Items to the total
    public void onClickNewItem (){
        // Pop the alert dialog on click
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CartActivity.this);
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
                String miscBuffer = input.getText().toString();
                BigDecimal newItem = new BigDecimal(miscBuffer);
                cart.addMisc(rid, newItem);
                adapter.notifyDataSetChanged();
                textView.setText(String.format("Total: $%.2f", cart.getTotal()));

            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.show();

    }

    public void onClickClearCart(View view) {
        // PROMPT WARNING FOR CLEAR CART------------------------------
        // Clear local cart and database cart

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Clear Cart");
        alertDialog.setMessage("Are you sure?");
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                cart.clearCart();
                db.clearCart(rid);

                // Update ListView and total text
                adapter.notifyDataSetChanged();
                textView.setText(String.format("Total: $%.2f", cart.getTotal()));
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
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.actionbar_cart, menu);
        return (super.onCreateOptionsMenu(menu));
    }

    public void onClickCheckout(View view) {
        // PROMPT CONFIRMATION FOR CHECKOUT----------------------------
        // Subtract total from meals and flexis balance
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Checkout Cart");
        String confirm = String.format("Total is : $%.2f\nProceed?", cart.getTotal());
        alertDialog.setMessage(confirm);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                BigDecimal totalMeals = new BigDecimal(sharedPrefs.getInt("meals", 0));
                BigDecimal totalFlexies = new BigDecimal(sharedPrefs.getString("flexis", "0"));
                BigDecimal valueMeal = new BigDecimal(8);
                BigDecimal total = cart.getTotal();
                BigDecimal amountMeals = total.divideToIntegralValue(valueMeal);
                BigDecimal compareMeal = amountMeals.min(new BigDecimal(meals));
                BigDecimal remainAmount = total.subtract(compareMeal.multiply(valueMeal));
                BigDecimal resultflexies;
                //Toast.makeText(getApplicationContext(), compareMeal.toString(), Toast.LENGTH_SHORT).show();
                BigDecimal resultMeals = totalMeals.subtract(compareMeal);
                if(remainAmount.compareTo(flexis) >= 0) {
                    resultflexies = totalFlexies.subtract(flexis);
                }else{
                    resultflexies = totalFlexies.subtract(remainAmount);
                }

                editPrefs.putInt("meals", resultMeals.intValue());
                // If only using cash or meals don't change flexies or if cash covers it
                if(flexis.compareTo(new BigDecimal(0)) > 0) {
                    editPrefs.putString("flexis", resultflexies.toString());
                }
                editPrefs.commit();

                // Clear local cart and database cart
                cart.clearCart();
                db.clearCart(rid);

                // Update ListView and total text
                adapter.notifyDataSetChanged();
                textView.setText(String.format("Total: $%.2f", cart.getTotal()));

            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Leaving Blank
            }
        });
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.show();


    }
}
