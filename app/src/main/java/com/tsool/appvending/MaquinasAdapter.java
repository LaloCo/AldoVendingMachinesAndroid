package com.tsool.appvending;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Diego on 23/01/2016.
 */
public class MaquinasAdapter extends BaseAdapter implements Filterable{

    Context c;
    ArrayList<Maquina> maquinas;
    CustomFilter filter;
    ArrayList<Maquina> filterList;

    public MaquinasAdapter(Context ctx, ArrayList<Maquina> maquinas) {
        this.c = ctx;
        this.maquinas = maquinas;
        this.filterList = maquinas;
    }

    @Override
    public int getCount() {
        return maquinas.size();
    }

    @Override
    public Object getItem(int position) {
        return maquinas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return maquinas.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null){
            convertView = inflater.inflate(R.layout.machine_item,null);
        }

        final TextView txvName = (TextView) convertView.findViewById(R.id.txvNameMachine);
        final ImageView ivKind = (ImageView) convertView.findViewById(R.id.ivIconMachine);

        txvName.setText(maquinas.get(position).Name);

        switch (maquinas.get(position).Kind){
            case 0:
                txvName.setTextColor(txvName.getContext().getResources().getColor(R.color.forest));
                ivKind.setImageResource(R.drawable.warehouse);
                break;
            case 1:
                txvName.setTextColor(txvName.getContext().getResources().getColor(R.color.gold));
                ivKind.setImageResource(R.drawable.snacks);
                break;
            case 2:
                txvName.setTextColor(txvName.getContext().getResources().getColor(R.color.gold));
                ivKind.setImageResource(R.drawable.snacks);
                break;
            case 3:
                txvName.setTextColor(txvName.getContext().getResources().getColor(R.color.rust));
                ivKind.setImageResource(R.drawable.coffee);
                break;
            default:
                break;
        }

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

                ArrayList<Maquina> filters = new ArrayList<Maquina>();

                for (int i = 0;i < filterList.size();i++){
                    if(filterList.get(i).getName().toUpperCase().contains(constraint)){
                        Maquina m = new Maquina(filterList.get(i).getName(),filterList.get(i).getKind());

                        filters.add(m);
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

            maquinas = (ArrayList<Maquina>) results.values;
            notifyDataSetChanged();

        }
    }
}
