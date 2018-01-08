package de.smac.smaccloud.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
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
import de.smac.smaccloud.helper.DataProvider;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.model.Media;
import de.smac.smaccloud.model.ThreadModel;
import de.smac.smaccloud.service.DownloadFileFromURL;
import de.smac.smaccloud.service.SMACCloudApplication;
import de.smac.smaccloud.widgets.UserCommentDialog;

import static de.smac.smaccloud.base.NetworkService.KEY_AUTHORIZATION;

/**
 * This class are use to show medias, It can also user to perform media related operations
 */
public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaHolder> implements DownloadFileFromURL.interfaceAsyncResponse, View.OnClickListener
{
    public final static String DOWNLOAD_ACTION = "com.samb.download";
    private static final String FILETYPE_VIDEO = "video";
    private static final String FILETYPE_VIDEO_MP4 = "video/mp4";
    private static final String FILETYPE_IMAGE = "image";
    private static final String FILETYPE_FOLDER = "folder";
    private static final String FILETYPE_PDF = "application/pdf";
    public InterfaceClickMedial intefaceClick;
    public ProgressDialog dialog;
    public Boolean checkLike;
    Media mediaCanceled;
    Thread thread = null;
    FragmentTransaction transaction;
    OnItemClickOfAdapter onItemClickOfAdapter;
    UserCommentDialog commentDialog;
    DownloadFileFromURL.interfaceAsyncResponse interfaceResponse = null;
    boolean isTabletSize;
    long blockSize = 0;
    RecyclerView recyclerView;
    Handler handler;
    boolean isCanceled = false;
    MediaHolder holder;
    long lastUpdate = 0;
    private LocalBroadcastManager broadcastManager;
    private Intent intent;
    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<Media> arraylistMedia;
    private boolean isGrid;
    private OnClickListener clickListener;

    public MediaAdapter(Activity activity, ArrayList<Media> mediaList, OnItemClickOfAdapter interfaceAdapter, RecyclerView recyclerView)
    {
        this.activity = activity;
        this.arraylistMedia = mediaList;
        this.inflater = LayoutInflater.from(this.activity);
        this.interfaceResponse = this;
        this.dialog = new ProgressDialog(activity);
        this.handler = new Handler();
        this.dialog.setCancelable(false);
        this.onItemClickOfAdapter = interfaceAdapter;
        this.recyclerView = recyclerView;
        transaction = ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction();
        //fragmentManager = getSupportFragmentManager();
        broadcastManager = LocalBroadcastManager.getInstance(activity);
    }

    public void updateData(ArrayList<Media> mediaList)
    {
        this.arraylistMedia.clear();
        this.arraylistMedia.addAll(mediaList);
        notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public MediaHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View rootView = inflater.inflate(isGrid ? R.layout.partial_media_item_grid : R.layout.partial_media_iteam_list, parent, false);

        return new MediaHolder(rootView);

    }

    public void setGrid(boolean isGrid)
    {
        this.isGrid = isGrid;
        notifyDataSetChanged();
    }

    public void setClickListener(OnClickListener mClickListener)
    {
        this.clickListener = mClickListener;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final MediaHolder holder2, final int position)
    {
        //holder = holder2;
        final MediaHolder holder = holder2;
        final int finalPosition = position;
        final Media media = arraylistMedia.get(finalPosition);
        holder.layout_parent_border.setBackgroundColor(Color.TRANSPARENT);
        holder.frameLayout_media_thumbnail.setBackgroundColor(Color.TRANSPARENT);
        holder.labelName.setText(media.name);
        holder.labelName.setTypeface(Helper.robotoMediumTypeface);
        holder.textFileSize.setTypeface(Helper.robotoLightTypeface);
        isTabletSize = activity.getResources().getBoolean(R.bool.isTablet);
        LinearLayout.LayoutParams imageLayoutParams = (LinearLayout.LayoutParams) holder.imageIcon.getLayoutParams();
        if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            if (Helper.isTablet(activity))
            {
                imageLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Helper.getDeviceHeight(activity) / 4);
            }
            else
            {
                imageLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Helper.getDeviceHeight(activity) / 3);
            }
        }
        else
        {
            if (Helper.isTablet(activity))
            {
                imageLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Helper.getDeviceHeight(activity) / 3);
            }
            else
            {
                imageLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Helper.getDeviceHeight(activity) / 3);
            }

        }


        imageLayoutParams.setMargins(1, 1, 1, 1);
        holder.imageIcon.setLayoutParams(imageLayoutParams);
        holder.textFileSize.setText(String.valueOf(android.text.format.Formatter.formatFileSize(activity, media.size)));
        checkLike = DataHelper.checkLike(activity, media.id, PreferenceHelper.getUserContext(activity));


        int channelId = DataHelper.getChannelId(activity, media.id);

        /*int countMedia = DataHelper.getCountMediaFromChannelId(activity, channelId);
        holder.txtMediaCount.setText(String.valueOf(countMedia) + " " + activity.getString(R.string.label_medias));*/
        if (media.type.equalsIgnoreCase(FILETYPE_FOLDER))
        {
            holder.relativeOption.setVisibility(View.GONE);
            holder.linearMediaCount.setVisibility(View.GONE);
        }
        else
        {
            holder.relativeOption.setVisibility(View.VISIBLE);
            holder.linearMediaCount.setVisibility(View.GONE);
        }
        holder.imgRate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (media.isDownloaded == 0)
                {

                    Helper.showSimpleDialog(activity, activity.getString(R.string.label_download_first_dialog));

                }
                else
                {
                    onItemClickOfAdapter.onItemClick(4, position);
                }

            }
        });
        holder.imgShare.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (media.isDownloaded == 0)
                {

                    Helper.showSimpleDialog(activity, activity.getString(R.string.label_download_first_dialog));

                }
                else
                {
                    onItemClickOfAdapter.onItemClick(2, position);
                }

            }
        });
        holder.imgInfo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onItemClickOfAdapter.onItemClick(1, position);
            }
        });
        holder.imgComment.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if (media.isDownloaded == 0)
                {
                    Helper.showSimpleDialog(activity, activity.getString(R.string.label_download_first_dialog));
                }
                else
                {
                    //  holder.imgComment.setEnabled(true);
                    // holder.imgComment.setVisibility(View.VISIBLE);
                    onItemClickOfAdapter.onItemClick(3, position);
                }

            }
        });
        if (media.type.equals(FILETYPE_FOLDER))
        {

            holder.imageFolder.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.imageFolder.setVisibility(View.GONE);
        }
        switch (media.type)
        {
            case FILETYPE_FOLDER:
                holder.imageMediaTypeIcon.setImageResource(R.drawable.ic_folder_icon);
                break;
            case FILETYPE_PDF:
                holder.imageMediaTypeIcon.setImageResource(R.drawable.ic_pdf);
                break;
            case FILETYPE_IMAGE:
                holder.imageMediaTypeIcon.setImageResource(R.drawable.ic_image);
                break;
            case FILETYPE_VIDEO:
            case FILETYPE_VIDEO_MP4:
                holder.imageMediaTypeIcon.setImageResource(R.drawable.ic_video);
                break;
            default:
                String[] contentType = media.type.split("/");
                if (contentType[0].equals(FILETYPE_IMAGE))
                    holder.imageMediaTypeIcon.setImageResource(R.drawable.ic_image);
                else
                    holder.imageMediaTypeIcon.setImageResource(R.drawable.ic_file_icon);
                break;
        }
        if (!media.type.equals(FILETYPE_FOLDER))
        {

            if (media.isDownloading == 1)
            {
                holder.downloadProgressBar.setVisibility(View.VISIBLE);
                holder.imgDownload.setVisibility(View.GONE);
                holder.downloadProgressBar.setProgress(media.progress);
                holder.imgCancelDownload.setVisibility(View.VISIBLE);
                holder.imgComment.setImageResource(R.drawable.ic_comment_grey);
                holder.imgRate.setImageResource(R.drawable.ic_like_grey);
                holder.imgShare.setImageResource(R.drawable.ic_share_grey);

            }
            else if (media.isDownloading == 0)
            {
                holder.downloadProgressBar.setVisibility(View.GONE);
                holder.downloadProgressBar.setProgress(media.progress);
                holder.imgCancelDownload.setVisibility(View.GONE);
                holder.imgDownload.setVisibility(View.VISIBLE);
                holder.imgComment.setImageResource(R.drawable.ic_comment_grey);
                holder.imgRate.setImageResource(R.drawable.ic_like_grey);
                holder.imgShare.setImageResource(R.drawable.ic_share_grey);
            }

            if (media.isDownloaded == 1)
            {
                holder.downloadProgressBar.setVisibility(View.GONE);
                holder.imgDownload.setVisibility(View.GONE);
                holder.imgCancelDownload.setVisibility(View.GONE);
                holder.imgComment.setImageResource(R.drawable.ic_comment);
                holder.imgRate.setImageResource(R.drawable.ic_unlike);
                holder.imgShare.setImageResource(R.drawable.ic_share);
            }

        }
        else
        {
            holder.downloadProgressBar.setVisibility(View.GONE);
            holder.imgDownload.setVisibility(View.GONE);
            holder.imgComment.setImageResource(R.drawable.ic_comment);
            holder.imgRate.setImageResource(R.drawable.ic_like);
            holder.imgShare.setImageResource(R.drawable.ic_share);
        }
        if (checkLike)
        {
            if(media.isDownloaded==1){
                holder.imgRate.setImageResource(R.drawable.ic_like);
            }
            else
            {
                holder.imgRate.setImageResource(R.drawable.ic_like_fill_grey);
            }

        }
        else
        {
            if (media.isDownloaded == 1)
            {
                holder.imgRate.setImageResource(R.drawable.ic_unlike);
            }
            else
            {
                holder.imgRate.setImageResource(R.drawable.ic_like_grey);
            }
        }
        if (clickListener != null)
        {
            View.OnClickListener onClickListener = new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (media.type.equals(FILETYPE_FOLDER))
                    {
                        try
                        {
                            clickListener.onItemClick(finalPosition, view);

                        }
                        catch (ParseException e)
                        {
                            e.printStackTrace();
                        }

                    }
                    else
                    {

                        switch (view.getId())
                        {
                            case R.id.compoundButtonDetail:
                                try
                                {
                                    clickListener.onItemClick(finalPosition, view);

                                }
                                catch (ParseException e)
                                {
                                    e.printStackTrace();
                                }
                                break;
                            case R.id.img_cancel_download:

                                View view1 = holder2.itemView;
                                stopDownloadOnCancelClicked(position, media, view1);
                                break;
                            case R.id.img_download:
                                if (media.isDownloaded == 1)
                                {
                                    onPauseIsCalled();
                                    Helper.openFileForImageViewer(activity, media, arraylistMedia);
                                }
                                else if (media.isDownloading == 1)
                                {
                                    // Log.e(" ****** "," download runnning : "+position);
                                }
                                else
                                {
                                    long mediaSize = arraylistMedia.get(position).size;
                                    if (mediaSize > Helper.availableBlocks(activity))
                                    {
                                        showNoFreeSpaceAvailableDialog(activity);
                                        break;
                                    }
                                    try
                                    {
                                        //holder.imgDownload.setVisibility(View.INVISIBLE);
                                        //holder.downloadProgressBar.setVisibility(View.VISIBLE);
                                        View viewDownload = holder2.itemView;
                                        ProgressBar prb = (ProgressBar) viewDownload.findViewById(R.id.downloadProgressBar);
                                        prb.setVisibility(View.VISIBLE);
                                        ImageView imageViewCancel = (ImageView) viewDownload.findViewById(R.id.img_cancel_download);
                                        imageViewCancel.setVisibility(View.VISIBLE);
                                        ImageView imageDownload = (ImageView) viewDownload.findViewById(R.id.img_download);
                                        imageDownload.setVisibility(View.GONE);
                                        isCanceled = false;
                                        //arraylistMedia.get(position).isDownloading=1;
                                        onNetworkReady(arraylistMedia.get(position), viewDownload, position, prb);
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
                                break;
                            case R.id.framelayout_media_thumbnail:

                                if (media.isDownloaded == 1)
                                {
                                    onPauseIsCalled();
                                    Helper.openFileForImageViewer(activity, media, arraylistMedia);
                                }
                                else if (media.isDownloading == 1)
                                {

                                }
                                else
                                {
                                    long mediaSize = arraylistMedia.get(position).size;
                                    if (mediaSize > Helper.availableBlocks(activity))
                                    {
                                        showNoFreeSpaceAvailableDialog(activity);
                                        break;
                                    }
                                    try
                                    {
                                        ProgressBar prb = (ProgressBar) view.findViewById(R.id.downloadProgressBar);
                                        prb.setVisibility(View.VISIBLE);
                                        ImageView imageViewCancel = (ImageView) view.findViewById(R.id.img_cancel_download);
                                        imageViewCancel.setVisibility(View.VISIBLE);
                                        ImageView imageDownload = (ImageView) view.findViewById(R.id.img_download);
                                        imageDownload.setVisibility(View.GONE);
                                        isCanceled = false;
                                        onNetworkReady(arraylistMedia.get(position), view, position, prb);
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
                                break;


                        }
                    }

                }
            };


            if (media != null)

            {
                if (media.type.equals(FILETYPE_FOLDER))
                {
                    holder.textFileSize.setVisibility(View.GONE);
                    holder.compoundButtonDetail.setVisibility(View.GONE);
                    holder.imgDownload.setVisibility(View.GONE);
                }
                /*if (media.type.equals(FILETYPE_FOLDER) && media.icon != null)
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
                                    holder.progressBarTemp.setVisibility(View.GONE);

                                    //  holder.imageIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);

                                }

                                @Override
                                public void onLoadFailed(Exception e, Drawable errorDrawable)
                                {
                                    super.onLoadFailed(e, errorDrawable);
                                    Glide.with(activity)
                                            .load(imageUri)
                                            .asBitmap()
                                            //    .placeholder(R.drawable.ic_loding)
                                            // .error(R.drawable.ic_folder_icon)
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(new SimpleTarget<Bitmap>()
                                            {
                                                @Override
                                                public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation)
                                                {
                                                    holder.imageIcon.setImageBitmap(bitmap);
                                                    holder.progressBarTemp.setVisibility(View.GONE);
                                                    //  holder.imageIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                                }

                                                @Override
                                                public void onLoadFailed(Exception e, Drawable errorDrawable)
                                                {
                                                    super.onLoadFailed(e, errorDrawable);
                                                    holder.progressBarTemp.setVisibility(View.GONE);
                                                    //Log.v("Glide", e.getMessage());
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
                            .asBitmap()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(new SimpleTarget<Bitmap>()
                            {
                                @Override
                                public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation)
                                {
                                    holder.imageIcon.setImageBitmap(bitmap);
                                    holder.progressBarTemp.setVisibility(View.GONE);
                                    //  holder.imageIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
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
                                                    holder.progressBarTemp.setVisibility(View.GONE);
                                                    //    holder.imageIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                                }

                                                @Override
                                                public void onLoadFailed(Exception e, Drawable errorDrawable)
                                                {
                                                    super.onLoadFailed(e, errorDrawable);
                                                    holder.progressBarTemp.setVisibility(View.GONE);
                                                    //Log.v("Glide", e.getMessage());
                                                }
                                            });
                                }
                            });
                }
                else if (media.type.equalsIgnoreCase(FILETYPE_VIDEO) || media.type.equalsIgnoreCase(FILETYPE_VIDEO_MP4))
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
                                    holder.progressBarTemp.setVisibility(View.GONE);
                                    //  holder.imageIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                }

                                @Override
                                public void onLoadFailed(Exception e, Drawable errorDrawable)
                                {
                                    super.onLoadFailed(e, errorDrawable);
                                    Glide.with(activity)
                                            .load(imageUri)
                                            .asBitmap()
                                            // .placeholder(R.drawable.ic_loding)
                                            .error(R.drawable.ic_folder_icon)
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(new SimpleTarget<Bitmap>()
                                            {
                                                @Override
                                                public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation)
                                                {
                                                    holder.imageIcon.setImageBitmap(bitmap);
                                                    holder.progressBarTemp.setVisibility(View.GONE);

                                                }

                                                @Override
                                                public void onLoadFailed(Exception e, Drawable errorDrawable)
                                                {
                                                    super.onLoadFailed(e, errorDrawable);
                                                    holder.progressBarTemp.setVisibility(View.GONE);

                                                }
                                            });
                                }
                            });
                }
                else
                {
                    String[] contentType = media.type.split("/");
                    if (contentType[0].equals(FILETYPE_IMAGE))
                    {
                        if (media.isDownloaded == 1)
                        {
                            final Uri imageUri = Uri.parse(activity.getFilesDir() + File.separator + media.id);
                            Glide.with(activity)
                                    .load(media.icon)
                                    .asBitmap()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(new SimpleTarget<Bitmap>()
                                    {
                                        @Override
                                        public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation)
                                        {
                                            holder.imageIcon.setImageBitmap(bitmap);
                                            holder.progressBarTemp.setVisibility(View.GONE);


                                        }

                                        @Override
                                        public void onLoadFailed(Exception e, Drawable errorDrawable)
                                        {
                                            super.onLoadFailed(e, errorDrawable);
                                            Glide.with(activity)
                                                    .load(new File(String.valueOf(imageUri)))
                                                    .asBitmap()
                                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                    .into(new SimpleTarget<Bitmap>()
                                                    {
                                                        @Override
                                                        public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation)
                                                        {
                                                            holder.imageIcon.setImageBitmap(bitmap);
                                                            holder.progressBarTemp.setVisibility(View.GONE);

                                                        }

                                                        @Override
                                                        public void onLoadFailed(Exception e, Drawable errorDrawable)
                                                        {
                                                            super.onLoadFailed(e, errorDrawable);
                                                            Log.v("Glide", e.getMessage());
                                                            holder.progressBarTemp.setVisibility(View.GONE);
                                                        }
                                                    });
                                        }
                                    });

                        }
                        else
                        {
                            final Uri imageUri = Uri.parse(media.icon);
                            Glide.with(activity)
                                    .load(imageUri)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(holder.imageIcon);
                        }
                    }
                    else
                    {
                        final Uri imageUri = Uri.parse(media.icon);
                        Glide.with(activity)
                                .load(imageUri)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(holder.imageIcon);
                    }
                }*/

                // Set image thumbnail
                final Uri imageUri = Uri.parse(media.icon);
                Glide.with(activity)
                        .load(imageUri)
                        .asBitmap()
                        // .placeholder(R.drawable.ic_loding)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(new SimpleTarget<Bitmap>()
                        {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation)
                            {

                                holder.imageIcon.setImageBitmap(bitmap);
                                holder.progressBarTemp.setVisibility(View.GONE);
                                //  holder.imageIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);

                            }

                            @Override
                            public void onLoadFailed(Exception e, Drawable errorDrawable)
                            {
                                super.onLoadFailed(e, errorDrawable);
                                holder.progressBarTemp.setVisibility(View.GONE);
                                holder.imageIcon.setImageBitmap(null);
                            }
                        });
            }

            holder.itemView.setOnClickListener(onClickListener);
            holder.compoundButtonDetail.setOnClickListener(onClickListener);
            holder.frameLayout_media_thumbnail.setOnClickListener(onClickListener);
            holder.linearPopup.setOnClickListener(onClickListener);
            holder.imgDownload.setOnClickListener(onClickListener);
            holder.imgCancelDownload.setOnClickListener(onClickListener);

        }

    }

    private void showNoFreeSpaceAvailableDialog(Context context)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(context.getString(R.string.app_title));
        alertDialog.setMessage(context.getString(R.string.no_available_space_message));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.ok),
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();


                    }
                });
        alertDialog.show();

    }

    private void onNetworkReady(final Media media1, final View view, final int position, final ProgressBar prg) throws ParseException, JSONException, UnsupportedEncodingException
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
                    JSONObject response = new JSONObject(networkResponse.getResponse());

                    if (response.optInt("Status") > 0)
                    {
                        if (response.optInt("Status") == 2113) // Status = 2113 means "USER_TOKEN_NOT_VALID"
                        {
                            NetworkRequest requestTokenNotValid = new NetworkRequest(activity);
                            requestTokenNotValid.setBodyType(NetworkRequest.REQUEST_BODY_MULTIPART);
                            requestTokenNotValid.setRequestType(NetworkRequest.REQUEST_TYPE_NORMAL);
                            requestTokenNotValid.setRequestUrl(DataProvider.ENDPOINT_UPDATE_TOKEN);
                            //headerNameValuePairs.add(new BasicNameValuePair(KEY_LANGUAGE_HEADER_PARAM, Locale.getDefault().getLanguage()));
                            try
                            {
                                if (PreferenceHelper.getUserContext(activity) != -1)
                                {
                                    int userId = PreferenceHelper.getUserContext(activity);
                                    String token = PreferenceHelper.getToken(activity) + String.valueOf(userId).length() + userId + Helper.getEpochTime();
                                    ArrayList<BasicNameValuePair> headerNameValuePairs1 = new ArrayList<>();
                                    if (token != null && !token.isEmpty())
                                    {
                                        headerNameValuePairs1.add(new BasicNameValuePair(KEY_AUTHORIZATION, token));
                                        requestTokenNotValid.setHeaders(headerNameValuePairs1);
                                    }
                                }
                            }
                            catch (Exception ex)
                            {
                                ex.printStackTrace();
                            }
                            requestTokenNotValid.execute();
                            requestTokenNotValid.setRequestListener(new NetworkRequest.RequestListener()
                            {
                                @Override
                                public void onRequestComplete(NetworkResponse networkResponse)
                                {
                                    try
                                    {
                                        JSONObject objUpdateTokenResponse = new JSONObject(networkResponse.getResponse().toString());
                                        if (objUpdateTokenResponse.optInt("Status") > 0)
                                        {
                                            Toast.makeText(activity, objUpdateTokenResponse.optString("Message"), Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            if (objUpdateTokenResponse.has("Payload"))
                                            {
                                                JSONObject objUpdateTokenPayload = objUpdateTokenResponse.getJSONObject("Payload");
                                                if (objUpdateTokenPayload.has("AccessToken") && !objUpdateTokenPayload.isNull("AccessToken"))
                                                {
                                                    PreferenceHelper.storeToken(activity, objUpdateTokenPayload.optString("AccessToken"));
                                                    onNetworkReady(media1, view, position, prg);
                                                }
                                            }
                                        }
                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }

                    }
                    else
                    {

                        media1.isDownloading = 1;
                        startDownloadThread(position, response.optString("Payload"), media1, view, prg);
                    }
                }
                else
                {

                    ProgressBar prb = (ProgressBar) view.findViewById(R.id.downloadProgressBar);
                    prb.setVisibility(View.GONE);
                    ImageView imageViewCancel = (ImageView) view.findViewById(R.id.img_cancel_download);
                    imageViewCancel.setVisibility(View.GONE);
                    ImageView imageDownload = (ImageView) view.findViewById(R.id.img_download);
                    imageDownload.setVisibility(View.VISIBLE);
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
    public int getItemCount()
    {
        return arraylistMedia.size();
    }

    @Override
    public void processFinish(String output)
    {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
        notifyDataSetChanged();

    }

    @Override
    public void onClick(View v)
    {

    }

    public void startDownloadThread(final int position, final String url, final Media media, final View view, final ProgressBar prb)
    {

        thread = new Thread(new Runnable()
        {
            int statusOfdownload = 0;
            OutputStream output;
            InputStream input;

            @Override
            public void run()
            {
                String urlDownload = url;
                int count = 0;
                try
                {

                    URL url = new URL(urlDownload);
                    URLConnection conexion = url.openConnection();
                    conexion.connect();

                    int lenghtOfFile = conexion.getContentLength();
                    input = new BufferedInputStream(conexion.getInputStream());
                    File mFolder = new File("" + activity.getFilesDir());
                    if (mFolder.exists())
                    {
                        mFolder.delete();
                    }
                    if (!mFolder.exists())
                        mFolder.mkdirs();
                    output = new FileOutputStream(activity.getFilesDir() + File.separator + media.id);

                    byte data[] = new byte[1024];
                    long total = 0;

                    while ((count = input.read(data)) != -1)
                    {
                        total += count;
                        statusOfdownload = (int) ((total * 100) / lenghtOfFile);
                        output.write(data, 0, count);
                        //Log.e(" @@@@@@@ "," in while loop status : "+statusOfdownload);
                        // Update ProgressBar
                        handler.post(new Runnable()
                        {
                            public void run()
                            {
                                // if(!Helper.isPaused) {
                                updateUIWhileDownloading(position, statusOfdownload, media, prb, 1);
                                //}
                            }
                        });

                    }
                    if (output != null)
                    {
                        output.flush();
                        output.close();
                    }
                    if (input != null)
                    {
                        input.close();
                    }

                }
                catch (Exception e)
                {
                    try
                    {
                        if (output != null)
                        {
                            output.flush();
                            output.close();
                        }
                        if (input != null)
                        {
                            input.close();
                        }

                    }
                    catch (IOException e1)
                    {
                        e1.printStackTrace();
                    }

                    if (!Helper.isNetworkAvailable(activity))
                    {
                        stopDownloadOnNetworkLost(position, media);
                    }
                    else
                    {
                        media.isDownloading = 0;
                        media.isDownloaded = 0;
                        media.progress = 0;
                    }
                    Log.e("", " exception while download : " + e.toString());
                }


            }
        });
        thread.start();
        ThreadModel threadModel = new ThreadModel();
        threadModel.setMediaId(media.id);
        threadModel.setThread(thread);
        ((SMACCloudApplication) activity.getApplication()).arrayListThread.add(threadModel);
    }

    public void updateUIWhileDownloading(int index, int statusOfDownloadFromThread, Media media, final ProgressBar prb, int flag)
    {
        if (!isCanceled)
        {

            if (media.isDownloaded == 0 && media.isDownloading == 1)
            {
                final long INTERVAL_BROADCAST = 0;
                if (statusOfDownloadFromThread <= 99)
                {

                    media.isDownloading = 1;
                    media.progress = statusOfDownloadFromThread;
                    prb.setProgress(statusOfDownloadFromThread);
                    for (Media object : arraylistMedia)
                    {
                        if (object.id == media.id)
                        {
                            arraylistMedia.set(arraylistMedia.indexOf(object), media);
                        }
                    }
                    if (Helper.isPaused)
                    {
                        //Helper.isPaused=false;
                        //if (System.currentTimeMillis() - lastUpdate > INTERVAL_BROADCAST) {
                        Log.e("", " receiver is calling : status : " + statusOfDownloadFromThread + " index : " + index);
                        lastUpdate = System.currentTimeMillis();

                        intent = new Intent(DOWNLOAD_ACTION);
                        media.isDownloading = 1;
                        media.progress = statusOfDownloadFromThread;
                        intent.putExtra("media_object", media);
                        intent.putExtra("position", String.valueOf(index));
                        //sending intent through BroadcastManager
                        broadcastManager.sendBroadcast(intent);
                        //}
                    }
                }
                else if (statusOfDownloadFromThread >= 100)
                {
                    prb.setProgress(statusOfDownloadFromThread);
                    media.isDownloaded = 1;
                    media.isDownloading = 0;
                    media.progress = 100;
                    for (int j = 0; j < arraylistMedia.size(); j++)
                    {
                        if (arraylistMedia.get(j).id == media.id)
                        {
                            arraylistMedia.set(j, media);
                        }
                    }
                    DataHelper.updateMedia(activity, media);

                    for (int i = 0; i < ((SMACCloudApplication) activity.getApplication()).arrayListThread.size(); i++)
                    {
                        if (((SMACCloudApplication) activity.getApplication()).arrayListThread.get(i).getMediaId() == media.id)
                        {
                            ((SMACCloudApplication) activity.getApplication()).arrayListThread.get(i).getThread().interrupt();
                            ((SMACCloudApplication) activity.getApplication()).arrayListThread.remove(i);
                            break;
                        }
                    }
                    notifyDataSetChanged();
                    if (Helper.isPaused)
                    {
                        //Helper.isPaused=false;
                        //if (System.currentTimeMillis() - lastUpdate > INTERVAL_BROADCAST) {
                        Log.e("", " receiver is calling : status : " + statusOfDownloadFromThread + " index : " + index);
                        lastUpdate = System.currentTimeMillis();

                        intent = new Intent(DOWNLOAD_ACTION);
                        media.isDownloading = 1;
                        media.progress = statusOfDownloadFromThread;
                        intent.putExtra("media_object", media);
                        intent.putExtra("position", String.valueOf(index));
                        //sending intent through BroadcastManager
                        broadcastManager.sendBroadcast(intent);
                        //}
                    }
                }

            }
            else
            {
                if (media.isDownloaded == 1)
                {
                    notifyDataSetChanged();
                }
            }

        }
        //}
    }

    public void stopDownloadOnNetworkLost(int pos, Media media)
    {

        if (((SMACCloudApplication) activity.getApplication()).arrayListThread.size() > 0)
        {
            for (int i = 0; i < ((SMACCloudApplication) activity.getApplication()).arrayListThread.size(); i++)
            {
                Log.e(" in for loop  ", " download stopped : " + i);
                ((SMACCloudApplication) activity.getApplication()).arrayListThread.get(i).getThread().interrupt();
                ((SMACCloudApplication) activity.getApplication()).arrayListThread.remove(i);
            }
        }
        //isCanceled = true;
        mediaCanceled = media;
        media.isDownloaded = 0;
        media.isDownloading = 0;
        media.progress = 0;
        DataHelper.updateMedia(activity, media);

        for (int j = 0; j < arraylistMedia.size(); j++)
        {
            arraylistMedia.get(j).isDownloaded = 0;
            arraylistMedia.get(j).isDownloading = 0;
            arraylistMedia.get(j).progress = 0;
        }
        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                notifyDataSetChanged();
            }
        });
    }

    public void stopDownloadOnCancelClicked(final int pos, final Media media, final View view)
    {
        if (((SMACCloudApplication) activity.getApplication()).arrayListThread.size() > 0)
        {
            for (int i = 0; i < ((SMACCloudApplication) activity.getApplication()).arrayListThread.size(); i++)
            {
                if (((SMACCloudApplication) activity.getApplication()).arrayListThread.get(i).getMediaId() == media.id)
                {

                    Log.e(" in for loop  ", " download stopped : " + i);
                    ((SMACCloudApplication) activity.getApplication()).arrayListThread.get(i).getThread().interrupt();
                    ((SMACCloudApplication) activity.getApplication()).arrayListThread.remove(i);
                    break;
                }
            }
        }
        media.isDownloading = 0;
        media.isDownloaded = 0;
        media.progress = 0;
        DataHelper.updateMedia(activity, media);
        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                ProgressBar prg = (ProgressBar) view.findViewById(R.id.downloadProgressBar);
                prg.setProgress(0);
                prg.setVisibility(View.GONE);

                ImageView imgCancel = (ImageView) view.findViewById(R.id.img_cancel_download);
                imgCancel.setVisibility(View.GONE);

                ImageView imgDownload = (ImageView) view.findViewById(R.id.img_download);
                imgDownload.setVisibility(View.VISIBLE);

                notifyItemChanged(pos, media);
            }
        });
    }

    public void onPauseIsCalled()
    {
        Helper.isPaused = true;
        ((SMACCloudApplication) activity.getApplication()).arrayListMediaTemp.clear();
        /*for (int i = 0; i <= arraylistMedia.size()-1;i++){
            if(arraylistMedia.get(i).isDownloading == 1){
                ((SMACCloudApplication) activity.getApplication()).arrayListMediaTemp.add(arraylistMedia.get(i));
            }
        }*/
        for (Media object : arraylistMedia)
        {
            if (object.isDownloading == 1)
            {
                ((SMACCloudApplication) activity.getApplication()).arrayListMediaTemp.add(object);
            }
        }
    }

    public interface InterfaceClickMedial
    {

        void onItemClicked();
    }

    public interface OnClickListener
    {
        public void onItemClick(int position, View view) throws ParseException;
    }

    public interface OnItemClickOfAdapter
    {
        void onItemClick(int pos, int itemPos);
    }

    class MediaHolder extends RecyclerView.ViewHolder
    {
        LinearLayout layout_parent_border;
        FrameLayout frameLayout_media_thumbnail;
        LinearLayout linearPopup;
        ImageView imageIcon, imageFolder;
        TextView textFileSize, txtMediaCount;
        ImageView imageMediaTypeIcon;
        TextView labelName;
        ImageView imgDownload;
        ProgressBar progressBarTemp;
        ProgressBar downloadProgressBar;
        ImageView compoundButtonDetail;
        LinearLayout relativeOption, linearMediaCount;
        ImageView imgRate, imgComment, imgShare, imgInfo, imgCancelDownload;

        //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public MediaHolder(View itemView)
        {
            super(itemView);
            itemView.setId(R.id.itemView);
            layout_parent_border = (LinearLayout) itemView.findViewById(R.id.layout_parent_border);
            frameLayout_media_thumbnail = (FrameLayout) itemView.findViewById(R.id.framelayout_media_thumbnail);
            linearPopup = (LinearLayout) itemView.findViewById(R.id.linearpopup);
            imageIcon = (ImageView) itemView.findViewById(R.id.imageIcon);
            imageFolder = (ImageView) itemView.findViewById(R.id.ic_folder);
            textFileSize = (TextView) itemView.findViewById(R.id.textFileSize);
            relativeOption = (LinearLayout) itemView.findViewById(R.id.relative_option);
            linearMediaCount = (LinearLayout) itemView.findViewById(R.id.linear_mediacount);
            imageMediaTypeIcon = (ImageView) itemView.findViewById(R.id.imageMediaTypeIcon);
            imgDownload = (ImageView) itemView.findViewById(R.id.img_download);
            imgDownload.setVisibility(View.GONE);
            downloadProgressBar = (ProgressBar) itemView.findViewById(R.id.downloadProgressBar);
            labelName = (TextView) itemView.findViewById(R.id.labelName);
            compoundButtonDetail = (ImageView) itemView.findViewById(R.id.compoundButtonDetail);
            txtMediaCount = (TextView) itemView.findViewById(R.id.textview_mediaCount);
            progressBarTemp = (ProgressBar) itemView.findViewById(R.id.progressTemp);

            imgRate = (ImageView) itemView.findViewById(R.id.img_rate);
            imgComment = (ImageView) itemView.findViewById(R.id.img_comment);
            imgInfo = (ImageView) itemView.findViewById(R.id.img_info);
            imgShare = (ImageView) itemView.findViewById(R.id.img_attach);
            imgCancelDownload = (ImageView) itemView.findViewById(R.id.img_cancel_download);
            imgCancelDownload.setVisibility(View.GONE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                imageIcon.setClipToOutline(true);
            }
        }
    }
}
