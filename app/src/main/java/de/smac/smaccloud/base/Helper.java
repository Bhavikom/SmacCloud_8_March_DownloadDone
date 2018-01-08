package de.smac.smaccloud.base;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import de.smac.smaccloud.R;
import de.smac.smaccloud.activity.DashboardActivity;
import de.smac.smaccloud.activity.DocumentViewerActivity;
import de.smac.smaccloud.activity.ImageViewerActivity;
import de.smac.smaccloud.activity.SetSignatureActivity;
import de.smac.smaccloud.activity.VideoViewerActivity;
import de.smac.smaccloud.data.DataHelper;
import de.smac.smaccloud.fragment.MediaFragment;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.model.Media;
import de.smac.smaccloud.model.MediaAllDownload;
import de.smac.smaccloud.model.UserComment;
import de.smac.smaccloud.model.UserLike;
import de.smac.smaccloud.service.DownloadService;

import static de.smac.smaccloud.fragment.MediaFragment.REQ_IS_MEDIA_DELETED;


@SuppressWarnings("unused")
public class Helper
{

    public static final int REQUEST_PLAY_RESOLUTION = -1001;
    public static final String PREFERENCE_GCM_ID = "gcm_reg_id";
    public static final String PREFERENCE_GCM_APP_ID = "gcm_app_id";
    public final static String DOWNLOAD_ACTION = "com.samb.download";

    public static final ArrayList<Integer> selectedMediaTypeList = new ArrayList<>();
    public static final ArrayList<String> selectedChannelsList = new ArrayList<>();

    public static boolean isPaused = false;
    public static long NETWORK_CALL_DURATION = 7000;
    public static boolean IS_DIALOG_SHOW = true;
    // Localization type
    public static String LOCALIZATION_TYPE_COUNTRY = "1";
    public static String LOCALIZATION_TYPE_DESIGNATION = "2";
    public static String LOCALIZATION_TYPE_ERROR_CODE = "3";
    public static String LOCALIZATION_TYPE_COMPANY_TYPE = "4";
    public static String LOCALIZATION_TYPE_COMPANY_SIZE = "5";
    public static int SCREEN_HEIGHT;
    public static Typeface robotoLightTypeface;
    public static Typeface robotoBlackTypeface;
    public static Typeface robotoBoldTypeface;
    public static Typeface robotoMediumTypeface;
    public static Typeface robotoRegularTypeface;

    public static String fontPath = "roboto.regular.ttf";
    public static String fontPathBold = "roboto.bold.ttf";
    public static String fontPathLight = "RobotoLight.ttf";
    public static String fontPathMedium = "roboto.medium.ttf";
    public static String fontPathBlack = "roboto.black.ttf";
    public static int SCREEN_WIDTH;

    public static SimpleDateFormat dateFormatGlobal;
    public static SimpleDateFormat dateFormatGlobal2;
    public static SimpleDateFormat dateFormatGlobalCurrentDateTime;
    public static DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
            .cacheOnDisc(true).resetViewBeforeLoading(true).build();
    //.showImageForEmptyUri(R.drawable.login_background)
    //.showImageOnFail(R.drawable.ic_image_icon)
    //.showImageOnLoading(R.drawable.ic_image_icon).build();
    public static int LAYOUT_FLAGS = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
    public static int HIDE_FLAGS = LAYOUT_FLAGS | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

    /**
     * Apply typeface to all the widget in child view of any parent layout
     */
    public static void setupTypeface(View view, Typeface globalFace)
    {
        try
        {
            if (view instanceof EditText)
            {
                if (((EditText) view).getTypeface().isBold())
                {
                    ((EditText) view).setTypeface(globalFace, Typeface.BOLD);
                }
                else
                {
                    ((EditText) view).setTypeface(globalFace);
                }
            }
            else if (view instanceof CheckBox)
            {
                ((CheckBox) view).setTypeface(globalFace);
            }
            else if (view instanceof TextView)
            {
                //((TextView) view).setTypeface(globalFace);
                //((TextView) view).setLineSpacing(getPixelsFromDp(1f), 1f);

                if (((TextView) view).getTypeface().isBold())
                {
                    ((TextView) view).setTypeface(globalFace, Typeface.BOLD);
                }
                else
                {
                    ((TextView) view).setTypeface(globalFace);
                }
            }
            else if (view instanceof ViewGroup)
            {
                ViewGroup vg = (ViewGroup) view;
                for (int i = 0; i < vg.getChildCount(); i++)
                {
                    View child = vg.getChildAt(i);
                    setupTypeface(child, globalFace);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static File createImageFile() throws IOException
    {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //String imageFileName = "JPEG_" + timeStamp + "_";
        String imageFileName = UUID.randomUUID().toString();
        File storageDir = new File(Environment.getExternalStorageDirectory() + File.separator + "Smac Cloud");
        if (!storageDir.exists())
        {
            storageDir.mkdirs();
            File noMedia = new File(storageDir.getAbsolutePath() + "/.nomedia");
            noMedia.createNewFile();
        }

        File file = new File(storageDir, imageFileName + ".jpg");
        return file;
    }

    public static boolean resizeImage(String originalFilePath, String compressedFilePath)
    {
        InputStream in = null;
        try
        {
            in = new FileInputStream(originalFilePath);
        }
        catch (FileNotFoundException e)
        {
            Log.e("TAG", "originalFilePath is not valid", e);
        }

        if (in == null)
        {
            return false;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap preview_bitmap;

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP)
            options.inSampleSize = 2;
        preview_bitmap = BitmapFactory.decodeStream(in, null, options);

        try
        {
            int width = 2656, height = 1496;

            if (preview_bitmap.getWidth() < width)
                width = preview_bitmap.getWidth();
            if (preview_bitmap.getHeight() < height)
                height = preview_bitmap.getHeight();

            preview_bitmap = Bitmap.createScaledBitmap(preview_bitmap, width, height, false);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        preview_bitmap.compress(Bitmap.CompressFormat.JPEG, 45, stream);
        byte[] byteArray = stream.toByteArray();

        FileOutputStream outStream = null;
        try
        {
            outStream = new FileOutputStream(compressedFilePath);
            outStream.write(byteArray);
            outStream.close();
        }
        catch (Exception e)
        {
            Log.e("TAG", "could not save", e);
        }

        return true;
    }

    public static long getTotalMediaSize(ArrayList<Media> mediaArrayList)
    {
        long size = 0;
        for (Media media : mediaArrayList)
            size += media.size;
        return size;
    }

    public static boolean checkStoragePermission(Context context)
    {
        return ActivityCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestStoragePermission(final android.app.Activity activity, View parentLayout)
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.

            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, SetSignatureActivity.PERMISSION_REQUEST_CODE);
        }
        else
        {
            // Storage permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, SetSignatureActivity.PERMISSION_REQUEST_CODE);
        }
    }

    public static void startDashboardActivity(Activity activity)
    {
        Intent dashboardIntent = new Intent(activity, DashboardActivity.class);
        dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(dashboardIntent);
        activity.finish();
    }

    public static void changeLanguage(Context context, String languageCode)
    {
        Locale locale;
        Configuration config = new Configuration();

        locale = new Locale(languageCode);
        Locale.setDefault(locale);
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    public static void setUpLanguage(Context context, String language)
    {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    public static String getStringByLocal(Activity context, int id, String locale)
    {
        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.setLocale(new Locale(locale));
        return context.createConfigurationContext(configuration).getResources().getString(id);
    }

    public static void demoUserDialog(Context context)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(context.getString(R.string.alert));
        alertDialog.setIcon(context.getResources().getDrawable(R.drawable.ic_alert));
        alertDialog.setMessage(context.getString(R.string.you_have_no_right));
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

    public static void showSimpleDialog(Context context, String message)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setMessage(message);
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

    public static void downloadAllFiles(final Activity activity, final boolean isFromSetting)
    {

        if (DownloadService.isDownloading)
        {
            activity.notifySimple(activity.getString(R.string.msg_downloading_already_in_progress));
        }
        else
        {
            final ArrayList<MediaAllDownload> arraylistDownloadList = new ArrayList<MediaAllDownload>();
            //item.setIcon(switchViews() ? R.drawable.ic_list : R.drawable.ic_grid);
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            /*long blockSize = stat.getBlockSizeLong();
            long availableBlocks = stat.getAvailableBlocksLong();*/
            long blockSize = 0;
            long availableBlocks = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2)
            {
                blockSize = stat.getTotalBytes();
                availableBlocks = stat.getAvailableBytes();
            }
            else
            {
                blockSize = (long) stat.getBlockSize() * (long) stat.getBlockCount();
                availableBlocks = (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
            }
            //activity.notifySimple(Formatter.formatFileSize(activity, availableBlocks * blockSize));
            final long downloadSize = DataHelper.getAllDownloadSize(activity);

            new AlertDialog.Builder(activity)
                    .setTitle("Download All Data")
                    .setMessage("Total Disk Size " + Helper.bytesConvertsToMb(availableBlocks, activity) + " Total Download Size " + Helper.bytesConvertsToMb(downloadSize, activity) + "\n Are You Sure To Download?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            DataHelper.getAllDownloadList(activity, arraylistDownloadList);
                            for (int i = 0; i < arraylistDownloadList.size(); i++)
                            {
                                int mediaParentId = arraylistDownloadList.get(i).mediaId;
                                int rootMediaId;
                                do
                                {
                                    rootMediaId = mediaParentId;
                                    mediaParentId = DataHelper.getMediaParentId(activity, mediaParentId);
                                }
                                while (mediaParentId != -1);
                                int channelId = DataHelper.getChannelIdFromMediaID(activity, rootMediaId);
                                arraylistDownloadList.get(i).channelId = channelId;
                            }

                                /*try
                                {*/
                                   /* notifySimple("Your Downloading Started In Background");
                                    AllMediaDownload allMediaDownload = new AllMediaDownload(context, arraylistDownloadList, arraylistDownloadList.get(0).mediaId);
                                    boolean updateCount=DataHelper.updateAllMedia(context);
                                    allMediaDownload.onNetworkReady(0);*/

                            for (int i = 0; i < arraylistDownloadList.size(); i++)
                            {
                                try
                                {
                                    Media tempMedia = new Media();
                                    tempMedia.id = arraylistDownloadList.get(i).mediaId;
                                    DataHelper.getMedia(activity, tempMedia);
                                    tempMedia.isDownloading = 1;
                                    DataHelper.updateMedia(activity, tempMedia);
                                }
                                catch (Exception ex)
                                {
                                    ex.printStackTrace();
                                }
                            }
                            Intent starDownload = new Intent(activity, DownloadService.class);
                            starDownload.putParcelableArrayListExtra("downloadlist", arraylistDownloadList);
                            activity.startService(starDownload);

                            if (!isFromSetting)
                                startDashboardActivity(activity);


                               /* }
                                catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }
                                catch (UnsupportedEncodingException e)
                                {
                                    e.printStackTrace();
                                }
                                catch (ParseException e)
                                {
                                    e.printStackTrace();
                                }*/
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    /**
     * Open an file from any screen
     *
     * @param activity
     * @param media
     */
    public static void openFileForImageViewer(Activity activity, Media media, ArrayList<Media> mediaList)
    {

        Media objMedia = new Media();
        ArrayList<Media> arrayListCopy = new ArrayList<>();
        mediaList.size();
        for (int i = 0; i < mediaList.size(); i++)
        {
            objMedia = (Media) mediaList.get(i);
            String[] contentType = objMedia.type.split("/");
            int isDownloaded = objMedia.isDownloaded;
            if (contentType[0].equals(MediaFragment.FILETYPE_IMAGE) && isDownloaded == 1)
            {
                arrayListCopy.add(objMedia);
            }
        }
        String[] contentType = media.type.split("/");
        if (contentType[0].equals(MediaFragment.FILETYPE_IMAGE))
        {
            String json = new Gson().toJson(arrayListCopy);
            Intent imageView = new Intent(activity, ImageViewerActivity.class);
            imageView.putExtra(DocumentViewerActivity.EXTRA_MEDIA, media);
            imageView.putExtra("image_arraylist", json);

            activity.startActivityForResult(imageView, REQ_IS_MEDIA_DELETED);

            DataHelper.insertRecentItem(activity, media.id, PreferenceHelper.getUserContext(activity));
        }
        else
        {
            openFile(activity, media);
        }
    }

    public static void openFile(Context context, Media media)
    {
        String[] contentType = media.type.split("/");
        if (media.type.equals(MediaFragment.FILETYPE_PDF))
        {
            Intent documentView = new Intent(context, DocumentViewerActivity.class);
            documentView.putExtra(DocumentViewerActivity.EXTRA_MEDIA, media);
            context.startActivity(documentView);
        }
        else if (contentType[0].equals(MediaFragment.FILETYPE_IMAGE))
        {
            Intent imageView = new Intent(context, ImageViewerActivity.class);
            imageView.putExtra(DocumentViewerActivity.EXTRA_MEDIA, media);
            context.startActivity(imageView);
        }
        else if (media.type.equals(MediaFragment.FILETYPE_MP3))
        {
            /*File audioFile = new File("" + context.getFilesDir() + "/" + media.id);
            new AudioPlayerDialog(context, media.id).show();*/
            Intent videoViewerActivityIntent = new Intent(context, VideoViewerActivity.class);
            videoViewerActivityIntent.putExtra(DocumentViewerActivity.EXTRA_MEDIA, media);
            context.startActivity(videoViewerActivityIntent);
        }
        else if (contentType[0].equals(MediaFragment.FILETYPE_VIDEO) || contentType[0].equals(MediaFragment.FILETYPE_VIDEO_MP4))
        {
            //new VideoPlayerDialog(context, media.id).show();
            Intent videoViewerActivityIntent = new Intent(context, VideoViewerActivity.class);
            videoViewerActivityIntent.putExtra(DocumentViewerActivity.EXTRA_MEDIA, media);
            context.startActivity(videoViewerActivityIntent);
        }

        DataHelper.insertRecentItem(context, media.id, PreferenceHelper.getUserContext(context));
    }

    public static void storeLikeOffline(Activity activity, Media media)
    {
        UserLike userLike = new UserLike();
        userLike.isSynced = 1;
        userLike.associatedId = media.id;
        userLike.userId = PreferenceHelper.getUserContext(activity);
        if (userLike.addOfflineLike(activity))
        {
            showMessage(activity, true, activity.getString(R.string.msg_like_done_please_sync));

        }
    }

    public static Bitmap getBitmapFromURI(Context context, Uri uri)
    {
        Bitmap bitmap = null;
        MediaMetadataRetriever myRetriever = new MediaMetadataRetriever();
        myRetriever.setDataSource(context, uri); // the URI of audio file
        byte[] artwork;
        artwork = myRetriever.getEmbeddedPicture();
        if (artwork != null)
        {
            bitmap = BitmapFactory.decodeByteArray(artwork, 0, artwork.length);
        }
        return bitmap;
    }

    public static void storeCommentOffline(Context context, Media media, String commentText)
    {
        UserComment userComment = new UserComment();
        userComment.isSynced = 1;
        userComment.fileId = media.id;
        userComment.userId = PreferenceHelper.getUserContext(context);
        userComment.fileId = media.id;
        userComment.comment = commentText;
        userComment.insertDate = new Date();
        userComment.addOfflineComments(context);
    }

    public static void showShareSuccessDialog(final Activity activity)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle(activity.getResources().getString(R.string.app_name));
        alertDialog.setMessage(activity.getString(R.string.msg_email_send));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                activity.finish();
            }
        });

        alertDialog.show();
    }

    public static void showMessage(Activity activity, boolean isSuccess, String message)
    {
        Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        TextView textView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        //textView.setTextColor(isSuccess ? Color.GREEN : Color.RED);
        textView.setTextColor(Color.WHITE);
        textView.setMaxLines(3);
        snackbar.show();
    }

    public static void showToastMessage(Context context, String message)
    {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * This method is use to hide keyboard from device screen
     *
     * @param activity
     */
    public static void hideSoftKeyboard(android.app.Activity activity)
    {
        if (activity.getCurrentFocus() != null)
        {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * @param dp
     * @return This method will convert pixel value from DP value
     */
    public static float getPixelsFromDp(float dp)
    {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    /**
     * @param bitmap
     * @param backgroundColor
     * @param borderColor
     * @return This method will return rounded corner bitmap image from given image with specified style
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int backgroundColor, int borderColor)
    {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth() + 12, bitmap.getHeight() + 12, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        //canvas.drawARGB(Color.alpha(backgroundColor), Color.red(backgroundColor), Color.green(backgroundColor), Color.blue(backgroundColor));

        Paint borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setColor(borderColor);
        borderPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        borderPaint.setShadowLayer(2.0f, 0.0f, 2.0f, Color.BLACK);

        int centerWidth = output.getWidth() / 2;
        int centerHeight = output.getHeight() / 2;
        canvas.drawCircle(centerWidth, centerHeight, ((centerWidth + centerHeight) / 2) - 4, borderPaint);

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        Rect rectS = new Rect(0, 0, output.getWidth() - 12, output.getHeight() - 12);
        Rect rectD = new Rect(0, 0, output.getWidth(), output.getHeight());

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        canvas.drawBitmap(bitmap, rectS, rectD, paint);

        return output;
    }

    /**
     * @param context
     * @return This method will return TRUE if Internet network is available otherwise return FALSE
     */
    public static boolean isNetworkAvailable(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * @param email
     * @return This method will return TRUE if specified email address is valid otherwise return false
     */

    public static boolean isEmailValid(CharSequence email)
    {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * This method will check individual email address from list
     *
     * @param emailList
     * @return
     */
    public static boolean checkEmailAddresses(List<String> emailList)
    {
        for (int i = 0; i < emailList.size(); i++)
        {
            if (!Helper.isEmailValid(emailList.get(i).trim()))
                return false;
        }
        return true;
    }

    /**
     * This method will return width of current device
     *
     * @param activity
     * @return
     */
    @SuppressLint("NewApi")
    public static int getDeviceWidth(android.app.Activity activity)
    {

        WindowManager wm = activity.getWindowManager();
        Point point = new Point();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            wm.getDefaultDisplay().getSize(point);
            return point.x;
        }
        else
        {
            return wm.getDefaultDisplay().getWidth();
        }
    }

    /**
     * This method will return height of current device
     *
     * @param activity
     * @return
     */
    @SuppressLint("NewApi")
    public static int getDeviceHeight(android.app.Activity activity)
    {
        WindowManager wm = activity.getWindowManager();
        Point point = new Point();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            wm.getDefaultDisplay().getSize(point);
            return point.y;
        }
        else
        {
            return wm.getDefaultDisplay().getHeight();
        }
    }

    /**
     * This method will return string contain device date with given pattern(format)
     *
     * @param pattern
     * @return
     */
    public static String getCurrentDateTime(String pattern)
    {
        return new SimpleDateFormat(pattern).format(new Date());
    }

    /**
     * This method will return string contain given date with given pattern(format)
     *
     * @param pattern
     * @param date
     * @return
     */
    public static String parseDate(String pattern, Date date)
    {
        return new SimpleDateFormat(pattern).format(date);
    }

    public static SimpleDateFormat getDateFormate()
    {
        dateFormatGlobal = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.US);
        dateFormatGlobal.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormatGlobal;
    }

    public static SimpleDateFormat getDateFormate2()
    {
        dateFormatGlobal2 = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        dateFormatGlobal2.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormatGlobal2;
    }

    public static SimpleDateFormat getDateForCommentDateLabel()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat;
    }

    public static SimpleDateFormat getDateFormatCurrentDateTime()
    {
        dateFormatGlobalCurrentDateTime = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        //dateFormatGlobalCurrentDateTime.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormatGlobalCurrentDateTime;
    }

    /**
     * This method will reformat given date with given output pattern
     *
     * @param Date
     * @param CurrentPattern
     * @param OutputPattern
     * @return
     */
    public static String parseDate(String Date, String CurrentPattern, String OutputPattern)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(CurrentPattern, Locale.US);

        try
        {
            Date startDate = sdf.parse(Date);
            sdf.applyPattern(OutputPattern);
            return sdf.format(startDate).toString().toUpperCase();
        }
        catch (Exception e)
        {
            return "";
        }
    }

    public static boolean isTablet(Context context)
    {
        return context.getResources().getBoolean(R.bool.isTablet);
    }

    /* getting current screenOrientation of screen */
    public static int getScreenOrientation(Activity activity)
    {
        /* will return 1 for portrait and 2 for landscape*/
        Display getOrient = activity.getWindowManager().getDefaultDisplay();
        int orientation = Configuration.ORIENTATION_UNDEFINED;
        if (getOrient.getWidth() == getOrient.getHeight())
        {
            orientation = Configuration.ORIENTATION_SQUARE;
        }
        else
        {
            if (getOrient.getWidth() < getOrient.getHeight())
            {
                orientation = Configuration.ORIENTATION_PORTRAIT;
            }
            else
            {
                orientation = Configuration.ORIENTATION_LANDSCAPE;
            }
        }
        return orientation;
    }

    public static Date stringToDate(String dateStr)
    {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        Date date = null;
        try
        {
            date = format.parse(dateStr);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return date;
    }

    public static void setupUI(final android.app.Activity activity, View view, final View parentLayout)
    {
        if (!(view instanceof EditText))
        {
            view.setOnTouchListener(new View.OnTouchListener()
            {
                public boolean onTouch(View v, MotionEvent event)
                {
                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(parentLayout.getWindowToken(), 0);
                    return false;
                }
            });
        }
        if (view instanceof ViewGroup)
        {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++)
            {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(activity, innerView, parentLayout);
            }
        }
    }

    public static Date stringTodate(String dateStr)
    {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        Date date = null;
        try
        {
            date = format.parse(dateStr);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return date;
    }

    public static boolean isUrlValid(CharSequence url)
    {
        return Patterns.WEB_URL.matcher(url).matches();
    }

    public static long availableBlocks(Context context)
    {
        long availableBlocks = 0;

        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2)
        {
            availableBlocks = stat.getAvailableBytes();
        }
        else
        {
            availableBlocks = (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
        }
        return availableBlocks;
    }

    public static long getEpochTime()
    {
        long ephoch;
        long millysec = System.currentTimeMillis();
        ephoch = millysec / 1000;
        return ephoch;
    }

    public static String bytesConvertsToMb(long bytes, Context context)
    {
        boolean si = true;
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static String readableFileSize(long size)
    {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }


    public static void retainOrientation(Activity activity)
    {
        if (!isTablet(activity))
        {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public static Drawable buildCounterDrawable(Context context, int count, int backgroundImageId)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.notifications_counter_menuitem_layout, null);
        view.setBackgroundResource(backgroundImageId);

        if (count == 0)
        {
            View counterTextPanel = view.findViewById(R.id.counterValuePanel);
            counterTextPanel.setVisibility(View.GONE);
        }
        else
        {
            TextView textView = (TextView) view.findViewById(R.id.txtCount);
            //CircularTextView textView = (CircularTextView) view.findViewById(R.id.txtCount);
            textView.setText("" + count);

            if (isTablet(context))
            {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 20); // context.getResources().getDimension(R.dimen.title_very_small));
            }

            textView.measure(0, 0);
            int viewHeight = textView.getMeasuredWidth();
            int viewWidth = textView.getMeasuredHeight();
            if (viewHeight > viewWidth)
                viewWidth = viewHeight;
            else
                viewHeight = viewWidth;

            ViewGroup.LayoutParams textViewLayoutParams = textView.getLayoutParams();
            textViewLayoutParams.height = (int) (viewHeight / 1);
            textViewLayoutParams.width = (int) (viewWidth / 1);
            textView.setLayoutParams(textViewLayoutParams);
            textView.setGravity(Gravity.CENTER);
        }

        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return new BitmapDrawable(context.getResources(), bitmap);
    }

    public static void preventTwoClick(final View view)
    {
        view.setEnabled(false);
        view.postDelayed(new Runnable()
        {
            public void run()
            {
                view.setEnabled(true);
            }
        }, 1000);
    }


    /**
     * This class is used to perform Google Cloud Messaging(GCM) related operations
     */
    public static class GCM
    {
        private static boolean checkAndResolvePlayService(Activity activity)
        {
            int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
            if (resultCode != ConnectionResult.SUCCESS)
            {
                if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                {
                    GooglePlayServicesUtil.getErrorDialog(resultCode, activity, REQUEST_PLAY_RESOLUTION).show();
                }
                else
                {
                    activity.finish();
                }
                return false;
            }
            return true;
        }

        public static void getCloudMessagingId(Activity activity, RegistrationComplete completeCallback)
        {
            if (checkAndResolvePlayService(activity))
            {
                CloudMessagingRegisterTask cloudMessagingRegisterTask = new CloudMessagingRegisterTask(activity, completeCallback);
                cloudMessagingRegisterTask.execute();
            }
        }


        public interface RegistrationComplete
        {
            public void onRegistrationComplete(String registrationId);
        }

        private static class CloudMessagingRegisterTask extends AsyncTask<Void, Void, String>
        {
            private Context context;
            private RegistrationComplete completeCallback;
            private int currentAppVersion;

            protected CloudMessagingRegisterTask(Context context, RegistrationComplete completeCallback)
            {
                this.context = context;
                this.completeCallback = completeCallback;
            }

            @Override
            protected String doInBackground(Void... voids)
            {
                try
                {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                    String registrationId = preferences.getString(PREFERENCE_GCM_ID, "");
                    if (!registrationId.isEmpty())
                    {
                        int registeredAppVersion = preferences.getInt(PREFERENCE_GCM_APP_ID, Integer.MIN_VALUE);
                        PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                        currentAppVersion = packageInfo.versionCode;
                        if (registeredAppVersion == currentAppVersion)
                            return registrationId;
                    }
                    GoogleCloudMessaging cloudMessaging = GoogleCloudMessaging.getInstance(context);
                    return cloudMessaging.register(context.getString(R.string.gcm_sender_id));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    return "";
                }
            }

            @Override
            protected void onPostExecute(String registrationId)
            {
                super.onPostExecute(registrationId);
                if (!registrationId.isEmpty())
                {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(PREFERENCE_GCM_ID, registrationId);
                    editor.putInt(PREFERENCE_GCM_APP_ID, currentAppVersion);
                    editor.apply();
                    completeCallback.onRegistrationComplete(registrationId);
                }
            }
        }
    }

    public static class ForegroundCheckTask extends AsyncTask<Context, Void, Boolean>
    {

        @Override
        protected Boolean doInBackground(Context... params)
        {
            final Context context = params[0].getApplicationContext();
            return isAppOnForeground(context);
        }

        private boolean isAppOnForeground(Context context)
        {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
            if (appProcesses == null)
            {
                return false;
            }
            final String packageName = context.getPackageName();
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses)
            {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName))
                {
                    return true;
                }
            }
            return false;
        }
    }
}

