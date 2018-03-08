package de.smac.smaccloud.activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import de.smac.smaccloud.R;
import de.smac.smaccloud.base.Activity;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.base.RequestParameter;
import de.smac.smaccloud.data.DataHelper;
import de.smac.smaccloud.helper.DataProvider;

import static de.smac.smaccloud.base.Helper.LOCALIZATION_TYPE_ERROR_CODE;

/**
 * Call at forgot strPassword
 */

public class ForgotPasswordActivity extends Activity
{

    private static final int REQUEST_FORGOT_PASSWORD = 4303;
    private static final String KEY_TEXT_VALUE = "textValue";
    TextView textViewForgotPassword, textViewForgotTitle;
    ProgressDialog progressDialog;
    private EditText editEmail;
    private LinearLayout parentLayout;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        Helper.retainOrientation(ForgotPasswordActivity.this);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle(getString(R.string.forgot_password));
        }
        Helper.setupTypeface(findViewById(R.id.parentLayout), Helper.robotoRegularTypeface);
        if (savedInstanceState != null)
        {
            CharSequence savedText = savedInstanceState.getCharSequence(KEY_TEXT_VALUE);
            editEmail.setText(savedText);
        }
    }

    @Override
    protected void initializeComponents()
    {
        super.initializeComponents();
        editEmail = (EditText) findViewById(R.id.textEmail);
        btnSubmit = (Button) findViewById(R.id.buttonSubmit);
        textViewForgotPassword = (TextView) findViewById(R.id.txt_forgot_password);
        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);
        textViewForgotTitle = (TextView) findViewById(R.id.forget_title);
    }


    @Override
    protected void bindEvents()
    {
        super.bindEvents();
        btnSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String email = editEmail.getText().toString();

                if (email.isEmpty())
                {
                    editEmail.setError(getString(R.string.enter_email_address));
                }
                else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    editEmail.setError(getString(R.string.invalid_email));
                }
                else
                {
                    if (Helper.isNetworkAvailable(context))
                    {
                        Helper.hideSoftKeyboard(ForgotPasswordActivity.this);
                        Helper.IS_DIALOG_SHOW = false;
                        progressDialog = new ProgressDialog(ForgotPasswordActivity.this);
                        progressDialog.setMessage(getString(R.string.recognizing_and_sending_mail));
                        progressDialog.setCancelable(false);
                        progressDialog.setIndeterminate(true);
                        progressDialog.show();

                        postNetworkRequest(REQUEST_FORGOT_PASSWORD, DataProvider.ENDPOINT_USER, DataProvider.Actions.FORGOT_PASSWORD,
                                RequestParameter.urlEncoded("Email", email));
                    }
                    else
                    {
                        notifySimple(getString(R.string.msg_network_connection_not_available));
                    }
                }

            }
        });
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

    @Override
    protected void onNetworkResponse(int requestCode, boolean status, String response)
    {
        super.onNetworkResponse(requestCode, status, response);
        Helper.IS_DIALOG_SHOW = true;
        if (requestCode == REQUEST_FORGOT_PASSWORD)
        {
            if (status)
            {
                try
                {
                    JSONObject responseJson = new JSONObject(response);
                    int requestStatus = responseJson.optInt("Status");
                    if (requestStatus > 0)
                    {
                        if (responseJson.has("Message") && !responseJson.isNull("Message") && !responseJson.optString("Message").equalsIgnoreCase("null"))
                            notifySimple(DataHelper.getLocalizationMessageFromCode(context, String.valueOf(requestStatus), LOCALIZATION_TYPE_ERROR_CODE));
                        else
                        {
                            // TODO: 16-Jun-17 Custom message
                            notifySimple(getString(R.string.change_password_database_message));
                        }
                    }
                    else
                    {
                        editEmail.setText("");
                        notifySimple(getString(R.string.msg_please_check_your_email));
                    }
                    if (progressDialog != null)
                    {
                        progressDialog.dismiss();
                    }
                }
                catch (JSONException e)
                {
                    if (progressDialog != null)
                    {
                        progressDialog.dismiss();
                    }
                    notifySimple(getString(R.string.msg_invalid_response_from_server));
                }
            }
            else
            {
                if (progressDialog != null)
                {
                    progressDialog.dismiss();

                }
                notifySimple(getString(R.string.msg_please_try_again_later));
            }

        }
        else
        {
            if (progressDialog != null)
            {
                progressDialog.dismiss();

            }
            notifySimple(getString(R.string.msg_cannot_complete_request));
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
        outState.putCharSequence(KEY_TEXT_VALUE, editEmail.getText());
    }

}
