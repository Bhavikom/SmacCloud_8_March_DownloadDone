package de.smac.smaccloud.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import de.smac.smaccloud.R;
import de.smac.smaccloud.activity.MediaActivity;
import de.smac.smaccloud.activity.MediaSearchActivity;
import de.smac.smaccloud.base.Fragment;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.base.NetworkRequest;
import de.smac.smaccloud.base.NetworkResponse;
import de.smac.smaccloud.base.NetworkService;
import de.smac.smaccloud.base.RequestParameter;
import de.smac.smaccloud.data.DataHelper;
import de.smac.smaccloud.helper.DataProvider;
import de.smac.smaccloud.helper.InterfaceStopDownload;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.model.Media;
import de.smac.smaccloud.service.DownloadFileFromURL;

import static de.smac.smaccloud.base.NetworkService.KEY_AUTHORIZATION;

public class ShowdownloadProcessFragment extends Fragment implements DownloadFileFromURL.interfaceAsyncResponse, InterfaceStopDownload
{

    public interfaceAsyncResponseDownloadProcess interfaceResponse = null;
    long total = 0;
    View rootView;
    String url;
    SeekBar seekBar;
    TextView txtStopStart, txtPercentage, txtSize, txtRemain;
    AsychTaskDownloadFileFromURL downloadContent;
    boolean flagStartStop = false;
    ArrayList<Media> arrayListMedia;
    private boolean isCallservice = true;
    private Media mediaItem;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.fragment_show_donwload_process, parent, false);
        return rootView;
    }

    @Override

    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            interfaceResponse = (interfaceAsyncResponseDownloadProcess) activity;
            if (activity instanceof MediaActivity)
                ((MediaActivity) activity).interfaceStopDownload = this;
            else if (activity instanceof MediaSearchActivity)
                ((MediaSearchActivity) activity).interfaceStopDownload = this;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString()
                    + " must implement TextClicked");
        }
    }

    @Override
    public void onDetach()
    {
        interfaceResponse = null; // => avoid leaking, thanks @Deepscorn
        super.onDetach();
    }

    @Override
    protected void initializeComponents()
    {
        super.initializeComponents();

        txtStopStart = (TextView) findViewById(R.id.txtStop);
        txtPercentage = (TextView) findViewById(R.id.txtPercentage);
        txtSize = (TextView) findViewById(R.id.txtSize);
        txtRemain = (TextView) findViewById(R.id.txtRemaining);
        seekBar = (SeekBar) findViewById(R.id.progressdialog);
        seekBar.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                return true;
            }
        });
        Bundle arguments = getArguments();
        if (arguments != null)
        {
            mediaItem = arguments.getParcelable(MediaFragment.EXTRA_MEDIA);
            url = arguments.getString("url");
            String strMediaList = arguments.getString("media_list");
            Gson gson = new Gson();
            Type type = new TypeToken<List<Media>>()
            {
            }.getType();
            arrayListMedia = gson.fromJson(strMediaList, type);
        }
        txtSize.setText(String.valueOf(mediaItem.size));
        downloadContent = new AsychTaskDownloadFileFromURL(activity, mediaItem);
        downloadContent.execute(url);
    }

    @Override
    protected void bindEvents()
    {
        super.bindEvents();

        txtStopStart.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if (mediaItem.isDownloaded == 0)
                {
                    if (flagStartStop == false)
                    {
                        flagStartStop = true;
                        if (downloadContent != null)
                        {
                            total = 0;
                            seekBar.setProgress(0);
                            txtPercentage.setText("0%");
                            txtRemain.setText("");
                            txtStopStart.setText("Start");
                            downloadContent.onPostExecute("");
                            downloadContent.onProgressUpdate("");
                            resetAllValue();
                        }

                    }
                    else
                    {
                        txtStopStart.setText("Stop");
                        callServiceTogetUrl();
                        flagStartStop = false;
                    }
                }


            }
        });
    }
    @Override
    public void processFinish(String output, Media media, int pos) {

    }

    @Override
    public void statusOfDownload(Media media, int pos) {

    }

    public void callServiceTogetUrl()
    {
        try
        {
            onNetworkReady(mediaItem);
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
                        // Toast.makeText(activity, response.optString("Message"), Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        /* again call download service to start download */
                        isCallservice = true;
                        downloadContent = new AsychTaskDownloadFileFromURL(activity, mediaItem);
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

    public void resetAllValue()
    {
        total = 0;
        seekBar.setProgress(0);
        txtPercentage.setText(String.valueOf(0 + " %"));
        txtRemain.setText(txtSize.getText().toString());
    }

    public void onBackPressed()
    {
        if (activity instanceof MediaSearchActivity)
        {
            ((MediaSearchActivity) activity).layoutDynamicFrame.setVisibility(View.GONE);
        }

        if (downloadContent != null)
        {
            downloadContent.cancel(true);
        }/*else {
            downloadContent = new AsychTaskDownloadFileFromURL(activity, mediaItem);
            downloadContent.execute("");
            downloadContent.cancel(true);
        }*/
        /*if(downloadContent != null) {
            downloadContent.cancel(true);
            downloadContent = null;
        }else {
            //getFragmentManager().popBackStack();
        }*/
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (downloadContent != null)
        {
            downloadContent.cancel(true);
        }
        resetAllValue();
    }

    @Override
    public void stopDownload()
    {
        onBackPressed();
    }

    public interface interfaceAsyncResponseDownloadProcess
    {
        void processFinish(String output);
    }

    private class AsychTaskDownloadFileFromURL extends AsyncTask<String, String, String>
    {
        Context context;
        Media media;
        Boolean isSuccess = false;

        public AsychTaskDownloadFileFromURL(Context context, Media media)
        {
            this.context = context;
            this.media = media;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params)
        {
            InputStream input = null;
            OutputStream output = null;
            seekBar.setVisibility(View.VISIBLE);
            int count;
            try
            {
                URL url = new URL(params[0]);
                URLConnection conection = url.openConnection();
                conection.setConnectTimeout(600 * 1000);
                conection.setReadTimeout(600 * 1000);



                int userId = PreferenceHelper.getUserContext(context);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
                //temporary static token
                String token = PreferenceHelper.getToken(context) + String.valueOf(userId).length() + userId + Helper.getEpochTime();
                conection.addRequestProperty(NetworkService.KEY_AUTHORIZATION, token);
                conection.connect();
                //conection.setDoOutput(false);

                // getting file length
                final int lenghtOfFile = conection.getContentLength();

                activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        txtSize.setText(Helper.readableFileSize(lenghtOfFile));
                    }
                });


                input = new BufferedInputStream(conection.getInputStream());


                File mFolder = new File("" + context.getFilesDir());
                if (mFolder.exists())
                {
                    mFolder.delete();
                }
                if (!mFolder.exists())
                    mFolder.mkdirs();
                output = new FileOutputStream(context.getFilesDir() + File.separator + media.id);
                //Log.e(" 11111 ", " output value : " + output.toString());


                byte[] data = new byte[(int) (media.size + 2)];
                int totalBytes = 0;
                while ((count = input.read(data)) != -1)
                {
                    totalBytes += count;

                    total += count;

                    publishProgress("" + (int) ((total * 100) / lenghtOfFile), "" + total);
                    output.write(data, 0, count);
                    //Log.e(" 22222 ", " output value : " + output.toString());
                }
                if (totalBytes == media.size)
                {
                    isSuccess = true;
                    media.isDownloading = 0;
                    Log.e("Downloaded Bytes", String.valueOf(totalBytes));
                }
                // flushing output
                output.flush();
                // closing streams
                output.close();
                input.close();
            }
            catch (Exception e)
            {
                Log.e("catch indoin background", " exception while download processs : " + e.toString());
                //resetAllValue();
                // flushing output
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
                // closing streams
                if (getActivity() != null)
                {
                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            resetAllValue();
                        }
                    });
                }


                e.printStackTrace();
                media.isDownloading = 0;
                isSuccess = false;

                if (isCallservice)
                {
                    //showLongToast("service call start now");
                    //isCallservice=false;
                    if (getActivity() != null)
                    {
                        getActivity().runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable()
                                {
                                    public void run()
                                    {
                                        // Actions to do after 10 seconds
                                        downloadContent = new AsychTaskDownloadFileFromURL(activity, mediaItem);
                                        downloadContent.execute(url);
                                    }
                                }, Helper.NETWORK_CALL_DURATION);
                            }
                        });
                    }

                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... progress)
        {

            Log.e(" progress update ", " exception while download processs : ");
            if (!flagStartStop)
            {
                if (!TextUtils.isEmpty(progress[0]))
                {
                    seekBar.setProgress(Integer.parseInt(progress[0]));
                    txtPercentage.setText(String.valueOf(Integer.parseInt(progress[0])) + " %");
                    txtRemain.setText(Helper.readableFileSize(Long.parseLong(progress[1])));
                }
            }
            else
            {
                resetAllValue();
            }

        }

        @Override
        protected void onCancelled()
        {
            super.onCancelled();
            Log.e(" on cancel", " exception while download processs : ");
            downloadContent.onPostExecute("");
            downloadContent.onProgressUpdate("");
            resetAllValue();
        }

        @Override
        protected void onCancelled(String s)
        {
            super.onCancelled(s);
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            Log.e(" on post execute ", " exception while download processs : ");
            //Log.e(" EEEEEEEE "," on post executed : "+s.toString());
            //seekBar.setVisibility(View.GONE);
            if (isSuccess)
            {
                Log.e(" on post execute ", " is success is true : " + isSuccess);
                media.isDownloaded = 1;
                media.isDownloading = 0;
                DataHelper.updateMedia(context, media);
                if (s != null)
                {
                    if (interfaceResponse != null)
                    {
                        interfaceResponse.processFinish(s);
                    }
                }
                else
                {
                    if (interfaceResponse != null)
                    {
                        interfaceResponse.processFinish("");
                    }
                }
                if (arrayListMedia != null && arrayListMedia.size() > 0)
                {
                    for (int i = 0; i < arrayListMedia.size(); i++)
                    {
                        if (media.id == arrayListMedia.get(i).id)
                        {
                            arrayListMedia.get(i).isDownloaded = 1;
                            break;
                        }
                    }
                }
                Helper.openFileForImageViewer(activity, media, arrayListMedia);

                if (getActivity() != null && getActivity().getSupportFragmentManager() != null && getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0)
                {
                    getActivity().getSupportFragmentManager().popBackStack();
                }

                //getActivity().finish();

            }
            else
            {
                media.isDownloaded = 0;
                media.isDownloading = 0;
                DataHelper.updateMedia(context, media);
                if (interfaceResponse != null)
                {
                    interfaceResponse.processFinish("fail");
                }
            }
        }

    }
}
