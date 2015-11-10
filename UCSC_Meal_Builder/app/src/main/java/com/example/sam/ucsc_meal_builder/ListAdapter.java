package com.example.sam.ucsc_meal_builder;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;


/**
 * Created by Jason on 11/5/2015.
 */
public class ListAdapter extends ArrayAdapter<Item> {

    // Fields
    private final Context context;
    private final ArrayList<Item> items;
    private BigDecimal budgetRemaining;

    // Static fields for views within each row
    static class ViewHolder {
        public TextView nameView;
        public TextView priceView;
    }

    // Constructor
    public ListAdapter(Context context, ArrayList<Item> items) {
        super(context, R.layout.row_layout, items);
        this.context = context;
        this.items = items;
        this.budgetRemaining = new BigDecimal(0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Recycle convertView if it is filled, or create new layout and ViewHolder if null
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.row_layout, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.nameView = (TextView) rowView.findViewById(R.id.item_name);
            holder.priceView = (TextView) rowView.findViewById(R.id.item_price);
            rowView.setTag(holder);
        }

        // Fill views with data
        ViewHolder holder = (ViewHolder) rowView.getTag();

        holder.nameView.setText(items.get(position).getName());
        holder.priceView.setText(String.format("$%s", items.get(position).getPrice()));

        // Compare item price with budgetRemaining for color highlighting
        if (items.get(position).getPrice().compareTo(budgetRemaining) > 0) {
            rowView.setBackgroundColor(Color.GRAY);
        } else {
            rowView.setBackgroundColor(Color.WHITE);
        }

        return rowView;
    }

    // Set budgetRemaining for comparison purposes
    public void setBudgetRemaining(BigDecimal budget) {
        this.budgetRemaining = budget;
    }
}
