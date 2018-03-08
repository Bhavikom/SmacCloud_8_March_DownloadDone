package de.smac.smaccloud.base;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import de.smac.smaccloud.R;
import de.smac.smaccloud.helper.DataProvider;
import de.smac.smaccloud.helper.PreferenceHelper;

/**
 * This class is used to perform network related service
 */
public class NetworkService extends Service
{
    public static String KEY_LANGUAGE_HEADER_PARAM = "Lang";
    public static String KEY_AUTHORIZATION = "Authorization";
    public NetworkRequest request;
    private Binder binder;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        if (binder == null)
            binder = new NetworkBinder();
        return binder;
    }

    public interface RequestCompleteCallback
    {
        public void onRequestComplete(int requestCode, boolean status, String payload);
    }

    public class NetworkBinder extends Binder
    {
        public void postWrappedJSONRequest(final Activity activity, final int requestCode, String url, String action, final RequestCompleteCallback callback, RequestParameter... requestParameters)
        {
            if (Helper.isNetworkAvailable(activity))
            {
                if (request != null)
                    try
                    {
                        throw new RuntimeException("Cannot execute concurrent requests");
                    }
                    catch (Exception ex)
                    {
                        ex.getMessage();
                    }

                try
                {
                    ArrayList<BasicNameValuePair> headerNameValuePairs = new ArrayList<>();
                    ArrayList<RequestParameter> parameters = new ArrayList<>();
                    ArrayList<RequestParameter> files = new ArrayList<>();
                    for (RequestParameter requestParameter : requestParameters)
                    {
                        if (requestParameter.getType().equals(RequestParameter.TYPE_MULTIPART))
                            files.add(requestParameter);
                        else
                            parameters.add(requestParameter);
                    }

                    JSONObject payloadJson = new JSONObject();
                    for (RequestParameter parameter : parameters)
                    {
                        if (parameter.getType().equals(RequestParameter.TYPE_JSON_OBJECT))
                        {
                            payloadJson.put(parameter.getName(), parameter.getJsonObject());
                        }
                        else if (parameter.getType().equals(RequestParameter.TYPE_JSON_ARRAY))
                        {
                            payloadJson.put(parameter.getName(), parameter.getJsonArray());
                        }
                        else
                            payloadJson.put(parameter.getName(), parameter.getValue());
                    }

                    JSONObject requestJson = new JSONObject();
                    requestJson.put("Action", action);
                    requestJson.put("Payload", payloadJson);
                    Log.e("JSON", requestJson.toString());
                    request = new NetworkRequest(activity);
                    request.setBodyType(NetworkRequest.REQUEST_BODY_MULTIPART);
                    request.setRequestType(NetworkRequest.REQUEST_TYPE_NORMAL);
                    request.setRequestListener(new NetworkRequest.RequestListener()
                    {
                        @Override
                        public void onRequestComplete(NetworkResponse networkResponse)
                        {
                            final NetworkRequest retryRequest = request;
                            request = null;
                            if (networkResponse != null && networkResponse.getStatusCode() == 200 && networkResponse.getResponse() != null && !networkResponse.getResponse().isEmpty())
                            {
                                try
                                {
                                    JSONObject jsonResponse = new JSONObject(networkResponse.getResponse());
                                    if (jsonResponse.has("Status") && !jsonResponse.isNull("Status") && jsonResponse.optInt("Status") == 2113) // Status = 2113 means "USER_TOKEN_NOT_VALID"
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
                                                                if (retryRequest != null)
                                                                    retryRequest.execute();
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
                                    else
                                    {
                                        request = null;
                                        callback.onRequestComplete(requestCode, true, networkResponse.getResponse());
                                    }
                                }
                                catch (Exception ex)
                                {
                                    request = null;
                                    activity.notifySimple(getString(R.string.try_again_downloading));
                                    callback.onRequestComplete(requestCode, true, networkResponse.getResponse());
                                    ex.printStackTrace();
                                }
                            }
                            else
                            {
                                callback.onRequestComplete(requestCode, false, String.valueOf(networkResponse.getStatusCode()));
                            }
                            //--------------------------------------------------------------------------------
                            /*if (networkResponse.getStatusCode() == 200)
                            {
                                if (networkResponse.getResponse() != null)
                                {
                                    try
                                    {
                                        JSONObject objResponse = new JSONObject(networkResponse.getResponse().toString());
                                        if (objResponse.has("Status") && objResponse.optInt("Status") == 2113) // Status = 2113 means "USER_TOKEN_NOT_VALID"
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
                                                        request.setHeaders(headerNameValuePairs1);
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
                                                    Toast.makeText(NetworkService.this, networkResponse.getResponse(), Toast.LENGTH_SHORT).show();
                                                    if positive
                                                    callback.onRequestComplete(requestCode, true, networkResponse.getResponse());
                                                }
                                            });
                                        }
                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                                else
                                {
                                    callback.onRequestComplete(requestCode, true, networkResponse.getResponse());
                                }
                            }
                            else
                            {
                                callback.onRequestComplete(requestCode, false, String.valueOf(networkResponse.getStatusCode()));
                            }*/
                            //request = null;
                        }
                    });
                    //request.setProgressMode(NetworkRequest.PROGRESS_MODE_DIALOG_SPINNER);
                    //request.setProgressMessage(getString(R.string.msg_please_wait));

                    request.setRequestUrl(url);
                    //headerNameValuePairs.add(new BasicNameValuePair(KEY_LANGUAGE_HEADER_PARAM, Locale.getDefault().getLanguage()));
                    try
                    {
                        if (PreferenceHelper.getUserContext(activity) != -1)
                        {
                            int userId = PreferenceHelper.getUserContext(activity);
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
                            String token = PreferenceHelper.getToken(activity) + String.valueOf(userId).length() + userId + Helper.getEpochTime();
                            if (token != null && !token.isEmpty())
                            {
                                headerNameValuePairs.add(new BasicNameValuePair(KEY_AUTHORIZATION, token));
                                request.setHeaders(headerNameValuePairs);
                            }
                        }
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                        Log.e(" ####$$$$ ", " exception while serivce call in network ");
                    }
                    parameters = new ArrayList<>();
                    parameters.add(RequestParameter.multiPart("Request", requestJson.toString()));
                    parameters.addAll(files);
                    request.setParameters(parameters);
                    request.execute();
                }
                catch (JSONException | IOException e)
                {
                    e.printStackTrace();
                    callback.onRequestComplete(requestCode, false, "UNKNOWN");
                }
            }
            else
            {
                activity.notifySimple(getString(R.string.msg_network_connection_not_available));
            }
        }

    }
}