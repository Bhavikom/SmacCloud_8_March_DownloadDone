package de.smac.smaccloud.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import de.smac.smaccloud.R;
import de.smac.smaccloud.base.Helper;

public class MenuDialogListViewAdapter extends BaseAdapter
{
    Activity activity;
    boolean isLiked;
    int[] resIds;
    String[] menuNames;

    public MenuDialogListViewAdapter(Activity activity, boolean isLiked, int[] resIds, String[] menuNames)
    {
        this.activity = activity;
        this.isLiked = isLiked;
        this.resIds = resIds;
        this.menuNames = menuNames;
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
            convertView = LayoutInflater.from(activity).inflate(R.layout.menu_item_single, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        }
        else
        {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }
        mViewHolder.textMenuName.setTypeface(Helper.robotoRegularTypeface);
        if (position == 1)
        {
            if (isLiked)
                mViewHolder.imgmenu.setImageResource(R.drawable.ic_like);
            else
                mViewHolder.imgmenu.setImageResource(resIds[position]);
        }
        else
            mViewHolder.imgmenu.setImageResource(resIds[position]);
        mViewHolder.textMenuName.setText(menuNames[position]);
        return convertView;
    }


    private class MyViewHolder
    {
        ImageView imgmenu;
        TextView textMenuName;


        public MyViewHolder(View convertView)
        {
            imgmenu = (ImageView) convertView.findViewById(R.id.img_menu);
            textMenuName = (TextView) convertView.findViewById(R.id.textmenuName);

        }
    }

}
