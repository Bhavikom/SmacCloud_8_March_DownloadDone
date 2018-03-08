package de.smac.smaccloud.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import de.smac.smaccloud.R;
import de.smac.smaccloud.base.Activity;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.base.RequestParameter;
import de.smac.smaccloud.data.DataHelper;
import de.smac.smaccloud.helper.DataProvider;
import de.smac.smaccloud.helper.PreferenceHelper;

import static de.smac.smaccloud.base.Helper.LOCALIZATION_TYPE_ERROR_CODE;

public class OrganizationActivity extends Activity implements View.OnClickListener
{

    public static final int REQUEST_ORGANIZATION = 101;
    public static final String IS_FROM_DEMO = "isFromDemo";
    private static final String KEY_TEXT_VALUE = "textValue";
    public LinearLayout parentLayout;
    public PreferenceHelper prefManager;
    Button btnContinue;
    EditText editOrganization;
    String validUrl;
    Boolean isFromDemo = false;
    Bundle bundle;
    ImageView btnInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        prefManager = new PreferenceHelper(this);
        setContentView(R.layout.activity_organization);
        Helper.retainOrientation(OrganizationActivity.this);
        btnContinue = (Button) findViewById(R.id.btn_continue);
        editOrganization = (EditText) findViewById(R.id.edit_organization);
        btnInfo = (ImageView) findViewById(R.id.img_info);
        //editOrganization.setText("http://138.201.245.106:3101/");
        //editOrganization.setText("http://46.4.49.27:2010/");
        //editOrganization.setText("https://smaccloud.smacsoftwares.de:2020/");
        editOrganization.setText("http://smac-local.sambt.xyz:2020/User");
        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);

        Helper.setupTypeface(parentLayout, Helper.robotoRegularTypeface);
        //btnContinue.setTypeface(Helper.robotoMediumTypeface);
        btnContinue.setOnClickListener(this);
        btnInfo.setOnClickListener(this);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle(getString(R.string.back));
        }
        if (savedInstanceState != null)
        {
            CharSequence savedText = savedInstanceState.getCharSequence(KEY_TEXT_VALUE);
            editOrganization.setText(savedText);
        }
        bundle = getIntent().getExtras();

        if (bundle != null)
        {
            if (bundle.containsKey(IS_FROM_DEMO))
            {
                if (isFromDemo != null)
                {
                    isFromDemo = bundle.getBoolean(IS_FROM_DEMO);
                }
            }
        }


    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (PreferenceHelper.hasUserContext(context))
        {
            finish();
        }


    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_continue:
                String email = editOrganization.getText().toString();
                if (email.isEmpty())
                {
                    editOrganization.setError(getString(R.string.enter_Organization_name));
                }
                else if (Helper.isUrlValid(email))
                {
                    validUrl = email;
                    String checkHealthUrl = "";
                    if (email.substring(email.length() - 1).equals("/"))
                    {
                        checkHealthUrl = email + DataProvider.ENDPOINT_CHECK_HEALTH;
                    }
                    else
                    {
                        checkHealthUrl = email + "/" + DataProvider.ENDPOINT_CHECK_HEALTH;
                    }
                    Helper.hideSoftKeyboard(OrganizationActivity.this);
                    if (Helper.isNetworkAvailable(context))
                    {
                        postNetworkRequest(REQUEST_ORGANIZATION, checkHealthUrl, DataProvider.Actions.SERVICE_HELTH,
                                RequestParameter.urlEncoded("OrgId", "0"));

                    }
                    else
                    {
                        Helper.showMessage(OrganizationActivity.this, false, getString(R.string.msg_please_check_your_connection));
                    }
                }
                else
                {
                    AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                    alertDialog.setTitle(context.getString(R.string.alert));
                    alertDialog.setIcon(context.getResources().getDrawable(R.drawable.ic_alert));
                    alertDialog.setMessage(context.getString(R.string.invalid_organization));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.ok),
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
                break;
            case R.id.img_info:
                final BottomSheetDialog btmSheetSortDialog = new BottomSheetDialog(context);
                btmSheetSortDialog.setContentView(R.layout.activity_bottom_sheet_behavior);
                btmSheetSortDialog.show();
                break;

        }

    }

    @Override
    protected void onNetworkResponse(int requestCode, boolean status, String response)
    {
        super.onNetworkResponse(requestCode, status, response);

        if (requestCode == REQUEST_ORGANIZATION)
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
                        PreferenceHelper.storeServiceWeburl(context, validUrl);

                        DataProvider.SERVICE_HOST = validUrl;
                        DataProvider.SERVICE_PATH = DataProvider.SERVICE_HOST;

                        DataProvider.ENDPOINT_USER = DataProvider.SERVICE_PATH + "User";
                        DataProvider.ENDPOINT_CHANNEL = DataProvider.SERVICE_PATH + "Channel";
                        DataProvider.ENDPOINT_FILE = DataProvider.SERVICE_PATH + "File";
                        DataProvider.ENDPOINT_SYNC = DataProvider.SERVICE_PATH + "Sync";
                        DataProvider.ENDPOINT_SHARE = DataProvider.SERVICE_PATH + "Share";
                        DataProvider.ENDPOINT_CHECK_HEALTH = "Check/ServiceHealth";
                        DataProvider.ENDPOINT_GET_LOCALIZATION = DataProvider.SERVICE_PATH + "GetALLDataForLocalization";
                        DataProvider.ENDPOINT_LOGOUT = DataProvider.SERVICE_PATH + "Logout";
                        DataProvider.ENDPOINT_ABOUTUS = DataProvider.SERVICE_PATH + "AboutUs";
                        DataProvider.ENDPOINT_UPDATE_TOKEN = DataProvider.SERVICE_PATH + "Update/Token";
                        prefManager.setFirstTimeConfigureServer(true);


                        if (bundle != null && bundle.containsKey(IS_FROM_DEMO))
                        {
                            launchDemoScreen();
                        }
                        else
                        {
                            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(i);
                            finish();
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
    public boolean onTouchEvent(MotionEvent event)
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(KEY_TEXT_VALUE, editOrganization.getText());
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

    public void launchDemoScreen()
    {
        Intent i = new Intent(OrganizationActivity.this, DemoActivity.class);
        startActivity(i);
        finish();
    }

}
