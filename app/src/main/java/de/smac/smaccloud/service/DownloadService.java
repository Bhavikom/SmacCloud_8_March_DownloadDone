package de.smac.smaccloud.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;

import de.smac.smaccloud.R;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.base.NetworkRequest;
import de.smac.smaccloud.base.NetworkResponse;
import de.smac.smaccloud.base.NetworkService;
import de.smac.smaccloud.base.RequestParameter;
import de.smac.smaccloud.data.DataHelper;
import de.smac.smaccloud.fragment.MediaFragment;
import de.smac.smaccloud.helper.DataProvider;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.model.Media;
import de.smac.smaccloud.model.MediaAllDownload;

import static de.smac.smaccloud.base.NetworkService.KEY_AUTHORIZATION;

/**
 * Media download service
 */
public class DownloadService extends IntentService {
    public static Boolean isDownloading = false;
    int id = 1;
    Context context;
    Media media;
    int downloadPosition;
    Bundle extra;
    ArrayList<MediaAllDownload> mediaAllDownloads;
    Intent downloadIntent;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder build;

    public DownloadService() {
        super("DownloadService");
        context = this;
    }

    public DownloadService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        downloadIntent = intent;
        extra = intent.getExtras();
        media = new Media();
        if (extra != null && extra.containsKey("downloadlist")) {
            mediaAllDownloads = extra.getParcelableArrayList("downloadlist");
        }
        if (mediaAllDownloads != null && mediaAllDownloads.size() > 0) {
            try {
                isDownloading = true;
                downloadMedia(0);
                mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                build = new NotificationCompat.Builder(context);
                build.setContentTitle(getString(R.string.download))
                        .setContentText(getString(R.string.msg_downloading_in_progress))
                        .setSmallIcon(R.drawable.ic_download_all)
                        .setOngoing(true);

                build.setProgress(100, 0, true);
                mNotifyManager.notify(id, build.build());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    protected void downloadMedia(int position) throws JSONException, UnsupportedEncodingException {
        downloadPosition = position;
        NetworkRequest request;
        ArrayList<RequestParameter> parameters = new ArrayList<>();
        JSONObject payloadJson = new JSONObject();

        payloadJson.put("ChannelId", String.valueOf(mediaAllDownloads.get(position).channelId));
        payloadJson.put("UserId", String.valueOf(PreferenceHelper.getUserContext(context)));
        payloadJson.put("MediaId", String.valueOf(mediaAllDownloads.get(position).mediaId));
        payloadJson.put("VersionId", String.valueOf(mediaAllDownloads.get(position).currentVersionId));

        /*media.id = downloadList.get(position).mediaId;
        DataHelper.getMedia(context, media);*/
        JSONObject requestJson = new JSONObject();
        requestJson.put("Action", DataProvider.Actions.GET_CHANNEL_MEDIA_CONTENT);
        requestJson.put("Payload", payloadJson);
        Log.e("JSON", requestJson.toString());
        request = new NetworkRequest(context);
        request.setBodyType(NetworkRequest.REQUEST_BODY_MULTIPART);
        request.setRequestType(NetworkRequest.REQUEST_TYPE_NORMAL);
        request.setRequestListener(new NetworkRequest.RequestListener() {
            @Override
            public void onRequestComplete(NetworkResponse networkResponse) throws JSONException, UnsupportedEncodingException {
                if (networkResponse.getStatusCode() == 200) {
                    final JSONObject response = new JSONObject(networkResponse.getResponse().toString());

                    if (response.optInt("Status") == 1) {
                        //  notifySimple(response.optString("Message"));
                    } else {
                        class DownloadAsync extends AsyncTask<String, Void, Void> {

                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                            }

                            @Override
                            protected Void doInBackground(String... params) {
                                if(isDownloading)
                                {
                                    try {
                                        downloadFile(response.optString("Payload"));
                                    }
                                    catch (Exception e){
                                        e.printStackTrace();
                                    }

                                }
                                else
                                {
                                    cancelNotification();
                                }
                                return null;
                            }

                            @Override
                            protected void onProgressUpdate(Void... values)
                            {
                                super.onProgressUpdate(values);
                                Log.e("onProgressUpdate>>", Arrays.toString(values));
                                if(!isDownloading)
                                    cancelNotification();
                            }

                            @Override
                            protected void onCancelled() {
                                super.onCancelled();
                                if(!isDownloading)
                                    cancelNotification();
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                try {
                                    Media tempMedia = new Media();
                                    tempMedia.id = mediaAllDownloads.get(downloadPosition).mediaId;
                                    DataHelper.getMedia(context, tempMedia);
                                    tempMedia.isDownloading = 0;
                                    DataHelper.updateMedia(context, tempMedia);

                                    Intent intent = new Intent(MediaFragment.BROADCAST_MEDIA_DOWNLOAD_COMPLETE);
                                    intent.putExtra("MEDIA", tempMedia);
                                    context.sendBroadcast(intent);

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }

                                if (isDownloading && downloadPosition + 1 != mediaAllDownloads.size())
                                {
                                    try
                                    {
                                        downloadMedia(downloadPosition + 1);
                                    } catch (JSONException e)
                                    {
                                        e.printStackTrace();
                                    } catch (UnsupportedEncodingException e)
                                    {
                                        e.printStackTrace();
                                    }
                                } else {
                                    isDownloading = false;
                                    cancelNotification();
                                }
                            }
                        }
                        DownloadAsync downloadAsync = new DownloadAsync();
                        downloadAsync.execute();

                    }
                } else {
                    cancelNotification();
                }
            }
        });
        request.setProgressMode(NetworkRequest.PROGRESS_MODE_NONE);
        request.setRequestUrl(DataProvider.ENDPOINT_FILE);
        parameters = new ArrayList<>();
        parameters.add(RequestParameter.multiPart("Request", requestJson.toString()));
        request.setParameters(parameters);
        int userId = PreferenceHelper.getUserContext(context);
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
            String token = PreferenceHelper.getToken(context) + String.valueOf(userId).length() + userId + Helper.getEpochTime();
            if (token != null && !token.isEmpty()) {
                ArrayList<BasicNameValuePair> headerNameValuePairs = new ArrayList<>();
                headerNameValuePairs.add(new BasicNameValuePair(KEY_AUTHORIZATION, token));
                request.setHeaders(headerNameValuePairs);

            }

        } catch (Exception e) {

        }
        request.execute();

    }

    protected void downloadFile(String urlToDownload) {
        int count;
        try {
            media.id = mediaAllDownloads.get(downloadPosition).mediaId;
            DataHelper.getMedia(context, media);
            URL url = new URL(urlToDownload);
            URLConnection conection = url.openConnection();
            int userId = PreferenceHelper.getUserContext(context);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

            String token = PreferenceHelper.getToken(context) + String.valueOf(userId).length() + userId + Helper.getEpochTime();
            conection.addRequestProperty(NetworkService.KEY_AUTHORIZATION, token);
            conection.connect();

            InputStream input = null;

            input = new BufferedInputStream(conection.getInputStream());

            File mFolder = new File("" + context.getFilesDir());
            if (!mFolder.exists())
                mFolder.mkdirs();
            OutputStream output = new FileOutputStream(context.getFilesDir() + "/" + media.id);
            media.isDownloading = 1;
            byte[] data = new byte[(int) (media.size + 2)];
            int totalBytes = 0;
            while ((count = input.read(data)) != -1) {
                totalBytes += count;
                output.write(data, 0, count);
            }
            if (totalBytes == media.size) {
                media.isDownloading = 0;
                media.isDownloaded = 1;
                //DataHelper.updateMedia(context, media);
                Log.e("Downloaded Bytes", String.valueOf(totalBytes));
            }
            // flushing output
            output.flush();
            // closing streams
            output.close();
            input.close();

        } catch (Exception e) {
            e.printStackTrace();
            media.isDownloading = 0;
            if(e instanceof SocketException){
                isDownloading = false;
                cancelNotification();
            }
        } finally {
            DataHelper.updateMedia(context, media);
        }

    }


    @Override
    public boolean stopService(Intent name)
    {
        cancelNotification();
        Log.e("TEST>>", "Stop Service called");
        return super.stopService(name);
    }

    public void cancelNotification()
    {
        build.setOngoing(false);
        mNotifyManager.cancel(id);
        try
        {
            for (MediaAllDownload mediaAllDownload : mediaAllDownloads)
            {
                Media tempMedia = new Media();
                tempMedia.id = mediaAllDownload.mediaId;
                DataHelper.getMedia(context, tempMedia);
                tempMedia.isDownloading = 0;
                DataHelper.updateMedia(context, tempMedia);
            }

            Intent intent = new Intent(MediaFragment.BROADCAST_MEDIA_DOWNLOAD_COMPLETE);
            context.sendBroadcast(intent);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        stopSelf();
    }
}
