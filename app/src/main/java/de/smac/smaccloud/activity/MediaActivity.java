package de.smac.smaccloud.activity;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;

import de.smac.smaccloud.R;
import de.smac.smaccloud.base.Activity;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.fragment.MediaFragment;
import de.smac.smaccloud.fragment.ShowdownloadProcessFragment;
import de.smac.smaccloud.helper.InterfaceStopDownload;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.model.Channel;
import de.smac.smaccloud.service.FCMMessagingService;

/**
 * Media activity for show media
 */
public class MediaActivity extends Activity implements ShowdownloadProcessFragment.interfaceAsyncResponseDownloadProcess
{

    public static final String EXTRA_CHANNEL = "extra_channel";
    public static final String EXTRA_PARENT = "extra_parent";
    public static final String EXTRA_VIEW = "extra_view";
    public static final String EXTRA_MEDIA = "extra_media";
    public static int REQUEST_LIKE = 2001;
    public static int REQUEST_COMMENT = 2002;
    public InterfaceStopDownload interfaceStopDownload;
    public MediaFragment mediaFragment;
    private Channel channel;
    private boolean isGrid = false;
    private int parentId = -1;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_media);
        Helper.retainOrientation(MediaActivity.this);
        Intent extras = getIntent();
        if (extras != null)
        {
            channel = extras.getExtras().getParcelable(EXTRA_CHANNEL);
            isGrid = extras.getExtras().getBoolean(EXTRA_VIEW);
            parentId = extras.getExtras().getInt(EXTRA_PARENT);
        }
        mediaFragment = new MediaFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable(EXTRA_CHANNEL, channel);
        arguments.putBoolean(EXTRA_VIEW, isGrid);
        arguments.putInt(EXTRA_PARENT, parentId);
        mediaFragment.setArguments(arguments);

        navigateToFragment(R.id.layoutDynamicFrame, mediaFragment, true);
        FCMMessagingService.themeChangeNotificationListener = new FCMMessagingService.ThemeChangeNotificationListener()
        {
            @Override
            public void onThemeChangeNotificationReceived()
            {
                applyThemeColor();
            }
        };


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void applyThemeColor()
    {
        updateParentThemeColor();
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle(channel.name);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(PreferenceHelper.getAppBackColor(context))));
            final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back_material_vector);
            upArrow.setColorFilter(Color.parseColor(PreferenceHelper.getAppColor(context)), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            toolbar.setTitleTextColor(Color.parseColor(PreferenceHelper.getAppColor(context)));
        }

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        applyThemeColor();
    }

    @Override
    public void onBackPressed()
    {
        if (fragmentManager.getBackStackEntryCount() == 3)
        {
            if (interfaceStopDownload != null)
                interfaceStopDownload.stopDownload();
        }
        if (fragmentManager.getBackStackEntryCount() == 1)
            finish();
        else
        {
            super.onBackPressed();
        }

    }

    @Override
    public void processFinish(String output)
    {

    }

    /*@Override
    public void onThemeChangeNotificationReceived()
    {
        applyThemeColor();
    }
*/

    /*@Override
    public void donwloadDone() {
        Toast.makeText(this,"done ",Toast.LENGTH_SHORT).show();
    }*/

/*    @Override
    public void download() {
        Toast.makeText(this,"done ",Toast.LENGTH_SHORT).show();
    }*/
}
