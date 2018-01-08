package de.smac.smaccloud.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import java.util.ArrayList;

import de.smac.smaccloud.R;
import de.smac.smaccloud.adapter.ChannelAttachAdapter;
import de.smac.smaccloud.base.Activity;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.data.DataHelper;
import de.smac.smaccloud.model.Channel;
import de.smac.smaccloud.model.Media;

import static de.smac.smaccloud.activity.ShareActivity.REQUEST_CODE_MEDIA_ATTACHMENT;

public class ShareAttachmentActivity extends Activity
{
    public static ArrayList<Media> selectedAttachmentList;
    RecyclerView recyclerChannels;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_share_attachment);
        Helper.retainOrientation(ShareAttachmentActivity.this);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle(getString(R.string.channels));
        }

        selectedAttachmentList = getIntent().getExtras().getParcelableArrayList(ShareActivity.KEY_SELECTED_MEDIA);

        recyclerChannels = (RecyclerView) findViewById(R.id.recyclerChannels);
        recyclerChannels.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        ArrayList<Channel> channelsArrayList = new ArrayList<>();

        try
        {
            DataHelper.getChannels(context, channelsArrayList);
            recyclerChannels.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
            recyclerChannels.setItemAnimator(new DefaultItemAnimator());
            recyclerChannels.setAdapter(new ChannelAttachAdapter(ShareAttachmentActivity.this, channelsArrayList));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MEDIA_ATTACHMENT && resultCode == Activity.RESULT_OK)
        {
            setResult(resultCode, data);
            finish();
        }
    }
}
