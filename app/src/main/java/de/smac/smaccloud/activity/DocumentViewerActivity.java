package de.smac.smaccloud.activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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

/**
 * User to view PDF files
 */
public class DocumentViewerActivity extends Activity implements View.OnClickListener
{
    public static final String EXTRA_MEDIA = "extraMedia";
    public static int COMMENT_ACTIVITY_REQUEST_CODE = 1001;
    PDFView pdfView;
    TextView textPageIndicator;
    Media media;
    Bundle extras;
    File filePdf;
    Boolean checkLike;
    LinearLayout btn_like;
    PreferenceHelper prefManager;
    LinearLayout btn_comment;
    LinearLayout btn_attach;
    LinearLayout btn_info;
    LinearLayout btn_done;
    RelativeLayout parentLayout;
    ImageView img_like;
    ImageView img_comment;
    ImageView img_attach;
    ImageView img_info;
    Channel channel;
    UserCommentDialog commentDialog;
    LinearLayout linearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        prefManager = new PreferenceHelper(context);
        setContentView(R.layout.activity_documentviewer);
        Helper.retainOrientation(DocumentViewerActivity.this);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(PreferenceHelper.getAppBackColor(context))));
        }

    }

    @Override
    protected void initializeComponents()
    {
        super.initializeComponents();
        pdfView = (PDFView) findViewById(R.id.pdfview);
        textPageIndicator = (TextView) findViewById(R.id.textPageIndicator);
        extras = getIntent().getExtras();
        if (extras != null)
        {
            if (extras.containsKey(EXTRA_MEDIA))
            {
                media = extras.getParcelable(EXTRA_MEDIA);
                setTitle(media.name);
            }
        }
        filePdf = new File("" + getFilesDir() + "/" + media.id);

        btn_like = (LinearLayout) findViewById(R.id.btn_like);
        btn_comment = (LinearLayout) findViewById(R.id.btn_comment);
        btn_attach = (LinearLayout) findViewById(R.id.btn_attach);
        btn_info = (LinearLayout) findViewById(R.id.btn_info);
        btn_done = (LinearLayout) findViewById(R.id.btn_done);
        linearLayout = (LinearLayout) findViewById(R.id.linear_pdf_parent);

        img_like = (ImageView) findViewById(R.id.img_like);
        img_comment = (ImageView) findViewById(R.id.img_comment);
        img_attach = (ImageView) findViewById(R.id.img_attach);
        img_info = (ImageView) findViewById(R.id.img_info);

        // img_like.setColorFilter(Color.parseColor(PreferenceHelper.getAppColor(context)));
      /*  img_attach.setColorFilter(Color.parseColor(PreferenceHelper.getAppColor(context)));
        img_comment.setColorFilter(Color.parseColor(PreferenceHelper.getAppColor(context)));*/

        parentLayout = (RelativeLayout) findViewById(R.id.parentLayout);
        Helper.setupTypeface(parentLayout, Helper.robotoRegularTypeface);

        btn_like.setOnClickListener(this);
        btn_comment.setOnClickListener(this);
        btn_attach.setOnClickListener(this);
        btn_info.setOnClickListener(this);
        btn_done.setOnClickListener(this);

        checkLike = DataHelper.checkLike(context, media.id, PreferenceHelper.getUserContext(context));
        if (checkLike)
            img_like.setBackground(getResources().getDrawable(R.drawable.ic_like));
    }

    @Override
    protected void bindEvents()
    {
        super.bindEvents();
        try
        {
            pdfView.fromFile(filePdf)
                    .onPageChange(new OnPageChangeListener()
                    {
                        @Override
                        public void onPageChanged(int page, int pageCount)
                        {
                            page = page + 1;
                            textPageIndicator.setText(page + "/" + pageCount);
                        }
                    })

                    //.defaultPage(0)
                    .swipeHorizontal(true)
                    .enableDoubletap(true)
                    .enableSwipe(true)
                    //.onTap(onTapListener)
                    .enableAnnotationRendering(false)
                    .load();
        }
        catch (Exception ex)
        {
            Toast.makeText(DocumentViewerActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
            finish();
        }
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
                onBackPressed();
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
                Intent mediaDetails = new Intent(DocumentViewerActivity.this, MediaDetailActivity.class);
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
                Pair<View, String> pair1 = Pair.create(findViewById(R.id.pdfview), getString(R.string.text_transition_animation_media_image));
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(DocumentViewerActivity.this, pair1);
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
                            {
                                img_like.setBackground(getResources().getDrawable(R.drawable.ic_like));
                                userLike.add(context);
                            }
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
}
