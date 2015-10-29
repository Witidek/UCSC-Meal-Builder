package com.example.sam.ucsc_meal_builder;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.math.BigDecimal;

public class CartActivity extends ListActivity {

    private Intent intent;
    private int rid;
    private DBHelper db;
    private ArrayAdapter<Item> adapter;
    private Cart cart;
    private TextView textView;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor editPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Get restaurant_id from intent
        intent = getIntent();
        rid = intent.getIntExtra("rid", 0);

        // Load SharedPreferences to get balance
        sharedPrefs = getSharedPreferences("balance", MODE_PRIVATE);
        editPrefs = sharedPrefs.edit();

        // Grab cart items for specified restaurant from database
        db = new DBHelper(this);
        cart = db.getCart(rid);

        // Create ListView adapter to display items in cart
        adapter = new ArrayAdapter<Item>(this,
                android.R.layout.simple_list_item_1,
                cart.items);
        setListAdapter(adapter);

        // TextView for total
        textView = (TextView) findViewById(R.id.totalText);
        textView.setText(String.format("Total: $%s", cart.getTotal().toString()));

        // onClick event for ListView items
        ListView listView = getListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // PROMPT WARNING FOR DELETE FROM CART--------------------------
                cart.deleteItem(position);
                adapter.notifyDataSetChanged();
                textView.setText(String.format("Total: $%s", cart.getTotal().toString()));
            }
        });
    }

    public void onClickClearCart(View view) {
        // PROMPT WARNING FOR CLEAR CART------------------------------
        // Clear local cart and database cart
        cart.clearCart();
        db.clearCart(rid);

        // Update ListView and total text
        adapter.notifyDataSetChanged();
        textView.setText(String.format("Total: $%s", cart.getTotal().toString()));
    }

    public void onClickCheckout(View view) {
        // PROMPT CONFIRMATION FOR CHECKOUT----------------------------
        // Subtract total from flexis balance
        BigDecimal balance = new BigDecimal(sharedPrefs.getString("flexis", "0"));
        BigDecimal total = cart.getTotal();
        BigDecimal result = balance.subtract(total);
        editPrefs.putString("flexis", result.toString());
        editPrefs.commit();

        // Clear local cart and database cart
        cart.clearCart();
        db.clearCart(rid);

        // Update ListView and total text
        adapter.notifyDataSetChanged();
        textView.setText(String.format("Total: $%s", cart.getTotal().toString()));
    }
}
