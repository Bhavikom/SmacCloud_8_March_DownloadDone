package de.smac.smaccloud.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.model.UserPreference;

/**
 * This class is use to work with Shared Preferences which is for store permanent data of application
 */
public class PreferenceHelper
{
    public static final String KEY_SWITCH_STATUS = "off";
    public static final String KEY_SERVICE_WEBURL = "serviceWebUrl";
    public static final String KEY_EMAIL_BODY = "emailBody";
    public static final String KEY_SET_SIGNATURE = "setSignature";
    public static final String KEY_SET_SIGNATURE_IMAGE = "setSignatureImage";
    public static final String KEY_EMAIL_CC_ADDRESS = "emailCCAddress";
    public static final String KEY_EMAIL_BCC_ADDRESS = "emailBCCAddress";
    public static final String KEY_USER_ID = "userId";
    private static final String KEY_LANGUAGE_SELECTED = "language";
    private static final String KEY_LAST_SYNC_DATE = "last_sync_date";
    private static final String KEY_MEDIA_SIZE = "mediaSize";
    private static final String KEY_APP_ICON = "appIcon";
    private static final String KEY_APP_COLOR = "appColor";
    private static final String KEY_APP_BACK_COLOR = "appBackColor";
    private static final String KEY_APP_FONT_COLOR = "appFontColor";
    private static final String KEY_APP_FONT_NAME = "appFontName";
    private static final String KEY_APP_ORG_ID = "org_id";
    // Shared preferences file name
    private static final String PREF_NAME = "smac_cloud";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String IS_DEMO_LOGIN = "IsDemoLogin";
    private static final String IS_FULL_DOWNLOAD_MEDIA = "IsFullDownLoadMedia";
    private static final String KEY_TOKEN = "Token";
    private static final String KEY_FCM_TOKEN_ID = "FCMTokenId";
    private static final String IS_FIRST_TIME_CONFIGURE_SERVER = "isFirstTimeConfigureServerLaunch";
    private static final String IS_DEMO_ACTIVITY = "isDemoActivity";

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    public PreferenceHelper(Context context)
    {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public static void storeUserContext(Context context, UserPreference preference)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_USER_ID, preference.userId);
        editor.apply();
    }

    public static void storeSelectedLanguage(Context context, String language)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_LANGUAGE_SELECTED, language);
        editor.apply();
        Helper.changeLanguage(context, language);
    }

    public static String getSelectedLanguage(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(KEY_LANGUAGE_SELECTED, "");
    }

    public static boolean hasUserContext(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(KEY_USER_ID, -1) != -1;
    }

    public static int getUserContext(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(KEY_USER_ID, -1);
    }

    public static void removeUserContext(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    public static void storeSyncStatus(Context context, boolean isChecked)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_SWITCH_STATUS, isChecked);
        editor.apply();

    }

    public static boolean getSyncStatus(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(KEY_SWITCH_STATUS, false);
    }

    public static String getServiceWeburl(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(KEY_SERVICE_WEBURL, "");
    }

    public static void storeServiceWeburl(Context context, String serviceWeburl)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_SERVICE_WEBURL, serviceWeburl);
        editor.apply();
    }

    public static void saveLastSyncDate(Context context, String date)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_LAST_SYNC_DATE, date);
        editor.apply();
    }

    public static String getLastSychDate(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(KEY_LAST_SYNC_DATE, "2001-01-01 00:00:00 AM");
    }

    public static void storeToken(Context context, String token)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public static String getToken(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(KEY_TOKEN, "");
    }

    public static void storeFCMTokenId(Context context, String token)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_FCM_TOKEN_ID, token);
        editor.apply();
    }

    public static String getFCMTokenId(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(KEY_FCM_TOKEN_ID, "00000-00000-00000-00000-00000");
    }

    public static void storeEmailBody(Context context, String emailBody)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_EMAIL_BODY, emailBody);
        editor.apply();
    }

    public static String getEmailBody(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(KEY_EMAIL_BODY, "");
    }

    public static void storeAppIcon(Context context, String appColor)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_APP_ICON, appColor);
        editor.apply();
    }

    public static String getAppIcon(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(KEY_APP_ICON, "");
    }

    public static void storeAppColor(Context context, String appColor)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_APP_COLOR, appColor);
        editor.apply();
    }

    public static String getAppColor(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(KEY_APP_COLOR, "#FF950F");
    }

    public static void storeAppBackColor(Context context, String appBackColor)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_APP_BACK_COLOR, appBackColor);
        editor.apply();
    }

    public static String getAppBackColor(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String colorCode = preferences.getString(KEY_APP_BACK_COLOR, "");
        return !colorCode.isEmpty() ? colorCode : "#000000";
    }

    public static void storeAppFontColor(Context context, String appFontColor)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_APP_FONT_COLOR, appFontColor);
        editor.apply();
    }

    public static String getAppFontColor(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(KEY_APP_FONT_COLOR, "#FFFFFF");
    }

    public static void storeAppFontName(Context context, String appFontName)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_APP_FONT_NAME, appFontName);
        editor.apply();
    }

    public static String getAppFontName(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(KEY_APP_FONT_NAME, "");
    }

    public static void storeMediaSize(Context context, long mediaSize)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(KEY_MEDIA_SIZE, mediaSize);
        editor.apply();
    }

    public static long getMediaSize(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getLong(KEY_MEDIA_SIZE, 0);
    }

    public static String getSignature(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(KEY_SET_SIGNATURE, "");
    }

    public static void storeSetSignature(Context context, String emailBody)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_SET_SIGNATURE, emailBody);
        editor.apply();
    }

  /*  public static void storeSetSignatureRemove(Context context, String emailBody)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(KEY_SET_SIGNATURE);
        editor.apply();
    }*/

    public static String getSignatureImage(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(KEY_SET_SIGNATURE_IMAGE, "");
    }

    public static void storeSetSignatureImage(Context context, String emailBody)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_SET_SIGNATURE_IMAGE, emailBody);
        editor.apply();
    }

    public static void storeSetSignatureImageRemove(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(KEY_SET_SIGNATURE_IMAGE);
        editor.apply();
    }

    public static void storeEmailCCAddress(Context context, String emailCcAddress)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_EMAIL_CC_ADDRESS, emailCcAddress);
        editor.apply();
    }


    public static String getEmailCcAddress(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(KEY_EMAIL_CC_ADDRESS, "");
    }

    public static void storeEmailBCCAddress(Context context, String emailCcAddress)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_EMAIL_BCC_ADDRESS, emailCcAddress);
        editor.apply();
    }

    public static String getEmailBccAddress(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(KEY_EMAIL_BCC_ADDRESS, "");
    }

    public static String getOrganizationId(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(KEY_APP_ORG_ID, "");
    }

    public static void storeOrganizationId(Context context, String organizationId)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_APP_ORG_ID, organizationId);
        editor.apply();
    }

    public static void removeUserThemePreferences(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(KEY_APP_ORG_ID);
        editor.remove(KEY_APP_FONT_NAME);
        editor.remove(KEY_APP_ICON);
        editor.remove(KEY_APP_COLOR);
        editor.remove(KEY_APP_BACK_COLOR);
        editor.remove(KEY_APP_FONT_COLOR);
        editor.commit();

    }

    public boolean isDemoLogin()
    {
        return pref.getBoolean(IS_DEMO_LOGIN, false);
    }

    public void saveDemoLogin(boolean demoLogin)
    {
        editor.putBoolean(IS_DEMO_LOGIN, demoLogin);
        editor.commit();
    }

    public boolean isFullDownloadMedia()
    {
        return pref.getBoolean(IS_FULL_DOWNLOAD_MEDIA, false);
    }

    public void saveFullDownloadMedia(boolean fullDownloadMedia)
    {
        editor.putBoolean(IS_FULL_DOWNLOAD_MEDIA, fullDownloadMedia);
        editor.commit();
    }


    public boolean isFirstTimeLaunch()
    {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setFirstTimeLaunch(boolean isFirstTime)
    {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }


    public boolean isFirstTimeConfigureServerLanuch()
    {
        return pref.getBoolean(IS_FIRST_TIME_CONFIGURE_SERVER, false);
    }

    public void setFirstTimeConfigureServer(boolean isFirstTimeConfigureServer)
    {
        editor.putBoolean(IS_FIRST_TIME_CONFIGURE_SERVER, isFirstTimeConfigureServer);
        editor.commit();
    }


}


