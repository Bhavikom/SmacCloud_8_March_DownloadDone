package de.smac.smaccloud.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.smac.smaccloud.R;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.base.RequestParameter;
import de.smac.smaccloud.data.DataHelper;
import de.smac.smaccloud.helper.DataProvider;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.model.LocalizationData;

import static de.smac.smaccloud.base.Helper.LOCALIZATION_TYPE_ERROR_CODE;


public class SplashActivity extends de.smac.smaccloud.base.Activity
{

    public static final int REQUEST_GETLOCALIZATION = 5001;
    private static int SPLASH_TIME_OUT = 3000;
    ArrayList<LocalizationData> arrayListLocalization = new ArrayList<>();
    Activity activity;
    ImageView img_app_icon;
    private Handler splashHandler;
    private Runnable splashRunnable;
    private LinearLayout parentLayout;
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splace);
        Helper.retainOrientation(SplashActivity.this);
        activity = this;
        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);

        Helper.robotoRegularTypeface = Typeface.createFromAsset(this.getAssets(), Helper.fontPathRoboto);

        Helper.setupTypeface(parentLayout, Helper.robotoRegularTypeface);

        img_app_icon = (ImageView) findViewById(R.id.img_app_icon);

        String iconPath = PreferenceHelper.getAppIcon(context);
        if (iconPath.isEmpty())
        {
            img_app_icon.setImageResource(R.drawable.ic_logo);
        }
        else
        {
            Glide.with(context)
                    .load(iconPath)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(img_app_icon);
        }

        String lang = PreferenceHelper.getSelectedLanguage(SplashActivity.this);
        if (lang != null && !lang.isEmpty())
        {
            Helper.changeLanguage(SplashActivity.this, lang);
        }
        else
        {
            lang = "de";
            Helper.changeLanguage(SplashActivity.this, lang);
        }

    }

    @Override
    public void onBackPressed()
    {

    }

    @Override
    protected void onNetworkReady()
    {
        super.onNetworkReady();
        if (Helper.isNetworkAvailable(activity))
        {
            try
            {
                Helper.IS_DIALOG_SHOW = false;
                postNetworkRequest(REQUEST_GETLOCALIZATION, DataProvider.ENDPOINT_GET_LOCALIZATION, DataProvider.Actions.ACTION_LOCALIZATION,
                        RequestParameter.urlEncoded("LastSyncDate", PreferenceHelper.getLastSychDate(context)));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            if (PreferenceHelper.hasUserContext(SplashActivity.this))
            {
                if (PreferenceHelper.getSyncStatus(SplashActivity.this))
                    startDashboardActivity();
                else
                    startSyncActivity();
            }
            else
            {
                Intent i = new Intent(SplashActivity.this, IntroScreenActivity.class);
                startActivity(i);
                finish();
            }

        }


    }

    @Override
    protected void onNetworkResponse(int requestCode, boolean status, String response)
    {
        super.onNetworkResponse(requestCode, status, response);
        Log.e("TEST>>", response);
        Helper.IS_DIALOG_SHOW = true;
        if (requestCode == REQUEST_GETLOCALIZATION)
        {
            if (status)
            {
                try
                {
                    JSONObject jsonObjectMain = new JSONObject(response);

                    int requestStatus = jsonObjectMain.optInt("Status");
                    if (requestStatus > 0)
                    {
                        notifySimple(DataHelper.getLocalizationMessageFromCode(context, String.valueOf(requestStatus), LOCALIZATION_TYPE_ERROR_CODE));
                    }
                    else
                    {

                        //PreferenceHelper.saveLastSyncDate(this, jsonObjectMain.getString("Message"));
                        JSONObject userJson = jsonObjectMain.optJSONObject("Payload");
                        PreferenceHelper.saveLastSyncDate(this, userJson.optString("LastSyncDate"));
                        if (userJson.has("LocalizationList"))
                        {
                            JSONArray jsonArray = userJson.getJSONArray("LocalizationList");
                            arrayListLocalization = new ArrayList<>();
                            arrayListLocalization = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<LocalizationData>>()
                            {

                            }.getType());

                            if (arrayListLocalization.size() > 0)
                            {
                                DataHelper.fillLocalizationTable(this, arrayListLocalization);
                            }
                            //Log.e("TEST>>"," get localization : " + DataHelper.getLocalizationMessageFromCode(this,"2101","en-en","3"));
                            if (PreferenceHelper.hasUserContext(SplashActivity.this))
                            {
                                if (PreferenceHelper.getSyncStatus(SplashActivity.this))
                                    startDashboardActivity();
                                else
                                    startSyncActivity();
                            }
                            else
                            {
                                Intent i = new Intent(SplashActivity.this, IntroScreenActivity.class);
                                startActivity(i);
                                finish();
                            }

                        }

                    }
                }
                catch (Exception e)
                {
                    notifySimple(getString(R.string.msg_invalid_response_from_server));
                }
            }
            else
            {
                if (PreferenceHelper.hasUserContext(SplashActivity.this))
                {
                    if (PreferenceHelper.getSyncStatus(SplashActivity.this))
                        startDashboardActivity();
                    else
                        startSyncActivity();
                }
                else
                {
                    Intent i = new Intent(SplashActivity.this, IntroScreenActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        }
        else
        {
            notifySimple(getString(R.string.msg_cannot_complete_request));
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        flag = false;
        Helper.IS_DIALOG_SHOW = true;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        flag = true;
        //splashHandler.postDelayed(splashRunnable, SPLASH_TIME_OUT);
    }

    private void startDashboardActivity()
    {
        Intent dashboardIntent = new Intent(SplashActivity.this, DashboardActivity.class);
        dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(dashboardIntent);
        finish();
    }

    private void startSyncActivity()
    {
        Intent dashboardIntent = new Intent(SplashActivity.this, SyncActivity.class);
        dashboardIntent.putExtra(SyncActivity.IS_FROM_SETTING, false);
        startActivity(dashboardIntent);
        finish();
    }
}
