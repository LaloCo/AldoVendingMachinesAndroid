package com.tsool.appvending;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Diego on 24/01/2016.
 */
public class ProductsAdapter extends ArrayAdapter<Bodega> {
    private Context context;
    private int layoutResourceId;

    public ProductsAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        final Bodega currentItem = getItem(position);

        if(row == null){
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
        }

        row.setTag(currentItem);

        TextView txvProductName = (TextView) row.findViewById(R.id.txvProductName);
        txvProductName.setText(currentItem.getName().toString());

        return row;
    }
}
