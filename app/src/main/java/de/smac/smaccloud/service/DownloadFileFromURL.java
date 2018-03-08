package de.smac.smaccloud.service;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.TimeZone;

import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.base.NetworkService;
import de.smac.smaccloud.data.DataHelper;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.model.Media;

/**
 * This class is use to download single file of media
 */
public class DownloadFileFromURL extends AsyncTask<String, String, String>
{
    public interfaceAsyncResponse interfaceResponse = null;
    Context context;
    Media media;

    Boolean isSuccess = false;
    String position;

    public DownloadFileFromURL(Context context, Media media, String pos, interfaceAsyncResponse delegate)
    {
        this.context = context;
        this.media = media;
        this.interfaceResponse = delegate;
        this.position = pos;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

    }

    @Override
    protected String doInBackground(String... params)
    {
        int statusOfdownload = 0;
        int count = 0;
        OutputStream output;
        InputStream input;
        try
        {
            URL url = new URL(params[0]);
            URLConnection conection = url.openConnection();
            //conection.setRequestProperty(KEY_LANGUAGE_HEADER_PARAM, Locale.getDefault().getLanguage());

            int userId = PreferenceHelper.getUserContext(context);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
            //temporary static token
            String token = PreferenceHelper.getToken(context) + String.valueOf(userId).length() + userId + Helper.getEpochTime();
            conection.addRequestProperty(NetworkService.KEY_AUTHORIZATION, token);
            conection.connect();

            int lenghtOfFile = conection.getContentLength();
            
            input = new BufferedInputStream(conection.getInputStream());

            File mFolder = new File("" + context.getFilesDir());
            if (!mFolder.exists())
                mFolder.mkdirs();
             output = new FileOutputStream(context.getFilesDir() + File.separator + media.id);

            byte data[] = new byte[1024];
            long total = 0;
            while ((count = input.read(data)) != -1)
            {
                if(Helper.mediaToCancel != null) {
                    if (Helper.mediaToCancel.id == media.id) {
                        Helper.mediaToCancel = null;
                        Log.e(" 4444444 "," download is canceled : "+position);
                        break;
                    }
                }
                total += count;
                statusOfdownload = (int) ((total * 100) / lenghtOfFile);
                output.write(data, 0, count);

                //Log.e(" length of file "," percentage of download : "+lenghtOfFile + " : "+count);

                //int percentage = (int) ((totalBytes * 100) / lenghtOfFile);

                //Log.e(" percentage "," percentage of download : "+statusOfdownload);

                media.progress = statusOfdownload;
                media.isDownloading = 1;
                interfaceResponse.statusOfDownload(media,Integer.parseInt(position));

                //output.write(data, 0, count);
            }
            if (total == media.size)
            {
                isSuccess = true;
                media.isDownloading = 0;
                media.isDownloaded = 1;
                Log.e("Downloaded Bytes", String.valueOf(total));
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


        }
        catch (Exception e)
        {
            Log.e(" in catch  "," download is stopped : "+e.toString());
            e.printStackTrace();
            media.isDownloading = 0;
            isSuccess = false;
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s)
    {
        super.onPostExecute(s);
        if (isSuccess)
        {

            media.isDownloaded = 1;
            media.isDownloading = 0;
            media.progress = 100;
            //DataHelper.updateMedia(context, media);
            interfaceResponse.processFinish(s,media,Integer.parseInt(position));
        }
        else
        {
            media.isDownloaded = 0;
            media.isDownloading = 0;
            media.progress = 0;
            //DataHelper.updateMedia(context, media);
            interfaceResponse.processFinish("fail",media,Integer.parseInt(position));
        }
    }
    public void cancelDownload(Media mediaReceived, String pos){
        Log.e(" 33333333 "," download is canceled : "+pos);
        Helper.mediaToCancel = mediaReceived;
        /*if(media.id == mediaReceived.id && pos==position){
            cancel(true);

        }*/

    }
    public interface interfaceAsyncResponse
    {
        void processFinish(String output,Media media, int pos);
        void statusOfDownload(Media media, int pos);
    }
}
