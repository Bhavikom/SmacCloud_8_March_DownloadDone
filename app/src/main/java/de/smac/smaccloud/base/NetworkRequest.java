package de.smac.smaccloud.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Base64;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EncodingUtils;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;


import de.smac.smaccloud.R;
import de.smac.smaccloud.service.DownloadService;

/**
 * This class is use to send request to server
 */

@SuppressWarnings("unused")
public class NetworkRequest extends AsyncTask<Void, Long, NetworkResponse>
{

    public static final String PROGRESS_MODE_NONE = "mode_none";
    public static final String PROGRESS_MODE_DIALOG_SPINNER = "mode_spinner";
    public static final String PROGRESS_MODE_NOTIFICATION = "mode_notification";

    public static final String METHOD_GET = "method_get";
    public static final String METHOD_POST = "method_post";

    public static final String AUTHENTICATION_NONE = "auth_none";
    public static final String AUTHENTICATION_BASIC = "auth_basic";

    public static final String REQUEST_BODY_URL_ENCODED = "body_url_encoded";
    public static final String REQUEST_BODY_MULTIPART = "body_multipart";
    public static final String REQUEST_BODY_BARE = "body_bare";

    public static final String REQUEST_TYPE_NORMAL = "type_normal";
    public static final String REQUEST_TYPE_DOWNLOAD = "type_download";
    public static final String REQUEST_TYPE_UPLOAD = "type_upload";

    protected ProgressDialog asyncProgressDialog;
    protected Context context;
    protected String requestUrl;

    private String progressMode = PROGRESS_MODE_DIALOG_SPINNER;
    private String requestMethod = METHOD_POST;
    private String authenticationMode = AUTHENTICATION_NONE;
    private String bodyType = REQUEST_BODY_URL_ENCODED;
    private String requestType = REQUEST_TYPE_NORMAL;
    private String progressMessage = "";
    private String authorizationToken;
    private RequestListener requestListener;
    private ArrayList<BasicNameValuePair> headers;
    private ArrayList<RequestParameter> parameters;

    public NetworkRequest(Context context)
    {
        this.context = context;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        switch (progressMode)
        {
            case PROGRESS_MODE_DIALOG_SPINNER:

                if (Helper.IS_DIALOG_SHOW)
                {
                    asyncProgressDialog = new ProgressDialog(context);
                    asyncProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    asyncProgressDialog.setIndeterminate(true);
                    asyncProgressDialog.setMessage(progressMessage.isEmpty() ? context.getString(R.string.msg_please_wait) : progressMessage);
                    asyncProgressDialog.setCancelable(false);
                    asyncProgressDialog.show();
                }
                break;
            case PROGRESS_MODE_NOTIFICATION:
            case PROGRESS_MODE_NONE:
            default:
                break;
        }
    }

    @Override
    protected NetworkResponse doInBackground(Void... voids)
    {
        NetworkResponse networkResponse = new NetworkResponse();

        try
        {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = null;
            HttpRequestBase request = null;

            switch (requestMethod)
            {
                case METHOD_GET:
                    StringBuilder builder = new StringBuilder(requestUrl + "?");
                    if (parameters != null)
                    {
                        for (RequestParameter parameter : parameters)
                        {
                            if (parameter.getType().equals(RequestParameter.TYPE_URL_ENCODED))
                            {
                                builder.append(parameter.getName());
                                builder.append("=");
                                builder.append(parameter.getValue());
                                builder.append("&");
                            }
                        }
                    }
                    builder.substring(0, builder.lastIndexOf("&"));
                    if (builder.length() > 255)
                    {
                        throw new RuntimeException("URL size exceeded 255 characters");
                    }
                    request = new HttpGet(builder.toString());
                    break;

                case METHOD_POST:
                    request = new HttpPost(requestUrl);

                    switch (bodyType)
                    {
                        case REQUEST_BODY_BARE:
                            ((HttpPost) request).setEntity(new StringEntity(parameters.get(0).getValue()));
                            break;

                        case REQUEST_BODY_URL_ENCODED:
                            ArrayList<BasicNameValuePair> nameValuePairs = new ArrayList<>();
                            if (parameters != null)
                            {
                                for (RequestParameter parameter : parameters)
                                {
                                    if (parameter.getType().equals(RequestParameter.TYPE_URL_ENCODED))
                                        nameValuePairs.add(new BasicNameValuePair(parameter.getName(), parameter.getValue()));
                                }
                            }
                            ((HttpPost) request).setEntity(new UrlEncodedFormEntity(nameValuePairs));
                            break;

                        case REQUEST_BODY_MULTIPART:
                            MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                            if (parameters != null)
                            {
                                for (RequestParameter parameter : parameters)
                                {
                                    if (parameter.getType().equals(RequestParameter.TYPE_MULTIPART))
                                        multipartEntity.addPart(parameter.getName(), parameter.getBodyPart());
                                }
                            }
                            ((HttpPost) request).setEntity(multipartEntity);
                            break;
                    }
                    break;
            }
            if (request != null)
            {
                switch (authenticationMode)
                {
                    case AUTHENTICATION_BASIC:
                        String headerValue = "Basic " + authorizationToken;
                        request.setHeader(HttpHeaders.AUTHORIZATION, headerValue);
                        break;
                }
                if (headers != null && headers.size() > 0)
                {
                    for (BasicNameValuePair header : headers)
                    {
                        request.setHeader(header.getName(), header.getValue());
                    }
                }
                // For set timeout
                final HttpParams httpParameters = httpClient.getParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 600 * 1000);
                HttpConnectionParams.setSoTimeout(httpParameters, 600 * 1000);

                response = httpClient.execute(request);
            }
            if (response != null)
            {
                int statusCode = response.getStatusLine().getStatusCode();
                networkResponse.setStatusCode(statusCode);
                HashMap<String, String> headers = new HashMap<>();
                for (Header header : response.getAllHeaders())
                {
                    headers.put(header.getName(), header.getValue());
                }
                networkResponse.setHeaders(headers);
                switch (statusCode / 100)
                {
                    case 2:
                        switch (requestType)
                        {
                            case REQUEST_TYPE_NORMAL:
                                networkResponse.setResponse(EntityUtils.toString(response.getEntity()));
                                break;

                            case REQUEST_TYPE_DOWNLOAD:
                                byte[] rawByteBuffer = new byte[8192];
                                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                                HttpEntity responseEntity = response.getEntity();
                                switch (progressMode)
                                {
                                    case PROGRESS_MODE_NONE:
                                        responseEntity.writeTo(byteStream);
                                        break;

                                    case PROGRESS_MODE_DIALOG_SPINNER:
                                    case PROGRESS_MODE_NOTIFICATION:
                                        InputStream networkStream = responseEntity.getContent();
                                        int length;
                                        long contentLength = responseEntity.getContentLength();
                                        long writtenBytes = 0;
                                        while ((length = networkStream.read(rawByteBuffer)) != -1)
                                        {
                                            byteStream.write(rawByteBuffer, 0, length);
                                            writtenBytes = writtenBytes + length;
                                            publishProgress(writtenBytes, (writtenBytes * 100) / contentLength);
                                        }
                                        networkStream.close();
                                        break;
                                }
                                networkResponse.setRawResponse(byteStream.toByteArray());
                                byteStream.close();

                            default:
                                break;
                        }
                        break;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            if (e instanceof UnknownHostException)
            {
                if (context instanceof Activity)
                    ((Activity) context).notifySimple(context.getString(R.string.msg_please_check_your_connection));
            }
            else if (e instanceof IOException)
            {
                if (context instanceof Activity)
                    ((Activity) context).notifySimple(context.getString(R.string.msg_cannot_get_response_from_server));

                if (DownloadService.isDownloading)
                    context.stopService(new Intent(context, DownloadService.class));
            }
        }
        return networkResponse;
    }

    @Override
    protected void onProgressUpdate(Long... values)
    {
        super.onProgressUpdate(values);
        switch (progressMode)
        {
            case PROGRESS_MODE_DIALOG_SPINNER:
                int percent = (int) values[1].longValue();

                if (asyncProgressDialog != null)
                {
                    asyncProgressDialog.setIndeterminate(false);
                    asyncProgressDialog.setProgress(percent);
                }
                if (requestListener instanceof ProgressiveRequestListener)
                {
                    ((ProgressiveRequestListener) requestListener).onProgressUpdate(percent);
                    ((ProgressiveRequestListener) requestListener).onProgressUpdate(values[0]);
                }
                break;
        }
    }

    @Override
    protected void onPostExecute(NetworkResponse response)
    {
        super.onPostExecute(response);
        Helper.IS_DIALOG_SHOW = true;
        switch (progressMode)
        {
            case PROGRESS_MODE_DIALOG_SPINNER:
                if (asyncProgressDialog != null)
                {
                    if (asyncProgressDialog.isShowing())
                    {
                        asyncProgressDialog.dismiss();
                    }
                }
                break;

            case PROGRESS_MODE_NONE:
            case PROGRESS_MODE_NOTIFICATION:
            default:
                break;
        }
        if (requestListener != null)
        {
            try
            {
                requestListener.onRequestComplete(response);
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

    public void setProgressMode(String progressMode)
    {
        this.progressMode = progressMode;
    }

    public void setRequestMethod(String requestMethod)
    {
        this.requestMethod = requestMethod;
    }

    public void setRequestUrl(String requestUrl)
    {
        this.requestUrl = requestUrl;
    }

    public void setAuthenticationMode(String authenticationMode)
    {
        this.authenticationMode = authenticationMode;
    }

    public void setCredentials(String username, String password)
    {
        authorizationToken = Base64.encodeToString(EncodingUtils.getAsciiBytes(username + ":" + password), Base64.NO_WRAP);
    }

    public void setHeaders(ArrayList<BasicNameValuePair> headers)
    {
        this.headers = headers;
    }

    public void setAuthorizationToken(String token)
    {
        this.authorizationToken = token;
    }

    public void setBodyType(String bodyType)
    {
        this.bodyType = bodyType;
    }

    public void setRequestType(String requestType)
    {
        this.requestType = requestType;
    }

    public void setProgressMessage(String progressMessage)
    {
        this.progressMessage = progressMessage;
    }

    public void setRequestListener(RequestListener requestListener)
    {
        this.requestListener = requestListener;
    }

    public void setParameters(ArrayList<RequestParameter> parameters)
    {
        this.parameters = parameters;
    }

    public interface RequestListener
    {
        public void onRequestComplete(NetworkResponse networkResponse) throws JSONException, UnsupportedEncodingException;
    }

    public interface ProgressiveRequestListener extends RequestListener
    {
        public void onProgressUpdate(int progressPercent);

        public void onProgressUpdate(long progressByteCount);
    }
}
