package de.smac.smaccloud.adapter;

import android.content.res.Configuration;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import de.smac.smaccloud.R;
import de.smac.smaccloud.base.Activity;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.model.Media;
import de.smac.smaccloud.widgets.SquareImageView;


public class MediaSearchAdapter extends BaseAdapter implements Filterable
{
    public ArrayList<Media> resultArrayList;
    LayoutInflater inflater;
    Activity activity;
    private ArrayList<Media> data;
    private ArrayList<Media> typeAheadData;

    public MediaSearchAdapter(Activity activity, ArrayList<Media> allData)
    {
        this.activity = activity;
        inflater = LayoutInflater.from(activity);
        data = new ArrayList<>();
        resultArrayList = new ArrayList<>();
        typeAheadData = new ArrayList<>();
        typeAheadData.addAll(allData);
    }

    public void addMoreData(ArrayList<Media> allData, String filterText)
    {
        typeAheadData.clear();
        typeAheadData.addAll(allData);
        getFilter().filter(filterText);
        notifyDataSetChanged();
    }


    @Override
    public Filter getFilter()
    {
        Filter filter = new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence constraint)
            {
                FilterResults filterResults = new FilterResults();
                if (!TextUtils.isEmpty(constraint))
                {
                    // Retrieve the autocomplete results.
                    List<Media> searchData = new ArrayList<>();
                    resultArrayList.clear();
                    for (Media media : typeAheadData)
                    {

                        //for (String str :typeAheadData)
                        {
                            if (media.name.toLowerCase().contains(constraint.toString().toLowerCase()))
                            {
                                searchData.add(media);
                                resultArrayList.add(media);
                            }
                        }
                    }

                    // Assign the data to the FilterResults
                    filterResults.values = searchData;
                    filterResults.count = searchData.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results)
            {
                if (results.values != null)
                    data = (ArrayList<Media>) results.values;
                else data.clear();
                notifyDataSetChanged();

            }
        };
        return filter;
    }

    @Override
    public int getCount()
    {
        return data.size();
    }

    @Override
    public Object getItem(int position)
    {
        return data.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final MyViewHolder mViewHolder;

        if (convertView == null)
        {
            //convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            convertView = inflater.inflate(R.layout.search_result_list_single, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        }
        else
        {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        Media currentListData = (Media) getItem(position);

        mViewHolder.txt_media_name.setText(currentListData.name);


        boolean isTabletSize = activity.getResources().getBoolean(R.bool.isTablet);
        if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            if (isTabletSize)
            {
                mViewHolder.imageIcon.setLayoutParams(new LinearLayout.LayoutParams(Helper.getDeviceWidth(activity) / 8, Helper.getDeviceHeight(activity) / 12));
            }
            else
            {
                mViewHolder.imageIcon.setLayoutParams(new LinearLayout.LayoutParams(Helper.getDeviceWidth(activity) / 6, Helper.getDeviceHeight(activity) / 6));
            }
        }
        else
        {
            if (isTabletSize)
            {
                mViewHolder.imageIcon.setLayoutParams(new LinearLayout.LayoutParams(Helper.getDeviceWidth(activity) / 11, Helper.getDeviceHeight(activity) / 14));
            }
            else
            {
                mViewHolder.imageIcon.setLayoutParams(new LinearLayout.LayoutParams(Helper.getDeviceWidth(activity) / 5, Helper.getDeviceHeight(activity) / 6));
            }
        }

        {
            Uri imageUri = Uri.parse(currentListData.icon);
            Glide.with(activity).load(imageUri).fitCenter().diskCacheStrategy(DiskCacheStrategy.ALL).into(mViewHolder.imageIcon);
            mViewHolder.imageIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        Helper.setupTypeface(mViewHolder.parentLayout, Helper.robotoRegularTypeface);

        return convertView;
    }


    private class MyViewHolder
    {
        SquareImageView imageIcon;
        TextView txt_media_name;
        LinearLayout parentLayout;


        public MyViewHolder(View convertView)
        {
            imageIcon = (SquareImageView) convertView.findViewById(R.id.imageIcon);
            txt_media_name = (TextView) convertView.findViewById(R.id.txt_media_name);
            parentLayout = (LinearLayout) convertView.findViewById(R.id.parentLayout1);

        }
    }
}
