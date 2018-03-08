package de.smac.smaccloud.adapter;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.smac.smaccloud.R;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.helper.PreferenceHelper;

public class LanguageListViewAdapter extends BaseAdapter
{
    private Activity activity;
    private int[] resIds;
    private String[] langNames;

    public LanguageListViewAdapter(Activity activity)
    {
        this.activity = activity;
        resIds = activity.getResources().getIntArray(R.array.arr_flag_res_ids);
        langNames = activity.getResources().getStringArray(R.array.arr_lang_names);

        TypedArray ar = activity.getResources().obtainTypedArray(R.array.arr_flag_res_ids);
        int len = ar.length();
        resIds = new int[len];
        for (int i = 0; i < len; i++)
            resIds[i] = ar.getResourceId(i, 0);
        ar.recycle();
    }

    @Override
    public int getCount()
    {
        return resIds.length;
    }

    @Override
    public Object getItem(int i)
    {
        return i;
    }

    @Override
    public long getItemId(int i)
    {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        MyViewHolder mViewHolder;


        if (convertView == null)
        {
            convertView = LayoutInflater.from(activity).inflate(R.layout.language_item_single, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        }
        else
        {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }
        //      mViewHolder.textLanguageName.setTypeface(Helper.robotoRegularTypeface);
        mViewHolder.imgFlag.setImageResource(resIds[position]);
        mViewHolder.textLanguageName.setText(langNames[position]);
        //mViewHolder.imgSelected.setVisibility(View.GONE);

        if ((PreferenceHelper.getSelectedLanguage(activity).equals("") || PreferenceHelper.getSelectedLanguage(activity).equals("en")) && position == 0)
        {
            if (PreferenceHelper.getSelectedLanguage(activity).equals(""))
                PreferenceHelper.storeSelectedLanguage(activity, "en");
            mViewHolder.imgSelected.setVisibility(View.VISIBLE);
        }
        else if (PreferenceHelper.getSelectedLanguage(activity).equals("de") && position == 1)
        {
            if (PreferenceHelper.getSelectedLanguage(activity).equals(""))
                PreferenceHelper.storeSelectedLanguage(activity, "de");
            mViewHolder.imgSelected.setVisibility(View.VISIBLE);
        }
        else
            mViewHolder.imgSelected.setVisibility(View.GONE);


        return convertView;
    }


    private class MyViewHolder
    {
        ImageView imgFlag;
        TextView textLanguageName;
        ImageView imgSelected;
        LinearLayout parentLayout;

        private MyViewHolder(View convertView)
        {
            imgFlag = (ImageView) convertView.findViewById(R.id.imgFlag);
            textLanguageName = (TextView) convertView.findViewById(R.id.textLanguageName);
            imgSelected = (ImageView) convertView.findViewById(R.id.imgSelected);
            parentLayout = (LinearLayout) convertView.findViewById(R.id.linear_parent);
            imgSelected.setColorFilter(Color.parseColor(PreferenceHelper.getAppColor(activity)));
            Helper.setupTypeface(parentLayout, Helper.robotoRegularTypeface);
        }
    }

}
