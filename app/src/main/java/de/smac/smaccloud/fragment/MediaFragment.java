package de.smac.smaccloud.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.Pair;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.michael.easydialog.EasyDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;


import de.smac.smaccloud.R;
import de.smac.smaccloud.activity.MediaDetailActivity;
import de.smac.smaccloud.activity.ShareActivity;
import de.smac.smaccloud.activity.UserCommentViewActivity;
import de.smac.smaccloud.adapter.MediaAdapter;
import de.smac.smaccloud.adapter.MenuDialogListViewAdapter;
import de.smac.smaccloud.base.Fragment;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.base.RequestParameter;
import de.smac.smaccloud.data.DataHelper;
import de.smac.smaccloud.helper.DataProvider;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.model.Channel;
import de.smac.smaccloud.model.Media;
import de.smac.smaccloud.model.User;
import de.smac.smaccloud.model.UserComment;
import de.smac.smaccloud.model.UserLike;
import de.smac.smaccloud.service.DownloadFileFromURL;
import de.smac.smaccloud.service.SMACCloudApplication;
import de.smac.smaccloud.widgets.UserCommentDialog;

import static android.app.Activity.RESULT_OK;
import static de.smac.smaccloud.activity.MediaActivity.REQUEST_COMMENT;
import static de.smac.smaccloud.activity.MediaActivity.REQUEST_LIKE;
import static de.smac.smaccloud.base.Helper.LOCALIZATION_TYPE_ERROR_CODE;

import android.os.Handler;


/**
 * Show arrayListMedia data
 */
public class MediaFragment extends Fragment implements DownloadFileFromURL.interfaceAsyncResponse, MediaAdapter.OnItemClickOfAdapter, ShowdownloadProcessFragment.interfaceAsyncResponseDownloadProcess
{

    public static final String EXTRA_CHANNEL = "extra_channel";
    public static final String EXTRA_PARENT = "extra_parent";
    public static final String EXTRA_VIEW = "extra_view";
    public static final String EXTRA_MEDIA = "extra_media";
    public static final String KEY_MEDIA_DETAIL_IS_DELETED = "isDeleted";
    public static final int REQ_IS_MEDIA_DELETED = 4001;
    public static final String FILETYPE_IMAGE = "image";
    public static final String FILETYPE_PDF = "application/pdf";
    public static final String FILETYPE_FOLDER = "folder";
    public static final String FILETYPE_AUDIO = "audio";
    public static final String FILETYPE_MP3 = "audio/mp3";
    public static final String FILETYPE_VIDEO = "video";
    public static final String FILETYPE_VIDEO_MP4 = "video/mp4";
    public static final String BROADCAST_MEDIA_DOWNLOAD_COMPLETE = "BROADCAST_MEDIA_DOWNLOAD_COMPLETE";
    private static final int REQ_GET_MEDIA_CONTENT = 4303;
    public static int COMMENT_ACTIVITY_REQUEST_CODE = 1001;
    public PreferenceHelper prefManager;
    public EasyDialog dialog;
    public boolean chklike;
    int mScrollState;
    DownloadFileFromURL.interfaceAsyncResponse interfaceResponse = null;
    UserCommentDialog commentDialog;
    boolean isTabletSize;
    Handler handler;
    private BroadcastReceiver broadcastReceiverToHandleDownload;
    private Media media1;
    private RecyclerView recyclerView;
    private MediaAdapter mediaAdapter;
    private ArrayList<Media> arrayListMedia;
    private Channel channel;
    private User user;
    private Media mediaItem;
    private boolean isGrid = false;
    private int parentId = -1;
    private BroadcastReceiver receiver;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        prefManager = new PreferenceHelper(getActivity());
        setHasOptionsMenu(false);
        interfaceResponse = this;
        handler = new Handler();

        broadcastReceiverToHandleDownload = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, final Intent intent)
            {
                final Intent intentlocal = intent;
                if (intent.getAction().equals(Helper.DOWNLOAD_ACTION))
                {
                    //Log.e(""," get string receiver : "+intent.getStringArrayExtra("media_object"));
                    final Media mediaReceived = intentlocal.getParcelableExtra("media_object");
                    final String position = intentlocal.getStringExtra("position");
                    handler.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if (isAdded())
                            {
                                for (Media object : arrayListMedia)
                                {
                                    if (object.id == mediaReceived.id)
                                    {
                                        arrayListMedia.set(arrayListMedia.indexOf(object), mediaReceived);
                                        if (mediaAdapter != null)
                                        {
                                            if (mScrollState == RecyclerView.SCROLL_STATE_IDLE)
                                            {
                                                mediaAdapter.notifyItemChanged(Integer.parseInt(position), mediaReceived);
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }, 0);
                }
            }
        };
    }

    @Override
    public void onStart()
    {
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((broadcastReceiverToHandleDownload),
                new IntentFilter(Helper.DOWNLOAD_ACTION)
        );
        try
        {
            receiver = new BroadcastReceiver()
            {
                @Override
                public void onReceive(Context context, Intent intent)
                {
                    if (intent.hasExtra("MEDIA"))
                    {
                        final Media tempMedia = intent.getParcelableExtra("MEDIA");
                        if (checkMediaId(tempMedia))
                        {
                            updateMediaList();
                            mediaAdapter.notifyDataSetChanged();
                        }
                    }
                    else
                    {
                        updateMediaList();
                        mediaAdapter.notifyDataSetChanged();
                    }
                }
            };
            activity.registerReceiver(receiver, new IntentFilter(BROADCAST_MEDIA_DOWNLOAD_COMPLETE));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public boolean checkMediaId(Media tempMedia)
    {
        for (int i = 0; i < arrayListMedia.size(); i++)
        {
            if (arrayListMedia.get(i).id == tempMedia.id)
                return true;
        }
        return false;
    }

    @Override
    public void onStop()
    {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiverToHandleDownload);
        //context.unregisterReceiver(receiver);
    }

    @Override
    public void onDestroy()
    {
        try
        {
            context.unregisterReceiver(receiver);
        }
        catch (IllegalArgumentException iaex)
        {
            iaex.printStackTrace();
        }
        super.onDestroy();
        mediaAdapter.onPauseIsCalled();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_media, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mediaAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Helper.IS_DIALOG_SHOW = true;
    }

    @Override
    protected void bindEvents()
    {
        super.bindEvents();
        MediaAdapter.OnClickListener clickListener = new MediaAdapter.OnClickListener()
        {
            @SuppressLint("RestrictedApi")
            @Override
            public void onItemClick(final int position, View view) throws ParseException
            {
                media1 = arrayListMedia.get(position);

                if (activity.getSupportActionBar() != null)
                {
                    activity.getSupportActionBar().setTitle(media1.name);
                }

                if ((arrayListMedia.get(position).type.equals(FILETYPE_FOLDER)))
                {
                    Fragment mediaFragment = new MediaFragment();
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(EXTRA_CHANNEL, channel);
                    arguments.putParcelable(EXTRA_MEDIA, media1);
                    arguments.putBoolean(EXTRA_VIEW, isGrid);
                    arguments.putInt(EXTRA_PARENT, media1.id);
                    mediaFragment.setArguments(arguments);
                    navigateToFragment(R.id.layoutDynamicFrame, mediaFragment, true);
                }
                else
                {
                    int[] resIds;
                    String[] menuNames;
                    if (arrayListMedia.get(position).isDownloaded == 1)
                    {
                        TypedArray ar = activity.getResources().obtainTypedArray(R.array.arr_menu_img_res_ids);
                        int len = ar.length();
                        resIds = new int[len];
                        for (int i = 0; i < len; i++)
                            resIds[i] = ar.getResourceId(i, 0);
                        ar.recycle();
                        menuNames = activity.getResources().getStringArray(R.array.arr_menu_names);
                    }
                    else
                    {
                        TypedArray ar = activity.getResources().obtainTypedArray(R.array.arr_menu_img_res_ids);
                        int len = ar.length();
                        resIds = new int[1];
                        for (int i = 0; i < 1; i++)
                            resIds[i] = ar.getResourceId(i, 0);
                        ar.recycle();
                        menuNames = new String[]{activity.getResources().getStringArray(R.array.arr_menu_names)[0]};
                    }
                    dialog = new EasyDialog(getActivity());
                    final View view1 = getActivity().getLayoutInflater().inflate(R.layout.activity_menu_dialog_list, null);
                    view1.setLayoutParams(new RelativeLayout.LayoutParams(Helper.getDeviceWidth(getActivity()) / 2
                            , ViewGroup.LayoutParams.WRAP_CONTENT));
                    final ListView menuList = (ListView) view1.findViewById(R.id.menuList);
                    MenuDialogListViewAdapter menuListViewAdapter = new MenuDialogListViewAdapter(getActivity(), DataHelper.checkLike(activity, media1.id, user.id), resIds, menuNames);
                    menuList.setAdapter(menuListViewAdapter);

                    menuList.setOnItemClickListener(new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int dialogPosition, long id)
                        {
                            if (dialogPosition == 0)
                            {
                                // info
                                Intent mediaDetails = new Intent(getActivity(), MediaDetailActivity.class);
                                mediaDetails.putExtra(EXTRA_CHANNEL, channel);
                                mediaDetails.putExtra(EXTRA_MEDIA, media1);
                                mediaDetails.putExtra(EXTRA_VIEW, isGrid);
                                mediaDetails.putExtra(EXTRA_PARENT, parentId);
                                // TODO: 10-Jan-17 Transmission Animation
                                Pair<View, String> pair1 = Pair.create(recyclerView.findViewHolderForLayoutPosition(position).itemView.findViewById(R.id.imageIcon), getString(R.string.text_transition_animation_media_image));
                                Pair<View, String> pair2 = Pair.create(recyclerView.findViewHolderForLayoutPosition(position).itemView.findViewById(R.id.labelName), getString(R.string.text_transition_animation_media_title));
                                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, pair1, pair2);
                                startActivityForResult(mediaDetails, REQ_IS_MEDIA_DELETED, optionsCompat.toBundle());

                            }
                            else if (dialogPosition == 1)
                            {
                                // rate
                                if (prefManager.isDemoLogin())
                                {
                                    Helper.demoUserDialog(context);
                                }
                                else
                                {
                                    if (DataHelper.checkLike(activity, media1.id, user.id))
                                    {
                                        notifySimple(getString(R.string.msg_it_already_like_by_you));
                                    }
                                    else
                                    {
                                        if (Helper.isNetworkAvailable(context))
                                        {
                                            Helper.IS_DIALOG_SHOW = false;
                                            postNetworkRequest(REQUEST_LIKE, DataProvider.ENDPOINT_FILE, DataProvider.Actions.MEDIA_LIKE,
                                                    RequestParameter.urlEncoded("ChannelId", String.valueOf(channel.id)),
                                                    RequestParameter.urlEncoded("UserId", String.valueOf(PreferenceHelper.getUserContext(context))),
                                                    RequestParameter.urlEncoded("MediaId", String.valueOf(media1.id)));
                                        }
                                        else
                                        {
                                            Helper.storeLikeOffline(activity, media1);
                                        }
                                    }
                                }
                            }
                            else if (dialogPosition == 2)
                            {
                                // comment
                                Intent userCommentIntent = new Intent(context, UserCommentViewActivity.class);
                                userCommentIntent.putExtra(MediaFragment.EXTRA_MEDIA, media1);
                                userCommentIntent.putExtra(MediaFragment.EXTRA_CHANNEL, channel);
                                startActivityForResult(userCommentIntent, COMMENT_ACTIVITY_REQUEST_CODE);

                            }
                            else if (dialogPosition == 3)
                            {
                                // share
                                user = new User();
                                user.id = PreferenceHelper.getUserContext(context);
                                Intent sharingDetails = new Intent(getActivity(), ShareActivity.class);
                                sharingDetails.putExtra(EXTRA_CHANNEL, channel);
                                sharingDetails.putExtra(EXTRA_MEDIA, media1);
                                sharingDetails.putExtra(EXTRA_VIEW, isGrid);
                                sharingDetails.putExtra(EXTRA_PARENT, parentId);
                                startActivity(sharingDetails);
                            }
                            dialog.dismiss();
                        }
                    });
                    dialog.setLayout(view1)
                            .setGravity(EasyDialog.GRAVITY_BOTTOM)
                            .setBackgroundColor(getActivity().getResources().getColor(R.color.white))
                            .setLocationByAttachedView(view)
                            .setTouchOutsideDismiss(true)
                            .setMatchParent(false)
                            .show();
                    DataHelper.updateMedia(context, arrayListMedia.get(position));
                    media1.isDownloading = arrayListMedia.get(position).isDownloading;
                    media1.isDownloaded = arrayListMedia.get(position).isDownloaded;
                }
            }

        };
        mediaAdapter.setClickListener(clickListener);
    }


    @Override
    protected void initializeComponents()
    {
        super.initializeComponents();
        Bundle arguments = getArguments();

        if (arguments != null)
        {
            channel = arguments.getParcelable(EXTRA_CHANNEL);
            isGrid = arguments.getBoolean(EXTRA_VIEW);
            parentId = arguments.getInt(EXTRA_PARENT);
            if (!(parentId == -1))
            {
                mediaItem = arguments.getParcelable(EXTRA_MEDIA);

            }
            else
            {
                Media channelAsMedia = new Media();
                channelAsMedia.id = channel.id;
                channelAsMedia.name = channel.name;
                channelAsMedia.icon = channel.thumbnail;

            }

        }
        recyclerView = (RecyclerView) findViewById(R.id.listChannels);
        Glide.with(activity)
                .load(channel.thumbnail)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<Bitmap>()
                {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation)
                    {
                        recyclerView.setBackground(new BitmapDrawable(getResources(), bitmap));

                    }
                });

        if (Helper.getScreenOrientation(activity) == 1)
        {
            // Portrait Mode
            if (Helper.isTablet(activity))
            {
                GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
                recyclerView.setLayoutManager(layoutManager);
            }
            else
            {
                GridLayoutManager layoutManager = new GridLayoutManager(context, 1);
                recyclerView.setLayoutManager(layoutManager);
            }


        }
        else
        {
            // Landscape Mode
            if (Helper.isTablet(activity))
            {
                GridLayoutManager layoutManager = new GridLayoutManager(context, 3);
                recyclerView.setLayoutManager(layoutManager);
            }
            else
            {
                GridLayoutManager layoutManager = new GridLayoutManager(context, 1);
                recyclerView.setLayoutManager(layoutManager);
            }

        }


        arrayListMedia = new ArrayList<>();
        mediaAdapter = new MediaAdapter(activity, arrayListMedia, this, recyclerView);
        mediaAdapter.setGrid(isGrid);
        recyclerView.setAdapter(mediaAdapter);
        updateMediaList();

        user = new User();
        user.id = PreferenceHelper.getUserContext(context);
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
                recyclerView.setLayoutManager(layoutManager);
            }
            else
            {
                GridLayoutManager layoutManager = new GridLayoutManager(context, 1);
                recyclerView.setLayoutManager(layoutManager);
            }
        }
        else
        {
            // Landscape Mode
            if (Helper.isTablet(activity))
            {
                GridLayoutManager layoutManager = new GridLayoutManager(context, 3);
                recyclerView.setLayoutManager(layoutManager);
            }
            else
            {
                GridLayoutManager layoutManager = new GridLayoutManager(context, 1);
                recyclerView.setLayoutManager(layoutManager);
            }
        }
    }

    public void updateMediaList()
    {
        try
        {
            arrayListMedia.clear();
            if (parentId == -1)
            {
                DataHelper.getMediaListFromParent(context, channel.id, arrayListMedia);
                Log.e("", " arrayListMedia size : " + arrayListMedia.size());
            }
            else
            {
                DataHelper.getMediaListFromChannelId(context, parentId, arrayListMedia);
                Log.e("", " arrayListMedia size : " + arrayListMedia.size());
            }
            if(((SMACCloudApplication) activity.getApplication()).arrayListMediaTemp.size() > 0){
                for (int i=0;i<((SMACCloudApplication) activity.getApplication()).arrayListMediaTemp.size();i++){
                    for (int j=0;j<arrayListMedia.size();j++){
                        if(((SMACCloudApplication) activity.getApplication()).arrayListMediaTemp.get(i).isDownloading ==1 &&
                                ((SMACCloudApplication) activity.getApplication()).arrayListMediaTemp.get(i).id == arrayListMedia.get(j).id){
                            arrayListMedia.set(j,((SMACCloudApplication) activity.getApplication()).arrayListMediaTemp.get(i));
                        }
                    }
                }
            }
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
            if (activity.getSupportActionBar() != null)
            {
                if (fragmentManager.getBackStackEntryCount() == 1)
                {
                    activity.getSupportActionBar().setTitle(channel.name);
                }
                else
                {
                    /*Bundle arguments = getArguments();
                    if (arguments != null && arguments.containsKey(EXTRA_MEDIA))
                    {
                        Media tempMediaItem = arguments.getParcelable(EXTRA_MEDIA);
                        activity.getSupportActionBar().setTitle(tempMediaItem.name);
                    }*/
                    activity.getSupportActionBar().setTitle(mediaItem.name);
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        super.onResume();
    }


    @Override
    public void processFinish(String output)
    {
        if (mediaAdapter.dialog != null && mediaAdapter.dialog.isShowing())
            mediaAdapter.dialog.dismiss();
        mediaAdapter.notifyDataSetChanged();
    }

    public void callCommentService(String commentText)
    {
        postNetworkRequest(REQUEST_COMMENT, DataProvider.ENDPOINT_FILE, DataProvider.Actions.MEDIA_COMMENT,
                RequestParameter.urlEncoded("ChannelId", String.valueOf(channel.id)),
                RequestParameter.urlEncoded("UserId", String.valueOf(PreferenceHelper.getUserContext(context))),
                RequestParameter.urlEncoded("MediaId", String.valueOf(media1.id)),
                RequestParameter.urlEncoded("Comment", commentText));
    }

    @Override
    protected void onNetworkResponse(int requestCode, boolean status, String response)
    {
        super.onNetworkResponse(requestCode, status, response);
        if (requestCode == REQUEST_LIKE)
        {
            if (status)
            {
                try
                {
                    JSONObject responseJson = new JSONObject(response);
                    int requestStatus = responseJson.optInt("Status");
                    if (requestStatus > 0)
                    {
                        Helper.storeLikeOffline(activity, media1);
                        if (responseJson.has("Message") && !responseJson.isNull("Message") && !responseJson.optString("Message").equalsIgnoreCase("null"))
                            notifySimple(DataHelper.getLocalizationMessageFromCode(context, String.valueOf(requestStatus), LOCALIZATION_TYPE_ERROR_CODE));
                        else
                        {
                            // TODO: 16-Jun-17 Custom message
                            notifySimple(getString(R.string.msg_please_try_again_later));
                        }
                    }
                    else
                    {

                        JSONObject userLikeJson = responseJson.optJSONObject("Payload");
                        UserLike userLike = new UserLike();
                        if (userLikeJson != null)
                        {
                            UserLike.parseFromJson(userLikeJson, userLike);
                            if (userLike.userId > 0)
                                userLike.add(context);
                        }
                        else if (responseJson.has("Message") && !responseJson.isNull("Message") && !responseJson.optString("Message").equalsIgnoreCase("null"))
                        {
                            if (responseJson.optString("Message").equalsIgnoreCase(DataProvider.Messages.USERLIKE_OBJECT_IS_EMPTY))
                            {
                                userLike.isSynced = 1;
                                userLike.associatedId = media1.id;
                                userLike.userId = PreferenceHelper.getUserContext(context);
                                DataHelper.addUserLikes(context, userLike);
                            }
                        }
                        Log.e("", " arrayListMedia size after change : " + arrayListMedia.size());
                        updateMediaList();
                        mediaAdapter.notifyDataSetChanged();
                    }
                }
                catch (JSONException | ParseException e)
                {
                    notifySimple(getString(R.string.msg_invalid_response_from_server));
                }
            }
            else
            {
                notifySimple(getString(R.string.msg_cannot_complete_request));
            }
        }
        else if (requestCode == REQUEST_COMMENT)
        {
            if (status)
            {
                try
                {
                    JSONObject responseJson = new JSONObject(response);
                    int requestStatus = responseJson.optInt("Status");
                    if (requestStatus > 0)
                    {
                        Helper.storeCommentOffline(activity, media1, commentDialog.edtMediaComment.getText().toString());
                        if (responseJson.has("Message") && !responseJson.isNull("Message") && !responseJson.optString("Message").equalsIgnoreCase("null"))
                            notifySimple(DataHelper.getLocalizationMessageFromCode(context, String.valueOf(requestStatus), LOCALIZATION_TYPE_ERROR_CODE));
                        else
                        {
                            // TODO: 16-Jun-17 Custom message
                            notifySimple(getString(R.string.msg_please_try_again_later));
                        }
                        if (commentDialog != null && commentDialog.isShowing())
                            commentDialog.dismiss();
                    }
                    else
                    {
                        JSONObject userCommentJson = responseJson.optJSONObject("Payload");
                        UserComment userComment = new UserComment();
                        if (userCommentJson != null)
                        {
                            UserComment.parseFromJSon(userCommentJson, userComment);
                            if (userComment.userId > 0)
                                userComment.add(context);
                        }
                        else if (responseJson.has("Message") && !responseJson.isNull("Message") && !responseJson.optString("Message").equalsIgnoreCase("null"))
                        {
                            /*if (responseJson.optString("Message").equalsIgnoreCase(DataProvider.Messages.USERLIKE_OBJECT_IS_EMPTY))
                            {
                                userComment.isSynced = 1;
                                userComment.associatedId = arrayListMedia.id;
                                userComment.userId = PreferenceHelper.getUserContext(context);
                                DataHelper.addUserLikes(context, userLike);
                            }*/
                        }
                        if (commentDialog != null && commentDialog.isShowing())
                            commentDialog.dismiss();
                    }
                }
                catch (JSONException | ParseException e)
                {
                    notifySimple(getString(R.string.msg_invalid_response_from_server));
                }
            }
            else
            {
                notifySimple(getString(R.string.msg_cannot_complete_request));
            }
        }
        Helper.IS_DIALOG_SHOW = true;
    }

    @Override
    public void onItemClick(int pos, int itemPos)
    {

        media1 = arrayListMedia.get(itemPos);
        if (pos == 1)
        {
            // info
            Intent mediaDetails = new Intent(getActivity(), MediaDetailActivity.class);
            mediaDetails.putExtra(EXTRA_CHANNEL, channel);
            mediaDetails.putExtra(EXTRA_MEDIA, media1);
            mediaDetails.putExtra(EXTRA_VIEW, isGrid);
            mediaDetails.putExtra(EXTRA_PARENT, parentId);
            // TODO: 10-Jan-17 Transmission Animation
            Pair<View, String> pair1 = Pair.create(recyclerView.findViewHolderForLayoutPosition(itemPos).itemView.findViewById(R.id.imageIcon), getString(R.string.text_transition_animation_media_image));
            Pair<View, String> pair2 = Pair.create(recyclerView.findViewHolderForLayoutPosition(itemPos).itemView.findViewById(R.id.labelName), getString(R.string.text_transition_animation_media_title));
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, pair1, pair2);
            startActivityForResult(mediaDetails, REQ_IS_MEDIA_DELETED, optionsCompat.toBundle());
        }
        else if (pos == 2)
        {
            // share
            user = new User();
            user.id = PreferenceHelper.getUserContext(context);
            Intent sharingDetails = new Intent(getActivity(), ShareActivity.class);
            sharingDetails.putExtra(EXTRA_CHANNEL, channel);
            sharingDetails.putExtra(EXTRA_MEDIA, media1);
            sharingDetails.putExtra(EXTRA_VIEW, isGrid);
            sharingDetails.putExtra(EXTRA_PARENT, parentId);
            startActivity(sharingDetails);
        }
        else if (pos == 3)
        {
            startUserCommentViewActivity();
        }
        else if (pos == 4)
        {
            // rate
            if (prefManager.isDemoLogin())
            {
                Helper.demoUserDialog(context);
            }
            else
            {
                if (DataHelper.checkLike(activity, media1.id, user.id))
                {
                    notifySimple(getString(R.string.msg_it_already_like_by_you));
                }
                else
                {
                    if (Helper.isNetworkAvailable(context))
                    {
                        Helper.IS_DIALOG_SHOW = false;
                        postNetworkRequest(REQUEST_LIKE, DataProvider.ENDPOINT_FILE, DataProvider.Actions.MEDIA_LIKE,
                                RequestParameter.urlEncoded("ChannelId", String.valueOf(channel.id)),
                                RequestParameter.urlEncoded("UserId", String.valueOf(PreferenceHelper.getUserContext(context))),
                                RequestParameter.urlEncoded("MediaId", String.valueOf(media1.id)));
                    }
                    else
                    {
                        Helper.storeLikeOffline(activity, media1);
                    }
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            //if (data != null && data.hasExtra(KEY_MEDIA_DETAIL_IS_DELETED) && data.getBooleanExtra(KEY_MEDIA_DETAIL_IS_DELETED, false)) {
            updateMediaList();
            mediaAdapter.notifyDataSetChanged();
            //}
        }
    }

    private void startUserCommentViewActivity()
    {
        Intent userCommentIntent = new Intent(context, UserCommentViewActivity.class);
        userCommentIntent.putExtra(MediaFragment.EXTRA_MEDIA, media1);
        userCommentIntent.putExtra(MediaFragment.EXTRA_CHANNEL, channel);
        startActivityForResult(userCommentIntent, COMMENT_ACTIVITY_REQUEST_CODE);
    }

    public void refreshAdapter()
    {
        if (mediaAdapter != null)
        {
            mediaAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser)
        {
            if (mediaAdapter != null)
            {
                mediaAdapter.notifyDataSetChanged();
            }
        }
        else
        {
        }

    }
}
