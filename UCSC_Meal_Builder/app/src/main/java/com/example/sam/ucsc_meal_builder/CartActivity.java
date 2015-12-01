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

    private DBHelper db;
    private CartAdapter adapter;
    private Budget budget;
    private Cart cart;

    private Intent intent;
    private String previous;
    private int rid;

    private int meals;
    private BigDecimal flexis;
    private BigDecimal cash;

    private TextView subtotalText;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor editPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load layout
        setContentView(R.layout.activity_cart);

        // Set home button and title
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setCustomView(R.layout.ab_title);
        TextView title = (TextView) findViewById(android.R.id.text1);
        title.setText("Cart");

        // Unpack budget from intent
        intent = getIntent();
        previous = intent.getStringExtra("previous");
        budget = intent.getParcelableExtra("budget");

        rid = budget.getRID();
        meals = budget.getMeals();
        flexis = budget.getFlexis();
        cash = budget.getCash();

        // If CartActivity was reached from FavoriteActivity, disable clear cart button
        if (previous.equals("FavoriteActivity")) {
            View clearCartButton = findViewById(R.id.clearCartButton);
            clearCartButton.setVisibility(View.GONE);
        }

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
        subtotalText = (TextView) findViewById(R.id.totalText);
        subtotalText.setText(String.format("Total: $%.2f", cart.getTotal()));

        // onClick event for ListView items only if previous was MenuActivity
        if (previous.equals("MenuActivity")) {
            ListView listView = getListView();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // PROMPT WARNING FOR DELETE FROM CART--------------------------
                    db.deleteItem(adapter.getItem(position));
                    cart.deleteItem(position);
                    adapter.notifyDataSetChanged();
                    subtotalText.setText(String.format("Total: $%.2f", cart.getTotal()));
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
                onClickFavorite();
                return true;

            case R.id.add_misc_button:
                onClickNewItem();
                return true;

            default:
                return super.onOptionsItemSelected(item);


        }

    }

    public void onClickFavorite() {
        // Prompt to name favorite
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CartActivity.this);
        alertDialog.setTitle("Adding Favorite Item");

        // Gets the input from the user and stores here
        final EditText editFavName = new EditText(this);
        editFavName.setInputType(InputType.TYPE_CLASS_TEXT);
        alertDialog.setView(editFavName);
        alertDialog.setMessage("What would you like to name the favorite?");
        alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String favName = editFavName.getText().toString();
                if (!favName.isEmpty() && cart.getSize() > 0) {
                    db.addToFavorites(cart.getItemList(), favName, budget);
                    String message = String.format("Saved %s to favorites!", favName);
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Empty
            }
        });
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.show();
    }

    // Adding Miscellaneous Items to the total
    public void onClickNewItem (){
        // Pop the alert dialog on click
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CartActivity.this);
        alertDialog.setTitle("Adding Miscellaneous Item");
        alertDialog.setMessage("How much is the item you would like to add?");

        // Gets the input from the user and stores here
        final EditText editMiscPrice = new EditText(this);
        editMiscPrice.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        alertDialog.setView(editMiscPrice);

        alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String miscPriceString = editMiscPrice.getText().toString();
                BigDecimal miscPrice = new BigDecimal(miscPriceString);
                cart.addMisc(rid, miscPrice);
                adapter.notifyDataSetChanged();
                subtotalText.setText(String.format("Total: $%.2f", cart.getTotal()));

            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Empty
            }
        });
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.show();

    }

    public void onClickClearCart(View view) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Clear Cart");
        alertDialog.setMessage("Are you sure?");
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                cart.clearCart();
                db.clearCart(rid);

                // Update ListView and total text
                adapter.notifyDataSetChanged();
                subtotalText.setText(String.format("Total: $%.2f", cart.getTotal()));
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Empty
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Disable action bar buttons if previous activity was FavoriteActivity
        if (previous.equals("FavoriteActivity")) {
            menu.findItem(R.id.favorite_button).setVisible(false);
            menu.findItem(R.id.add_misc_button).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
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
                BigDecimal resultMeals = totalMeals.subtract(compareMeal);
                if(remainAmount.compareTo(flexis) >= 0) {
                    resultflexies = totalFlexies.subtract(flexis);
                } else {
                    resultflexies = totalFlexies.subtract(remainAmount);
                }

                editPrefs.putInt("meals", resultMeals.intValue());
                // If only using cash or meals don't change flexies or if cash covers it
                if(flexis.compareTo(new BigDecimal(0)) > 0) {
                    editPrefs.putString("flexis", resultflexies.toString());
                }
                editPrefs.commit();

                // Clear database cart
                db.clearCart(rid);

                // Return to home
                startActivity(new Intent(CartActivity.this, MainActivity.class));

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
