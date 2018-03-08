package de.smac.smaccloud.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.Display;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import de.smac.smaccloud.R;
import de.smac.smaccloud.base.Activity;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.base.RequestParameter;
import de.smac.smaccloud.data.DataHelper;
import de.smac.smaccloud.fragment.MediaFragment;
import de.smac.smaccloud.helper.DataProvider;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.model.Channel;
import de.smac.smaccloud.model.Media;
import de.smac.smaccloud.model.UserComment;
import de.smac.smaccloud.model.UserLike;
import de.smac.smaccloud.widgets.UserCommentDialog;

import static de.smac.smaccloud.activity.MediaActivity.REQUEST_COMMENT;
import static de.smac.smaccloud.activity.MediaActivity.REQUEST_LIKE;
import static de.smac.smaccloud.base.Helper.LOCALIZATION_TYPE_ERROR_CODE;
import static de.smac.smaccloud.fragment.MediaFragment.BROADCAST_MEDIA_DOWNLOAD_COMPLETE;
import static de.smac.smaccloud.fragment.MediaFragment.EXTRA_CHANNEL;
import static de.smac.smaccloud.fragment.MediaFragment.EXTRA_PARENT;
import static de.smac.smaccloud.fragment.MediaFragment.FILETYPE_MP3;
import static de.smac.smaccloud.fragment.MediaFragment.FILETYPE_VIDEO_MP4;

/**
 * Created by Ssoft on 02-Mar-17.
 */
public class VideoViewerActivity extends Activity implements View.OnClickListener, MediaPlayer.OnPreparedListener, MediaController.MediaPlayerControl
{
    public static final String EXTRA_MEDIA = "extraMedia";
    public static int COMMENT_ACTIVITY_REQUEST_CODE = 1001;
    public Boolean checkLike;
    public PreferenceHelper prefManager;
    Display currentDisplay;
    SurfaceHolder surfaceHolder;
    MediaPlayer mediaPlayer;
    ImageView btn_play_pause_music;
    //AppCompatSeekBar seekBarVideo;
    TextView txtTime;
    Handler seekHandler;
    Runnable runVideo;
    int videoWidth = 0, videoHeight = 0;
    boolean readyToPlay = false;
    Bundle extras;
    File mFolder;
    Channel channel;
    Media media;
    LinearLayout btn_like;
    LinearLayout btn_comment;
    LinearLayout btn_attach;
    LinearLayout btn_info;
    LinearLayout btn_done;
    ImageView img_like;
    ImageView img_comment;
    ImageView img_attach;
    ImageView img_info;
    UserCommentDialog commentDialog;
    Bitmap bitmapMp3;
    Uri uriForPlayer;
    private MediaController mediaController;
    private Handler handler = new Handler();
    //MediaPlayer mediaPlayer;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        prefManager = new PreferenceHelper(context);
        setContentView(R.layout.activity_video_viewer);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(PreferenceHelper.getAppBackColor(context))));
        }

    }

    @Override
    protected void initializeComponents()
    {
        super.initializeComponents();
        extras = getIntent().getExtras();
        if (extras != null)
        {
            if (extras.containsKey(EXTRA_MEDIA))
            {
                media = extras.getParcelable(EXTRA_MEDIA);
                if (media != null)
                    setTitle(media.name);
            }
        }
        mFolder = new File("" + getFilesDir() + "/" + media.id);
        final File videoFile = new File("" + context.getFilesDir() + "/" + media.id);
        uriForPlayer = Uri.fromFile(videoFile);

        btn_done = (LinearLayout) findViewById(R.id.btn_done);
        btn_like = (LinearLayout) findViewById(R.id.btn_like);
        btn_comment = (LinearLayout) findViewById(R.id.btn_comment);
        btn_attach = (LinearLayout) findViewById(R.id.btn_attach);
        btn_info = (LinearLayout) findViewById(R.id.btn_info);

        btn_done.setOnClickListener(this);
        btn_like.setOnClickListener(this);
        btn_comment.setOnClickListener(this);
        btn_attach.setOnClickListener(this);
        btn_info.setOnClickListener(this);

        if (media.type.equalsIgnoreCase(FILETYPE_MP3))
        {
            playAudio(uriForPlayer);
            // for audio file
        }
        else
        {
            playVideo(uriForPlayer);
            // for video file
        }
        img_like = (ImageView) findViewById(R.id.img_like);
        img_comment = (ImageView) findViewById(R.id.img_comment);
        img_attach = (ImageView) findViewById(R.id.img_attach);
        img_info = (ImageView) findViewById(R.id.img_info);
        //   img_like.setColorFilter(Color.parseColor(PreferenceHelper.getAppColor(context)));
        /*mediaPlayer = MediaPlayer.create(context, uriForPlayer);

        surfaceView = (VideoSurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();

        surfaceHolder.addCallback(this);
        surfaceView.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Log.d("UiVisibility", "onClick");
                hideUI();
            }
        });
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
        {
            @Override
            public void onSystemUiVisibilityChange(int visibility)
            {
                // Note that system bars will only be "visible" if none of the
                // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
                if ((visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0)
                {
                    // The system bars are visible. Make any desired
                    // adjustments to your UI
                    VideoViewerActivity.this.findViewById(R.id.layout_progress).setVisibility(View.VISIBLE);
                    VideoViewerActivity.this.findViewById(R.id.layout_header).setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

                }
                else
                {
                    // The system bars are NOT visible. Make any desired
                    // adjustments to your UI.
                    VideoViewerActivity.this.findViewById(R.id.layout_progress).setVisibility(View.GONE);
                    VideoViewerActivity.this.findViewById(R.id.layout_header).setVisibility(View.GONE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

                }
            }
        });
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        if (media.type.split("/")[0].equalsIgnoreCase("audio") || media.type.split("/")[1].equalsIgnoreCase("audio"))
        {

            bitmapMp3 = Helper.getBitmapFromURI(this, uriForPlayer);
            if (bitmapMp3 != null)
            {
                Drawable drawable = new BitmapDrawable(getResources(), bitmapMp3);
                surfaceView.setBackground(drawable);
            }
        }
        btn_play_pause_music = (ImageView) findViewById(R.id.btn_play_pause_music);
        seekBarVideo = (AppCompatSeekBar) findViewById(R.id.seekBarVideo);
        txtTime = (TextView) findViewById(R.id.txtTime);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnVideoSizeChangedListener(this);
        mediaPlayer.setScreenOnWhilePlaying(true);
        String filePath = Environment.getExternalStorageDirectory().getPath()
                + "/Test.m4v";

        try
        {
            mediaPlayer.setDataSource("" + context.getFilesDir() + "/" + media.id);
        }
        catch (Exception e)
        {
            Log.v("TEST", e.getMessage());
        }
        currentDisplay = getWindow().getWindowManager().getDefaultDisplay();

        seekBarVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if (fromUser)
                {
                    mediaPlayer.seekTo(progress);
                    seekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });

        seekHandler = new Handler();
        runVideo = new Runnable()
        {
            @Override
            public void run()
            {
                if (mediaPlayer != null && mediaPlayer.isPlaying())
                {
                    if (mediaPlayer.getCurrentPosition() < mediaPlayer.getDuration())
                    {
                        seekBarVideo.setProgress(mediaPlayer.getCurrentPosition());
                        txtTime.setText(DateUtils.formatElapsedTime(mediaPlayer.getCurrentPosition() / 1000) + " / " + DateUtils.formatElapsedTime(mediaPlayer.getDuration() / 1000));
                        seekHandler.postDelayed(this, 100);
                    }
                    else
                    {
                        if (mediaPlayer.isPlaying())
                        {
                            mediaPlayer.pause();
                            seekBarVideo.setProgress(0);
                            btn_play_pause_music.setImageResource(R.drawable.ic_play);
                            surfaceView.destroyDrawingCache();
                        }
                    }
                }

            }
        };

        btn_play_pause_music.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (mediaPlayer.isPlaying())
                {
                    mediaPlayer.pause();
                    btn_play_pause_music.setImageResource(R.drawable.ic_play);
                }
                else
                {
                    mediaPlayer.start();
                    btn_play_pause_music.setImageResource(R.drawable.ic_pause);
                    seekHandler.postDelayed(runVideo, 100);
                }
            }
        });


        *//* End Surface View*//*

        btn_done = (LinearLayout) findViewById(R.id.btn_done);
        btn_like = (LinearLayout) findViewById(R.id.btn_like);
        btn_comment = (LinearLayout) findViewById(R.id.btn_comment);
        btn_attach = (LinearLayout) findViewById(R.id.btn_attach);
        btn_info = (LinearLayout) findViewById(R.id.btn_info);

        btn_done.setOnClickListener(this);
        btn_like.setOnClickListener(this);
        btn_comment.setOnClickListener(this);
        btn_attach.setOnClickListener(this);
        btn_info.setOnClickListener(this);




        checkLike = DataHelper.checkLike(context, media.id, PreferenceHelper.getUserContext(context));
        if (checkLike)
            img_like.setBackground(getResources().getDrawable(R.drawable.ic_star_white));*/

    }

    public void playAudio(Uri uri)
    {
        //set up MediaPlayer
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        mediaController = new MediaController(this);
        RelativeLayout relative = (RelativeLayout) findViewById(R.id.mainview);
        bitmapMp3 = Helper.getBitmapFromURI(this, uriForPlayer);
        if (bitmapMp3 != null)
        {
            Drawable drawable = new BitmapDrawable(getResources(), bitmapMp3);
            relative.setBackground(drawable);
        }
        try
        {
            mediaPlayer.setDataSource(String.valueOf(uri));
            mediaPlayer.prepare();
            mediaPlayer.start();
        }
        catch (IOException e)
        {
            // Log.e(TAG, "Could not open file " + audioFile + " for playback.", e);
        }

        /*try {
            mp.setDataSource("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3");
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            mp.prepare();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mp.start();*/
    }

    public void playVideo(Uri uri)
    {
        //videoView = (VideoView) findViewById(R.id.videoview);
        RelativeLayout relative = (RelativeLayout) findViewById(R.id.mainview);

        videoView = new VideoView(this);
        RelativeLayout.LayoutParams parmas = new RelativeLayout.LayoutParams
                (RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        videoView.setLayoutParams(parmas);

        relative.addView(videoView);

        // Set the media controller buttons
        if (mediaController == null)
        {
            mediaController = new MediaController(VideoViewerActivity.this);


            // Set the videoView that acts as the anchor for the MediaController.
            mediaController.setAnchorView(videoView);


            // Set MediaController for VideoView
            videoView.setMediaController(mediaController);
        }

        Uri video = Uri.parse(String.valueOf(uri));
        videoView.setVideoURI(video);
        //videoView.setBackgroundResource(R.mipmap.ic_launcher);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
        {
            @Override
            public void onPrepared(MediaPlayer mp)
            {
                mp.setLooping(true);
                videoView.start();
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        //resizeSurfaceView(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
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

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_done:
            {
                onBackPressed();
                break;
            }
            case R.id.btn_like:
                if (prefManager.isDemoLogin())
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(getString(R.string.disable_like_title));
                    builder.setMessage(getString(R.string.disable_like_message));
                    builder.setPositiveButton(getString(R.string.ok),
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    dialog.dismiss();
                                }
                            });

                    AlertDialog dialog = builder.create();

                    dialog.show();
                    //Helper.demoUserDialog(context);
                }
                else
                {
                    if (DataHelper.checkLike(this, media.id, PreferenceHelper.getUserContext(this)))
                    {
                        notifySimple(getString(R.string.msg_it_already_like_by_you));
                    }
                    else
                    {
                        if (Helper.isNetworkAvailable(context))
                        {
                            Helper.IS_DIALOG_SHOW = false;
                            postNetworkRequest(REQUEST_LIKE, DataProvider.ENDPOINT_FILE, DataProvider.Actions.MEDIA_LIKE,
                                    RequestParameter.urlEncoded("ChannelId", String.valueOf(DataHelper.getChannelId(context, media.id))),
                                    RequestParameter.urlEncoded("UserId", String.valueOf(PreferenceHelper.getUserContext(context))),
                                    RequestParameter.urlEncoded("MediaId", String.valueOf(media.id)), RequestParameter.urlEncoded("Org_Id", String.valueOf(PreferenceHelper.getOrganizationId(context))));
                        }
                        else
                        {
                            Helper.storeLikeOffline(this, media);
                        }
                    }
                }
                break;
            case R.id.btn_comment:
                Intent userCommentIntent = new Intent(context, UserCommentViewActivity.class);
                userCommentIntent.putExtra(MediaFragment.EXTRA_MEDIA, media);
                userCommentIntent.putExtra(MediaFragment.EXTRA_CHANNEL, channel);
                startActivityForResult(userCommentIntent, COMMENT_ACTIVITY_REQUEST_CODE);
                break;
            case R.id.btn_attach:
                startUserShareViewActivity();
                break;
            case R.id.btn_info:
                Intent mediaDetails = new Intent(VideoViewerActivity.this, MediaDetailActivity.class);
                Channel channel = new Channel();
                try
                {
                    channel.id = DataHelper.getChannelIdFromMediaID(context, media.id);
                    DataHelper.getChannel(context, channel);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
                mediaDetails.putExtra(EXTRA_CHANNEL, channel);
                mediaDetails.putExtra(MediaFragment.EXTRA_MEDIA, media);
                mediaDetails.putExtra(EXTRA_PARENT, media.parentId);
                // TODO: 10-Jan-17 Transmission Animation
                Pair<View, String> pair1 = Pair.create(findViewById(R.id.surfaceView), getString(R.string.text_transition_animation_media_image));
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(VideoViewerActivity.this, pair1);
                startActivity(mediaDetails, optionsCompat.toBundle());
                break;
        }
    }

    private void startUserShareViewActivity()
    {
        try
        {
            Channel channel = new Channel();
            channel.id = DataHelper.getChannelIdFromMediaID(context, media.id);
            DataHelper.getChannel(context, channel);
            Intent shareViewActivityIntent = new Intent(context, ShareActivity.class);
            shareViewActivityIntent.putExtra(MediaFragment.EXTRA_MEDIA, media);
            shareViewActivityIntent.putExtra(MediaFragment.EXTRA_CHANNEL, channel);
            startActivity(shareViewActivityIntent);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void callCommentService(String commentText)
    {
        postNetworkRequest(REQUEST_COMMENT, DataProvider.ENDPOINT_FILE, DataProvider.Actions.MEDIA_COMMENT,
                RequestParameter.urlEncoded("ChannelId", String.valueOf(DataHelper.getChannelIdFromMediaID(this, media.id))),
                RequestParameter.urlEncoded("UserId", String.valueOf(PreferenceHelper.getUserContext(context))),
                RequestParameter.urlEncoded("MediaId", String.valueOf(media.id)),
                RequestParameter.urlEncoded("Comment", commentText), RequestParameter.urlEncoded("Org_Id", String.valueOf(PreferenceHelper.getOrganizationId(context))));
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
                        Helper.storeLikeOffline(this, media);
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
                                userLike.associatedId = media.id;
                                userLike.userId = PreferenceHelper.getUserContext(context);
                                DataHelper.addUserLikes(context, userLike);
                            }
                        }
                    }
                    img_like.setBackground(getResources().getDrawable(R.drawable.ic_like));

                    context.sendBroadcast(new Intent(BROADCAST_MEDIA_DOWNLOAD_COMPLETE));
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
                        Helper.storeCommentOffline(this, media, commentDialog.edtMediaComment.getText().toString());
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
                                userComment.associatedId = media.id;
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
    protected void onPause()
    {
        super.onPause();
        if (media.type.equalsIgnoreCase(FILETYPE_VIDEO_MP4))
        {
            if (videoView != null)
            {
                if (videoView.isPlaying())
                {
                    //btn_play_pause_music.setImageResource(R.drawable.ic_pause);
                    //seekBarVideo.setProgress(mediaPlayer.getCurrentPosition());
                    videoView.pause();
                }
            }
        }
    }

    /*@Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (media.type.equalsIgnoreCase(FILETYPE_VIDEO_MP4))
        {
            if(videoView != null) {
                if (videoView.isPlaying()) {
                    //btn_play_pause_music.setImageResource(R.drawable.ic_pause);
                    //seekBarVideo.setProgress(mediaPlayer.getCurrentPosition());
                    videoView.stopPlayback();
                }
            }
        }else {
            if(mediaPlayer != null) {
                if (mediaPlayer.isPlaying())
                    mediaPlayer.stop();
            }
        }
    }*/
    @Override
    protected void onResume()
    {
        super.onResume();
        //seekBarVideo.setProgress(mediaPlayer.getCurrentPosition());
        if (videoView != null)
        {
            if (!videoView.isPlaying())
                videoView.start();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (videoView != null && videoView.isPlaying())
        {
            //btn_play_pause_music.setImageResource(R.drawable.ic_pause);
            //seekBarVideo.setProgress(mediaPlayer.getCurrentPosition());
            videoView.pause();
        }
        if (mediaPlayer != null && mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        //the MediaController will hide after 3 seconds - tap the screen to make it appear again
        if (mediaController != null)
        {
            mediaController.show();
        }
        return false;
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        if (mediaController != null)
        {
            mediaController.hide();
        }
        if (mediaPlayer != null)
        {
            //mediaPlayer.stop();
            //mediaPlayer.release();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp)
    {
        //Log.d(TAG, "onPrepared");
        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(findViewById(R.id.mainview));

        handler.post(new Runnable()
        {
            public void run()
            {
                mediaController.setEnabled(true);
                mediaController.show();
            }
        });
    }

    @Override
    public void start()
    {
        if (mediaPlayer != null)
        {
            mediaPlayer.start();
        }
    }

    @Override
    public void pause()
    {
        if (mediaPlayer != null)
        {
            mediaPlayer.pause();
        }
    }

    @Override
    public int getDuration()
    {
        if (mediaPlayer != null)
        {
            return mediaPlayer.getDuration();
        }
        else
        {
            return 0;
        }
    }

    @Override
    public int getCurrentPosition()
    {
        if (mediaPlayer != null)
        {
            return mediaPlayer.getCurrentPosition();
        }
        else
        {
            return 0;
        }
    }

    @Override
    public void seekTo(int pos)
    {
        if (mediaPlayer != null)
        {
            mediaPlayer.seekTo(pos);
        }
    }

    @Override
    public boolean isPlaying()
    {
        return mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage()
    {
        return 0;
    }

    @Override
    public boolean canPause()
    {
        return true;
    }

    @Override
    public boolean canSeekBackward()
    {
        return true;
    }

    @Override
    public boolean canSeekForward()
    {
        return true;
    }

    @Override
    public int getAudioSessionId()
    {
        return 0;
    }
}
