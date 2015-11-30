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
* Created by Jason on 11/29/2015.
*/
public class CartAdapter extends ArrayAdapter<Item> {

    // Fields
    private final Context context;
    private final ArrayList<Item> items;

    // Static fields for views within each row
    static class ViewHolder {
        public TextView quantityView;
        public TextView nameView;
        public TextView priceView;
    }

    // Constructor
    public CartAdapter(Context context, ArrayList<Item> items) {
        super(context, R.layout.row_cart, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Recycle convertView if it is filled, or create new layout and ViewHolder if null
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.row_cart, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.quantityView = (TextView) rowView.findViewById(R.id.item_quantity);
            holder.nameView = (TextView) rowView.findViewById(R.id.item_name);
            holder.priceView = (TextView) rowView.findViewById(R.id.item_price);
            rowView.setTag(holder);
        }

        // Fill views with data
        ViewHolder holder = (ViewHolder) rowView.getTag();

        holder.quantityView.setText(String.format("%2d", items.get(position).getQuantity()));
        holder.nameView.setText(items.get(position).getName());
        holder.priceView.setText(String.format("$%.2f", items.get(position).getPrice()));

        return rowView;
    }

}