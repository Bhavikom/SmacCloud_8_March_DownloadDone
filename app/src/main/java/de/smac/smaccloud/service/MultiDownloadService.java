package de.smac.smaccloud.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import de.smac.smaccloud.adapter.MediaAdapter;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.model.Media;

/**
 * Created by S Soft on 01-Mar-18.
 */

public class MultiDownloadService extends Service implements DownloadFileFromURL.interfaceAsyncResponse{

    DownloadFileFromURL.interfaceAsyncResponse interfaceResponse = null;
    public static final String MUTLIPLE_DOWNLOD_ACTION = "com.samb";
    Intent intent;
    Media media;
    Bundle extra;
    Context context;
    String url="",position="";
    DownloadFileFromURL downloadContent;
    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(MUTLIPLE_DOWNLOD_ACTION);
    }
    public MultiDownloadService() {
        // super("DownloadService2");
        context = this;
        this.interfaceResponse = this;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent != null) {
            if (intent.getAction().equals(Helper.START_DOWNLOAD)) {
                extra = intent.getExtras();
                media = new Media();
                if (extra != null) {
                    media = extra.getParcelable("media_object");
                    url = extra.getString("url");
                    position = extra.getString("position");

                    downloadContent = new DownloadFileFromURL(context, media,position, interfaceResponse);
                    //downloadContent.execute(url);
                    downloadContent.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,url);
                }
            }else {
                extra = intent.getExtras();
                media = new Media();
                if (extra != null) {
                    media = extra.getParcelable("media_object");
                    position = extra.getString("position");

                    //DownloadFileFromURL downloadContent = new DownloadFileFromURL(context, media,position, interfaceResponse);
                    if(downloadContent != null) {
                        Log.e(" 222222 "," download is canceled : "+position);
                        downloadContent.cancelDownload(media, position);
                    }
                }
            }
        }
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {


        return null;
    }

    @Override
    public void processFinish(String output,Media media, int pos) {

        if(!TextUtils.isEmpty(output) && output.equalsIgnoreCase("fail")) {
            sendIntentFail(output, media, pos);
            Log.e(" 55555555 "," download is canceled : "+pos);
        }

    }

    @Override
    public void statusOfDownload(Media media, int pos) {
        //Log.e(" in status of download "," status of download : "+media.id+ " : "+media.progress);

        sendIntentInProgress(media,pos);
    }
    private void sendIntentInProgress(Media media, int position){

        Intent intent = new Intent(MediaAdapter.MESSAGE_PROGRESS);
        intent.putExtra("media_from_service",media);
        intent.putExtra("position",String.valueOf(position));
        LocalBroadcastManager.getInstance(MultiDownloadService.this).sendBroadcast(intent);
    }
    private void sendIntentFail(String result,Media media, int position){

        Intent intent = new Intent(MediaAdapter.MESSAGE_FAIL);
        intent.putExtra("media_from_service",media);
        intent.putExtra("position",String.valueOf(position));
        LocalBroadcastManager.getInstance(MultiDownloadService.this).sendBroadcast(intent);
    }
}
