package de.smac.smaccloud.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.michael.easydialog.EasyDialog;

import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

import de.smac.smaccloud.R;
import de.smac.smaccloud.adapter.AnnouncementListViewAdapter;
import de.smac.smaccloud.base.Activity;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.data.DataHelper;
import de.smac.smaccloud.fragment.ChannelsFragment;
import de.smac.smaccloud.fragment.RecentActivitiesFragment;
import de.smac.smaccloud.fragment.SettingsFragment;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.model.Announcement;
import de.smac.smaccloud.model.MediaAllDownload;
import de.smac.smaccloud.model.User;
import de.smac.smaccloud.model.UserPreference;
import de.smac.smaccloud.service.FCMMessagingService;

/**
 * Main activity class
 */
public class DashboardActivity extends Activity implements SettingsFragment.InterfacechangeLanguage
{
    private static ActionBarDrawerToggle drawerToggle;
    public LinearLayout parentLayout;
    public NavigationView navigationDashboard;
    public EasyDialog notificationDialog;
    private DrawerLayout drawerLayout;
    private TextView textviewNavigationHeader;
    private int currentNavigationItem = 0;
    private NavigationView.OnNavigationItemSelectedListener menuItemCallback;
    private ArrayList<MediaAllDownload> arraylistDownloadList = new ArrayList<MediaAllDownload>();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    public static void disableDrawerNavigation()
    {
        actionBar.setDisplayHomeAsUpEnabled(true);
        drawerToggle.setDrawerIndicatorEnabled(false);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Helper.retainOrientation(DashboardActivity.this);
        User user = new User();
        UserPreference userPreference = new UserPreference();
        user.id = PreferenceHelper.getUserContext(context);
        try
        {
            user.populateUsingId(context);
            userPreference.userId = user.id;
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        textviewNavigationHeader.setText(user.name + "\n" + user.email);
        navigate(R.id.menuChannels, false);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        Helper.setupTypeface(findViewById(R.id.parentLayout), Helper.robotoRegularTypeface);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        if (getIntent() != null && getIntent().getExtras() != null && !getIntent().getExtras().isEmpty())
        {
            if (getIntent().getExtras().containsKey(FCMMessagingService.KEY_NOTIFICATION_DATA))
            {
                try
                {
                    JSONObject jsonNotificationData = new JSONObject(getIntent().getExtras().getString(FCMMessagingService.KEY_NOTIFICATION_DATA));
                    if (jsonNotificationData.has(FCMMessagingService.KEY_DATA_ACTION_TYPE) && jsonNotificationData.optString(FCMMessagingService.KEY_DATA_ACTION_TYPE).equals(String.valueOf(FCMMessagingService.ACTION_TYPE_CONTENT_UPDATED)))
                    {
                        askForSync();
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        applyTheme();
    }

    @Override
    protected void initializeComponents()
    {
        super.initializeComponents();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationDashboard = (NavigationView) findViewById(R.id.navigationDashboard);

        textviewNavigationHeader = (TextView) navigationDashboard.getHeaderView(0).findViewById(R.id.labelNavigationHeader);
        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);

        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name)
        {
            @Override
            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView)
            {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset)
            {
                super.onDrawerSlide(drawerView, 0);
            }
        };

        applyTheme();
    }

    @Override
    protected void bindEvents()
    {
        super.bindEvents();

        menuItemCallback = new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(MenuItem item)
            {
                navigate(item.getItemId(), false);
                return true;
            }
        };
        navigationDashboard.setNavigationItemSelectedListener(menuItemCallback);
        drawerLayout.setDrawerListener(drawerToggle);
    }

    public void applyTheme()
    {
        updateParentThemeColor();
        //change actionbar color
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(PreferenceHelper.getAppBackColor(context))));
        }
        if (toolbar != null)
        {
            toolbar.setTitleTextColor(Color.parseColor(PreferenceHelper.getAppColor(context)));
        }

        if (drawerToggle != null)
        {
            DrawerArrowDrawable drawerArrowDrawable = drawerToggle.getDrawerArrowDrawable();
            //drawerArrowDrawable.setColor(Color.GRAY);
            drawerArrowDrawable.setColorFilter(Color.parseColor(PreferenceHelper.getAppColor(context)), PorterDuff.Mode.MULTIPLY);
            drawerToggle.setDrawerArrowDrawable(drawerArrowDrawable);
        }

        if (navigationDashboard != null && navigationDashboard.getMenu() != null)
        {
            Helper.setupTypeface(textviewNavigationHeader, Helper.robotoRegularTypeface);
            applyTypefaceToNavigationDrawer();

            //change channel icon color
            navigationDashboard.getMenu()
                    .findItem(R.id.menuChannels)
                    .getIcon()
                    .setColorFilter(Color.parseColor(PreferenceHelper.getAppColor(context)), PorterDuff.Mode.SRC_ATOP);


            //change recent icon color
            navigationDashboard.getMenu()
                    .findItem(R.id.menuActivities)
                    .getIcon()
                    .setColorFilter(Color.parseColor(PreferenceHelper.getAppColor(context)), PorterDuff.Mode.SRC_ATOP);

            //change setting icon color
            navigationDashboard.getMenu()
                    .findItem(R.id.menuSettings)
                    .getIcon()
                    .setColorFilter(Color.parseColor(PreferenceHelper.getAppColor(context)), PorterDuff.Mode.SRC_ATOP);
        }
    }

    /**
     * Redirect to navigation menu
     *
     * @param id
     * @param addToBackStack
     */
    private void navigate(int id, boolean addToBackStack)
    {
        if (id != currentNavigationItem)
        {
            switch (id)
            {
                case R.id.menuChannels:

                    navigateToFragment(R.id.layoutFrame, new ChannelsFragment(), getSupportFragmentManager().getBackStackEntryCount() != 0);
                    actionBar.setTitle(getString(R.string.label_channels));
                    // ((DashboardActivity) this).getSupportActionBar().setTitle(R.string.label_channels);
                    drawerLayout.closeDrawer(navigationDashboard);
                    break;

                case R.id.menuActivities:
                    navigateToFragment(R.id.layoutFrame, new RecentActivitiesFragment(), true);
                    //((DashboardActivity) this).getSupportActionBar().setTitle(R.string.label_recent);
                    actionBar.setTitle(getString(R.string.label_recent));
                    drawerLayout.closeDrawer(navigationDashboard);
                    break;
                case R.id.menuAnnouncements:
                    actionBar.setTitle(getString(R.string.announcements));
                    break;
                case R.id.menuLogout:
                    drawerLayout.closeDrawer(navigationDashboard);
                    actionBar.setTitle(getString(R.string.lable_sign_out));
                    buildDialog(R.style.DialogAnimation, getString(R.string.sign_out_message));
                    break;

                case R.id.menuSettings:
                    navigateToFragment(R.id.layoutFrame, new SettingsFragment(), true);
                    actionBar.setTitle(getString(R.string.settings));
                    //((DashboardActivity) this).getSupportActionBar().setTitle(R.string.settings);
                    drawerLayout.closeDrawer(navigationDashboard);
                    break;
            }
            currentNavigationItem = id;
        }
        else
        {
            drawerLayout.closeDrawer(navigationDashboard);
        }
    }


    private void buildDialog(int animationSource, String type)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.label_sign_out));
        builder.setIcon(R.drawable.ic_signout);
        builder.setMessage(getString(R.string.sign_out_message));
        builder.setMessage(type);
        builder.setPositiveButton(getString(R.string.yes),
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        PreferenceHelper.removeUserContext(context);
                        Intent loginActivity = new Intent(getApplicationContext(), DemoActivity.class);
                        loginActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(loginActivity);
                        finish();
                    }
                });
        builder.setNegativeButton(getString(R.string.no),
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = animationSource;

        dialog.show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.download_All:
                Helper.downloadAllFiles(DashboardActivity.this, true);
                break;
            case R.id.action_notifications:

                View notificationListView = getLayoutInflater().inflate(R.layout.dialog_notification_list, null);
                notificationListView.setLayoutParams(new RelativeLayout.LayoutParams((int) (Helper.getDeviceWidth(this) / 1.5), ViewGroup.LayoutParams.WRAP_CONTENT));
                ListView lstNotifications = (ListView) notificationListView.findViewById(R.id.lstNotifications);
                try
                {
                    ArrayList<Announcement> announcements = new ArrayList<>();
                    DataHelper.getAnnouncementData(context, announcements);

                    if (!announcements.isEmpty())
                    {
                        AnnouncementListViewAdapter announcementListViewAdapter = new AnnouncementListViewAdapter(this, announcements);
                        lstNotifications.setAdapter(announcementListViewAdapter);

                        notificationDialog = new EasyDialog(this);
                        notificationDialog.setLayout(notificationListView)
                                .setGravity(EasyDialog.GRAVITY_BOTTOM)
                                .setBackgroundColor(getResources().getColor(R.color.white1))
                                .setTouchOutsideDismiss(true)
                                .setMatchParent(false);
                        if (toolbar != null && toolbar.findViewById(R.id.action_notifications) != null)
                            notificationDialog.setLocationByAttachedView(toolbar.findViewById(R.id.action_notifications));
                        notificationDialog.show();
                    }
                    else
                    {
                        if (toolbar != null && toolbar.findViewById(R.id.action_notifications) != null)
                        {
                            View announcementView = toolbar.findViewById(R.id.action_notifications);
                            announcementView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_shaking));
                        }
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
                break;
            case R.id.action_search:
                /*SearchDialog searchDialog = new SearchDialog(DashboardActivity.this);
                searchDialog.show();*/
                startActivity(new Intent(DashboardActivity.this, MediaSearchActivity.class));
                break;
        }
        return true;
    }

    /**
     * Start forgot activity
     */
    @Override
    public void onBackPressed()
    {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
        {
            getSupportFragmentManager().popBackStackImmediate();
            if (getSupportFragmentManager().getBackStackEntryCount() == 0)
            {
                //actionBar.setDisplayHomeAsUpEnabled(false);
                //drawerToggle.setDrawerIndicatorEnabled(true);

                navigationDashboard.getMenu().findItem(R.id.menuChannels).setCheckable(true).setChecked(true);
                currentNavigationItem = R.id.menuChannels;
                actionBar.setTitle(getString(R.string.channels));
            }
            else
            {
                Fragment fragment = getFragment();
                setTitleFromFragment(fragment);
            }
        }
        else
        {
            super.onBackPressed();
        }
        //}
    }

    public void setTitleFromFragment(Fragment fragment)
    {
        if (fragment.getClass().getName().equals(ChannelsFragment.class.getName()))
        {
            actionBar.setTitle(getString(R.string.channels));
            currentNavigationItem = R.id.menuChannels;
        }
        else if (fragment.getClass().getName().equals(SettingsFragment.class.getName()))
        {
            actionBar.setTitle(getString(R.string.settings));
            currentNavigationItem = R.id.menuSettings;
            if (toolbar != null && toolbar.getMenu() != null && toolbar.getMenu().findItem(R.id.action_search) != null)
                toolbar.getMenu().findItem(R.id.action_search).setVisible(false);
            if (toolbar != null && toolbar.getMenu() != null && toolbar.getMenu().findItem(R.id.action_notifications) != null)
                toolbar.getMenu().findItem(R.id.action_notifications).setVisible(false);
        }
        else if (fragment.getClass().getName().equals(RecentActivitiesFragment.class.getName()))
        {
            actionBar.setTitle(getString(R.string.label_recent));
            currentNavigationItem = R.id.menuActivities;
            if (toolbar != null && toolbar.getMenu() != null && toolbar.getMenu().findItem(R.id.action_search) != null)
                toolbar.getMenu().findItem(R.id.action_search).setVisible(false);
            if (toolbar != null && toolbar.getMenu() != null && toolbar.getMenu().findItem(R.id.action_notifications) != null)
                toolbar.getMenu().findItem(R.id.action_notifications).setVisible(false);
        }
    }

    private Fragment getFragment()
    {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.layoutFrame);
        return fragment;
    }

    public void updateNavigationMenuString()
    {
        navigationDashboard.getMenu().removeGroup(R.id.menuDashboard);
        navigationDashboard.inflateMenu(R.menu.menu_activity_dashboard);
        navigationDashboard.getMenu().findItem(R.id.menuSettings).setCheckable(true).setChecked(true);
        // Helper.setupTypeface((View) navigationDashboard.getMenu().findItem(R.id.menuChannels),Helper.robotoRegularTypeface);
        //navigationDashboard.getMenu().findItem(R.id.menuSettings).setCheckable(true).setChecked(true);
    }

    public void applyTypefaceToNavigationDrawer()
    {
        Menu m = navigationDashboard.getMenu();
        if (m != null)
        {
            for (int i = 0; i < m.size(); i++)
            {
                MenuItem mi = m.getItem(i);

                //for applying a font to subMenu ...
                SubMenu subMenu = mi.getSubMenu();
                if (subMenu != null && subMenu.size() > 0)
                {
                    for (int j = 0; j < subMenu.size(); j++)
                    {
                        MenuItem subMenuItem = subMenu.getItem(j);
                        applyFontToMenuItem(subMenuItem);
                    }
                }

                //the method we have create in activity
                applyFontToMenuItem(mi);
            }
        }
    }

    private void applyFontToMenuItem(MenuItem mi)
    {
        Typeface font = Helper.getCurrentTypeface();
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        //getMenuInflater().inflate(R.menu.menu_activity_dashboard, menu);
        return false;
    }*/

    public void changeLangaugeMenu(String lang)
    {
        Menu menu = navigationDashboard.getMenu();
        menu.findItem(R.id.menuChannels).setTitle(getString(R.string.channels));
        menu.findItem(R.id.menuActivities).setTitle(getString(R.string.activities));
        menu.findItem(R.id.menuAnnouncements).setTitle(getString(R.string.announcements));
        menu.findItem(R.id.menuSettings).setTitle(getString(R.string.settings));
        menu.findItem(R.id.menuLogout).setTitle(getString(R.string.label_sign_out));
        actionBar.setTitle(getString(R.string.settings));
    }


    @Override
    public void changeLanguage(String lang)
    {
        changeLangaugeMenu(lang);
    }

    public class CustomTypefaceSpan extends TypefaceSpan
    {

        private final Typeface newType;

        public CustomTypefaceSpan(String family, Typeface type)
        {
            super(family);
            newType = type;
        }

        private void applyCustomTypeFace(Paint paint, Typeface tf)
        {
            int oldStyle;
            Typeface old = paint.getTypeface();
            if (old == null)
            {
                oldStyle = 0;
            }
            else
            {
                oldStyle = old.getStyle();
            }

            int fake = oldStyle & ~tf.getStyle();
            if ((fake & Typeface.BOLD) != 0)
            {
                paint.setFakeBoldText(true);
            }

            if ((fake & Typeface.ITALIC) != 0)
            {
                paint.setTextSkewX(-0.25f);
            }

            paint.setTypeface(tf);
        }

        @Override
        public void updateDrawState(TextPaint ds)
        {
            applyCustomTypeFace(ds, newType);
        }

        @Override
        public void updateMeasureState(TextPaint paint)
        {
            applyCustomTypeFace(paint, newType);
        }
    }
}