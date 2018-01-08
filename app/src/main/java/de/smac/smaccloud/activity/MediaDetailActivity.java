package de.smac.smaccloud.activity;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import de.smac.smaccloud.R;
import de.smac.smaccloud.base.Activity;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.base.NetworkRequest;
import de.smac.smaccloud.base.NetworkResponse;
import de.smac.smaccloud.base.NetworkService;
import de.smac.smaccloud.base.RequestParameter;
import de.smac.smaccloud.data.DataHelper;
import de.smac.smaccloud.fragment.MediaFragment;
import de.smac.smaccloud.helper.DataProvider;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.model.Channel;
import de.smac.smaccloud.model.Media;
import de.smac.smaccloud.model.MediaVersion;
import de.smac.smaccloud.model.User;
import de.smac.smaccloud.service.DownloadFileFromURL;

import static de.smac.smaccloud.base.NetworkService.KEY_AUTHORIZATION;

public class MediaDetailActivity extends Activity implements DownloadFileFromURL.interfaceAsyncResponse
{
    private static final String FILETYPE_IMAGE = "image";
    //public InterfaceDonwloaded download;
    public static int COMMENT_ACTIVITY_REQUEST_CODE = 1001;
    public int parentId;
    public ArrayList<Media> mediaList;
    public PreferenceHelper prefManager;
    public ProgressDialog dialog;
    Activity activity;
    DownloadFileFromURL.interfaceAsyncResponse interfaceResponse = null;
    private Channel channel;
    private User user;
    private Media media;
    private ImageView mediaImage;
    private TextView txtFileName;
    private TextView txtInsertTime;
    private LinearLayout btn_open;
    private TextView txtLikesCounter, txtCommentCounter, txtOpen;
    private ImageView btnShare;
    private LinearLayout layoutFileDescription;
    private TextView txtFileDescription;
    private TextView textMediaType, textMediaSize, textMediaAvailableOnDevice, textMediaOwner, textMediaLocation, textMediaCreatedDate, textMediaModifiedDate;
    private Button btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        prefManager = new PreferenceHelper(context);
        setContentView(R.layout.fragment_media_detail_view);
        Helper.retainOrientation(MediaDetailActivity.this);
        Bundle arguments = getIntent().getExtras();
        channel = arguments.getParcelable(MediaFragment.EXTRA_CHANNEL);
        media = arguments.getParcelable(MediaFragment.EXTRA_MEDIA);
        this.dialog = new ProgressDialog(context);
        this.interfaceResponse = this;
        this.dialog.setCancelable(false);
        this.activity = this;
        user = new User();
        setMediaDetails(media);
        if (media.isDownloaded == 0)
        {
            txtOpen.setText(getString(R.string.download));
        }
        else
        {
            txtOpen.setText(getString(R.string.open));
        }
        activity.getSupportActionBar().setTitle("");
        txtOpen.setTypeface(Helper.robotoBoldTypeface);
        btnDelete.setTypeface(Helper.robotoBoldTypeface);
        txtFileName.setTypeface(Helper.robotoBoldTypeface);
    }

    @Override
    protected void initializeComponents()
    {
        super.initializeComponents();
        mediaImage = (ImageView) findViewById(R.id.imageMediaView);
        mediaImage.setLayoutParams(new CollapsingToolbarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Helper.getDeviceHeight(this) / 3));

        txtFileName = (TextView) findViewById(R.id.txtFileName);
        txtInsertTime = (TextView) findViewById(R.id.txtInsertTime);
        txtOpen = (TextView) findViewById(R.id.txt_open);
        btn_open = (LinearLayout) findViewById(R.id.btn_open);
        txtLikesCounter = (TextView) findViewById(R.id.txtLikesCounter);
        txtCommentCounter = (TextView) findViewById(R.id.txtCommentCounter);
        btnShare = (ImageView) findViewById(R.id.btnShare);
        layoutFileDescription = (LinearLayout) findViewById(R.id.layoutFileDescription);
        txtFileDescription = (TextView) findViewById(R.id.txtFileDescription);
        textMediaType = (TextView) findViewById(R.id.textMediaType);
        textMediaSize = (TextView) findViewById(R.id.textMediaSize);
        textMediaAvailableOnDevice = (TextView) findViewById(R.id.textMediaAvailableOnDevice);
        textMediaOwner = (TextView) findViewById(R.id.textMediaOwner);
        textMediaLocation = (TextView) findViewById(R.id.textMediaLocation);
        textMediaCreatedDate = (TextView) findViewById(R.id.textMediaCreatedDate);
        textMediaModifiedDate = (TextView) findViewById(R.id.textMediaModifiedDate);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        Helper.setupTypeface(findViewById(R.id.parentLayout), Helper.robotoBoldTypeface);


    }

    @Override
    protected void bindEvents()
    {
        super.bindEvents();
        final View.OnClickListener clickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                switch (view.getId())
                {
                    case R.id.btn_open:
                        if (media.isDownloaded == 0)
                        {
                            try
                            {
                                onNetworkReady(media);
                            }
                            catch (ParseException e)
                            {
                                e.printStackTrace();
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                            catch (UnsupportedEncodingException e)
                            {
                                e.printStackTrace();
                            }

                        }
                        else
                        {
                            String[] contentType = media.type.split("/");
                            if (contentType[0].equals(FILETYPE_IMAGE))
                            {
                                try
                                {
                                    ArrayList<Media> mediaArrayList = new ArrayList<>();
                                    if (media.parentId == -1)
                                    {
                                        DataHelper.getMediaListFromParent(context, channel.id, mediaArrayList);
                                        Log.e("", " media size : " + mediaArrayList.size());
                                    }
                                    else
                                    {
                                        DataHelper.getMediaListFromChannelId(context, media.parentId, mediaArrayList);
                                        Log.e("", " media size : " + mediaArrayList.size());
                                    }
                                    Helper.openFileForImageViewer(MediaDetailActivity.this, media, mediaArrayList);
                                }
                                catch (Exception ex)
                                {
                                    Helper.openFile(context, media);
                                    ex.printStackTrace();
                                }
                            }
                            else
                            {
                                Helper.openFile(context, media);
                            }

                        }
                        break;
                    case R.id.txtLikesCounter:
                        if (media.isDownloaded == 1)
                        {
                            startUserLikeViewActivity();

                        }
                        else if (media.isDownloading == 1)
                        {
                            Helper.showSimpleDialog(context, getString(R.string.label_download_first_dialog));
                        }
                        else
                        {

                            Helper.showSimpleDialog(context, getString(R.string.label_download_first_dialog));
                        }

                        break;
                    case R.id.txtCommentCounter:
                        if (media.isDownloaded == 1)
                        {
                            startUserCommentViewActivity();

                        }
                        else if (media.isDownloading == 1)
                        {
                            Helper.showSimpleDialog(context, getString(R.string.label_download_first_dialog));
                        }
                        else
                        {
                            Helper.showSimpleDialog(context, getString(R.string.label_download_first_dialog));
                        }

                        break;
                    case R.id.btnShare:
                        if (media.isDownloaded == 1)
                        {
                            startUserShareViewActivity();
                        }
                        else if (media.isDownloading == 1)
                        {
                            Helper.showSimpleDialog(context, getString(R.string.label_download_first_dialog));
                        }
                        else
                        {
                            Helper.showSimpleDialog(context, getString(R.string.label_download_first_dialog));
                        }

                        break;
                    case R.id.btnDelete:
                        showDeleteConfirmDialog(R.style.DialogAnimation, getString(R.string.sign_out_message));
                        break;
                }
            }
        };
        btn_open.setOnClickListener(clickListener);
        txtLikesCounter.setOnClickListener(clickListener);
        txtCommentCounter.setOnClickListener(clickListener);
        btnShare.setOnClickListener(clickListener);
        btnDelete.setOnClickListener(clickListener);

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
    protected void onResume()
    {
        super.onResume();
        txtLikesCounter.setText(String.valueOf(DataHelper.getMediaLikeCount(context, media.id)));
        txtCommentCounter.setText(String.valueOf(DataHelper.getMediaCommentCount(context, media.id)));
    }

    private void startUserLikeViewActivity()
    {
        Intent dashboardIntent = new Intent(context, UserLikeViewActivity.class);
        dashboardIntent.putExtra(MediaFragment.EXTRA_MEDIA, media);
        dashboardIntent.putExtra(MediaFragment.EXTRA_CHANNEL, channel);
        startActivity(dashboardIntent);
    }

    private void startUserCommentViewActivity()
    {
        Intent userCommentIntent = new Intent(context, UserCommentViewActivity.class);
        userCommentIntent.putExtra(MediaFragment.EXTRA_MEDIA, media);
        userCommentIntent.putExtra(MediaFragment.EXTRA_CHANNEL, channel);
        startActivityForResult(userCommentIntent, COMMENT_ACTIVITY_REQUEST_CODE);
    }

    private void startUserShareViewActivity()
    {
        Intent shareViewActivityIntent = new Intent(context, ShareActivity.class);
        shareViewActivityIntent.putExtra(MediaFragment.EXTRA_MEDIA, media);
        shareViewActivityIntent.putExtra(MediaFragment.EXTRA_CHANNEL, channel);
        startActivity(shareViewActivityIntent);
    }

    private void setMediaDetails(Media mediaTemp)
    {
        //mFolder = new File("" + getFilesDir() + "/" + media.id);
        user.id = PreferenceHelper.getUserContext(context);
        Glide.with(context).load(media.icon).diskCacheStrategy(DiskCacheStrategy.ALL).into(mediaImage);
      /*  if (actionBar != null)
        {
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
        }*/

        txtFileName.setText(mediaTemp.name);
        txtInsertTime.setText(Helper.getDateFormate2().format(mediaTemp.insertDate));
        /*txtLikesCounter.setText(String.valueOf(DataHelper.getMediaLikeCount(context, mediaTemp.id)));
        txtCommentCounter.setText(String.valueOf(DataHelper.getMediaCommentCount(context, mediaTemp.id)));*/
        if (mediaTemp.description != null && !mediaTemp.description.trim().isEmpty())
        {
            layoutFileDescription.setVisibility(View.VISIBLE);
            txtFileDescription.setText(mediaTemp.description.trim());
        }
        else
        {
            layoutFileDescription.setVisibility(View.GONE);
        }
        textMediaType.setText(mediaTemp.type);
        textMediaSize.setText(android.text.format.Formatter.formatFileSize(context, media.size));
        textMediaAvailableOnDevice.setText(mediaTemp.isDownloaded == 1 ? getString(R.string.yes) : getString(R.string.no));
        try
        {
            // Get user detail from media version id
            MediaVersion mediaVersion = new MediaVersion();
            mediaVersion.id = mediaTemp.currentVersionId;
            DataHelper.getMediaVersion(context, mediaVersion);
            User ownerDetail = new User();
            ownerDetail.id = mediaVersion.creatorId;
            DataHelper.getUser(context, ownerDetail);

            textMediaOwner.setText(ownerDetail.name);
            textMediaCreatedDate.setText(Helper.getDateFormate2().format(mediaTemp.insertDate) + " " + getString(R.string.label_by) + " " + ownerDetail.name);
            ownerDetail.id = mediaVersion.modifierId;
            DataHelper.getUser(context, ownerDetail);
            textMediaModifiedDate.setText(Helper.getDateFormate2().format(mediaTemp.updateDate) + " " + getString(R.string.label_by) + " " + ownerDetail.name);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        textMediaLocation.setText(channel.name);

    }

    private void onNetworkReady(final Media media1) throws ParseException, JSONException, UnsupportedEncodingException
    {

        NetworkRequest request;
        ArrayList<RequestParameter> parameters = new ArrayList<>();
        JSONObject payloadJson = new JSONObject();

        payloadJson.put("ChannelId", String.valueOf(DataHelper.getChannelId(context, media1.id)));
        payloadJson.put("UserId", String.valueOf(PreferenceHelper.getUserContext(context)));
        payloadJson.put("MediaId", String.valueOf(media1.id));
        payloadJson.put("VersionId", String.valueOf(media1.currentVersionId));

        NetworkService.RequestCompleteCallback callback;
        JSONObject requestJson = new JSONObject();
        requestJson.put("Action", DataProvider.Actions.GET_CHANNEL_MEDIA_CONTENT);
        requestJson.put("Payload", payloadJson);
        Log.e("JSON", requestJson.toString());
        request = new NetworkRequest(context);
        request.setBodyType(NetworkRequest.REQUEST_BODY_MULTIPART);
        request.setRequestType(NetworkRequest.REQUEST_TYPE_NORMAL);

        request.setRequestListener(new NetworkRequest.RequestListener()
        {
            @Override
            public void onRequestComplete(NetworkResponse networkResponse) throws JSONException
            {
                if (networkResponse.getStatusCode() == 200)
                {
                    JSONObject response = new JSONObject(networkResponse.getResponse().toString());

                    if (response.optInt("Status") > 0)
                    {
                        Toast.makeText(context, response.optString("Message"), Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        dialog.setMessage(context.getString(R.string.menu_download_option_download));
                        dialog.show();
                        media1.isDownloading = 1;
                        DownloadFileFromURL downloadContent = new DownloadFileFromURL(context, media1, interfaceResponse);
                        downloadContent.execute(response.optString("Payload"));

                    }
                }
            }
        });
        request.setProgressMode(NetworkRequest.PROGRESS_MODE_NONE);
        request.setProgressMessage(getString(R.string.msg_please_wait));

        request.setRequestUrl(DataProvider.ENDPOINT_FILE);
        parameters = new ArrayList<>();
        parameters.add(RequestParameter.multiPart("Request", requestJson.toString()));
        request.setParameters(parameters);
        int userId = PreferenceHelper.getUserContext(context);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

        String token = PreferenceHelper.getToken(context) + String.valueOf(userId).length() + userId + Helper.getEpochTime();
        if (token != null && !token.isEmpty())
        {
            ArrayList<BasicNameValuePair> headerNameValuePairs = new ArrayList<>();
            headerNameValuePairs.add(new BasicNameValuePair(KEY_AUTHORIZATION, token));
            request.setHeaders(headerNameValuePairs);

        }

        request.execute();
    }

    @Override
    public void processFinish(String output)
    {

        setResult(RESULT_OK);

        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
        txtOpen.setText(getString(R.string.open));

        String[] contentType = media.type.split("/");
        if (contentType[0].equals(FILETYPE_IMAGE))
        {
            try
            {
                ArrayList<Media> mediaArrayList = new ArrayList<>();
                if (media.parentId == -1)
                {
                    DataHelper.getMediaListFromParent(context, channel.id, mediaArrayList);
                    Log.e("", " media size : " + mediaArrayList.size());
                }
                else
                {
                    DataHelper.getMediaListFromChannelId(context, media.parentId, mediaArrayList);
                    Log.e("", " media size : " + mediaArrayList.size());
                }
                Helper.openFileForImageViewer(MediaDetailActivity.this, media, mediaArrayList);
            }
            catch (Exception ex)
            {
                Helper.openFile(context, media);
                ex.printStackTrace();
            }
        }
        else
        {
            Helper.openFile(context, media);
        }
    }


    private void showDeleteConfirmDialog(int animationSource, String type)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getString(R.string.label_delete_dialog));
        builder.setIcon(R.drawable.ic_delete_file);
        builder.setMessage(getString(R.string.sign_out_message));
        builder.setMessage(type);
        builder.setPositiveButton(getString(R.string.yes),
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (media.isDownloaded == 1)
                        {
                            File mediaFile = new File(context.getFilesDir() + File.separator + media.id);
                            if (mediaFile.exists() && Helper.checkStoragePermission(context))
                            {
                                Log.e("TEST", "File is " + (mediaFile.delete() ? "Deleted" : "Not delete"));
                            }
                        }
                        DataHelper.removeMediaFromAllTablesById(context, media.id);
                        Intent intent = new Intent();
                        intent.putExtra(MediaFragment.KEY_MEDIA_DETAIL_IS_DELETED, true);
                        setResult(RESULT_OK, intent);
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


}