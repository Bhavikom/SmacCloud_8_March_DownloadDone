package de.smac.smaccloud.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;

import de.smac.smaccloud.R;
import de.smac.smaccloud.base.Activity;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.data.DataHelper;
import de.smac.smaccloud.fragment.MediaFragment;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.model.Media;
import de.smac.smaccloud.service.FCMMessagingService;

public class StorageActivity extends Activity
{

    public LinearLayout parentLayout;
    TextView textTotalDiskSpace;
    TextView textUsedDiskSpace;
    TextView textFreeSpace;
    TextView textImagesMemorySize;
    TextView textVideosMemorySize;
    TextView textAudioMemorySize;
    TextView textDocumentsMemorySize;
    TextView txtStorage;
    TextView txtMedia;
    ArrayList<Media> mediaArrayList;
    long mediaDownloadSize;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_storage);
        Helper.retainOrientation(StorageActivity.this);


    }

    @Override
    protected void initializeComponents()
    {
        super.initializeComponents();

        long blockSize = 0;
        long availableBlocks = 0;
        long usedBlocks = 0;
        long imageMemorySize = 0;
        long videosMemorySize = 0;
        long AudioMemorySize = 0;
        long documentMemorySize = 0;


        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2)
        {
            blockSize = stat.getTotalBytes();
            availableBlocks = stat.getAvailableBytes();
            usedBlocks = stat.getTotalBytes() - stat.getAvailableBytes();
        }
        else
        {
            blockSize = (long) stat.getBlockSize() * (long) stat.getBlockCount();
            availableBlocks = (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
            usedBlocks = ((long) stat.getBlockSize() * (long) stat.getBlockCount()) - ((long) stat.getBlockSize() * (long) stat.getAvailableBlocks());
        }
        txtMedia = (TextView) findViewById(R.id.txt_media);
        txtStorage = (TextView) findViewById(R.id.txt_storage);
        textTotalDiskSpace = (TextView) findViewById(R.id.textTotalDiskSpace);
        textTotalDiskSpace.setText(Helper.bytesConvertsToMb(blockSize, context));

        textUsedDiskSpace = (TextView) findViewById(R.id.textUsedDiskSpace);


        textFreeSpace = (TextView) findViewById(R.id.textFreeSpace);
        textFreeSpace.setText(Helper.bytesConvertsToMb(availableBlocks, context));

        textImagesMemorySize = (TextView) findViewById(R.id.textImagesMemorySize);
        textVideosMemorySize = (TextView) findViewById(R.id.textVideosMemorySize);
        textAudioMemorySize = (TextView) findViewById(R.id.textAudiosMemorySize);
        textDocumentsMemorySize = (TextView) findViewById(R.id.textDocumentsMemorySize);
        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);
        applyThemeColor();
        FCMMessagingService.themeChangeNotificationListener=new FCMMessagingService.ThemeChangeNotificationListener()
        {
            @Override
            public void onThemeChangeNotificationReceived()
            {
                applyThemeColor();
            }
        };



        try
        {
            mediaArrayList = new ArrayList<>();
            DataHelper.getAllMediaList(context, mediaArrayList);

            long totalSize = 0;
            for (Media media : mediaArrayList)
            {
                if (!media.type.contains(MediaFragment.FILETYPE_FOLDER))
                {
                    totalSize += media.size;
                    if (media.type.contains(MediaFragment.FILETYPE_IMAGE) && media.isDownloaded == 1 && media.isDownloading == 0)
                        imageMemorySize += media.size;
                    else if (media.type.contains(MediaFragment.FILETYPE_VIDEO) && media.type.contains(MediaFragment.FILETYPE_VIDEO_MP4) && media.isDownloaded == 1 && media.isDownloading == 0)
                        videosMemorySize += media.size;
                    else if (media.type.contains(MediaFragment.FILETYPE_AUDIO) && media.type.contains(MediaFragment.FILETYPE_MP3) && media.isDownloaded == 1 && media.isDownloading == 0)
                        AudioMemorySize += media.size;
                    else if (media.type.contains(MediaFragment.FILETYPE_PDF) && media.isDownloaded == 1 && media.isDownloading == 0)
                        documentMemorySize += media.size;
                }
            }

            mediaDownloadSize += imageMemorySize + videosMemorySize + AudioMemorySize + documentMemorySize;
            textUsedDiskSpace.setText(Helper.bytesConvertsToMb(mediaDownloadSize, context));


            if (imageMemorySize > 0)
                textImagesMemorySize.setText(Helper.bytesConvertsToMb(imageMemorySize, context));
            else
                textImagesMemorySize.setText(getString(R.string.label_byte));

            if (videosMemorySize > 0)
                textVideosMemorySize.setText(Helper.bytesConvertsToMb(videosMemorySize, context));
            else
                textVideosMemorySize.setText(getString(R.string.label_byte));

            if (AudioMemorySize > 0)
                textAudioMemorySize.setText(Helper.bytesConvertsToMb(AudioMemorySize, context));
            else
                textAudioMemorySize.setText(getString(R.string.label_byte));

            if (documentMemorySize > 0)
                textDocumentsMemorySize.setText(Helper.bytesConvertsToMb(documentMemorySize, context));
            else
                textDocumentsMemorySize.setText(getString(R.string.label_byte));

        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }


    }

    public void applyThemeColor()
    {
        updateParentThemeColor();
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle(getString(R.string.label_storage));
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(PreferenceHelper.getAppBackColor(context))));
            final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back_material_vector);
            upArrow.setColorFilter(Color.parseColor(PreferenceHelper.getAppColor(context)), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            toolbar.setTitleTextColor(Color.parseColor(PreferenceHelper.getAppColor(context)));
        }
        Helper.setupTypeface(findViewById(R.id.parentLayout), Helper.robotoRegularTypeface);

    }

    @Override
    protected void bindEvents()
    {
        super.bindEvents();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Open sync activity
     */
    private void startSyncActivity()
    {
        Intent syncIntent = new Intent(this, SyncActivity.class);
        syncIntent.putExtra(SyncActivity.IS_FROM_SETTING, true);
        startActivity(syncIntent);
        //finish();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        applyThemeColor();
    }
}
