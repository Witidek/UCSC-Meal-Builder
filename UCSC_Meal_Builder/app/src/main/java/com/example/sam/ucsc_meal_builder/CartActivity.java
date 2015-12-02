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

/**
 * This activity can be started from MenuActivity or FavoriteActivity. It contains a ListView made
 * with CartAdapter to display item quantities, names and prices. Display and interaction with this
 * activity changes depending on what the previous activity was.
 */
public class CartActivity extends ListActivity {

    private static final BigDecimal mealValue = new BigDecimal(8);

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

    /**
     * Actionbar is loaded and setup, the database is queried for a list of items in cart,
     * a CartAdapter is constructed to display quantities, names and prices of items in a ListView,
     * and an on click listener for the ListView deletes the item at whatever position the user
     * presses.
     *
     * If CartActivity was started from FavoriteActivity then actionbar menu is hidden, clear cart
     * is hidden, and user will not be able to delete items by pressing ListView rows.
     */
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

        rid = budget.getRestaurantID();
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

    /**
     * Enable the home button in top left of actionbar to return to MainActivity when pressed,
     * a favorite button which prompts the user to save the cart with a name, and a button to add
     * a miscellaneous item.
     */
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

    /**
     * Enables the actionbar menu and loads the appropriate layout
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.actionbar_cart, menu);
        return (super.onCreateOptionsMenu(menu));
    }

    /**
     * Hides actionbar menu buttons if previous activity was FavoriteActivity
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (previous.equals("FavoriteActivity")) {
            menu.findItem(R.id.favorite_button).setVisible(false);
            menu.findItem(R.id.add_misc_button).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Prompts user to save the current cart for the current restaurant with a user inputted name.
     * The cart with the favorite name is then saved to Favorite table the and budget information to
     * Budget table. Favorites can have duplicate names and the exact same carts.
     */
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
                if (!favName.isEmpty() && !cart.isEmpty()) {
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

    /**
     * Prompts the user to enter a numerical decimal value for an extra cost. This does not check
     * against the budget and this item will not be saved into any favorite. Trying to add a second
     * miscellaneous item when one already exists will simply overwrite the existing item's price.
     */
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

    /**
     * Prompts user for confirmation and upon confirmation deletes all rows in Cart table for this
     * restaurant as well as clearing the local cart object and ListView.
     */
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

    /**
     * Prompts for the user for confirmation and upon confirmation will try and optimize meal and
     * flexis spending usage through a priority pattern where meals are used first, then flexis,
     * and cash last. The cart is also cleared at the end, and the user will return to MainActivity.
     */
    public void onClickCheckout(View view) {

        // Subtract total from meals and flexis balance
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Checkout Cart");
        String confirm = String.format("Total is : $%.2f\nProceed?", cart.getTotal());
        alertDialog.setMessage(confirm);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Load meals and flexis balances
                BigDecimal mealsBalance = new BigDecimal(sharedPrefs.getInt("meals", 0));
                BigDecimal flexisBalance = new BigDecimal(sharedPrefs.getString("flexis", "0"));

                // Get cart total
                BigDecimal totalRemaining = cart.getTotal();

                // Keep track of how much used from each currency type
                BigDecimal mealsUsed = new BigDecimal(0);
                BigDecimal flexisUsed = new BigDecimal(0);

                // Divide total by mealValue (8) and floor it
                mealsUsed = totalRemaining.divideToIntegralValue(mealValue);

                // Use min of mealsBudget and mealsUsed calculated in previous line
                mealsUsed = mealsUsed.min(new BigDecimal(meals));

                // Subtract mealsUsed * mealValue from totalRemaining
                totalRemaining = totalRemaining.subtract(mealsUsed.multiply(mealValue));

                // Check edge case for 1 missing meal
                if (totalRemaining.compareTo(flexis) > 0) {
                    mealsUsed = mealsUsed.add(new BigDecimal(1));
                    totalRemaining = totalRemaining.subtract(mealValue);
                }

                // Only continue calculating flexis if there is totalRemaining left
                if (totalRemaining.compareTo(new BigDecimal(0)) > 0) {
                    // Calculate flexis used next
                    if (totalRemaining.compareTo(flexis) > 0) {
                        flexisUsed = flexis;
                    } else {
                        flexisUsed = totalRemaining;
                    }

                    // Check cash just in case
                    if (totalRemaining.compareTo(cash) > 0) {
                        // Should never reach here!
                        Toast.makeText(CartActivity.this, "Bad calculation!", Toast.LENGTH_SHORT).show();
                    }
                }

                // Subtract stuff used from balance, save changes
                BigDecimal mealsResult = mealsBalance.subtract(mealsUsed);
                BigDecimal flexisResult = flexisBalance.subtract(flexisUsed);
                editPrefs.putInt("meals", mealsResult.intValue());
                editPrefs.putString("flexis", flexisResult.toString());
                editPrefs.apply();

                // Clear cart in DB
                db.clearCart(rid);
                String message = String.format("Done! Spent %d meals and %.2f flexis!", mealsUsed.intValue(), flexisUsed);
                Toast.makeText(CartActivity.this, message, Toast.LENGTH_SHORT).show();

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
