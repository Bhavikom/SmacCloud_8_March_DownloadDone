package de.smac.smaccloud.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

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
import de.smac.smaccloud.activity.ShareActivity;
import de.smac.smaccloud.activity.ShareAttachmentActivity;
import de.smac.smaccloud.base.Activity;
import de.smac.smaccloud.base.Fragment;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.base.NetworkRequest;
import de.smac.smaccloud.base.NetworkResponse;
import de.smac.smaccloud.base.NetworkService;
import de.smac.smaccloud.base.RequestParameter;
import de.smac.smaccloud.data.DataHelper;
import de.smac.smaccloud.fragment.MediaAttachmentFragment;
import de.smac.smaccloud.helper.DataProvider;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.model.Channel;
import de.smac.smaccloud.model.Media;
import de.smac.smaccloud.service.DownloadFileFromURL;

import static android.app.Activity.RESULT_OK;
import static de.smac.smaccloud.base.NetworkService.KEY_AUTHORIZATION;
import static de.smac.smaccloud.fragment.MediaFragment.EXTRA_CHANNEL;
import static de.smac.smaccloud.fragment.MediaFragment.EXTRA_MEDIA;
import static de.smac.smaccloud.fragment.MediaFragment.EXTRA_PARENT;

public class MediaAttachAdapter extends RecyclerView.Adapter<MediaAttachAdapter.MediaHolder> implements DownloadFileFromURL.interfaceAsyncResponse
{
    private static final String FILETYPE_VIDEO = "video";
    private static final String FILETYPE_VIDEO_MP4 = "video/mp4";
    private static final String FILETYPE_IMAGE = "image";
    private static final String FILETYPE_FOLDER = "folder";
    private static final String FILETYPE_PDF = "application/pdf";
    public ProgressDialog dialog;
    DownloadFileFromURL.interfaceAsyncResponse interfaceResponse = null;
    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<Media> mediaList;
    private boolean selectionMode;


    public MediaAttachAdapter(Activity activity, ArrayList<Media> mediaList)
    {
        this.activity = activity;
        this.mediaList = mediaList;
        this.inflater = LayoutInflater.from(this.activity);
        this.dialog = new ProgressDialog(activity);
        this.dialog.setCancelable(false);
        this.interfaceResponse = this;
        this.selectionMode = ShareAttachmentActivity.selectedAttachmentList != null && !ShareAttachmentActivity.selectedAttachmentList.isEmpty();
    }

    @Override
    public MediaAttachAdapter.MediaHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View rootView = inflater.inflate(R.layout.activity_media_atteach_list_single, viewGroup, false);
        return new MediaHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final MediaHolder holder, final int position)
    {
        final Media media = mediaList.get(position);
        holder.labelName.setText(media.name);
        boolean isTabletSize = activity.getResources().getBoolean(R.bool.isTablet);
        if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            if (isTabletSize)
            {
                holder.imageIcon.setLayoutParams(new LinearLayout.LayoutParams(Helper.getDeviceWidth(activity) / 8, Helper.getDeviceHeight(activity) / 12));
            }
            else
            {

                holder.imageIcon.setLayoutParams(new LinearLayout.LayoutParams(Helper.getDeviceWidth(activity) / 5, Helper.getDeviceHeight(activity) / 6));
            }
        }
        else
        {
            if (isTabletSize)
            {
                holder.imageIcon.setLayoutParams(new LinearLayout.LayoutParams(Helper.getDeviceWidth(activity) / 11, Helper.getDeviceHeight(activity) / 14));
            }
            else
            {

                holder.imageIcon.setLayoutParams(new LinearLayout.LayoutParams(Helper.getDeviceWidth(activity) / 5, Helper.getDeviceHeight(activity) / 6));
            }
        }
        if (media.type.equals(FILETYPE_FOLDER))
        {
            holder.imgViewNext.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.imgViewNext.setVisibility(View.GONE);
        }

        if (ShareAttachmentActivity.selectedAttachmentList != null && !ShareAttachmentActivity.selectedAttachmentList.isEmpty())
            holder.isSelected = ShareAttachmentActivity.selectedAttachmentList.contains(media);

        if (media != null)
        {
            if (media.type.equals(FILETYPE_FOLDER) && media.icon != null)
            {
                final Uri imageUri = Uri.parse(media.icon);
                Glide.with(activity)
                        .load(imageUri)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(new SimpleTarget<Bitmap>()
                        {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation)
                            {
                                holder.imageIcon.setImageBitmap(bitmap);
                                holder.imageIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            }

                            @Override
                            public void onLoadFailed(Exception e, Drawable errorDrawable)
                            {
                                super.onLoadFailed(e, errorDrawable);
                                Glide.with(activity)
                                        .load(imageUri)
                                        .asBitmap()
                                        .error(R.drawable.ic_folder_icon)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(new SimpleTarget<Bitmap>()
                                        {
                                            @Override
                                            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation)
                                            {
                                                holder.imageIcon.setImageBitmap(bitmap);
                                                holder.imageIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                            }

                                            @Override
                                            public void onLoadFailed(Exception e, Drawable errorDrawable)
                                            {
                                                super.onLoadFailed(e, errorDrawable);
                                                if (e != null && e.getMessage() != null)
                                                    Log.e("Glide", e.getMessage());
                                            }
                                        });
                            }
                        });
            }
            else if (media.type.equals(FILETYPE_PDF))
            {
                final Uri imageUri = Uri.parse(media.icon);
                Glide.with(activity)
                        .load(imageUri)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.imageIcon);
            }
            else if (media.type.equalsIgnoreCase(FILETYPE_VIDEO) || media.type.equalsIgnoreCase(FILETYPE_VIDEO_MP4))
            {
                final Uri videoUri = Uri.parse(activity.getFilesDir() + File.separator + media.id);
                final Uri imageUri = Uri.parse(media.icon);
                    /*Bitmap thumb = ThumbnailUtils.createVideoThumbnail(videoUri.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
                    if (thumb != null)
                        holder.imageIcon.setImageBitmap(thumb);
                    else*/
                Glide.with(activity)
                        .load(imageUri)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.imageIcon);
            }
            else
            {
                String[] contentType = media.type.split("/");
                if (contentType[0].equals(FILETYPE_IMAGE))
                {
                    final Uri imageUri = Uri.parse(activity.getFilesDir() + File.separator + media.id);
                    if (media.isDownloaded == 1)
                    {
                        Glide.with(activity)
                                .load(imageUri)
                                .asBitmap()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(new SimpleTarget<Bitmap>()
                                {
                                    @Override
                                    public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation)
                                    {
                                        holder.imageIcon.setImageBitmap(bitmap);
                                    }

                                    @Override
                                    public void onLoadFailed(Exception e, Drawable errorDrawable)
                                    {
                                        super.onLoadFailed(e, errorDrawable);
                                        Glide.with(activity)
                                                .load(new File(String.valueOf(imageUri)))
                                                .asBitmap()
                                                .error(R.drawable.ic_channel)
                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                .into(new SimpleTarget<Bitmap>()
                                                {
                                                    @Override
                                                    public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation)
                                                    {
                                                        holder.imageIcon.setImageBitmap(bitmap);
                                                    }

                                                    @Override
                                                    public void onLoadFailed(Exception e, Drawable errorDrawable)
                                                    {
                                                        super.onLoadFailed(e, errorDrawable);
                                                        if (e.getMessage() != null)
                                                            Log.e("Glide", e.getMessage());
                                                    }
                                                });

                                    }
                                });
                    }
                    else
                    {
                        //holder.imageIcon.setImageResource(R.drawable.ic_file_icon);
                        Uri uri = Uri.parse(media.icon);
                        Glide.with(activity).load(uri).fitCenter().diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.imageIcon);
                        holder.imageIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }
                }
                else
                {
                    holder.imageIcon.setImageResource(R.drawable.ic_file_icon);
                }
            }
            if (checkMediaSelected(media))
            {
                holder.imageAdd.setVisibility(View.VISIBLE);
                //holder.parentLayout.setBackgroundColor(Color.LTGRAY);
                holder.isSelected = true;
            }
            else
            {
                holder.parentLayout.setBackgroundColor(Color.TRANSPARENT);
                holder.imageAdd.setVisibility(View.GONE);
                holder.isSelected = false;
            }
        }
        holder.parentLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final Media media = mediaList.get(position);

//                    holder.imgdownload.setVisibility(View.GONE);
                Channel channel = new Channel();
                try
                {
                    channel.id = DataHelper.getChannelIdFromMediaID(activity, media.id);
                    DataHelper.getChannel(activity, channel);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
                if (media.type.equals(FILETYPE_FOLDER))
                {

                    Fragment mediaFragment = new MediaAttachmentFragment();
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(EXTRA_CHANNEL, channel);
                    arguments.putParcelable(EXTRA_MEDIA, media);
                    arguments.putInt(EXTRA_PARENT, media.id);
                    mediaFragment.setArguments(arguments);
                    navigateToFragment(R.id.layoutDynamicFrame, mediaFragment, true);
                    // holder.imgViewNext.setVisibility(View.VISIBLE);
                }
                else
                {
                    if (media.isDownloaded == 1)
                    {
                        if (selectionMode)
                        {
                            if (holder.isSelected)
                            {
                                holder.parentLayout.setBackgroundColor(Color.TRANSPARENT);
                                holder.imageAdd.setVisibility(View.GONE);
                                holder.isSelected = false;
                                int inx = getSelectedMediaId(media);
                                if (inx != -1)
                                    ShareAttachmentActivity.selectedAttachmentList.remove(inx);

                                if (ShareAttachmentActivity.selectedAttachmentList.isEmpty())
                                    selectionMode = false;
                            }
                            else
                            {
                                holder.imageAdd.setVisibility(View.VISIBLE);
                                ShareAttachmentActivity.selectedAttachmentList.add(media);
                                holder.isSelected = true;
                            }
                        }
                        else
                        {
                            ShareAttachmentActivity.selectedAttachmentList.add(media);
                            Intent intentReturn = new Intent();
                            intentReturn.putExtra(ShareActivity.KEY_SELECTED_MEDIA, ShareAttachmentActivity.selectedAttachmentList);
                            intentReturn.setFlags(RESULT_OK);
                            activity.setResult(RESULT_OK, intentReturn);
                            activity.finish();
                        }
                    }
                    else
                    {
                        if (media.isDownloaded == 0)
                        {

                            AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
                            alertDialog.setTitle(activity.getString(R.string.label_delete_dialog));
                            alertDialog.setMessage(activity.getString(R.string.download_file_first));
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, activity.getString(R.string.download),
                                    new DialogInterface.OnClickListener()
                                    {
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            if (media.isDownloaded == 0)
                                            {
                                                try
                                                {
                                                    if (Helper.isNetworkAvailable(activity))
                                                    {
                                                        onNetworkReady(media);
                                                    }
                                                    else
                                                    {
                                                        Helper.showMessage(activity, false, activity.getString(R.string.msg_please_check_your_connection));
                                                    }
                                                    holder.imageAdd.setVisibility(View.VISIBLE);
                                                    // holder.parentLayout.setBackgroundColor(Color.LTGRAY);
                                                    //((MediaAttachmentActivity) activity).selectedMediaList.add(media);
                                                    media.isDownloaded = 1;
                                                    ShareAttachmentActivity.selectedAttachmentList.add(media);
                                                    holder.isSelected = true;

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

                                        }


                                    });
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, activity.getString(R.string.ok),
                                    new DialogInterface.OnClickListener()
                                    {
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            dialog.dismiss();
                                        }
                                    });


                            alertDialog.show();

                        }
                        else
                        {
                            ShareAttachmentActivity.selectedAttachmentList.add(media);
                            Intent intentReturn = new Intent();
                            intentReturn.putExtra(ShareActivity.KEY_SELECTED_MEDIA, ShareAttachmentActivity.selectedAttachmentList);
                            intentReturn.setFlags(RESULT_OK);
                            activity.setResult(RESULT_OK, intentReturn);
                            activity.finish();
                        }
                        //((MediaAttachmentActivity) activity).selectedMediaList.add(media);

                    }
                }


            }


        });
      /*  holder.imgdownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/


        holder.parentLayout.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                if (!selectionMode)
                {
                    Media media = mediaList.get(position);
                    Channel channel = new Channel();
                    try
                    {
                        channel.id = DataHelper.getChannelIdFromMediaID(activity, media.id);
                        DataHelper.getChannel(activity, channel);
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                    if (!media.type.equals(FILETYPE_FOLDER) && !holder.isSelected)
                    {
                        holder.imageAdd.setVisibility(View.VISIBLE);
                        // holder.parentLayout.setBackgroundColor(Color.LTGRAY);
                        //((MediaAttachmentActivity) activity).selectedMediaList.add(media);
                        ShareAttachmentActivity.selectedAttachmentList.add(media);
                        holder.isSelected = true;
                        selectionMode = true;
                    }
                }

                return true;
            }
        });
        holder.labelName.setTypeface(Helper.robotoMediumTypeface);

    }

    private void navigateToFragment(int containerId, Fragment fragment, boolean addToBackStack)
    {
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.replace(containerId, fragment, fragment.getClass().getSimpleName());
        if (addToBackStack)
            transaction.addToBackStack(fragment.getClass().getSimpleName());
        transaction.commit();
    }

    @Override
    public int getItemCount()
    {
        return mediaList.size();
    }

    private boolean checkMediaSelected(Media media)
    {
        for (Media media1 : ShareAttachmentActivity.selectedAttachmentList)
        {
            if (media1.id == media.id)
                return true;
        }
        return false;
    }

    private int getSelectedMediaId(Media media)
    {
        for (int i = 0; i < ShareAttachmentActivity.selectedAttachmentList.size(); i++)
        {
            if (ShareAttachmentActivity.selectedAttachmentList.get(i).id == media.id)
                return i;
        }
        return -1;
    }

    private void onNetworkReady(final Media media1) throws ParseException, JSONException, UnsupportedEncodingException
    {
        NetworkRequest request;
        ArrayList<RequestParameter> parameters = new ArrayList<>();
        JSONObject payloadJson = new JSONObject();

        payloadJson.put("ChannelId", String.valueOf(DataHelper.getChannelId(activity, media1.id)));
        payloadJson.put("UserId", String.valueOf(PreferenceHelper.getUserContext(activity)));
        payloadJson.put("MediaId", String.valueOf(media1.id));
        payloadJson.put("VersionId", String.valueOf(media1.currentVersionId));

        NetworkService.RequestCompleteCallback callback;
        JSONObject requestJson = new JSONObject();
        requestJson.put("Action", DataProvider.Actions.GET_CHANNEL_MEDIA_CONTENT);
        requestJson.put("Payload", payloadJson);
        Log.e("JSON", requestJson.toString());
        request = new NetworkRequest(activity);
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
                        Toast.makeText(activity, response.optString("Message"), Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        dialog.setMessage(activity.getString(R.string.menu_download_option_download));
                        //dialog.show();
                        media1.isDownloading = 1;
                        DownloadFileFromURL downloadContent = new DownloadFileFromURL(activity, media1, interfaceResponse);
                        downloadContent.execute(response.optString("Payload"));

                    }
                }
            }
        });
        request.setProgressMode(NetworkRequest.PROGRESS_MODE_NONE);
        request.setProgressMessage(activity.getString(R.string.msg_please_wait));
        request.setRequestUrl(DataProvider.ENDPOINT_FILE);
        parameters = new ArrayList<>();
        parameters.add(RequestParameter.multiPart("Request", requestJson.toString()));
        request.setParameters(parameters);
        int userId = PreferenceHelper.getUserContext(activity);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

        String token = PreferenceHelper.getToken(activity) + String.valueOf(userId).length() + userId + Helper.getEpochTime();
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
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
        notifyDataSetChanged();


    }

    class MediaHolder extends RecyclerView.ViewHolder
    {
        LinearLayout parentLayout;
        ImageView imageIcon, imageAdd;
        TextView labelName;
        ProgressBar downloadProgressBar;
        boolean isSelected;
        ImageView imgViewNext;

        private MediaHolder(View itemView)
        {
            super(itemView);
            itemView.setId(R.id.itemView);
            parentLayout = (LinearLayout) itemView.findViewById(R.id.parentLayout);
            imageIcon = (ImageView) itemView.findViewById(R.id.imageIcon);
            imageAdd = (ImageView) itemView.findViewById(R.id.img_add);
            labelName = (TextView) itemView.findViewById(R.id.txt_channel_name);
            imgViewNext = (ImageView) itemView.findViewById(R.id.img_next);
            downloadProgressBar = (ProgressBar) itemView.findViewById(R.id.downloadProgressBar);
            isSelected = false;

        }
    }
}
