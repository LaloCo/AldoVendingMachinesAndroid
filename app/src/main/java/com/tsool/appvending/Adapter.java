package com.tsool.appvending;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by maste on 04/02/2016.
 */
public class Adapter extends BaseAdapter implements Filterable {

    Context c;
    ArrayList<Bodega> productos;
    CustomFilter filter;
    ArrayList<Bodega> filterList;

    public Adapter(Context ctx, ArrayList<Bodega> productos) {
        this.c = ctx;
        this.productos = productos;
        this.filterList = productos;
    }

    @Override
    public int getCount() {
        return productos.size();
    }

    @Override
    public Object getItem(int position) {
        return productos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return productos.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null){
            convertView = inflater.inflate(R.layout.pruduct_item,null);
        }

        final TextView txvName = (TextView) convertView.findViewById(R.id.txvProductName);

        txvName.setText(productos.get(position).Name);

        return convertView;
    }

    @Override
    public Filter getFilter() {

        if (filter == null){
            filter = new CustomFilter();
        }

        return filter;
    }

    class CustomFilter extends Filter
    {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();

            if(constraint != null && constraint.length() > 0){
                constraint = constraint.toString().toUpperCase();

                ArrayList<Bodega> filters = new ArrayList<Bodega>();

                for (int i = 0;i < filterList.size();i++){
                    if(filterList.get(i).getName().toUpperCase().contains(constraint)){
                        Bodega b = new Bodega(filterList.get(i).getName());

                        filters.add(b);
                    }
                }
                results.count = filters.size();
                results.values = filters;
            }else {
                results.count = filterList.size();
                results.values = filterList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            productos = (ArrayList<Bodega>) results.values;
            notifyDataSetChanged();

        }
    }
}
