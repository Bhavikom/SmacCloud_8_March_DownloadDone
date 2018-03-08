package de.smac.smaccloud.fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.text.ParseException;
import java.util.ArrayList;

import de.smac.smaccloud.R;
import de.smac.smaccloud.activity.DashboardActivity;
import de.smac.smaccloud.activity.MediaActivity;
import de.smac.smaccloud.adapter.ChannelsAdapter;
import de.smac.smaccloud.base.Activity;
import de.smac.smaccloud.base.Fragment;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.data.DataHelper;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.model.Channel;

/**
 * Show channel data
 */
public class ChannelsFragment extends Fragment
{
    public Menu mMenu;
    boolean isTabletSize;
    private RecyclerView recyclerViewChannels;
    private RecyclerView.LayoutManager gridManager;
    private RecyclerView.LayoutManager listManager;
    private ArrayList<Channel> arraylistChannels;
    private ChannelsAdapter adapterChannels;
    private boolean isGrid = true;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {

        return inflater.inflate(R.layout.fragment_channels, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        adapterChannels.notifyDataSetChanged();
    }

    @Override
    protected void initializeComponents()
    {
        super.initializeComponents();
        isTabletSize = getResources().getBoolean(R.bool.isTablet);

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            // Portrait Mode
            if (isTabletSize)
            {
                gridManager = new GridLayoutManager(context, 2);
            }
            else
            {
                gridManager = new GridLayoutManager(context, 1);
            }

        }
        else
        {
            // Landscape Mode
            if (isTabletSize)
            {
                gridManager = new GridLayoutManager(context, 3);
            }
            else
            {
                gridManager = new GridLayoutManager(context, 1);
            }


        }
        listManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerViewChannels = (RecyclerView) findViewById(R.id.listChannels);
        if (isGrid)
            recyclerViewChannels.setLayoutManager(gridManager);
        else
            recyclerViewChannels.setLayoutManager(listManager);
        arraylistChannels = new ArrayList<>();
        if (!arraylistChannels.isEmpty())
        {
            arraylistChannels.clear();
        }
        try
        {
            DataHelper.getChannels(context, arraylistChannels);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        adapterChannels = new ChannelsAdapter(activity, arraylistChannels);
        adapterChannels.setGrid(isGrid);
        recyclerViewChannels.setAdapter(adapterChannels);

        Activity.notificationIconValueChangeListener = new Activity.NotificationIconValueChangeListener()
        {
            @Override
            public void onNotificationIconValueChanged()
            {
                applyThemeColor();
            }
        };
    }

    @Override
    protected void bindEvents()
    {
        super.bindEvents();
        ChannelsAdapter.OnClickListener clickListener = new ChannelsAdapter.OnClickListener()
        {
            @Override
            public void onItemClick(int position, View view)
            {
                Channel channel = arraylistChannels.get(position);
                //  Bundle arguments = new Bundle();
                Intent mediaIntent = new Intent(getActivity(), MediaActivity.class);
                mediaIntent.putExtra(MediaActivity.EXTRA_CHANNEL, channel);
                mediaIntent.putExtra(MediaActivity.EXTRA_VIEW, isGrid);
                mediaIntent.putExtra(MediaActivity.EXTRA_PARENT, -1);
                startActivity(mediaIntent);

            }

            @Override
            public void onItemDetailClick(int position, View view)
            {
                notifySimple("item detail view");
            }
        };
        adapterChannels.setClickListener(clickListener);

    }

    /**
     * Switch view from Grid to List or vice versa
     *
     * @return
     */
    private Boolean switchViews()
    {
        if (isGrid)
            recyclerViewChannels.setLayoutManager(listManager);
        else
            recyclerViewChannels.setLayoutManager(gridManager);
        isGrid = !isGrid;
        adapterChannels.setGrid(isGrid);
        return isGrid;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
        //inflater.inflate(R.menu.menu_media_all_download, menu);
        inflater.inflate(R.menu.menu_fragment_channels, menu);
        mMenu = menu;
        applyThemeColor();
        // Do something that differs the Activity's menu here
        //super.onCreateOptionsMenu(menu, inflater);
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
                GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
                recyclerViewChannels.setLayoutManager(layoutManager);
            }
            else
            {
                GridLayoutManager layoutManager = new GridLayoutManager(context, 1);
                recyclerViewChannels.setLayoutManager(layoutManager);
            }
        }
        else
        {
            // Landscape Mode
            if (Helper.isTablet(activity))
            {
                GridLayoutManager layoutManager = new GridLayoutManager(context, 3);
                recyclerViewChannels.setLayoutManager(layoutManager);
            }
            else
            {
                GridLayoutManager layoutManager = new GridLayoutManager(context, 1);
                recyclerViewChannels.setLayoutManager(layoutManager);
            }
        }
    }

    public void applyThemeColor()
    {
        activity.updateParentThemeColor();
        if (activity instanceof DashboardActivity)
        {
            ((DashboardActivity) activity).applyTheme();
        }
        activity.updateParentThemeColor();

        if (mMenu != null && mMenu.findItem(R.id.action_notifications) != null)
        {
            MenuItem menuItemNotification = mMenu.findItem(R.id.action_notifications);
            Drawable icon = context.getResources().getDrawable(R.drawable.ic_notify);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                icon.setTint(Color.parseColor(PreferenceHelper.getAppColor(context)));
            }
            else
            {
                icon.setColorFilter(Color.parseColor(PreferenceHelper.getAppColor(context)), PorterDuff.Mode.SRC_ATOP);
            }
            menuItemNotification.setIcon(Helper.buildCounterDrawable(context, DataHelper.getAnnouncementCount(context), icon));
        }
        if (mMenu != null && mMenu.findItem(R.id.action_search) != null)
        {
            MenuItem menuItemSearch = mMenu.findItem(R.id.action_search);
            Drawable icon = context.getResources().getDrawable(R.drawable.ic_search);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                icon.setTint(Color.parseColor(PreferenceHelper.getAppColor(context)));
            }
            else
            {
                icon.setColorFilter(Color.parseColor(PreferenceHelper.getAppColor(context)), PorterDuff.Mode.SRC_ATOP);
            }
            menuItemSearch.setIcon(icon);
        }

        recyclerViewChannels.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (activity.getSupportActionBar() != null)
        {
            activity.getSupportActionBar().setTitle(R.string.label_channels);
        }
        if (activity instanceof DashboardActivity)
        {
            ((DashboardActivity) (activity)).navigationDashboard.getMenu().findItem(R.id.menuChannels).setCheckable(true).setChecked(true);
        }
        applyThemeColor();
    }
}