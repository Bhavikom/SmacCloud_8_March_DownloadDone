package de.smac.smaccloud.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.michael.easydialog.EasyDialog;

import de.smac.smaccloud.R;
import de.smac.smaccloud.adapter.LanguageListViewAdapter;
import de.smac.smaccloud.base.Activity;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.helper.PreferenceHelper;

import static de.smac.smaccloud.activity.OrganizationActivity.IS_FROM_DEMO;

public class DemoActivity extends Activity implements View.OnClickListener
{
    public EasyDialog dialog;
    Button buttonLogin, buttonTryDemo;
    TextView textViewTitle, textViewConfigureServer;
    Activity activity;
    PreferenceHelper preManager;
    private ImageView languageChange;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_demo);
        preManager = new PreferenceHelper(this);
        Helper.retainOrientation(DemoActivity.this);
        textViewTitle = (TextView) findViewById(R.id.demo_title);
        textViewConfigureServer = (TextView) findViewById(R.id.txt_Configure_Server);
        languageChange = (ImageView) findViewById(R.id.language_english);
        buttonLogin = (Button) findViewById(R.id.btn_login);
        buttonTryDemo = (Button) findViewById(R.id.btn_try_demo);

        textViewTitle.setTypeface(Helper.robotoBoldTypeface);
        buttonLogin.setTypeface(Helper.robotoMediumTypeface);
        buttonTryDemo.setTypeface(Helper.robotoMediumTypeface);
        textViewConfigureServer.setTypeface(Helper.robotoBoldTypeface);

        buttonLogin.setOnClickListener(this);
        buttonTryDemo.setOnClickListener(this);
        languageChange.setOnClickListener(this);
        textViewConfigureServer.setOnClickListener(this);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle(getString(R.string.app_name));
            actionBar.setHomeButtonEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
        }
        updateLanguage();
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

    public void updateLanguage()
    {
        /*if (PreferenceHelper.getSelectedLanguage(context).equals("") || PreferenceHelper.getSelectedLanguage(context).equals("en"))
        {
            //if (PreferenceHelper.getSelectedLanguage(context).equals("")){
            PreferenceHelper.storeSelectedLanguage(context, "en");
            Helper.setUpLanguage(context, PreferenceHelper.getSelectedLanguage(context));
            languageChange.setImageResource(R.drawable.ic_flag_english);
            buttonLogin.setText(getResources().getString(R.string.login));
            buttonTryDemo.setText(getResources().getString(R.string.tra_demo));
            //}

        }
        else if (PreferenceHelper.getSelectedLanguage(context).equals("") || PreferenceHelper.getSelectedLanguage(context).equals("de"))
        {
            PreferenceHelper.storeSelectedLanguage(context, "de");
            Helper.setUpLanguage(context, PreferenceHelper.getSelectedLanguage(context));
            languageChange.setImageResource(R.drawable.ic_flag_german);
            buttonLogin.setText(getResources().getString(R.string.login));
            buttonTryDemo.setText(getResources().getString(R.string.tra_demo));
        }*/

        if (PreferenceHelper.getSelectedLanguage(context).equals("") || PreferenceHelper.getSelectedLanguage(context).equals("en"))
        {
            if (PreferenceHelper.getSelectedLanguage(context).equals(""))
                PreferenceHelper.storeSelectedLanguage(context, "en");

            Helper.setUpLanguage(context, PreferenceHelper.getSelectedLanguage(context));
            languageChange.setImageResource(R.drawable.ic_flag_english);
        }
        else if (PreferenceHelper.getSelectedLanguage(context).equals("") || PreferenceHelper.getSelectedLanguage(context).equals("de"))
        {
            PreferenceHelper.storeSelectedLanguage(context, "de");
            Helper.setUpLanguage(context, PreferenceHelper.getSelectedLanguage(context));
            languageChange.setImageResource(R.drawable.ic_flag_german);
        }
        buttonLogin.setText(getResources().getString(R.string.login));
        buttonTryDemo.setText(getResources().getString(R.string.tra_demo));
        textViewConfigureServer.setText(getResources().getString(R.string.configure_url));

    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_login:
                if (preManager.isFirstTimeConfigureServerLanuch())
                {
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                }
                else
                {
                    Intent i = new Intent(getApplicationContext(), OrganizationActivity.class);
                    startActivity(i);
                }
                break;

            case R.id.btn_try_demo:
                Intent i1 = new Intent(getApplicationContext(), TryDemoActivity.class);
                startActivity(i1);
                break;

            case R.id.language_english:
                showDialogLikeTooltip();
                break;

            case R.id.txt_Configure_Server:
                Intent configureServer = new Intent(getApplicationContext(), OrganizationActivity.class);
                configureServer.putExtra(IS_FROM_DEMO, false);
                startActivity(configureServer);
                break;
        }
    }

    public void showDialogLikeTooltip()
    {
        dialog = new EasyDialog(DemoActivity.this);
        final View view = getLayoutInflater().inflate(R.layout.dialog_language_list, null);
        if (Helper.isTablet(DemoActivity.this))
        {
            view.setLayoutParams(new RelativeLayout.LayoutParams(Helper.getDeviceWidth(this) / 4, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        else
        {
            view.setLayoutParams(new RelativeLayout.LayoutParams(Helper.getDeviceWidth(this) / 2, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        final ListView listLanguage = (ListView) view.findViewById(R.id.listLanguage);
        LanguageListViewAdapter languageListViewAdapter = new LanguageListViewAdapter(DemoActivity.this);
        listLanguage.setAdapter(languageListViewAdapter);
        listLanguage.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                if (position == 0)
                {
                    PreferenceHelper.storeSelectedLanguage(DemoActivity.this, "en");
                    Helper.setUpLanguage(context, PreferenceHelper.getSelectedLanguage(context));
                    languageChange.setImageResource(R.drawable.ic_flag_english);
                }
                else if (position == 1)
                {
                    PreferenceHelper.storeSelectedLanguage(DemoActivity.this, "de");
                    Helper.setUpLanguage(context, PreferenceHelper.getSelectedLanguage(context));
                    languageChange.setImageResource(R.drawable.ic_flag_german);
                }
                updateLanguage();
                dialog.dismiss();
            }
        });
        dialog.setLayout(view)
                .setGravity(EasyDialog.GRAVITY_BOTTOM)
                .setBackgroundColor(DemoActivity.this.getResources().getColor(R.color.white_color))
                .setLocationByAttachedView(languageChange)
                .setTouchOutsideDismiss(true)
                .setMatchParent(false)
                .show();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        updateLanguage();
    }

}
