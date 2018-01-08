package de.smac.smaccloud.helper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import de.smac.smaccloud.service.SMACCloudApplication;

public class DataProvider

{
    public static final String SERVICE_PROTOCOL = "http://";
    public static String SERVICE_HOST = (!PreferenceHelper.getServiceWeburl(SMACCloudApplication.getInstance()).isEmpty() ? PreferenceHelper.getServiceWeburl(SMACCloudApplication.getInstance()) : SERVICE_PROTOCOL + "46.4.49.27:2010//");
    //public static final String SERVICE_HOST = SERVICE_PROTOCOL + "truckerservices.ssoft.in:8092/";
    /*public static final String SERVICE_HOST = SERVICE_PROTOCOL + "salescloud.ssoft.in:8082/";*/
    //public static final String SERVICE_HOST = SERVICE_PROTOCOL + "salescloud.dharminfotech.com:8082/";
    //public static final String SERVICE_HOST = SERVICE_PROTOCOL + "smaccloud.azurewebsites.net/";
    /*public static final String SERVICE_PATH = SERVICE_HOST + "SalesCloudService.svc/json/";*/
    public static String SERVICE_PATH = SERVICE_HOST + "";
    //public static final String SERVICE_PATH = SERVICE_HOST + "SmacCloudService/";
    //public static final String SERVICE_PATH = SERVICE_HOST + "SmacCloud/";
    public static String ENDPOINT_USER = SERVICE_PATH + "User";
    public static String ENDPOINT_CHANNEL = SERVICE_PATH + "Channel";
    public static String ENDPOINT_FILE = SERVICE_PATH + "File";
    public static String ENDPOINT_SYNC = SERVICE_PATH + "Sync";
    public static String ENDPOINT_SHARE = SERVICE_PATH + "Share";
    //public static final String ENDPOINT_CHECK_HEALTH = "SalesCloudService.svc/json/" + "Check/ServiceHealth";
    public static String ENDPOINT_CHECK_HEALTH = "Check/ServiceHealth";
    public static String ENDPOINT_GET_LOCALIZATION = SERVICE_PATH + "GetALLDataForLocalization";
    public static String ENDPOINT_LOGOUT = SERVICE_PATH + "Logout";
    public static String ENDPOINT_ABOUTUS = SERVICE_PATH + "AboutUs";
    public static String ENDPOINT_UPDATE_TOKEN = SERVICE_PATH + "Update/Token";


    /**
     * Generate random database string from alpha-numeric value
     *
     * @return
     */
    public static String random()
    {
        String validChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        for (int i = 0; i < 8; i++)
        {
            randomStringBuilder.append(validChars.charAt(generator.nextInt(validChars.length())));
        }
        return randomStringBuilder.toString();
    }

    public static JSONObject jsonForGetLocalizationService()
    {
        //String strObj="";
        JSONObject obj = new JSONObject();
        try
        {
            JSONObject objSub = new JSONObject();
            objSub.put("LastSyncDate", "2001-01-01 00:00:00 AM");
            obj.put("Action", "GET_LOCALIZATIONDATA");
            obj.put("Payload", objSub);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return obj;
    }

    public static class Actions
    {
        public static final String AUTHENTICATE_USER = "AUTHENTICATE_USER";
        public static final String DEMO_USER = "CREATE_DEMO_USER";
        public static final String SERVICE_HELTH = "SERVICE_HELTH";
        public static final String FORGOT_PASSWORD = "FORGOT_PASSWORD";
        public static final String GET_USER_CHANNELS = "GET_USER_CHANNELS";
        public static final String GET_MEDIA = "GET_MEDIA";
        public static final String CHANGE_PASSWORD = "CHANGE_PASSWORD";
        public static final String GET_MEDIA_CONTENT = "GET_MEDIA_CONTENT";
        public static final String GET_MEDIA_SIZE = "GET_MEDIA_SIZE";
        public static final String GET_CHANNEL_MEDIA_CONTENT = "GET_CHANNEL_MEDIA_CONTENT";
        public static final String SYNC = "SYNC";
        public static final String LOGOUT = "LOGOUT";
        public static final String MEDIA_LIKE = "MEDIA_LIKE";
        public static final String MEDIA_COMMENT = "MEDIA_COMMENT";
        public static final String SEND_MESSAGE = "SEND_MESSAGE";
        public static final String ABOUTUS = "ABOUTUS";

        public static final String ACTION_LOCALIZATION = "GET_LOCALIZATIONDATA";
    }

    public static class Messages
    {
        public static final String USERLIKE_OBJECT_IS_EMPTY = "UserLike Object is Empty";
    }
}