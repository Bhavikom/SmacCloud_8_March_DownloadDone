package de.smac.smaccloud.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.suke.widget.SwitchButton;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

import de.smac.smaccloud.R;
import de.smac.smaccloud.adapter.CountryListAdapter;
import de.smac.smaccloud.base.Activity;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.base.RequestParameter;
import de.smac.smaccloud.data.DataHelper;
import de.smac.smaccloud.helper.DataProvider;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.model.Channel;
import de.smac.smaccloud.model.ChannelFiles;
import de.smac.smaccloud.model.ChannelUser;
import de.smac.smaccloud.model.Media;
import de.smac.smaccloud.model.MediaVersion;
import de.smac.smaccloud.model.User;
import de.smac.smaccloud.model.UserComment;
import de.smac.smaccloud.model.UserLike;
import de.smac.smaccloud.model.UserPreference;
import de.smac.smaccloud.service.FCMInstanceIdService;
import de.smac.smaccloud.widgets.RecyclerItemClickListener;

import static de.smac.smaccloud.activity.SyncActivity.REQUEST_SYNC;
import static de.smac.smaccloud.base.Helper.LOCALIZATION_TYPE_COMPANY_SIZE;
import static de.smac.smaccloud.base.Helper.LOCALIZATION_TYPE_COMPANY_TYPE;
import static de.smac.smaccloud.base.Helper.LOCALIZATION_TYPE_COUNTRY;
import static de.smac.smaccloud.base.Helper.LOCALIZATION_TYPE_DESIGNATION;
import static de.smac.smaccloud.base.Helper.LOCALIZATION_TYPE_ERROR_CODE;

public class TryDemoActivity extends Activity implements View.OnClickListener
{

    public static final int REQUEST_LOGIN = 4301;
    private static final String KEY_TEXT_VALUE = "textValue";
    public LinearLayout parentLayout;
    public LinearLayout countryList;
    public JSONArray jsonArrayUserLikes, jsonArrayUserComments;
    public String deviceId = "00000-00000-00000-00000-00000";
    EditText editName, editEmail;
    Button btnStartDemo;
    SwitchButton toggleNewsLetter;
    TextView textViewNewsLetter, textViewSelectCountry;
    ArrayList<String> arrayListCountry;
    ArrayList<String> arrayListDesignation;
    ArrayList<String> arrayListCompanyType;
    ArrayList<String> arrayListCompanySize;
    MaterialBetterSpinner spinnerOccupation, spinnerCompanyType, spinnerEmployeeStrength;
    ProgressDialog progressDialog;
    private PreferenceHelper prefManager;
    private ArrayList<UserLike> arrayListUserLikes;
    private ArrayList<UserComment> arrayListUserComments;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        prefManager = new PreferenceHelper(this);
        setContentView(R.layout.activity_try_demo);
        Helper.retainOrientation(TryDemoActivity.this);
        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);
        countryList = (LinearLayout) findViewById(R.id.linear_Country_List);
        Helper.setupUI(TryDemoActivity.this, parentLayout, parentLayout);

        editName = (EditText) findViewById(R.id.textName);
        editEmail = (EditText) findViewById(R.id.textEmail);

        spinnerOccupation = (MaterialBetterSpinner) findViewById(R.id.spinner_occupation);
        spinnerCompanyType = (MaterialBetterSpinner) findViewById(R.id.spinner_company_type);
        spinnerEmployeeStrength = (MaterialBetterSpinner) findViewById(R.id.spinner_employee_Strength);

        toggleNewsLetter = (SwitchButton) findViewById(R.id.toggleNewsLatter);
        toggleNewsLetter.setChecked(true);
        textViewNewsLetter = (TextView) findViewById(R.id.textNewsLetter);
        textViewSelectCountry = (TextView) findViewById(R.id.text_select_country);

        btnStartDemo = (Button) findViewById(R.id.btn_start_demo);

        Helper.setupTypeface(parentLayout, Helper.robotoRegularTypeface);


        btnStartDemo.setOnClickListener(this);
        countryList.setOnClickListener(this);

        arrayListCountry = DataHelper.getLocalizationCountry(context, Locale.getDefault().getLanguage() + "-" + Locale.getDefault().getLanguage(), LOCALIZATION_TYPE_COUNTRY);
        arrayListDesignation = DataHelper.getLocalizationCountry(context, Locale.getDefault().getLanguage() + "-" + Locale.getDefault().getLanguage(), LOCALIZATION_TYPE_DESIGNATION);
        arrayListCompanyType = DataHelper.getLocalizationCountry(context, Locale.getDefault().getLanguage() + "-" + Locale.getDefault().getLanguage(), LOCALIZATION_TYPE_COMPANY_TYPE);
        //static pass employee size.
        arrayListCompanySize = DataHelper.getLocalizationCountry(context, "en-en", LOCALIZATION_TYPE_COMPANY_SIZE);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.
                R.layout.simple_spinner_dropdown_item, arrayListDesignation);
        spinnerOccupation.setAdapter(adapter);

        ArrayAdapter<String> companyTypeAdapter = new ArrayAdapter<String>(this, android.
                R.layout.simple_spinner_dropdown_item, arrayListCompanyType);
        spinnerCompanyType.setAdapter(companyTypeAdapter);

        ArrayAdapter<String> empStrengthAdapter = new ArrayAdapter<String>(this, android.
                R.layout.simple_spinner_dropdown_item, arrayListCompanySize);
        spinnerEmployeeStrength.setAdapter(empStrengthAdapter);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle(getString(R.string.app_name));
        }

        Helper.setupUI(this, parentLayout, parentLayout);
        Helper.GCM.getCloudMessagingId(TryDemoActivity.this, new Helper.GCM.RegistrationComplete()
        {
            @Override
            public void onRegistrationComplete(String registrationId)
            {
                deviceId = registrationId;
            }
        });

        new FCMInstanceIdService(context).onTokenRefresh();

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

    protected void addCreator(User creator)
    {
        switch (creator.syncType)
        {
            case 0:
                creator.saveChanges(context);
                break;
            case 1:
                creator.add(context);
                break;
            case 2:
                creator.saveChanges(context);
                break;
            case 3:
                creator.remove(context);
                break;
        }
    }

    protected void addMediaVersion(MediaVersion currentVersion)
    {
        if (currentVersion != null)
        {
            switch (currentVersion.syncType)
            {
                case 0:
                    currentVersion.saveChanges(context);
                    addCreator(currentVersion.creator);
                    break;
                case 1:
                    currentVersion.add(context);
                    addCreator(currentVersion.creator);
                    break;
                case 2:
                    currentVersion.saveChanges(context);
                    addCreator(currentVersion.creator);
                    break;
                case 3:
                    currentVersion.remove(context);
                    addCreator(currentVersion.creator);
                    break;
            }
        }

    }

    private void startDashboardActivity()
    {
        Intent dashboardIntent = new Intent(context, DashboardActivity.class);
        dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(dashboardIntent);
        finish();
    }

    @Override
    protected void onNetworkResponse(int requestCode, boolean status, final String response)
    {
        super.onNetworkResponse(requestCode, status, response);
        Helper.IS_DIALOG_SHOW = true;
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

                    }
                    else
                    {
                        JSONObject userJson = responseJson.optJSONObject("Payload");
                        String org_id = userJson.optString("Org_Id");
                        PreferenceHelper.storeOrganizationId(context, org_id);
                        User user = new User();
                        User.parseFromJson(userJson, user);
                        UserPreference userPreference = new UserPreference();
                        // TODO: 20-Jul-17 Static user id for avoid extra login service call
                        userPreference.userId = user.id; //user.id;
                        userPreference.populateUsingUserId(context);
                        if (userJson.has("AccessToken"))
                            PreferenceHelper.storeToken(context, userJson.optString("AccessToken"));
                        if (userPreference.userId == -1)
                        {

                            userPreference.userId = user.id; //user.id;
                            userPreference.lastSyncDate = "01/01/2001";
                            userPreference.databaseName = DataProvider.random();
                            userPreference.add(context);
                            PreferenceHelper.storeUserContext(context, userPreference);
                            user.add(context);
                            Helper.IS_DIALOG_SHOW = false;

                            // Call sync service
                            arrayListUserLikes = new ArrayList<>();
                            arrayListUserComments = new ArrayList<>();
                            jsonArrayUserLikes = new JSONArray();
                            jsonArrayUserComments = new JSONArray();
                            userPreference.populateUsingUserId(context);
                            String lastSyncDate = "01/01/2001";

                            postNetworkRequest(REQUEST_SYNC, DataProvider.ENDPOINT_SYNC, DataProvider.Actions.SYNC,
                                    RequestParameter.jsonArray("UserLikes", jsonArrayUserLikes), RequestParameter.jsonArray("UserComments", jsonArrayUserComments),
                                    RequestParameter.urlEncoded("UserId", String.valueOf(user.id)), RequestParameter.urlEncoded("LastSyncDate", lastSyncDate), RequestParameter.urlEncoded("Org_Id", PreferenceHelper.getOrganizationId(context)));

                        }
                        else
                        {
                            userPreference.userId = user.id; // user.id;
                            userPreference.populateUsingUserId(context);
                            PreferenceHelper.storeUserContext(context, userPreference);

                            PreferenceHelper.storeSyncStatus(context, true);
                            startDashboardActivity();
                        }
                        prefManager.saveDemoLogin(true);
                    }
                }
                catch (JSONException | ParseException e)
                {
                    notifySimple(getString(R.string.msg_invalid_response_from_server));
                    if (progressDialog != null)
                    {
                        progressDialog.dismiss();
                    }

                }

            }
            else
            {
                notifySimple(getString(R.string.msg_network_connection_not_available));
                if (progressDialog != null)
                {
                    progressDialog.dismiss();
                }
            }

        }
        else if (requestCode == REQUEST_SYNC)
        {
            Helper.IS_DIALOG_SHOW = true;
            if (status)
            {
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            JSONObject syncJson = new JSONObject(response);
                            int requestStatus = syncJson.optInt("Status");
                            if (requestStatus > 0)
                            {
                                notifySimple(syncJson.optString("Message"));

                            }
                            else
                            {

                             //   notifySimple(getString(R.string.msg_data_sync));
                                syncJson = syncJson.optJSONObject("Payload");
                                Log.e("payload", syncJson.toString());

                                if (!(arrayListUserLikes.size() <= 0))
                                {
                                    for (UserLike userLike : arrayListUserLikes)
                                    {
                                        userLike.removeoffline(context);
                                    }
                                }
                                if (!(arrayListUserComments.size() <= 0))
                                {
                                    for (UserComment userComment : arrayListUserComments)
                                    {
                                        userComment.removeOffline(context);
                                    }
                                }
                                ArrayList<Channel> arraylistChannels = new ArrayList<>();
                                JSONArray channelJsonArray = syncJson.optJSONArray("Channels");
                                try
                                {
                                    Channel.parseListFromJson(channelJsonArray, arraylistChannels);
                                    for (Channel channel : arraylistChannels)
                                    {
                                        switch (channel.syncType)
                                        {
                                            case 1:
                                                channel.add(context);
                                                break;

                                            case 2:
                                                channel.saveChanges(context);
                                                break;

                                            case 3:
                                                channel.remove(context);
                                                break;
                                        }
                                        addCreator(channel.creator);
                                    }
                                    JSONArray mediaJsonArray = syncJson.optJSONArray("Media");
                                    ArrayList<Media> arrayListMediaList = new ArrayList<>();
                                    Media.parseListFromJson(mediaJsonArray, arrayListMediaList);
                                    for (Media media : arrayListMediaList)
                                    {
                                        switch (media.syncType)
                                        {
                                            case 1:
                                                media.add(context);

                                                break;
                                            case 2:
                                                media.saveChanges(context);
                                                break;

                                            case 3:
                                                media.remove(context);
                                                break;
                                        }
                                        Log.e("Media type", media.type + media.currentVersionId);
                                        if (!(media.type.equals("folder")))
                                        {
                                            addMediaVersion(media.currentVersion);
                                        }

                                    }
                                    JSONArray channelFilesJsonArray = syncJson.optJSONArray("ChannelFiles");
                                    ArrayList<ChannelFiles> arraylistChhannelFiles = new ArrayList<>();
                                    ChannelFiles.parseListFromJson(channelFilesJsonArray, arraylistChhannelFiles);
                                    for (ChannelFiles channelFile : arraylistChhannelFiles)
                                    {
                                        switch (channelFile.syncType)
                                        {
                                            case 1:
                                                channelFile.add(context);
                                                break;

                                            case 2:
                                                channelFile.saveChanges(context);
                                                break;

                                            case 3:
                                                channelFile.remove(context);
                                                break;
                                        }
                                    }

                                    JSONArray channelUsersJsonArray = syncJson.optJSONArray("ChannelUsers");
                                    ArrayList<ChannelUser> arraylistChannelUsers = new ArrayList<>();
                                    ChannelUser.parseListFromJson(channelUsersJsonArray, arraylistChannelUsers);
                                    for (ChannelUser channelUser : arraylistChannelUsers)
                                    {
                                        switch (channelUser.syncType)
                                        {
                                            case 1:
                                                channelUser.add(context);
                                                break;
                                            case 2:
                                                channelUser.saveChanges(context);
                                                break;
                                            case 3:
                                                channelUser.remove(context);
                                                break;
                                        }
                                    }

                                    JSONArray userCommentsJsonArray = syncJson.optJSONArray("UserComments");
                                    ArrayList<UserComment> arraylistuserComments = new ArrayList<>();
                                    UserComment.parseListFromJson(userCommentsJsonArray, arraylistuserComments);
                                    for (UserComment usercomment : arraylistuserComments)
                                    {
                                        switch (usercomment.syncType)
                                        {
                                            case 1:
                                                usercomment.add(context);
                                                break;
                                            case 2:
                                                usercomment.saveChanges(context);
                                                break;
                                            case 3:
                                                usercomment.remove(context);
                                                break;
                                        }
                                    }
                                    JSONArray userLikeJsonArray = syncJson.optJSONArray("UserLikes");
                                    ArrayList<UserLike> arraylistuserLikes = new ArrayList<>();
                                    UserLike.parseListFromJson(userLikeJsonArray, arraylistuserLikes);
                                    for (UserLike userLike : arraylistuserLikes)
                                    {
                                        switch (userLike.syncType)
                                        {
                                            case 1:
                                                userLike.add(context);
                                                break;
                                            case 2:
                                                userLike.saveChanges(context);
                                                break;
                                            case 3:
                                                userLike.remove(context);
                                                break;
                                        }
                                        addCreator(userLike.user);
                                    }

                                    UserPreference userPreference = new UserPreference();
                                    userPreference.userId = PreferenceHelper.getUserContext(context);
                                    userPreference.populateUsingUserId(context);
                                    userPreference.lastSyncDate = syncJson.optString("LastSyncDate");
                                    userPreference.saveChanges(context);
                                    Log.e("lastsync", userPreference.lastSyncDate);
                                    User user = new User();
                                    user.id = PreferenceHelper.getUserContext(context);
                                    user.populateUsingId(context);
                                    userPreference.userId = user.id;

                                    PreferenceHelper.storeSyncStatus(context, true);
                                    startDashboardActivity();
                                    finish();


                                }
                                catch (JSONException | ParseException e)
                                {
                                    if (progressDialog != null)
                                    {
                                        progressDialog.dismiss();
                                    }
                                    e.printStackTrace();
                                }
                                catch (Exception e)
                                {
                                    if (progressDialog != null)
                                    {
                                        progressDialog.dismiss();
                                    }
                                    e.printStackTrace();
                                }
                                if (progressDialog != null)
                                {
                                    progressDialog.dismiss();
                                }

                            }
                        }
                        catch (JSONException e)
                        {
                            notifySimple(getString(R.string.msg_invalid_response_from_server));
                            if (progressDialog != null)
                            {
                                progressDialog.dismiss();
                            }
                        }

                    }
                }).start();


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
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_start_demo:
                String editName = this.editName.getText().toString().trim();
                String editEmail = this.editEmail.getText().toString().trim();
                String selectCountry = textViewSelectCountry.getText().toString().trim();
                prefManager.saveFullDownloadMedia(false);

                if (editName.isEmpty())
                {
                    //this.editName.setError(getString(R.string.enter_name));
                    Snackbar.make(parentLayout, getString(R.string.enter_name), Snackbar.LENGTH_LONG).show();

                }
                else if (spinnerCompanyType.getText().toString().isEmpty())
                {
                    //this.spinnerCompanyType.setError(getString(R.string.enter_company_type));
                    Snackbar.make(parentLayout, getString(R.string.enter_company_type), Snackbar.LENGTH_LONG).show();
                }
                else if (editEmail.isEmpty())
                {
                    //this.editEmail.setError(getString(R.string.enter_email_address));
                    Snackbar.make(parentLayout, getString(R.string.enter_email_address), Snackbar.LENGTH_LONG).show();

                }
                else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(editEmail).matches() && deviceId != null)
                {
                    //this.editEmail.setError(getString(R.string.invalid_email));
                    Snackbar.make(parentLayout, getString(R.string.invalid_email), Snackbar.LENGTH_LONG).show();
                }
                else if (selectCountry.equalsIgnoreCase(getString(R.string.select_country)))
                {
                    Snackbar.make(parentLayout, getString(R.string.select_country), Snackbar.LENGTH_LONG).show();
                    // this.textViewSelectCountry.setError(getString(R.string.select_country));

                }
                else
                {
                    if (Helper.isNetworkAvailable(context))
                    {
                        Helper.IS_DIALOG_SHOW = false;
                        progressDialog = new ProgressDialog(TryDemoActivity.this);
                        progressDialog.setMessage(getString(R.string.msg_please_wait));
                        progressDialog.setCancelable(false);
                        progressDialog.setIndeterminate(true);
                        progressDialog.show();
                        Helper.hideSoftKeyboard(TryDemoActivity.this);
                        Helper.IS_DIALOG_SHOW = false;

                        postNetworkRequest(REQUEST_LOGIN, DataProvider.ENDPOINT_USER, DataProvider.Actions.DEMO_USER,
                                RequestParameter.urlEncoded("Name", editName), RequestParameter.urlEncoded("BusinessName", spinnerOccupation.getText().toString()), RequestParameter.urlEncoded("IndustryType", spinnerCompanyType.getText().toString()), RequestParameter.urlEncoded("NumOfEmployee", spinnerEmployeeStrength.getText().toString()), RequestParameter.urlEncoded("NewsLatterAllow", toggleNewsLetter.isChecked() ? "true" : "false"), RequestParameter.urlEncoded("Email", editEmail), RequestParameter.urlEncoded("Country", selectCountry), RequestParameter.urlEncoded("DeviceToken", PreferenceHelper.getFCMTokenId(context)), RequestParameter.urlEncoded("DeviceType", "Android"), RequestParameter.urlEncoded("IsDeleted", "false"), RequestParameter.urlEncoded("IsDemoUser", "true"))
                        ;
                    }
                    else
                        Helper.showMessage(TryDemoActivity.this, false, getString(R.string.msg_please_check_your_connection));

                }
                break;
            case R.id.linear_Country_List:
                final Dialog dialog = new Dialog(this, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
                RecyclerView.LayoutManager recyclerViewLayoutManager;
                View view = getLayoutInflater().inflate(R.layout.activity_country_list, null);
                dialog.setContentView(view);
                dialog.setCancelable(true);
                Toolbar toolbar = (Toolbar) view.findViewById(R.id.navigationBar);
                toolbar.setTitle(getString(R.string.app_name));
                RecyclerView recyclerListCountry = (RecyclerView) view.findViewById(R.id.country_list);
                recyclerViewLayoutManager = new LinearLayoutManager(context);
                recyclerListCountry.setLayoutManager(recyclerViewLayoutManager);
                CountryListAdapter countryListAdapter = new CountryListAdapter(context, arrayListCountry);
                LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.parentLayout);
                recyclerListCountry.setAdapter(countryListAdapter);
                recyclerListCountry.addOnItemTouchListener(
                        new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener()
                        {
                            @Override
                            public void onItemClick(View view, int position)
                            {
                                textViewSelectCountry.setText(arrayListCountry.get(position));
                                textViewSelectCountry.setError(null);
                                dialog.dismiss();
                            }
                        })
                );
                countryListAdapter = new CountryListAdapter(context, arrayListCountry);
                recyclerListCountry.setAdapter(countryListAdapter);
                dialog.show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(KEY_TEXT_VALUE, editEmail.getText());
        outState.putCharSequence(KEY_TEXT_VALUE, editName.getText());
        outState.putCharSequence(KEY_TEXT_VALUE, spinnerOccupation.getText());
        outState.putCharSequence(KEY_TEXT_VALUE, spinnerCompanyType.getText());
        outState.putCharSequence(KEY_TEXT_VALUE, spinnerEmployeeStrength.getText());
        outState.putCharSequence(KEY_TEXT_VALUE, textViewSelectCountry.getText());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

}
