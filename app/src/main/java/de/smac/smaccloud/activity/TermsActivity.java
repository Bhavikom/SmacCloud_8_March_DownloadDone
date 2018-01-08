package de.smac.smaccloud.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import de.smac.smaccloud.R;
import de.smac.smaccloud.base.Activity;

public class TermsActivity extends Activity
{
    WebView webViewTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        webViewTerms = (WebView) findViewById(R.id.wvterms);
        webViewTerms.loadUrl("file:///android_asset/Terms/index.html");
        webViewTerms.clearCache(true);
        webViewTerms.clearHistory();
        webViewTerms.getSettings().setJavaScriptEnabled(true);
        webViewTerms.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);


        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle(getString(R.string.label_terms));
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
