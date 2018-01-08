package de.smac.smaccloud.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import de.smac.smaccloud.R;
import de.smac.smaccloud.base.Activity;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.fragment.MediaAttachmentFragment;
import de.smac.smaccloud.model.Channel;

public class MediaAttachmentActivity extends Activity
{
    public static final String EXTRA_CHANNEL = "extra_channel";
    public static final String EXTRA_PARENT = "extra_parent";
    public static final String EXTRA_VIEW = "extra_view";
    public static final String EXTRA_MEDIA = "extra_media";
    MediaAttachmentFragment mediaFragment;
    private MenuInflater inflater;
    private Channel channel;
    private int parentId = -1;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_attach);
        Helper.retainOrientation(MediaAttachmentActivity.this);
        Intent extras = getIntent();
        if (extras != null)
        {
            channel = extras.getExtras().getParcelable(EXTRA_CHANNEL);
            parentId = extras.getExtras().getInt(EXTRA_PARENT);
            if (getSupportActionBar() != null)
            {
                getSupportActionBar().setTitle(channel.name);

            }
        }


        mediaFragment = new MediaAttachmentFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable(EXTRA_CHANNEL, channel);
        arguments.putInt(EXTRA_PARENT, parentId);
        mediaFragment.setArguments(arguments);
        navigateToFragment(R.id.layoutDynamicFrame, mediaFragment, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_media_attachment_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_add:
                Intent intentReturn = new Intent();
                intentReturn.putExtra(ShareActivity.KEY_SELECTED_MEDIA, ShareAttachmentActivity.selectedAttachmentList);
                intentReturn.setFlags(Activity.RESULT_OK);
                setResult(Activity.RESULT_OK, intentReturn);
                finish();
                break;
        }
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        if (fragmentManager.getBackStackEntryCount() == 1)
            finish();
        else
            super.onBackPressed();

    }
}
