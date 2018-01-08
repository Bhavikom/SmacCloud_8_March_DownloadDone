package de.smac.smaccloud.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import de.smac.smaccloud.R;
import de.smac.smaccloud.base.Activity;

public class OpenSourceLibrariesActivity extends Activity
{
    WebView webViewOpenSource;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_source_libraris);
        webViewOpenSource = (WebView) findViewById(R.id.wvOpenSourceLiberies);
        webViewOpenSource.loadUrl("file:///android_asset/libraries/Android.html");
        webViewOpenSource.clearCache(true);
        webViewOpenSource.clearHistory();
        webViewOpenSource.getSettings().setJavaScriptEnabled(true);
        webViewOpenSource.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);


        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle(getString(R.string.label_open_source_libraries));
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

}
