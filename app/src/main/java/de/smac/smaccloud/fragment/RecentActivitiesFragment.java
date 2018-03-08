package de.smac.smaccloud.fragment;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import java.text.ParseException;
import java.util.ArrayList;

import de.smac.smaccloud.R;
import de.smac.smaccloud.activity.DashboardActivity;
import de.smac.smaccloud.adapter.MostVisitedItemRecyclerAdapter;
import de.smac.smaccloud.adapter.RecentItemRecyclerAdapter;
import de.smac.smaccloud.base.Fragment;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.data.DataHelper;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.model.RecentItem;
import de.smac.smaccloud.service.FCMMessagingService;
import info.hoang8f.android.segmented.SegmentedGroup;

public class RecentActivitiesFragment extends Fragment
{

    SegmentedGroup segmentTab;
    RadioButton rdoRecent;
    RadioButton rdoMostVisited;
    RecyclerView recyclerRecent;
    RecyclerView recyclerMostVisited;
    RecyclerView.LayoutManager recentListManager;
    RecyclerView.LayoutManager mostVisitedlistManager;
    ArrayList<RecentItem> recentItems;
    RecentItemRecyclerAdapter recentItemRecyclerAdapter;
    ArrayList<RecentItem> mostVisitedItems;
    MostVisitedItemRecyclerAdapter mostVisitedItemRecyclerAdapter;
    private LinearLayout parentLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        return inflater.inflate(R.layout.fragment_recent_activities, container, false);
    }

    @Override
    protected void initializeComponents()
    {
        parentLayout = (LinearLayout) getActivity().findViewById(R.id.parentLayout);
        segmentTab = (SegmentedGroup) getActivity().findViewById(R.id.segmentTab);
        rdoRecent = (RadioButton) getActivity().findViewById(R.id.rdoRecent);
        rdoMostVisited = (RadioButton) getActivity().findViewById(R.id.rdoMostVisited);
        recentListManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerRecent = (RecyclerView) getActivity().findViewById(R.id.recyclerRecent);
        recyclerRecent.setHasFixedSize(true);
        recyclerRecent.setLayoutManager(recentListManager);

        mostVisitedlistManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerMostVisited = (RecyclerView) getActivity().findViewById(R.id.recyclerMostVisited);
        recyclerMostVisited.setHasFixedSize(true);
        recyclerMostVisited.setLayoutManager(mostVisitedlistManager);

        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            if (Helper.isTablet(activity))
            {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) segmentTab.getLayoutParams();
                params.setMargins((int) getResources().getDimension(R.dimen.seventy_five_dp),
                        0, (int) getResources().getDimension(R.dimen.seventy_five_dp), 0); //substitute parameters for left, top, right, bottom
                segmentTab.setLayoutParams(params);
                GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
                recyclerRecent.setLayoutManager(layoutManager);
                GridLayoutManager layoutManager1 = new GridLayoutManager(context, 2);
                recyclerMostVisited.setLayoutManager(layoutManager1);

            }
        }
        else
        {
            // Landscape Mode
            if (Helper.isTablet(activity))
            {

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) segmentTab.getLayoutParams();
                params.setMargins((int) getResources().getDimension(R.dimen.two_hundred_eightee),
                        0, (int) getResources().getDimension(R.dimen.two_hundred_eightee), 0); //substitute parameters for left, top, right, bottom
                segmentTab.setLayoutParams(params);
                GridLayoutManager layoutManager = new GridLayoutManager(context, 3);
                recyclerRecent.setLayoutManager(layoutManager);
                GridLayoutManager layoutManager1 = new GridLayoutManager(context, 3);
                recyclerMostVisited.setLayoutManager(layoutManager1);

            }
        }

        recentItems = new ArrayList<>();
        mostVisitedItems = new ArrayList<>();
        try
        {
            DataHelper.getRecentVisitedItems(context, recentItems);
            if (recyclerRecent != null && recentItems != null)
            {
                recentItemRecyclerAdapter = new RecentItemRecyclerAdapter(getActivity(), recentItems);
                recyclerRecent.setAdapter(recentItemRecyclerAdapter);
            }

            DataHelper.getMostVisitedItems(context, mostVisitedItems);
            if (recyclerMostVisited != null && mostVisitedItems != null)
            {
                mostVisitedItemRecyclerAdapter = new MostVisitedItemRecyclerAdapter(getActivity(), mostVisitedItems);
                recyclerMostVisited.setAdapter(mostVisitedItemRecyclerAdapter);
            }
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        applyThemeColor();
        FCMMessagingService.themeChangeNotificationListener = new FCMMessagingService.ThemeChangeNotificationListener()
        {
            @Override
            public void onThemeChangeNotificationReceived()
            {

                applyThemeColor();
            }
        };

    }

    public void applyThemeColor()
    {
        activity.updateParentThemeColor();
        segmentTab.setTintColor(Color.parseColor(PreferenceHelper.getAppColor(context)));
        Helper.setupTypeface(parentLayout, Helper.robotoRegularTypeface);
        recyclerRecent.getAdapter().notifyDataSetChanged();
        recyclerMostVisited.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void bindEvents()
    {

        CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if (compoundButton.isChecked())
                {
                    switch (compoundButton.getId())
                    {
                        case R.id.rdoRecent:
                            if (activity.getSupportActionBar() != null)
                            {
                                activity.getSupportActionBar().setTitle(R.string.label_recent);
                                //(activity).getSupportActionBar().setTitle(R.string.label_recent);
                            }
                            recyclerRecent.setVisibility(View.VISIBLE);
                            recyclerMostVisited.setVisibility(View.GONE);
                            break;
                        case R.id.rdoMostVisited:
                            if (activity.getSupportActionBar() != null)
                            {
                                activity.getSupportActionBar().setTitle(R.string.label_most_visited);

                            }
                            recyclerMostVisited.setVisibility(View.VISIBLE);
                            recyclerRecent.setVisibility(View.GONE);
                            break;
                    }
                }
            }
        };
        rdoRecent.setOnCheckedChangeListener(checkedChangeListener);
        rdoMostVisited.setOnCheckedChangeListener(checkedChangeListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        try
        {
            if (activity.getSupportActionBar() != null)
            {
                activity.getSupportActionBar().setTitle(R.string.label_recent);
            }
            if (activity instanceof DashboardActivity)
            {
                ((DashboardActivity) (activity)).navigationDashboard.getMenu().findItem(R.id.menuActivities).setCheckable(true).setChecked(true);
            }
            recentItems.clear();
            DataHelper.getRecentVisitedItems(context, recentItems);
            if (recentItemRecyclerAdapter != null && recentItems != null)
            {
                recentItemRecyclerAdapter.addMoreData(recentItems);
            }
            mostVisitedItems.clear();
            DataHelper.getMostVisitedItems(context, mostVisitedItems);
            if (mostVisitedItemRecyclerAdapter != null && mostVisitedItems != null)
            {
                mostVisitedItemRecyclerAdapter.addMoreData(mostVisitedItems);
            }
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        applyThemeColor();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            // Portrait Mode
            if (Helper.isTablet(activity))
            {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) segmentTab.getLayoutParams();
                params.setMargins((int) getResources().getDimension(R.dimen.seventy_five_dp),
                        0, (int) getResources().getDimension(R.dimen.seventy_five_dp), 0); //substitute parameters for left, top, right, bottom
                segmentTab.setLayoutParams(params);
                GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
                recyclerRecent.setLayoutManager(layoutManager);
                GridLayoutManager layoutManager1 = new GridLayoutManager(context, 2);
                recyclerMostVisited.setLayoutManager(layoutManager1);
            }
        }
        else
        {
            // Landscape Mode
            if (Helper.isTablet(activity))
            {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) segmentTab.getLayoutParams();
                params.setMargins((int) getResources().getDimension(R.dimen.two_hundred_eightee),
                        0, (int) getResources().getDimension(R.dimen.two_hundred_eightee), 0); //substitute parameters for left, top, right, bottom
                segmentTab.setLayoutParams(params);
                GridLayoutManager layoutManager = new GridLayoutManager(context, 3);
                recyclerRecent.setLayoutManager(layoutManager);
                GridLayoutManager layoutManager1 = new GridLayoutManager(context, 3);
                recyclerMostVisited.setLayoutManager(layoutManager1);
            }
        }
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration
    {
        private int space;

        public SpacesItemDecoration(int space)
        {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
        {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildPosition(view) == 0)
                outRect.top = space;
        }
    }

}
