package de.smac.smaccloud.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.smac.smaccloud.R;
import de.smac.smaccloud.base.Helper;

public class CountryListAdapter extends RecyclerView.Adapter<CountryListAdapter.ViewHolder>
{

    ArrayList<String> arraylistLocal;
    Context context;
    View view1;
    ViewHolder viewHolder1;
    TextView textView;

    public CountryListAdapter(Context context1, ArrayList<String> arraylist)
    {

        arraylistLocal = arraylist;
        context = context1;
    }

    @Override
    public CountryListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {

        view1 = LayoutInflater.from(context).inflate(R.layout.activiti_country_list_item, parent, false);

        viewHolder1 = new ViewHolder(view1);

        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {

        holder.textViewCountryListItem.setText(arraylistLocal.get(position));
    }

    @Override
    public int getItemCount()
    {

        return arraylistLocal.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {

        public TextView textViewCountryListItem;
        public ImageView imgSelected;
        public LinearLayout linearParent;

        public ViewHolder(View v)
        {

            super(v);
            textViewCountryListItem = (TextView) v.findViewById(R.id.country_list_item);
            imgSelected = (ImageView) v.findViewById(R.id.imgSelected);
            linearParent = (LinearLayout) v.findViewById(R.id.parentLayout);
            Helper.setupTypeface(linearParent,Helper.robotoRegularTypeface);



        }
    }
}