package com.example.sam.ucsc_meal_builder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * A custom ArrayAdapter class to display items in a ListView for CartActivity.
 */
public class CartAdapter extends ArrayAdapter<Item> {

    /** Context this instance was created from */
    private final Context context;
    /** ArrayList of items to display */
    private final ArrayList<Item> items;

    /** Static inner class to keep track of layout Views */
    static class ViewHolder {
        public TextView quantityView;
        public TextView nameView;
        public TextView priceView;
    }

    /** Constructor for CartAdapter */
    public CartAdapter(Context context, ArrayList<Item> items) {
        super(context, R.layout.row_cart, items);
        this.context = context;
        this.items = items;
    }

    /**
     * Creates new view or recycles old view to display item quantity aligned left and item price
     * aligned right in %.2f with item name in the middle with no overlap.
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