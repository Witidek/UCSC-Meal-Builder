package com.example.sam.ucsc_meal_builder;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class CartActivity extends ListActivity {

    ArrayAdapter<Item> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Intent intent = getIntent();
        final Cart checkoutCart = intent.getExtras().getParcelable("cart");

        adapter = new ArrayAdapter<Item>(this,
                android.R.layout.simple_list_item_1,
                checkoutCart.items);
        setListAdapter(adapter);

        //Toast.makeText(getApplicationContext(), Integer.toString(MenuActivity.myCart.getSize()), Toast.LENGTH_SHORT).show();

        final TextView textView = (TextView) findViewById(R.id.totalText);
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
}
