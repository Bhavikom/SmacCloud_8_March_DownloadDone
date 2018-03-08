package de.smac.smaccloud.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.michael.easydialog.EasyDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

import de.smac.smaccloud.R;
import de.smac.smaccloud.base.Activity;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.base.RequestParameter;
import de.smac.smaccloud.data.DataHelper;
import de.smac.smaccloud.helper.DataProvider;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.model.Channel;
import de.smac.smaccloud.model.User;
import de.smac.smaccloud.model.UserPreference;
import de.smac.smaccloud.service.FCMInstanceIdService;

import static de.smac.smaccloud.base.Helper.LOCALIZATION_TYPE_ERROR_CODE;

public class LoginActivity extends Activity
{
    public static final int REQUEST_CHECK_HEALTH = 101;
    public static final int REQUEST_LOGIN = 4301;
    public static final int REQUEST_ORGANIZATION = 4306;
    public static final int REQUEST_GET_SETTINGS = 102;
    public static final int REQUEST_MEDIA_SIZE = 4302;
    private static final int REQUEST_FORGOT_PASSWORD = 4303;
    private static final String KEY_TEXT_VALUE = "textValue";
    public PreferenceHelper prefManager;
    public LinearLayout parentLayout;
    String validUrl;
    String strOrganization = "";
    String strEmail = "";
    String strPassword = "";
    android.app.AlertDialog alertDialogForgetPassword;
    TextInputLayout textInputMail, textInputPassword;
    //CustomProgressDialog progressDialog;
    ProgressDialog progressDialog;
    long totalSizeInByte;
    ImageView imageVieworganizationInfo;
    private TextView textTitle;
    private EditText editEmail;
    private EditText editPassword;
    private EditText editOrganization;
    private ImageView imgVisibility;
    private Button buttonForgetPassword;
    private Button btnLogin;
    private String deviceId = "00000-00000-00000-00000-00000";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        prefManager = new PreferenceHelper(this);
        if (PreferenceHelper.hasUserContext(context))
        {
            startDashboardActivity();
        }
        setContentView(R.layout.activity_login);
        Helper.retainOrientation(LoginActivity.this);

        if (!PreferenceHelper.getSelectedLanguage(context).equals(""))
            Helper.changeLanguage(context, PreferenceHelper.getSelectedLanguage(context));
        Helper.GCM.getCloudMessagingId(LoginActivity.this, new Helper.GCM.RegistrationComplete()
        {
            @Override
            public void onRegistrationComplete(String registrationId)
            {
                deviceId = registrationId;
            }
        });

        new FCMInstanceIdService(context).onTokenRefresh();

        Helper.setupTypeface(findViewById(R.id.parentLayout), Helper.robotoRegularTypeface);

        if (savedInstanceState != null)
        {
            CharSequence savedText = savedInstanceState.getCharSequence(KEY_TEXT_VALUE);
            editEmail.setText(savedText);
            editPassword.setText(savedText);
        }
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle(getString(R.string.app_name));
            // actionBar.setHomeButtonEnabled(false);
           /* actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);*/
        }

    }

    @Override
    protected void initializeComponents()
    {
        super.initializeComponents();
        textInputMail = (TextInputLayout) findViewById(R.id.textInputEmail);
        textInputPassword = (TextInputLayout) findViewById(R.id.textInputPassword);
        editEmail = (EditText) findViewById(R.id.textEmail);
        editPassword = (EditText) findViewById(R.id.textPassword);
        editOrganization = (EditText) findViewById(R.id.editOrganization);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN)
        {
            editPassword.setCompoundDrawables(getResources().getDrawable(R.drawable.ic_key), null, null, null);
        }
        imgVisibility = (ImageView) findViewById(R.id.compoundButtonVisibility);
        buttonForgetPassword = (Button) findViewById(R.id.btnForgetPassword);
        btnLogin = (Button) findViewById(R.id.buttonLogin);
        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);
        textTitle = (TextView) findViewById(R.id.textTitle);
        imageVieworganizationInfo = (ImageView) findViewById(R.id.img_organization_info);


    }

    @Override
    protected void bindEvents()
    {
        super.bindEvents();
        View.OnClickListener clickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                switch (view.getId())
                {
                    case R.id.btnForgetPassword:
                        startForgotActivity();
                        break;
                    case R.id.img_organization_info:
                        orgnizationURLInfoDialog();
                        break;
                    case R.id.compoundButtonVisibility:
                        if (editPassword.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD)
                        {
                            editPassword.setInputType(129);
                            imgVisibility.setImageResource(R.drawable.ic_visibility);
                        }
                        else
                        {
                            editPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            imgVisibility.setImageResource(R.drawable.ic_visibility_off);
                        }
                        break;
                    case R.id.buttonLogin:
                        Helper.preventTwoClick(btnLogin);

                        strOrganization = editOrganization.getText().toString();
                        strEmail = editEmail.getText().toString().trim();
                        strPassword = editPassword.getText().toString().trim();
                        if (strOrganization.isEmpty())
                        {
                            editOrganization.setError(getString(R.string.enter_Organization_name));
                        }
                        else if (strEmail.isEmpty())
                        {
                            editEmail.setError(getString(R.string.enter_email_address));
                        }
                        else if (strPassword.isEmpty())
                        {
                            editPassword.setError(getString(R.string.enter_password));
                        }
                        else if (TextUtils.isEmpty(strPassword) || strPassword.length() < 6)
                        {
                            editPassword.setError(getString(R.string.password_length_message));
                        }
                        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(strEmail).matches() && deviceId != null)
                        {
                            editEmail.setError(getString(R.string.invalid_email));
                        }

                        else if (Helper.isNetworkAvailable(context))
                        {
                            progressDialog = new ProgressDialog(context);
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressDialog.setIndeterminate(true);
                            progressDialog.setMessage(getString(R.string.msg_please_wait));
                            progressDialog.setCancelable(false);
                            progressDialog.show();

                            Helper.hideSoftKeyboard(LoginActivity.this);
                            Helper.IS_DIALOG_SHOW = false;
                            if (Helper.isUrlValid(strOrganization))
                            {

                                validUrl = strOrganization;
                                // call check health service call s
                                String checkHealthUrl = "";
                                if (strOrganization.substring(strOrganization.length() - 1).equals("/"))
                                {
                                    checkHealthUrl = strOrganization + DataProvider.ENDPOINT_CHECK_HEALTH;
                                }
                                else
                                {
                                    checkHealthUrl = strOrganization + "/" + DataProvider.ENDPOINT_CHECK_HEALTH;
                                }

                                postNetworkRequest(REQUEST_CHECK_HEALTH, checkHealthUrl, DataProvider.Actions.SERVICE_HELTH,
                                        RequestParameter.urlEncoded("OrgId", "0"));

                            }
                            else
                            {
                                // call service to check organization is exist or not
                                postNetworkRequest(REQUEST_ORGANIZATION, DataProvider.ENDPOINT_USER, DataProvider.Actions.GET_ORG_URL,
                                        RequestParameter.urlEncoded("OrganizationName", strOrganization));

                            }

                        }
                        else
                        {
                            Helper.showMessage(LoginActivity.this, false, getString(R.string.msg_please_check_your_connection));
                        }
                        break;

                }
            }
        };
        imgVisibility.setOnClickListener(clickListener);
        buttonForgetPassword.setOnClickListener(clickListener);
        btnLogin.setOnClickListener(clickListener);
        imageVieworganizationInfo.setOnClickListener(clickListener);

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
    protected void onNetworkResponse(int requestCode, boolean status, String response)
    {
        super.onNetworkResponse(requestCode, status, response);

        if (requestCode == REQUEST_CHECK_HEALTH)
        {
            if (status)
            {
                try
                {
                    JSONObject responseJson = new JSONObject(response);
                    int requestStatus = responseJson.optInt("Status");
                    if (requestStatus > 0)
                    {
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();

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


                        postNetworkRequest(REQUEST_LOGIN, DataProvider.ENDPOINT_USER, DataProvider.Actions.AUTHENTICATE_USER,
                                RequestParameter.urlEncoded("Email", strEmail),
                                RequestParameter.urlEncoded("Password", strPassword),
                                RequestParameter.urlEncoded("Platform", "Android"),
                                RequestParameter.urlEncoded("DeviceId", PreferenceHelper.getFCMTokenId(context)));

                    }
                }
                catch (JSONException e)
                {
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();

                    notifySimple(getString(R.string.msg_invalid_response_from_server));
                }
            }
            else
            {
                notifySimple(getString(R.string.msg_cannot_complete_request));
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        }
        else if (requestCode == REQUEST_ORGANIZATION)
        {
            if (status)
            {
                try
                {
                    JSONObject responseJson = new JSONObject(response);
                    int requestStatus = responseJson.optInt("Status");
                    if (requestStatus > 0)
                    {
                        if (DataHelper.getLocalizationMessageFromCode(context, String.valueOf(requestStatus), LOCALIZATION_TYPE_ERROR_CODE).isEmpty())
                        {
                            if (responseJson.has("Message"))
                            {
                                String message = responseJson.optString("Message");
                                notifySimple(message);
                            }
                        }
                        else
                        {
                            notifySimple(DataHelper.getLocalizationMessageFromCode(context, String.valueOf(requestStatus), LOCALIZATION_TYPE_ERROR_CODE));
                        }
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();


                    }
                    else
                    {
                        // organization is valid so call login service here
                        postNetworkRequest(REQUEST_LOGIN, DataProvider.ENDPOINT_USER, DataProvider.Actions.AUTHENTICATE_USER,
                                RequestParameter.urlEncoded("Email", strEmail), RequestParameter.urlEncoded("Password", strPassword),
                                RequestParameter.urlEncoded("Platform", "Android"), RequestParameter.urlEncoded("DeviceId", PreferenceHelper.getFCMTokenId(context)), RequestParameter.urlEncoded("OrganizationName", strOrganization));
                    }
                }
                catch (Exception e)
                {

                }
            }
            else
            {
                notifySimple(getString(R.string.msg_cannot_complete_request));
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        }
        else if (requestCode == REQUEST_LOGIN)
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
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                    else
                    {
                        JSONObject userJson = responseJson.optJSONObject("Payload");
                        String org_id = userJson.optString("Org_Id");
                        PreferenceHelper.storeOrganizationId(context, org_id);
                        JSONObject userThemeData = userJson.optJSONObject("ThemeData");
                        JSONObject userResult = userThemeData.optJSONObject("Result");
                        if (userJson.has("ThemeData"))
                        {
                            if (userThemeData.has("Result"))
                            {
                                String appColor = userResult.optString("AppColor");
                                String appBackColor = userResult.optString("AppBackColor");
                                String appFontColor = userResult.optString("AppFontColor");
                                String appFont = userResult.optString("AppFont");

                                PreferenceHelper.storeAppColor(context, appColor);
                                PreferenceHelper.storeAppBackColor(context, appBackColor);
                                PreferenceHelper.storeAppFontColor(context, appFontColor);
                                PreferenceHelper.storeAppFontName(context, appFont);
                            }
                        }
                        User user = new User();
                        User.parseFromJson(userJson, user);
                        UserPreference userPreference = new UserPreference();
                        userPreference.userId = user.id;
                        userPreference.populateUsingUserId(context);
                        prefManager.saveDemoLogin(false);
                        if (userJson.has("AccessToken"))
                            PreferenceHelper.storeToken(context, userJson.optString("AccessToken"));
                        if (userPreference.userId == -1)
                        {
                            userPreference.userId = user.id;
                            userPreference.lastSyncDate = "01/01/2001";
                            userPreference.databaseName = DataProvider.random();
                            userPreference.add(context);
                            PreferenceHelper.storeUserContext(context, userPreference);
                            user.add(context);
                            Helper.IS_DIALOG_SHOW = false;
                            if (progressDialog != null && !progressDialog.isShowing())
                                progressDialog.show();
                            postNetworkRequest(REQUEST_MEDIA_SIZE, DataProvider.ENDPOINT_USER, DataProvider.Actions.GET_MEDIA_SIZE,
                                    RequestParameter.urlEncoded("UserId", String.valueOf(userPreference.userId)));

                        }
                        else
                        {
                            PreferenceHelper.storeUserContext(context, userPreference);
                            DataHelper.updateUser(context, user);
                            userPreference.userId = user.id;
                            userPreference.populateUsingUserId(context);
                            //startSyncActivity();
                            ArrayList<Channel> channels = new ArrayList<>();
                            DataHelper.getChannels(context, channels);
                            if (PreferenceHelper.getSyncStatus(LoginActivity.this) || !channels.isEmpty())
                            {
                                PreferenceHelper.storeSyncStatus(LoginActivity.this, true);
                                startDashboardActivity();
                            }
                            else
                            {
                                startSyncActivity();
                            }
                        }

                    }
                }
                catch (JSONException | ParseException e)
                {
                    notifySimple(getString(R.string.msg_invalid_response_from_server));
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                }
            }
            else
            {
                notifySimple(getString(R.string.msg_cannot_complete_request));
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        }
        else if (requestCode == REQUEST_MEDIA_SIZE)
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
                        totalSizeInByte = responseJson.optLong("Payload");
                        Helper.bytesConvertsToMb(totalSizeInByte, context);
                        PreferenceHelper.storeMediaSize(context, totalSizeInByte);

                        postNetworkRequest(REQUEST_GET_SETTINGS, DataProvider.ENDPOINT_USER, DataProvider.Actions.GET_SETTINGS,
                                RequestParameter.urlEncoded("Org_Id", PreferenceHelper.getOrganizationId(context)));
                    }
                }
                catch (JSONException e)
                {
                    notifySimple(getString(R.string.msg_invalid_response_from_server));
                }
                finally
                {
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                }
            }
            else
            {
                notifySimple(getString(R.string.msg_cannot_complete_request));
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        }
        else if (requestCode == REQUEST_FORGOT_PASSWORD)
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
                        editEmail.setText("");
                        notifySimple(getString(R.string.msg_please_check_your_email));

                    }
                    if (!isFinishing() && alertDialogForgetPassword != null && alertDialogForgetPassword.isShowing())
                        alertDialogForgetPassword.dismiss();
                }
                catch (JSONException e)
                {
                    notifySimple(getString(R.string.msg_invalid_response_from_server));
                }
            }
            else
            {
                notifySimple(getString(R.string.msg_please_try_again_later));
            }
        }
        else if (requestCode == REQUEST_GET_SETTINGS)
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
                        JSONObject userJson = responseJson.optJSONObject("Payload");

                        Intent dashboardIntent = new Intent(context, SyncActivity.class);
                        dashboardIntent.putExtra(SyncActivity.IS_FROM_SETTING, false);
                        dashboardIntent.putExtra(SyncActivity.KEY_MEDIA_SIZE, totalSizeInByte);
                        startActivity(dashboardIntent);
                        finish();

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

    private void startDashboardActivity()
    {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();

        Intent dashboardIntent = new Intent(context, DashboardActivity.class);
        dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(dashboardIntent);
        finish();
    }

    private void startSyncActivity()
    {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();

        Intent dashboardIntent = new Intent(context, SyncActivity.class);
        dashboardIntent.putExtra(SyncActivity.IS_FROM_SETTING, false);
        startActivity(dashboardIntent);
        finish();
    }

    private void startForgotActivity()
    {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();

        Intent i = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
        startActivity(i);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(KEY_TEXT_VALUE, editEmail.getText());
        outState.putCharSequence(KEY_TEXT_VALUE, editPassword.getText());
    }

    public void orgnizationURLInfoDialog()
    {
        final EasyDialog dialog = new EasyDialog(context);
        View view = getLayoutInflater().inflate(R.layout.activity_organization_information, null);
        view.setLayoutParams(new LinearLayout.LayoutParams(Helper.getDeviceWidth(LoginActivity.this) / 2, ViewGroup.LayoutParams.WRAP_CONTENT));
        TextView txtSignOutTitle = (TextView) view.findViewById(R.id.txt_organization_info);
        txtSignOutTitle.setText(getResources().getString(R.string.organization_url_info));
        dialog.setLayout(view)
                .setGravity(EasyDialog.GRAVITY_BOTTOM)
                .setBackgroundColor(context.getResources().getColor(R.color.white1))
                .setLocationByAttachedView(imageVieworganizationInfo)
                .setTouchOutsideDismiss(true)
                .setMatchParent(false)
                .show();

    }


}
