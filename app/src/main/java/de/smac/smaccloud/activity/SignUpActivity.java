package de.smac.smaccloud.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import de.smac.smaccloud.R;
import de.smac.smaccloud.base.Activity;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.base.NetworkRequest;
import de.smac.smaccloud.base.NetworkResponse;
import de.smac.smaccloud.base.RequestParameter;
import de.smac.smaccloud.data.DataHelper;
import de.smac.smaccloud.helper.DataProvider;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.model.Channel;
import de.smac.smaccloud.model.User;
import de.smac.smaccloud.model.UserPreference;

import static de.smac.smaccloud.base.Helper.LOCALIZATION_TYPE_ERROR_CODE;
import static de.smac.smaccloud.base.NetworkService.KEY_AUTHORIZATION;

/**
 * Created by S Soft on 2/27/2018.
 */

public class SignUpActivity extends Activity implements View.OnClickListener
{
    public static final int REQUEST_LOGIN = 4302;
    public static final int REQUEST_MEDIA_SIZE = 4303;
    private static final int REQUEST_CHECK_ORAGNIZATION = 4301;
    public PreferenceHelper prefManager;
    long totalSizeInByte;
    ProgressDialog progressDialog;
    EditText editUserName, editEmailId, editAddress, editContact, editMobileNo, editOragnization, editPassword, editConfirmPassword;
    Button buttonSignUp;
    LinearLayout linearParentLayout;
    String MobilePattern = "[0-9]{10,15}";
    String strName = "", strEmailId = "", strPassword = "", strMobileNo = "",
            strOrganization = "", strPasswordConfirm = "", strContactNo = "",
            strAddress = "", strUserLanguage = "";
    MenuInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        context = this;
        prefManager = new PreferenceHelper(this);
        editUserName = (EditText) findViewById(R.id.textUserName);
        editEmailId = (EditText) findViewById(R.id.textEmailId);
        editAddress = (EditText) findViewById(R.id.textAddress);
        editContact = (EditText) findViewById(R.id.textContact);
        editMobileNo = (EditText) findViewById(R.id.textMobileNo);
        editOragnization = (EditText) findViewById(R.id.textOrgnization);
        editPassword = (EditText) findViewById(R.id.textPassword);
        editConfirmPassword = (EditText) findViewById(R.id.textCPassword);
        buttonSignUp = (Button) findViewById(R.id.btn_signUp);
        linearParentLayout = (LinearLayout) findViewById(R.id.parentLayout);
        Helper.setupUI(SignUpActivity.this, linearParentLayout, linearParentLayout);


        buttonSignUp.setOnClickListener(this);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle(getString(R.string.sign_up));

        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_signUp:
                strName = editUserName.getText().toString().trim();
                strEmailId = editEmailId.getText().toString().trim();
                strPassword = editPassword.getText().toString().trim();
                strMobileNo = editMobileNo.getText().toString().trim();
                strOrganization = editOragnization.getText().toString().trim();
                strPasswordConfirm = editConfirmPassword.getText().toString().trim();
                strContactNo = editContact.getText().toString().trim();
                strAddress = editAddress.getText().toString().trim();
                strUserLanguage = Locale.getDefault().getLanguage();

                if (strName.isEmpty())
                {
                    Snackbar.make(linearParentLayout, getString(R.string.name_validation), Snackbar.LENGTH_LONG).show();
                }
                else if (strEmailId.isEmpty())
                {
                    Snackbar.make(linearParentLayout, getString(R.string.emailid_validation), Snackbar.LENGTH_LONG).show();
                }
                else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(strEmailId).matches())
                {
                    Snackbar.make(linearParentLayout, getString(R.string.invalid_email), Snackbar.LENGTH_LONG).show();
                }
                else if (strMobileNo.isEmpty())
                {
                    Snackbar.make(linearParentLayout, getString(R.string.mobileno_validation), Snackbar.LENGTH_LONG).show();
                }
                else if (!strMobileNo.matches(MobilePattern) || strMobileNo.length() > 15)
                {
                    Snackbar.make(linearParentLayout, getString(R.string.invalidMobileNo_validation), Snackbar.LENGTH_LONG).show();
                }
                else if (strOrganization.isEmpty())
                {
                    Snackbar.make(linearParentLayout, getString(R.string.oragnization_validation), Snackbar.LENGTH_LONG).show();
                }
                else if (strPassword.isEmpty())
                {
                    Snackbar.make(linearParentLayout, getString(R.string.password_validation), Snackbar.LENGTH_LONG).show();
                }
                else if (TextUtils.isEmpty(strPassword) || strPassword.length() < 8 || !Helper.validatePassword(strPassword))
                {
                    Snackbar.make(linearParentLayout, getString(R.string.password_spacial_character_validation), Snackbar.LENGTH_LONG).show();
                }
                else if (!strPassword.equals(strPasswordConfirm))
                {
                    Snackbar.make(linearParentLayout, getString(R.string.password_mismatch_validation_message), Snackbar.LENGTH_LONG).show();
                }
                else
                {
                    if (Helper.isNetworkAvailable(context))
                    {
                        try
                        {
                            Helper.hideSoftKeyboard(SignUpActivity.this);
                            showProgressDialog();
                            postNetworkRequest(REQUEST_CHECK_ORAGNIZATION, DataProvider.ENDPOINT_USER, DataProvider.Actions.CHECK_ORGNIZATION,
                                    RequestParameter.urlEncoded("Email", strEmailId),
                                    RequestParameter.urlEncoded("OrganizationName", strOrganization));

                        }
                        catch (Exception ex)
                        {
                            hidProgressDialog();
                            ex.printStackTrace();
                        }
                    }
                    else
                    {
                        notifySimple(getString(R.string.msg_network_connection_not_available));
                    }
                }
                break;
        }
    }

    private void callRegisterService() throws ParseException, JSONException, UnsupportedEncodingException
    {
        final NetworkRequest request;
        ArrayList<RequestParameter> parameters = new ArrayList<>();
        parameters.add(RequestParameter.multiPart("Name", strName));
        parameters.add(RequestParameter.multiPart("Email", strEmailId));
        parameters.add(RequestParameter.multiPart("Password", strPassword));
        parameters.add(RequestParameter.multiPart("UserMobileNo", strMobileNo));
        parameters.add(RequestParameter.multiPart("OrganizationName", strOrganization));
        parameters.add(RequestParameter.multiPart("Contact", strContactNo));
        parameters.add(RequestParameter.multiPart("Address", strAddress));
        parameters.add(RequestParameter.multiPart("UserLanguage", strUserLanguage));

        request = new NetworkRequest(this);
        request.setBodyType(NetworkRequest.REQUEST_BODY_MULTIPART);
        request.setRequestType(NetworkRequest.REQUEST_TYPE_NORMAL);

        request.setRequestListener(new NetworkRequest.RequestListener()
        {
            @Override
            public void onRequestComplete(NetworkResponse networkResponse) throws JSONException
            {
                if (networkResponse.getStatusCode() == 200)
                {
                    JSONObject response = new JSONObject(networkResponse.getResponse());

                    if (response.optInt("Status") > 0)
                    {
                        hidProgressDialog();
                        if (response.optInt("Status") == 2113) // Status = 2113 means "USER_TOKEN_NOT_VALID"
                        {
                            NetworkRequest requestTokenNotValid = new NetworkRequest(context);
                            requestTokenNotValid.setBodyType(NetworkRequest.REQUEST_BODY_MULTIPART);
                            requestTokenNotValid.setRequestType(NetworkRequest.REQUEST_TYPE_NORMAL);
                            requestTokenNotValid.setRequestUrl(DataProvider.ENDPOINT_UPDATE_TOKEN);
                            //headerNameValuePairs.add(new BasicNameValuePair(KEY_LANGUAGE_HEADER_PARAM, Locale.getDefault().getLanguage()));
                            try
                            {
                                if (PreferenceHelper.getUserContext(SignUpActivity.this) != -1)
                                {
                                    int userId = PreferenceHelper.getUserContext(SignUpActivity.this);
                                    String token = PreferenceHelper.getToken(SignUpActivity.this) + String.valueOf(userId).length() + userId + Helper.getEpochTime();
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
                                            Toast.makeText(SignUpActivity.this, objUpdateTokenResponse.optString("Message"), Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            if (objUpdateTokenResponse.has("Payload"))
                                            {
                                                JSONObject objUpdateTokenPayload = objUpdateTokenResponse.getJSONObject("Payload");
                                                if (objUpdateTokenPayload.has("AccessToken") && !objUpdateTokenPayload.isNull("AccessToken"))
                                                {
                                                    PreferenceHelper.storeToken(SignUpActivity.this, objUpdateTokenPayload.optString("AccessToken"));
                                                    showProgressDialog();
                                                    callRegisterService();
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

                    }
                    else
                    {
                        postNetworkRequest(REQUEST_LOGIN, DataProvider.ENDPOINT_USER, DataProvider.Actions.AUTHENTICATE_USER,
                                RequestParameter.urlEncoded("Email", strEmailId), RequestParameter.urlEncoded("Password", strPassword),
                                RequestParameter.urlEncoded("Platform", "Android"), RequestParameter.urlEncoded("DeviceId", PreferenceHelper.getFCMTokenId(context)), RequestParameter.urlEncoded("OrganizationName", strOrganization));
                    }
                }
                else
                {
                    hidProgressDialog();
                    notifySimple(getString(R.string.msg_invalid_response_from_server));
                }
            }
        });
        request.setProgressMode(NetworkRequest.PROGRESS_MODE_NONE);
        request.setProgressMessage(getString(R.string.msg_please_wait));
        request.setRequestUrl(DataProvider.ENDPOINT_SIGNUP_URL);

        request.setParameters(parameters);
        int userId = PreferenceHelper.getUserContext(this);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

        String token = PreferenceHelper.getToken(this) + String.valueOf(userId).length() + userId + Helper.getEpochTime();
        if (token != null && !token.isEmpty())
        {
            ArrayList<BasicNameValuePair> headerNameValuePairs = new ArrayList<>();
            headerNameValuePairs.add(new BasicNameValuePair(KEY_AUTHORIZATION, token));
            request.setHeaders(headerNameValuePairs);
        }
        request.execute();


    }

    @Override
    protected void onNetworkResponse(int requestCode, boolean status, String response)
    {
        super.onNetworkResponse(requestCode, status, response);
        if (requestCode == REQUEST_CHECK_ORAGNIZATION)
        {
            if (status)
            {
                try
                {
                    JSONObject responseJson = new JSONObject(response);
                    int requestStatus = responseJson.optInt("Status");
                    //String message = responseJson.optString("Message");
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
                        hidProgressDialog();
                    }
                    else
                    {
                        try
                        {
                            callRegisterService();
                        }
                        catch (Exception ex)
                        {
                            ex.getStackTrace();
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
                notifySimple(getString(R.string.msg_please_try_again_later));
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
                            if (PreferenceHelper.getSyncStatus(SignUpActivity.this) || !channels.isEmpty())
                            {
                                PreferenceHelper.storeSyncStatus(SignUpActivity.this, true);
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

                        //navigate to synch screen
                        hidProgressDialog();
                        startSyncActivity();

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
        else
        {
            notifySimple(getString(R.string.msg_cannot_complete_request));
        }
    }

    private void showProgressDialog()
    {
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.msg_please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void hidProgressDialog()
    {
        if (progressDialog != null && progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }
    }

    private void startDashboardActivity()
    {
        Intent dashboardIntent = new Intent(context, DashboardActivity.class);
        dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(dashboardIntent);
        finish();
    }

    private void startSyncActivity()
    {
        Intent dashboardIntent = new Intent(context, SyncActivity.class);
        dashboardIntent.putExtra(SyncActivity.IS_FROM_SETTING, false);
        startActivity(dashboardIntent);
        finish();
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
    public boolean onCreateOptionsMenu(Menu menu)
    {
        inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_help, menu);
        applayThemeColor();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_help:
                //Helper.downloadAllFiles(SignUpActivity.this, true);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@smacsoftwares.de"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "");
                intent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(intent, ""));
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void applayThemeColor()
    {
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_help, null);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.parseColor(PreferenceHelper.getAppColor(context)));
    }

}
