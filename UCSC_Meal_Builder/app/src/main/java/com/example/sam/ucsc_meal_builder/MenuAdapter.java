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
 * A custom ArrayAdapter class to display items in a ListView for MenuActivity.
 */
public class MenuAdapter extends ArrayAdapter<Item> {

    /** Context this instance was created from */
    private final Context context;
    /** ArrayList of items to display */
    private final ArrayList<Item> items;
    /** Additional information to do a custom view */
    private BigDecimal budgetRemaining;

    /** Static inner class to keep track of layout Views */
    static class ViewHolder {
        public TextView nameView;
        public TextView priceView;
    }

    /** Constructor for MenuAdapter */
    public MenuAdapter(Context context, ArrayList<Item> items) {
        super(context, R.layout.row_menu, items);
        this.context = context;
        this.items = items;
        this.budgetRemaining = new BigDecimal(0);
    }

    /**
     * Creates new view or recycles old view to display item name aligned left and item price
     * aligned right in %.2f with no overlap.
     * Item price text color changes to red if the item price exceeds budgetRemaining.
     *
     * @param position     row position in ListView
     * @param convertView  recycled view
     * @param parent       ViewGroup
     * @return             the view to be displayed on app
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Recycle convertView if it is filled, or create new layout and ViewHolder if null
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.row_menu, parent, false);
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
            holder.priceView.setTextColor(Color.RED);
        } else {
            holder.priceView.setTextColor(Color.BLACK);
        }

        return rowView;
    }

    /**
     * @param budget  set remaining budget for price comparisons
     */
    public void setBudgetRemaining(BigDecimal budget) {
        this.budgetRemaining = budget;
    }
}
