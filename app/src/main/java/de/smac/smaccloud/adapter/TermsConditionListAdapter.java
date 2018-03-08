package de.smac.smaccloud.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import de.smac.smaccloud.R;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.model.TermsAndCondition;


public class TermsConditionListAdapter extends ArrayAdapter<TermsAndCondition>
{
    Context context;

    public TermsConditionListAdapter(Context context, int resourceId,
                                     List<TermsAndCondition> items)
    {
        super(context, resourceId, items);
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder = null;
        TermsAndCondition termsItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.activity_termslist, null);
            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView.findViewById(R.id.txt_terms_title);
            holder.txtDesc = (TextView) convertView.findViewById(R.id.txt_terms_description);
            holder.parentLayout = (LinearLayout) convertView.findViewById(R.id.parentLayout);
            Helper.setupTypeface(holder.parentLayout, Helper.robotoRegularTypeface);
            holder.txtTitle.setTextColor(Color.parseColor(PreferenceHelper.getAppColor(context)));
            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();

        holder.txtTitle.setText(termsItem.getTitle());
        holder.txtDesc.setText(termsItem.getDesc());


        return convertView;
    }

    private class ViewHolder
    {

        TextView txtTitle;
        TextView txtDesc;
        LinearLayout parentLayout;
    }
}
