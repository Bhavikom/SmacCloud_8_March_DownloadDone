package de.smac.smaccloud.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.smac.smaccloud.BuildConfig;
import de.smac.smaccloud.R;
import de.smac.smaccloud.base.Activity;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.data.DataHelper;
import de.smac.smaccloud.helper.DataProvider;

import static de.smac.smaccloud.base.Helper.LOCALIZATION_TYPE_ERROR_CODE;

public class AboutUsActivity extends Activity
{

    public static final int REQUEST_ABOUTUS = 101;
    public static String appName = "AppName";
    public static String releseNumber = "Release VNo";
    public static String license = "License";
    public static String plan = "Plan";
    public static String appNameAndYear = "AppNameAndYear";
    public LinearLayout parentLayout;
    TextView textViewAppTitle, textViewReleaseNo, textViewReleaseNoValue, textViewLicence, textViewLicenceValue, textViewPlan, textViewPlanValue, textViewAppyear, textViewCopirights;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        Helper.retainOrientation(AboutUsActivity.this);
        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);
        textViewAppTitle = (TextView) findViewById(R.id.txt_app_title);
        textViewReleaseNo = (TextView) findViewById(R.id.txt_release_no);
        textViewReleaseNoValue = (TextView) findViewById(R.id.txt_release_no_value);
        textViewLicence = (TextView) findViewById(R.id.txt_licence);
        textViewLicenceValue = (TextView) findViewById(R.id.txt_licence_value);
        textViewPlan = (TextView) findViewById(R.id.txt_plan);
        textViewPlanValue = (TextView) findViewById(R.id.txt_plan_value);
        textViewAppyear = (TextView) findViewById(R.id.txt_app_name_year);
        textViewCopirights = (TextView) findViewById(R.id.txt_copyRights);
        Helper.robotoBoldTypeface = Typeface.createFromAsset(this.getAssets(), Helper.fontPathBold);

        Helper.setupTypeface(parentLayout, Helper.robotoRegularTypeface);
        textViewAppTitle.setTypeface(Helper.robotoBoldTypeface);
        textViewAppyear.setTypeface(Helper.robotoBoldTypeface);
        textViewCopirights.setTypeface(Helper.robotoBoldTypeface);


        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle(getString(R.string.label_about_us));
        }
        if (Helper.isNetworkAvailable(context))
        {
            postNetworkRequest(REQUEST_ABOUTUS, DataProvider.ENDPOINT_ABOUTUS, DataProvider.Actions.ABOUTUS);
        }
        else
        {
            Helper.showMessage(this, false, getString(R.string.msg_please_check_your_connection));
        }

        textViewReleaseNoValue.setText(BuildConfig.VERSION_NAME);

    }

    @Override
    protected void onNetworkReady()
    {
        super.onNetworkReady();
        if (Helper.isNetworkAvailable(context))
        {
            try
            {
                postNetworkRequest(REQUEST_ABOUTUS, DataProvider.ENDPOINT_ABOUTUS, DataProvider.Actions.ABOUTUS);

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            Helper.showMessage(this, false, getString(R.string.msg_please_check_your_connection));
        }

    }


    @Override
    protected void onNetworkResponse(int requestCode, boolean status, String response)
    {
        super.onNetworkResponse(requestCode, status, response);
        if (requestCode == REQUEST_ABOUTUS)
        {
            if (status)
            {
                try
                {
                    JSONObject responseJson = new JSONObject(response);
                    int requestStatus = responseJson.optInt("Status");

                    if (requestStatus > 0)
                    {
                        notifySimple(DataHelper.getLocalizationMessageFromCode(context, String.valueOf(requestStatus), LOCALIZATION_TYPE_ERROR_CODE));
                    }
                    else
                    {
                        JSONArray jsonMainNode = responseJson.getJSONArray("Payload");
                        if (jsonMainNode.length() > 0)
                        {
                            for (int i = 0; i < jsonMainNode.length(); i++)
                            {
                                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                                String key = jsonChildNode.optString("Key");
                                String value = jsonChildNode.optString("Value");

                                if (key.equals(appName))
                                {
                                    textViewAppTitle.setText(value);
                                }
                                /*else if(key.equals(releseNumber)){
                                    //textViewReleaseNo.setText(key);
                                    textViewReleaseNoValue.setText(value);
                                }*/
                                else if (key.equals(license))
                                {
                                    // textViewLicence.setText(key);
                                    textViewLicenceValue.setText(value);
                                }
                                else if (key.equals(plan))
                                {
                                    //textViewPlan.setText(key);
                                    textViewPlanValue.setText(value);
                                }
                                else if (key.equals(appNameAndYear))
                                {
                                    textViewAppyear.setText(value);
                                }


                            }

                        }


                    }
                }
                catch (JSONException e)
                {
                    notifySimple(getString(R.string.msg_invalid_response_from_server));
                }
            }
            else
            {
                notifySimple(getString(R.string.msg_cannot_complete_request));
            }


        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
