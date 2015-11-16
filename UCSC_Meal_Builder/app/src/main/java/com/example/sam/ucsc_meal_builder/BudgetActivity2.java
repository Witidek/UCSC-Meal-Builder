package com.example.sam.ucsc_meal_builder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;

public class BudgetActivity2 extends Activity {

    BigDecimal cash = new BigDecimal(0);
    BigDecimal flexis = new BigDecimal(0);

    TextView cashText;
    TextView flexiText;

    //The rid needs to be carried from the previous
    //(Restaurant)Activity to the next (Menu)Activity
    private Intent intent;
    private int rid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget2);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        //Receive rid
        intent = getIntent();
        rid = intent.getIntExtra("rid", 0);

        Toast.makeText(getApplicationContext(), "#2!!!!", Toast.LENGTH_SHORT).show();

        // Grab TextViews
        cashText = (TextView) findViewById(R.id.cashBudgetText);
        flexiText = (TextView) findViewById(R.id.flexiBudgetText);

        // Save number values to local variable when EditText focus is lost (done editing)
        cashText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    cash = new BigDecimal(cashText.getText().toString());
                }
            }
        });
        flexiText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    flexis = new BigDecimal(flexiText.getText().toString());
                }
            }
        });

        // Display initial values
        cashText.setText(String.format("%.2f", cash));
        flexiText.setText(String.format("%.2f", flexis));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void incrementCash (View view) {
        cash = new BigDecimal(cashText.getText().toString());
        cash = cash.add(new BigDecimal(1));
        cashText.setText(String.format("%.2f", cash));
    }

    public void decrementCash(View view) {
        cash = new BigDecimal(cashText.getText().toString());
        if (cash.compareTo(new BigDecimal(1)) != -1) {
            cash = cash.subtract(new BigDecimal(1));
        }
        cashText.setText(String.format("%.2f", cash));
    }

    public void incrementFlexis (View view) {
        flexis = new BigDecimal(flexiText.getText().toString());
        flexis = flexis.add(new BigDecimal(1));
        flexiText.setText(String.format("%.2f", flexis));
    }

    public void decrementFlexis(View view) {
        flexis = new BigDecimal(flexiText.getText().toString());
        if (flexis.compareTo(new BigDecimal(1)) != -1) {
            flexis = flexis.subtract(new BigDecimal(1));
        }
        flexiText.setText(String.format("%.2f", flexis));
    }

    public void onClickArrow(View view) {
        Intent intent = new Intent(BudgetActivity2.this, MenuActivity.class);

        //Send off rid again
        intent.putExtra("rid", rid);

        //Send off budget values
        String flexisString = flexiText.getText().toString();
        String cashstring = cashText.getText().toString();
        intent.putExtra("whichBudgetActivity",2);
        intent.putExtra("flexis",flexisString);
        intent.putExtra("cash",cashstring);

        startActivity(intent);
    }
}
