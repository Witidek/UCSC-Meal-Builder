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
import android.widget.Toast;

import org.w3c.dom.Text;

public class CartActivity extends ListActivity {

    Intent intent;
    Cart checkoutCart;
    ArrayAdapter<Item> adapter;
    TextView textView;
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        intent = getIntent();
        checkoutCart = intent.getExtras().getParcelable("cart");

        sharedPrefs = getSharedPreferences("balance", MODE_PRIVATE);
        editPrefs = sharedPrefs.edit();

        adapter = new ArrayAdapter<Item>(this,
                android.R.layout.simple_list_item_1,
                checkoutCart.items);
        setListAdapter(adapter);

        textView = (TextView) findViewById(R.id.totalText);
        ListView listView = getListView();

        textView.setText("Total: $" + String.format("%.2f", checkoutCart.getTotal()));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                checkoutCart.deleteItem(position);
                adapter.notifyDataSetChanged();
                textView.setText("Total: $" + String.format("%.2f", checkoutCart.getTotal()));
            }
        });
    }

    public void onClickClearCart(View view) {
        checkoutCart.clearCart();
        adapter.notifyDataSetChanged();
        textView.setText("Total: $" + String.format("%.2f", checkoutCart.getTotal()));
    }

    public void onClickCheckout(View view) {
        float balance = sharedPrefs.getFloat("flexis", 0);
        double total = checkoutCart.getTotal();
        float result = balance - (float)total;
        editPrefs.putFloat("flexis", result);
        editPrefs.commit();
        checkoutCart.clearCart();
        adapter.notifyDataSetChanged();
        textView.setText("Total: $" + String.format("%.2f", checkoutCart.getTotal()));
    }
 }
