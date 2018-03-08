package de.smac.smaccloud.activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import de.smac.smaccloud.R;
import de.smac.smaccloud.adapter.TermsConditionListAdapter;
import de.smac.smaccloud.base.Activity;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.model.TermsAndCondition;
import de.smac.smaccloud.service.FCMMessagingService;

public class OpenSourceLibrariesActivity extends Activity
{

    ListView openSourceList;
    List<TermsAndCondition> rowItems;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_source_libraris);
        openSourceList = (ListView) findViewById(R.id.openSourceList);
        String[] titles = new String[]{getString(R.string.openSource_title1), getString(R.string.openSource_title2), getString(R.string.openSource_title3), getString(R.string.openSource_title4), getString(R.string.openSource_title5), getString(R.string.openSource_title6), getString(R.string.openSource_title7), getString(R.string.openSource_title8)};
        String[] descriptions = new String[]{getString(R.string.openSource_description1), getString(R.string.openSource_description2), getString(R.string.openSource_description3), getString(R.string.openSource_description4), getString(R.string.openSource_description5), getString(R.string.openSource_description6), getString(R.string.openSource_description7), getString(R.string.openSource_description8)};

        rowItems = new ArrayList<TermsAndCondition>();
        for (int i = 0; i < titles.length; i++)
        {
            TermsAndCondition item = new TermsAndCondition(titles[i], descriptions[i]);
            rowItems.add(item);
        }
        TermsConditionListAdapter adapter = new TermsConditionListAdapter(this,
                R.layout.activity_termslist, rowItems);
        openSourceList.setAdapter(adapter);
        applyThemeColor();
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
        updateParentThemeColor();
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle(getString(R.string.label_open_source_libraries));
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(PreferenceHelper.getAppBackColor(context))));
            final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back_material_vector);
            upArrow.setColorFilter(Color.parseColor(PreferenceHelper.getAppColor(context)), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            toolbar.setTitleTextColor(Color.parseColor(PreferenceHelper.getAppColor(context)));
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

    @Override
    protected void onResume()
    {
        super.onResume();
        applyThemeColor();
    }
}
