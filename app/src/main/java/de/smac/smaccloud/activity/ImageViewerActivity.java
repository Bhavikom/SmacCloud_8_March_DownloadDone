package de.smac.smaccloud.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;

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
import de.smac.smaccloud.widgets.EnableDisableViewPager;
import de.smac.smaccloud.widgets.TouchImageView;
import de.smac.smaccloud.widgets.UserCommentDialog;

import static de.smac.smaccloud.activity.MediaActivity.REQUEST_COMMENT;
import static de.smac.smaccloud.activity.MediaActivity.REQUEST_LIKE;
import static de.smac.smaccloud.base.Helper.LOCALIZATION_TYPE_ERROR_CODE;
import static de.smac.smaccloud.fragment.MediaFragment.BROADCAST_MEDIA_DOWNLOAD_COMPLETE;
import static de.smac.smaccloud.fragment.MediaFragment.EXTRA_CHANNEL;
import static de.smac.smaccloud.fragment.MediaFragment.EXTRA_PARENT;

public class ImageViewerActivity extends Activity implements View.OnClickListener, ViewPager.OnPageChangeListener
{
    public static final String EXTRA_MEDIA = "extraMedia";
    public static int COMMENT_ACTIVITY_REQUEST_CODE = 1001;
    public Boolean checkLike;
    public RelativeLayout parentLayout;
    public boolean isvisiblefooter = true;
    String json = "";
    //ImageViewTouch imageToDisplayFullScreen;
    Bundle extras;
    File mFolder;
    Media media;
    Channel channel;
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
    PreferenceHelper prefManager;
    ArrayList<Media> arrayListMedia;
    LinearLayout linearSectionIndicator;
    EnableDisableViewPager viewPager;
    LinearLayout layoutmediafooterOption;
    LinearLayout footerLayout, chiledLayout;
    FrameLayout frameLayout;
    private ViewPagerAdapter viewPagerAdapter;
    private int dotsCount;
    private ImageView[] imgDots;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        prefManager = new PreferenceHelper(context);
        setContentView(R.layout.activity_image_viewer);
        footerLayout = (LinearLayout) findViewById(R.id.layout_header);
        Helper.retainOrientation(ImageViewerActivity.this);

    }

    @Override
    protected void initializeComponents()
    {
        super.initializeComponents();
        arrayListMedia = new ArrayList<>();
        extras = getIntent().getExtras();
        if (extras != null)
        {
            if (extras.containsKey(EXTRA_MEDIA))
            {
                media = extras.getParcelable(EXTRA_MEDIA);
                json = extras.getString("image_arraylist");
                Gson gson = new Gson();
                Type typedValue = new TypeToken<ArrayList<Media>>()
                {
                }.getType();
                ArrayList<Media> imageList = gson.fromJson(json, typedValue);
                if (imageList != null)
                {
                    arrayListMedia.addAll(imageList);
                }
                else
                {
                    arrayListMedia.add(media);
                }
                if (media != null)
                    setTitle(media.name);
            }
        }
        linearSectionIndicator = (LinearLayout) findViewById(R.id.linear_page_indicator);
        viewPager = (EnableDisableViewPager) findViewById(R.id.viewpager);
        viewPager.requestDisallowInterceptTouchEvent(true);

        viewPagerAdapter = new ViewPagerAdapter(this, arrayListMedia);
        viewPager.setAdapter(viewPagerAdapter);

        for (int i = 0; i < arrayListMedia.size(); i++)
        {
            if (arrayListMedia.get(i).id == media.id)
            {
                viewPager.setCurrentItem(i);
            }
        }

        viewPager.setOnPageChangeListener(this);


        parentLayout = (RelativeLayout) findViewById(R.id.parentLayout);
        btn_like = (LinearLayout) findViewById(R.id.btn_like);
        btn_comment = (LinearLayout) findViewById(R.id.btn_comment);
        btn_attach = (LinearLayout) findViewById(R.id.btn_attach);
        btn_info = (LinearLayout) findViewById(R.id.btn_info);
        btn_done = (LinearLayout) findViewById(R.id.btn_done);


        btn_like.setOnClickListener(this);
        btn_comment.setOnClickListener(this);
        btn_attach.setOnClickListener(this);
        btn_info.setOnClickListener(this);
        btn_done.setOnClickListener(this);
        img_like = (ImageView) findViewById(R.id.img_like);
        img_comment = (ImageView) findViewById(R.id.img_comment);
        img_attach = (ImageView) findViewById(R.id.img_attach);
        img_info = (ImageView) findViewById(R.id.img_info);

        checkLike = DataHelper.checkLike(context, media.id, PreferenceHelper.getUserContext(context));
        if (checkLike)
            img_like.setBackground(getResources().getDrawable(R.drawable.ic_like));

    }

    @Override
    protected void bindEvents()
    {
        super.bindEvents();
        //Glide.with(context).load(mFolder).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageToDisplayFullScreen);
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
                setResult(RESULT_OK);
                finish();
                break;

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
                   // Helper.demoUserDialog(context);
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
                Intent mediaDetails = new Intent(ImageViewerActivity.this, MediaDetailActivity.class);
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
                mediaDetails.putExtra(de.smac.smaccloud.fragment.MediaFragment.EXTRA_MEDIA, media);
                mediaDetails.putExtra(EXTRA_PARENT, media.parentId);
                // TODO: 10-Jan-17 Transmission Animation
                Pair<View, String> pair1 = Pair.create(findViewById(R.id.imageFullScreen), getString(R.string.text_transition_animation_media_image));
                Pair<View, String> pair2 = Pair.create(findViewById(R.id.imageFullScreen), getString(R.string.text_transition_animation_media_title));
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(ImageViewerActivity.this, pair1, pair2);
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
            shareViewActivityIntent.putExtra(EXTRA_CHANNEL, channel);
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
                    img_like.setBackground(getResources().getDrawable(R.drawable.ic_like));
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
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
    {

    }

    @Override
    public void onPageSelected(int position)
    {
        setPageTitle(position);
        media = arrayListMedia.get(position);
        checkLike = DataHelper.checkLike(context, media.id, PreferenceHelper.getUserContext(context));
        if (checkLike)
            img_like.setBackground(getResources().getDrawable(R.drawable.ic_like));
        else
            img_like.setBackground(getResources().getDrawable(R.drawable.ic_unlike));
        //setPageIndicator();
    }

    @Override
    public void onPageScrollStateChanged(int state)
    {


    }

    private void setPageTitle(int pos)
    {
        setTitle(arrayListMedia.get(pos).name);
    }

    private void setPageIndicator()
    {
        dotsCount = viewPagerAdapter.getCount();
        imgDots = new ImageView[dotsCount];
        for (int i = 0; i < dotsCount; i++)
        {
            imgDots[i] = new ImageView(this);
            imgDots[i].setImageDrawable(getResources().getDrawable(R.drawable.dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(4, 0, 4, 0);
            linearSectionIndicator.addView(imgDots[i], params);
        }
        imgDots[0].setImageDrawable(getResources().getDrawable(R.drawable.dot_selected));
    }

    public class ViewPagerAdapter extends PagerAdapter
    {

        ArrayList<Media> arrayList;
        private Context mContext;

        public ViewPagerAdapter(Context mContext, ArrayList<Media> arrayList)
        {
            this.mContext = mContext;
            this.arrayList = arrayList;
        }

        @Override
        public int getCount()
        {
            return arrayList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object)
        {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.pager_item, container, false);
            final TouchImageView imageView = (TouchImageView) itemView.findViewById(R.id.imageFullScreen);
            mFolder = new File("" + getFilesDir() + File.separator + arrayList.get(position).id);
            if (getSupportActionBar() != null)
            {
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(PreferenceHelper.getAppBackColor(context))));
            }

            /*final Uri imageUri = Uri.parse(arrayList.get(position).icon);
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
            imageLoader.displayImage(String.valueOf(imageUri), imageView, Helper.options);*/

            if (mFolder.exists())
            {
                //Glide.with(context).load(mFolder).fitCenter().diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);

                Glide.with(context)
                        .load(mFolder)
                        .asBitmap()
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(new SimpleTarget<Bitmap>()
                        {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation)
                            {
                                imageView.setImageBitmap(resource);
                            }

                            @Override
                            public void onLoadFailed(Exception e, Drawable errorDrawable)
                            {
                                super.onLoadFailed(e, errorDrawable);
                                imageView.setImageBitmap(null);
                            }
                        });
            }
            else
            {
                final Uri imageUri = Uri.parse(arrayList.get(position).icon);
                Glide.with(context).load(imageUri).fitCenter().diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
            }
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.gravity = Gravity.CENTER;
            imageView.setLayoutParams(layoutParams);
            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            container.removeView((LinearLayout) object);
        }
    }
}
