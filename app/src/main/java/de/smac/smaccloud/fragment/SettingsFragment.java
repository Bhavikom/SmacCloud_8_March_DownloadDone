package de.smac.smaccloud.fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.michael.easydialog.EasyDialog;
import com.suke.widget.SwitchButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

import de.smac.smaccloud.R;
import de.smac.smaccloud.activity.AboutUsActivity;
import de.smac.smaccloud.activity.BccAddressChipLayoutActivity;
import de.smac.smaccloud.activity.CcAddressChipLayoutActivity;
import de.smac.smaccloud.activity.ChangePasswordActivity;
import de.smac.smaccloud.activity.DashboardActivity;
import de.smac.smaccloud.activity.DemoActivity;
import de.smac.smaccloud.activity.EmailBodyActivity;
import de.smac.smaccloud.activity.OpenSourceLibrariesActivity;
import de.smac.smaccloud.activity.PrivacyPolicyActivity;
import de.smac.smaccloud.activity.SetSignatureActivity;
import de.smac.smaccloud.activity.StorageActivity;
import de.smac.smaccloud.activity.SyncActivity;
import de.smac.smaccloud.activity.TermsActivity;
import de.smac.smaccloud.adapter.LanguageListViewAdapter;
import de.smac.smaccloud.base.Activity;
import de.smac.smaccloud.base.Fragment;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.base.RequestParameter;
import de.smac.smaccloud.data.DataHelper;
import de.smac.smaccloud.helper.DataProvider;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.model.Channel;
import de.smac.smaccloud.model.ChannelFiles;
import de.smac.smaccloud.model.ChannelUser;
import de.smac.smaccloud.model.Media;
import de.smac.smaccloud.model.MediaAllDownload;
import de.smac.smaccloud.model.MediaVersion;
import de.smac.smaccloud.model.User;
import de.smac.smaccloud.model.UserComment;
import de.smac.smaccloud.model.UserLike;
import de.smac.smaccloud.model.UserPreference;
import de.smac.smaccloud.service.DownloadService;
import de.smac.smaccloud.service.FCMInstanceIdService;
import de.smac.smaccloud.service.FCMMessagingService;

import static de.smac.smaccloud.activity.SetSignatureActivity.PERMISSION_REQUEST_CODE;
import static de.smac.smaccloud.base.Helper.LOCALIZATION_TYPE_ERROR_CODE;

/**
 * Show settings
 */
public class SettingsFragment extends Fragment implements FCMMessagingService.ThemeChangeNotificationListener
{

    public static final int REQUEST_CODE_SAVE_SIGNATURE = 101;
    public static final int REQUEST_SYNC = 4302;
    public static final int REQUEST_LOGOUT = 102;
    public static boolean DELETE_USER_PREFERENCE = false;
    public LinearLayout parentLayout;
    public boolean isFullDownload = false;
    public int batteryStatus = 20;
    int id = 1;
    String selectedLang = "en";
    SettingsFragment.InterfacechangeLanguage interfacechangeLanguage;
    RelativeLayout btnChangeLanguage;
    RelativeLayout btnPassword;
    RelativeLayout btnEmailBody;
    RelativeLayout btnCCAddress;
    RelativeLayout btnBCCAddress;
    RelativeLayout buttonStorage;
    RelativeLayout buttonTerms;
    RelativeLayout buttonPrivacyPolicy;
    RelativeLayout buttonOpenSourceLibraries;
    RelativeLayout buttonHelp;
    RelativeLayout btnSynchronizeNow;
    RelativeLayout btnAboutUs;
    RelativeLayout btnSignature;
    RelativeLayout btnSignOut;
    LinearLayout btnAutoDownload;
    User user;
    long mediaSize = 0;
    SwitchButton toggleButtonAutoDownload;
    TextView textViewSharing, textViewEmailBody, textViewCc, textViewBcc, textViewSignature, textViewTitleSynchronization, textViewSynchronizeNow, textViewAutoDownload, textViewInformation, textViewStorage, textViewAboutUs, textViewLogin, textViewChangePassword, textViewSignOut, textViewChangeLanguage, textViewAccount, textViewTerms, textViewOpenSorceLibraries, textViewHelp;
    private ArrayList<UserLike> arrayListUserLikes;
    private ArrayList<UserComment> arrayListUserComments;
    private JSONArray jsonArrayUserLikes, jsonArrayUserComments;
    private PreferenceHelper prefManager;
    private Intent intentDownloadService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        //applyThemeColor();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (activity instanceof DashboardActivity)
        {
            if (activity.getSupportActionBar() != null)
            {

                activity.getSupportActionBar().setTitle(R.string.settings);

            }
            ((DashboardActivity) (activity)).navigationDashboard.getMenu().findItem(R.id.menuSettings).setCheckable(true).setChecked(true);
        }
        applyThemeColor();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (activity instanceof DashboardActivity)
        {
            ((DashboardActivity) (activity)).navigationDashboard.getMenu().findItem(R.id.menuSettings).setCheckable(true).setChecked(true);
        }
    }

    @Override
    protected void initializeComponents()
    {
        this.user = user;
        arrayListUserLikes = new ArrayList<>();
        arrayListUserComments = new ArrayList<>();
        jsonArrayUserLikes = new JSONArray();
        jsonArrayUserComments = new JSONArray();

        prefManager = new PreferenceHelper(getActivity());
        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);
        btnChangeLanguage = (RelativeLayout) findViewById(R.id.btn_change_language);
        btnPassword = (RelativeLayout) findViewById(R.id.btn_password);
        btnEmailBody = (RelativeLayout) findViewById(R.id.btnEmailBody);
        btnCCAddress = (RelativeLayout) findViewById(R.id.btnCCAddress);
        btnBCCAddress = (RelativeLayout) findViewById(R.id.btnBCCAddress);
        btnSignature = (RelativeLayout) findViewById(R.id.btn_signature);
        btnSynchronizeNow = (RelativeLayout) findViewById(R.id.btn_synchronize_now);
        buttonStorage = (RelativeLayout) findViewById(R.id.buttonStorage);
        buttonTerms = (RelativeLayout) findViewById(R.id.buttonTerms);
        buttonPrivacyPolicy = (RelativeLayout) findViewById(R.id.buttonPrivacy);
        buttonOpenSourceLibraries = (RelativeLayout) findViewById(R.id.button_openSource_Libraries);
        buttonHelp = (RelativeLayout) findViewById(R.id.button_help);
        btnAboutUs = (RelativeLayout) findViewById(R.id.btn_about_us);
        btnSignOut = (RelativeLayout) findViewById(R.id.btn_sign_out);
        btnAutoDownload = (LinearLayout) findViewById(R.id.btn_auto_download);

        textViewAccount = (TextView) findViewById(R.id.txt_account);
        textViewChangeLanguage = (TextView) findViewById(R.id.txt_change_language);
        textViewChangePassword = (TextView) findViewById(R.id.txt_password);
        textViewSharing = (TextView) findViewById(R.id.txt_sharing);
        textViewEmailBody = (TextView) findViewById(R.id.txt_email_body);
        textViewCc = (TextView) findViewById(R.id.txt_cc);
        textViewBcc = (TextView) findViewById(R.id.txt_bcc);
        textViewSignature = (TextView) findViewById(R.id.txt_signature);
        textViewTitleSynchronization = (TextView) findViewById(R.id.txt_title_synchronization);
        textViewAutoDownload = (TextView) findViewById(R.id.txt_auto_download);
        textViewInformation = (TextView) findViewById(R.id.txt_information);
        textViewStorage = (TextView) findViewById(R.id.txt_storage);
        textViewTerms = (TextView) findViewById(R.id.txt_terms);
        textViewOpenSorceLibraries = (TextView) findViewById(R.id.txt_openSource_Libraries);
        textViewHelp = (TextView) findViewById(R.id.txt_help);
        textViewAboutUs = (TextView) findViewById(R.id.txt_about_us);
        textViewLogin = (TextView) findViewById(R.id.txt_login);
        textViewSignOut = (TextView) findViewById(R.id.txt_sign_out);
        textViewSynchronizeNow = (TextView) findViewById(R.id.txt_synchronize_now);
        toggleButtonAutoDownload = (SwitchButton) findViewById(R.id.toggleAutoDownload);


        new FCMInstanceIdService(context).onTokenRefresh();
        applyThemeColor();

        if (prefManager.isFullDownloadMedia())
        {
            toggleButtonAutoDownload.setChecked(true);
        }
        else
        {
            toggleButtonAutoDownload.setChecked(false);
        }
        toggleButtonAutoDownload.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(SwitchButton switchButton, boolean isChecked)
            {
                if (isChecked)
                {
                    if (mediaSize > Helper.availableBlocks(context))
                    {
                        showNoFreeSpaceAvailableDialog();

                    }
                    else if (getBatteryLevel() <= batteryStatus)
                    {
                        showLowBatteryStatusDialog();
                    }
                    else
                    {
                        fullDownloadMedia();
                    }
                    //fullDownloadMedia();
                }
                else
                {
                    if (DownloadService.isDownloading)
                    {
                        DownloadService.isDownloading = false;
                        if (intentDownloadService != null)
                        {
                            if (activity.stopService(intentDownloadService))
                            {
                                Toast.makeText(activity, "Service stop by intent!!", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                activity.stopService(new Intent(activity, DownloadService.class));
                            }
                        }
                        else
                        {
                            activity.stopService(new Intent(activity, DownloadService.class));
                        }
                    }
                }
                prefManager.saveFullDownloadMedia(isChecked);
            }

        });
        btnAutoDownload.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (toggleButtonAutoDownload.isChecked())
                {
                    toggleButtonAutoDownload.setChecked(false);
                }
                else
                {
                    toggleButtonAutoDownload.setChecked(true);
                }
            }
        });
        FCMMessagingService.themeChangeNotificationListener = new FCMMessagingService.ThemeChangeNotificationListener()
        {
            @Override
            public void onThemeChangeNotificationReceived()
            {
                applyThemeColor();
            }
        };
    }

    public void applyThemeColor()
    {
        activity.updateParentThemeColor();
        // TODO: 2/20/2018 Remove unnecessary code
        Helper.setupTypeface(findViewById(R.id.parentLayout), Helper.robotoRegularTypeface);
        Helper.setupTypeface(parentLayout, Helper.robotoRegularTypeface);
        Helper.setupTypeface(getActivity().findViewById(R.id.parentLayout), Helper.robotoRegularTypeface);
    }

    public void showLowBatteryStatusDialog()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(context.getString(R.string.app_title));
        alertDialog.setMessage(getString(R.string.battery_low_message) + "\n" + getString(R.string.download_message) + "\t" + Helper.bytesConvertsToMb(DataHelper.getAllDownloadSize(context), context) + "\t" + getString(R.string.download_message1) + "\n" + getString(R.string.download_message2) + "\t" + Helper.bytesConvertsToMb(Helper.availableBlocks(context), context));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.ok),
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        fullDownloadMedia();
                        //isFullDownload = false;
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.label_cancel),
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        toggleButtonAutoDownload.setChecked(false);
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }

    public void showNoFreeSpaceAvailableDialog()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(context.getString(R.string.app_title));
        alertDialog.setMessage(context.getString(R.string.no_available_space_message));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.ok),
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        toggleButtonAutoDownload.setChecked(false);
                        dialog.dismiss();


                    }
                });
        alertDialog.show();

    }

    public void fullDownloadMedia()
    {

        final ArrayList<MediaAllDownload> arraylistDownloadList = new ArrayList<MediaAllDownload>();
        DataHelper.getAllDownloadList(activity, arraylistDownloadList);
        for (int i = 0; i < arraylistDownloadList.size(); i++)
        {
            int mediaParentId = arraylistDownloadList.get(i).mediaId;
            int rootMediaId;
            do
            {
                rootMediaId = mediaParentId;
                mediaParentId = DataHelper.getMediaParentId(activity, mediaParentId);
            }
            while (mediaParentId != -1);
            int channelId = DataHelper.getChannelIdFromMediaID(activity, rootMediaId);
            arraylistDownloadList.get(i).channelId = channelId;
        }

        for (int i = 0; i < arraylistDownloadList.size(); i++)
        {
            try
            {
                Media tempMedia = new Media();
                tempMedia.id = arraylistDownloadList.get(i).mediaId;
                DataHelper.getMedia(activity, tempMedia);
                tempMedia.isDownloading = 1;
                DataHelper.updateMedia(activity, tempMedia);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        intentDownloadService = new Intent(activity, DownloadService.class);
        intentDownloadService.putParcelableArrayListExtra("downloadlist", arraylistDownloadList);
        activity.startService(intentDownloadService);
    }

    public float getBatteryLevel()
    {
        Intent batteryIntent = getActivity().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        if (level == -1 || scale == -1)
        {
            return 50.0f;
        }
        return ((float) level / (float) scale) * 100.0f;
    }


    @Override
    protected void bindEvents()
    {

        final View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final AlertDialog alertDialog;
                switch (view.getId())
                {
                    case R.id.btn_change_language:
                        showDialogLikeTooltip();
                        break;

                    case R.id.btnEmailBody:
                        startActivity(new Intent(getActivity(), EmailBodyActivity.class));
                        break;

                    case R.id.btn_signature:
                        startActivityForResult(new Intent(getActivity(), SetSignatureActivity.class), REQUEST_CODE_SAVE_SIGNATURE);
                        break;

                    case R.id.btnCCAddress:
                        startActivity(new Intent(getActivity(), CcAddressChipLayoutActivity.class));
                        break;

                    case R.id.btnBCCAddress:
                        startActivity(new Intent(getActivity(), BccAddressChipLayoutActivity.class));
                        break;

                    case R.id.buttonStorage:
                        startActivity(new Intent(getActivity(), StorageActivity.class));
                        break;

                    case R.id.buttonTerms:
                        startActivity(new Intent(getActivity(), TermsActivity.class));
                        break;

                    case R.id.buttonPrivacy:
                        startActivity(new Intent(getActivity(), PrivacyPolicyActivity.class));
                        break;

                    case R.id.button_openSource_Libraries:
                        startActivity(new Intent(getActivity(), OpenSourceLibrariesActivity.class));
                        break;


                    case R.id.button_help:
                        Helper.preventTwoClick(buttonHelp);
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("plain/text");
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@smacsoftwares.de"});
                        intent.putExtra(Intent.EXTRA_SUBJECT, "");
                        intent.putExtra(Intent.EXTRA_TEXT, "");
                        startActivity(Intent.createChooser(intent, ""));
                        break;

                    case R.id.btn_synchronize_now:
                        Helper.preventTwoClick(btnSynchronizeNow);
                        AlertDialog dialog = new AlertDialog.Builder(context).create();
                        dialog.setTitle(context.getString(R.string.app_name));
                        dialog.setMessage(context.getString(R.string.sync_update_dialog));
                        dialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.ok),
                                new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        if (Helper.isNetworkAvailable(context))
                                        {
                                            UserPreference userPreference = new UserPreference();
                                            userPreference.userId = PreferenceHelper.getUserContext(context);
                                            userPreference.populateUsingUserId(context);
                                            String lastSyncDate = userPreference.lastSyncDate;
                                            try
                                            {
                                                DataHelper.getUserLike(context, arrayListUserLikes);
                                                DataHelper.getUserComments(context, arrayListUserComments);
                                                for (UserLike like : arrayListUserLikes)
                                                {
                                                    JSONObject likeJson = like.toJson();
                                                    int channelId = DataHelper.getChannelId(context, like.associatedId);
                                                    likeJson.put("ChannelId", channelId);
                                                    jsonArrayUserLikes.put(likeJson);
                                                }
                                                for (UserComment userComment : arrayListUserComments)
                                                {
                                                    JSONObject commentJson = userComment.toJson();
                                                    int channelId = DataHelper.getChannelId(context, userComment.fileId);
                                                    commentJson.put("ChannelId", channelId);
                                                    jsonArrayUserComments.put(commentJson);
                                                    Log.e("TEST SYNC Comment>>", commentJson.toString());
                                                }
                                            }
                                            catch (Exception ex)
                                            {

                                            }
                                            postNetworkRequest(REQUEST_SYNC, DataProvider.ENDPOINT_SYNC, DataProvider.Actions.SYNC,
                                                    /*RequestParameter.urlEncoded("Org_Id", String.valueOf()),*/
                                                    RequestParameter.jsonArray("UserLikes", jsonArrayUserLikes), RequestParameter.jsonArray("UserComments", jsonArrayUserComments),
                                                    RequestParameter.urlEncoded("UserId", String.valueOf(userPreference.userId)), RequestParameter.urlEncoded("LastSyncDate", lastSyncDate), RequestParameter.urlEncoded("Org_Id", String.valueOf(PreferenceHelper.getOrganizationId(context))));


                                        }
                                        else
                                        {
                                            Helper.showMessage((Activity) getActivity(), false, getString(R.string.msg_please_check_your_connection));
                                        }


                                    }

                                });
                        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.label_cancel),
                                new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int which)
                                    {

                                        dialog.cancel();
                                    }
                                });
                        dialog.show();
                        break;

                    case R.id.btn_about_us:
                        startActivity(new Intent(getActivity(), AboutUsActivity.class));
                        break;

                    case R.id.btn_password:
                        if (prefManager.isDemoLogin())
                        {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                            builder1.setTitle(getString(R.string.access_denied_title));
                            builder1.setMessage(getString(R.string.access_denied_message));
                            builder1.setPositiveButton(getString(R.string.ok),
                                    new DialogInterface.OnClickListener()
                                    {
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            dialog.dismiss();
                                        }
                                    });

                            AlertDialog dialog1 = builder1.create();

                            dialog1.show();
                        }
                        else
                        {
                            startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
                        }
                        break;

                    case R.id.btn_sign_out:
                        showSignOutDialog();
                        break;

                    case R.id.compoundButtonSyncNow:
                        startSyncActivity();
                        break;

                }
            }
        };

        btnPassword.setOnClickListener(onClickListener);
        btnChangeLanguage.setOnClickListener(onClickListener);
        btnEmailBody.setOnClickListener(onClickListener);
        btnCCAddress.setOnClickListener(onClickListener);
        btnBCCAddress.setOnClickListener(onClickListener);
        btnSignature.setOnClickListener(onClickListener);
        buttonStorage.setOnClickListener(onClickListener);
        buttonTerms.setOnClickListener(onClickListener);
        buttonPrivacyPolicy.setOnClickListener(onClickListener);
        buttonOpenSourceLibraries.setOnClickListener(onClickListener);
        buttonHelp.setOnClickListener(onClickListener);
        btnAboutUs.setOnClickListener(onClickListener);
        btnSynchronizeNow.setOnClickListener(onClickListener);
        btnSignOut.setOnClickListener(onClickListener);
        buttonTerms.setOnClickListener(onClickListener);
        buttonOpenSourceLibraries.setOnClickListener(onClickListener);
        buttonHelp.setOnClickListener(onClickListener);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE)
        {
            if (!Helper.checkStoragePermission(context))
                Helper.requestStoragePermission(activity, findViewById(R.id.parentLayout));
        }
    }

    public void showSignOutDialog()
    {
        final EasyDialog dialog = new EasyDialog(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_signout, null);
        view.setLayoutParams(new RelativeLayout.LayoutParams(Helper.getDeviceWidth(getActivity()) / 2, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout linearParent = (LinearLayout) view.findViewById(R.id.parentLayout);
        Helper.setupTypeface(linearParent, Helper.robotoRegularTypeface);

        TextView txtSignOutTitle = (TextView) view.findViewById(R.id.txt_signOut_title);
        txtSignOutTitle.setTextColor(Color.parseColor(PreferenceHelper.getAppColor(context)));

        RelativeLayout keepDownloadFile = (RelativeLayout) view.findViewById(R.id.btn_keep_download_files);
        final RelativeLayout deleteDownloadFile = (RelativeLayout) view.findViewById(R.id.btn_delete_download_files);
        RelativeLayout btnCancel = (RelativeLayout) view.findViewById(R.id.btn_cancel);

        keepDownloadFile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (Helper.isNetworkAvailable(context))
                {
                    DELETE_USER_PREFERENCE = false;
                    postNetworkRequest(REQUEST_LOGOUT, DataProvider.ENDPOINT_LOGOUT, DataProvider.Actions.LOGOUT,
                            RequestParameter.urlEncoded("DeviceToken", PreferenceHelper.getFCMTokenId(context)), RequestParameter.urlEncoded("Platform", "Android"), RequestParameter.urlEncoded("Org_Id", PreferenceHelper.getOrganizationId(context)));

                }
                else
                {
                    Helper.showMessage(activity, false, getString(R.string.msg_please_check_your_connection));
                }
                dialog.dismiss();
            }


        });
        deleteDownloadFile.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                if (Helper.isNetworkAvailable(context))
                {
                    DELETE_USER_PREFERENCE = true;
                    postNetworkRequest(REQUEST_LOGOUT, DataProvider.ENDPOINT_LOGOUT, DataProvider.Actions.LOGOUT,
                            RequestParameter.urlEncoded("DeviceToken", PreferenceHelper.getFCMTokenId(context)), RequestParameter.urlEncoded("Platform", "Android"), RequestParameter.urlEncoded("Org_Id", PreferenceHelper.getOrganizationId(context)));

                }
                else
                {
                    Helper.showMessage(activity, false, getString(R.string.msg_please_check_your_connection));
                }
                dialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });
        dialog.setLayout(view)
                .setGravity(EasyDialog.GRAVITY_TOP)
                .setBackgroundColor(getActivity().getResources().getColor(R.color.white1))
                .setLocationByAttachedView(btnSignOut)
                .setTouchOutsideDismiss(true)
                .setMatchParent(false)
                .show();

    }

    public void showDialogLikeTooltip()
    {
        final EasyDialog dialog = new EasyDialog(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_language_list, null);
        boolean isTabletSize = context.getResources().getBoolean(R.bool.isTablet);
        if (isTabletSize)
        {
            view.setLayoutParams(new RelativeLayout.LayoutParams(Helper.getDeviceWidth(getActivity()) / 3, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        else
        {
            view.setLayoutParams(new RelativeLayout.LayoutParams(Helper.getDeviceWidth(getActivity()) / 2, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        final ListView listLanguage = (ListView) view.findViewById(R.id.listLanguage);
        LanguageListViewAdapter languageListViewAdapter = new LanguageListViewAdapter(getActivity());
        listLanguage.setAdapter(languageListViewAdapter);
        listLanguage.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                if (position == 0)
                {
                    selectedLang = "en";
                    PreferenceHelper.storeSelectedLanguage(getActivity(), "en");

                }
                else if (position == 1)
                {
                    selectedLang = "de";
                    PreferenceHelper.storeSelectedLanguage(getActivity(), "de");

                }
                updateLanguage();
                ((DashboardActivity) activity).updateNavigationMenuString();
                dialog.dismiss();
            }
        });
        dialog.setLayout(view)
                .setGravity(EasyDialog.GRAVITY_BOTTOM)
                .setBackgroundColor(getActivity().getResources().getColor(R.color.white1))
                .setLocationByAttachedView(textViewChangeLanguage)
                .setTouchOutsideDismiss(true)
                .setMatchParent(false)
                .show();

    }


    public void updateLanguage()
    {

        if (PreferenceHelper.getSelectedLanguage(context).equals("") || PreferenceHelper.getSelectedLanguage(context).equals("en"))
        {
            if (PreferenceHelper.getSelectedLanguage(context).equals(""))
                PreferenceHelper.storeSelectedLanguage(context, "en");

            Helper.setUpLanguage(context, PreferenceHelper.getSelectedLanguage(context));
        }
        else if (PreferenceHelper.getSelectedLanguage(context).equals("") || PreferenceHelper.getSelectedLanguage(context).equals("de"))
        {
            PreferenceHelper.storeSelectedLanguage(context, "de");

            Helper.setUpLanguage(context, PreferenceHelper.getSelectedLanguage(context));
        }
        if (!prefManager.isDemoLogin())
        {

            textViewChangePassword.setText(R.string.label_password);
        }
        textViewAccount.setText(R.string.label_account);
        textViewChangeLanguage.setText(R.string.label_change_language);
        textViewChangePassword.setText(R.string.label_password);
        textViewSharing.setText(R.string.label_sharing1);
        textViewEmailBody.setText(R.string.label_email_body);
        textViewCc.setText(R.string.label_cc_address);
        textViewBcc.setText(R.string.label_bcc_address);
        textViewSignature.setText(R.string.label_signature);
        textViewTitleSynchronization.setText(R.string.label_synchronization);
        textViewSynchronizeNow.setText(R.string.label_synchronize_now);
        textViewAutoDownload.setText(R.string.label_auto_download);
        textViewInformation.setText(R.string.label_information);
        textViewStorage.setText(R.string.label_storage);
        textViewTerms.setText(R.string.label_terms);
        textViewHelp.setText(R.string.label_help);
        textViewOpenSorceLibraries.setText(R.string.label_open_source_libraries);
        textViewAboutUs.setText(R.string.label_about_us);
        textViewLogin.setText(R.string.label_login_setting);
        textViewSignOut.setText(R.string.lable_sign_out);
        activity.getSupportActionBar().setTitle(R.string.settings);


        interfacechangeLanguage.changeLanguage(selectedLang);
    }

    @Override
    protected void onNetworkResponse(int requestCode, boolean status, String response)
    {
        super.onNetworkResponse(requestCode, status, response);
        if (requestCode == REQUEST_SYNC)
        {
            if (status)
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

                        syncJson = syncJson.optJSONObject("Payload");
                        Log.e("Setting Sync Payload>>", syncJson.toString());

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
                        ArrayList<Channel> arrayListChannels = new ArrayList<>();
                        JSONArray channelJsonArray = syncJson.optJSONArray("Channels");
                        try
                        {
                            Channel.parseListFromJson(channelJsonArray, arrayListChannels);
                            for (Channel channel : arrayListChannels)
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

                            }

                            if (syncJson.has("Media") && !syncJson.isNull("Media"))
                            {
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
                                            Media oldMedia = new Media();
                                            oldMedia.id = media.id;
                                            DataHelper.getMedia(context, oldMedia);
                                            media.isDownloaded = oldMedia.isDownloaded;
                                            media.isDownloading = oldMedia.isDownloading;
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
                            }

                            if (syncJson.has("ChannelFiles") && !syncJson.isNull("ChannelFiles"))
                            {
                                JSONArray channelFilesJsonArray = syncJson.optJSONArray("ChannelFiles");
                                ArrayList<ChannelFiles> arrayListChannelFiles = new ArrayList<>();
                                ChannelFiles.parseListFromJson(channelFilesJsonArray, arrayListChannelFiles);
                                for (ChannelFiles channelFile : arrayListChannelFiles)
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
                            }

                            if (syncJson.has("ChannelUsers") && !syncJson.isNull("ChannelUsers"))
                            {
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
                            }

                            if (syncJson.has("UserComments") && !syncJson.isNull("UserComments"))
                            {
                                JSONArray userCommentsJsonArray = syncJson.optJSONArray("UserComments");
                                ArrayList<UserComment> arraylistuserComments = new ArrayList<>();
                                UserComment.parseListFromJson(userCommentsJsonArray, arraylistuserComments);
                                for (UserComment usercomment : arraylistuserComments)
                                {
                                    switch (usercomment.syncType)
                                    {
                                        case 1:
                                            if (!DataHelper.isCommentExist(context, usercomment.id))
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
                            }

                            if (syncJson.has("UserLikes") && !syncJson.isNull("UserLikes"))
                            {
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
                            showUpdatedDialog(getString(R.string.title_synchronization), getString(R.string.sync_data_update_sucessfully));
                            //Helper.showSimpleDialog(context, getString(R.string.sync_data_update_sucessfully));


                        }
                        catch (JSONException | ParseException e)
                        {
                            e.printStackTrace();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
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
        else if (requestCode == REQUEST_LOGOUT)
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

                        if (DELETE_USER_PREFERENCE)
                        {
                            UserPreference userPreference = new UserPreference();
                            userPreference.userId = PreferenceHelper.getUserContext(context);
                            DataHelper.getUserPreference(getActivity(), userPreference);
                            DataHelper.removeMediaPhysically(activity, parentLayout);
                            DataHelper.closeLocalDatabase(context);
                            PreferenceHelper.removeUserThemePreferences(context);
                            prefManager.saveFullDownloadMedia(false);

                            if (context.deleteDatabase(userPreference.databaseName))
                                Log.e("Test>>", "Database delete successfully");
                            else
                                Log.e("Test>>", "Database delete failure");

                            userPreference.remove(activity);
                        }

                        PreferenceHelper.removeUserContext(context);
                        Intent loginActivity = new Intent(getActivity(), DemoActivity.class);
                        loginActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        new FCMInstanceIdService(context).deleteInstanceId();
                        PreferenceHelper.removeUserThemePreferences(context);
                        startActivity(loginActivity);
                        getActivity().finish();
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

    public void showUpdatedDialog(String title, String message)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setMessage(message);
        alertDialog.setTitle(title);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                //  onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Open sync activity
     */
    private void startSyncActivity()
    {
        Intent syncIntent = new Intent(getActivity(), SyncActivity.class);
        syncIntent.putExtra(SyncActivity.IS_FROM_SETTING, true);
        startActivity(syncIntent);

    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        interfacechangeLanguage = (InterfacechangeLanguage) context;

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        updateLanguage();
    }

    @Override
    public void onThemeChangeNotificationReceived()
    {
        applyThemeColor();
    }

    public interface InterfacechangeLanguage
    {
        void changeLanguage(String lang);
    }
}

