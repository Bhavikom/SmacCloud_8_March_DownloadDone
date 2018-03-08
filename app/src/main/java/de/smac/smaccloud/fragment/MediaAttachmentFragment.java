package de.smac.smaccloud.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.ParseException;
import java.util.ArrayList;

import de.smac.smaccloud.R;
import de.smac.smaccloud.adapter.MediaAttachAdapter;
import de.smac.smaccloud.base.Fragment;
import de.smac.smaccloud.data.DataHelper;
import de.smac.smaccloud.model.Channel;
import de.smac.smaccloud.model.Media;
import de.smac.smaccloud.service.DownloadFileFromURL;
import de.smac.smaccloud.widgets.DividerItemDecoration;

public class MediaAttachmentFragment extends Fragment implements DownloadFileFromURL.interfaceAsyncResponse
{
    public static final String EXTRA_CHANNEL = "extra_channel";
    public static final String EXTRA_PARENT = "extra_parent";
    public static final String EXTRA_MEDIA = "extra_media";
    private RecyclerView listChannels;
    private RecyclerView recyclerView;
    private MediaAttachAdapter mediaAdapter;
    private ArrayList<Media> media;
    private Channel channel;
    private Media mediaItem;
    private int parentId = -1;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_media_attach, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mediaAdapter.notifyDataSetChanged();
    }


    @Override
    protected void initializeComponents()
    {
        super.initializeComponents();
        Bundle arguments = getArguments();
        if (arguments != null)
        {
            channel = arguments.getParcelable(EXTRA_CHANNEL);
            parentId = arguments.getInt(EXTRA_PARENT);
            if (!(parentId == -1))
            {
                mediaItem = arguments.getParcelable(EXTRA_MEDIA);
            }

        }
        listChannels = (RecyclerView) findViewById(R.id.listChannels);
        listChannels.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        listChannels.setLayoutManager(mLayoutManager);
        listChannels.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        listChannels.setItemAnimator(new DefaultItemAnimator());

        media = new ArrayList<>();
        mediaAdapter = new MediaAttachAdapter(activity, media);
        listChannels.setAdapter(mediaAdapter);
        try
        {
            if (parentId == -1)
                DataHelper.getMediaListFromParent(context, channel.id, media);
            else
                DataHelper.getMediaListFromChannelId(context, parentId, media);
            mediaAdapter.notifyDataSetChanged();
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume()
    {
        try
        {
            if (parentId != -1)
            {
                Media currentMedia = new Media();
                currentMedia.id = parentId;
                DataHelper.getMedia(context, currentMedia);
                activity.getSupportActionBar().setTitle(currentMedia.name);
            }
            else
            {
                activity.getSupportActionBar().setTitle(channel.name);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        super.onResume();
        super.onResume();
    }

    @Override
    public void processFinish(String output, Media media, int pos) {

    }

    @Override
    public void statusOfDownload(Media media, int pos) {

    }

}
