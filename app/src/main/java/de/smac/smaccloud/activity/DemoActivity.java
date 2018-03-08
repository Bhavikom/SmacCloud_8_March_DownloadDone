package de.smac.smaccloud.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

public class DemoActivity extends Activity implements View.OnClickListener
{
    public EasyDialog dialog;
    Button buttonLogin, buttonSignUp;
    TextView textViewTitle, textViewSmacSoftwareLink;
    Activity activity;
    PreferenceHelper preManager;
    MenuInflater inflater;
    private ImageView languageChange;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_demo);
        preManager = new PreferenceHelper(this);
        Helper.retainOrientation(DemoActivity.this);
        textViewTitle = (TextView) findViewById(R.id.demo_title);
        textViewSmacSoftwareLink = (TextView) findViewById(R.id.txt_smac_link);
        languageChange = (ImageView) findViewById(R.id.language_english);
        buttonLogin = (Button) findViewById(R.id.btn_login);
        buttonSignUp = (Button) findViewById(R.id.btn_sign_up);

        buttonLogin.setOnClickListener(this);
        buttonSignUp.setOnClickListener(this);
        languageChange.setOnClickListener(this);
        textViewSmacSoftwareLink.setOnClickListener(this);
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
        buttonSignUp.setText(getResources().getString(R.string.sign_up));
        textViewSmacSoftwareLink.setText(getResources().getString(R.string.smac_link));

    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_login:

                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                break;

            case R.id.btn_sign_up:
                Intent i1 = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(i1);
                break;

            case R.id.language_english:
                showDialogLikeTooltip();
                break;

            case R.id.txt_smac_link:
                String url = "https://www.smacsoftwares.com";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_information:
                String url = "https://www.smaccloud.com/help/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_information, menu);
        applyThemeColor();
        return super.onCreateOptionsMenu(menu);
    }

    public void applyThemeColor()
    {
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_info, null);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, getResources().getColor(R.color.orange_color));
    }


}
