package de.smac.smaccloud.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    public static final int REQUEST_LOGIN = 4301;
    public static final int REQUEST_MEDIA_SIZE = 4302;
    private static final int REQUEST_FORGOT_PASSWORD = 4303;
    private static final String KEY_TEXT_VALUE = "textValue";
    public PreferenceHelper prefManager;
    public LinearLayout parentLayout;
    android.app.AlertDialog alertDialogForgetPassword;
    TextInputLayout textInputMail, textInputPassword;
    //CustomProgressDialog dialog;
    ProgressDialog dialog;
    private TextView textTitle;
    private EditText editEmail;
    private EditText editPassword;
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
        textTitle.setTypeface(Helper.robotoBoldTypeface);
        buttonForgetPassword.setTypeface(Helper.robotoMediumTypeface);
        btnLogin.setTypeface(Helper.robotoMediumTypeface);
        if (savedInstanceState != null)
        {
            CharSequence savedText = savedInstanceState.getCharSequence(KEY_TEXT_VALUE);
            editEmail.setText(savedText);
            editPassword.setText(savedText);
        }
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle(getString(R.string.app_name));
            actionBar.setHomeButtonEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
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
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN)
        {
            editPassword.setCompoundDrawables(getResources().getDrawable(R.drawable.ic_key), null, null, null);
        }
        imgVisibility = (ImageView) findViewById(R.id.compoundButtonVisibility);
        buttonForgetPassword = (Button) findViewById(R.id.btnForgetPassword);
        btnLogin = (Button) findViewById(R.id.buttonLogin);
        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);
        textTitle = (TextView) findViewById(R.id.textTitle);
        editEmail.setText("test@sambinfo.in");
        editPassword.setText("123456");

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
                        String email = editEmail.getText().toString();
                        String password = editPassword.getText().toString();
                        if (email.isEmpty())
                        {
                            editEmail.setError(getString(R.string.enter_email_address));
                        }
                        else if (password.isEmpty())
                        {
                            editPassword.setError(getString(R.string.enter_password));
                        }
                        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && deviceId != null)
                        {
                            editEmail.setError(getString(R.string.invalid_email));
                        }
                        else if (Helper.isNetworkAvailable(context))
                        {
                            /*dialog = new CustomProgressDialog(LoginActivity.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.setCancelable(false);
                            dialog.show();*/

                            dialog = new ProgressDialog(context);
                            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            dialog.setIndeterminate(true);
                            dialog.setMessage(getString(R.string.msg_please_wait));
                            dialog.setCancelable(false);
                            dialog.show();

                            Helper.hideSoftKeyboard(LoginActivity.this);
                            Helper.IS_DIALOG_SHOW = false;
                            postNetworkRequest(REQUEST_LOGIN, DataProvider.ENDPOINT_USER, DataProvider.Actions.AUTHENTICATE_USER,
                                    RequestParameter.urlEncoded("Email", email), RequestParameter.urlEncoded("Password", password),
                                    RequestParameter.urlEncoded("Platform", "Android"), RequestParameter.urlEncoded("DeviceId", PreferenceHelper.getFCMTokenId(context)));


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
        if (requestCode == REQUEST_LOGIN)
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
                        if (dialog != null && dialog.isShowing())
                            dialog.dismiss();
                    }
                    else
                    {
                        JSONObject userJson = responseJson.optJSONObject("Payload");
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
                            //PreferenceHelper.getToken(context);
                            Helper.IS_DIALOG_SHOW = false;
                            if (dialog != null && !dialog.isShowing())
                                dialog.show();
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
                    if (dialog != null && dialog.isShowing())
                        dialog.dismiss();
                }
            }
            else
            {
                notifySimple(getString(R.string.msg_cannot_complete_request));
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
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
                        long totalSizeInByte = responseJson.optLong("Payload");
                        Helper.bytesConvertsToMb(totalSizeInByte, context);
                        Intent dashboardIntent = new Intent(context, SyncActivity.class);
                        dashboardIntent.putExtra(SyncActivity.IS_FROM_SETTING, false);
                        dashboardIntent.putExtra(SyncActivity.KEY_MEDIA_SIZE, totalSizeInByte);
                        PreferenceHelper.storeMediaSize(context, totalSizeInByte);
                        startActivity(dashboardIntent);
                        finish();
                    }
                }
                catch (JSONException e)
                {
                    notifySimple(getString(R.string.msg_invalid_response_from_server));
                }
                finally
                {
                    if (dialog != null && dialog.isShowing())
                        dialog.dismiss();
                }
            }
            else
            {
                notifySimple(getString(R.string.msg_cannot_complete_request));
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
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
                        //  startLoginActivity();
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
    }

    private void startDashboardActivity()
    {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();

        Intent dashboardIntent = new Intent(context, DashboardActivity.class);
        dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(dashboardIntent);
        finish();
    }

    private void startSyncActivity()
    {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();

        Intent dashboardIntent = new Intent(context, SyncActivity.class);
        dashboardIntent.putExtra(SyncActivity.IS_FROM_SETTING, false);
        startActivity(dashboardIntent);
        finish();
    }

    private void startForgotActivity()
    {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();

        Intent i = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
        startActivity(i);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(KEY_TEXT_VALUE, editEmail.getText());
        outState.putCharSequence(KEY_TEXT_VALUE, editPassword.getText());
    }


}
