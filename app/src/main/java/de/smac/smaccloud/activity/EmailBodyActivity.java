package de.smac.smaccloud.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import de.smac.smaccloud.R;
import de.smac.smaccloud.base.Activity;
import de.smac.smaccloud.helper.PreferenceHelper;

public class EmailBodyActivity extends Activity
{

    EditText editEmailBody;
    MenuInflater inflater;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_body);
        editEmailBody = (EditText) findViewById(R.id.edit_emailBody);


        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle(getString(R.string.label_email_body));
        }
        editEmailBody.setText(PreferenceHelper.getEmailBody(context));
        editEmailBody.setSelection(editEmailBody.getText().length());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_done:
                PreferenceHelper.storeEmailBody(context, editEmailBody.getText().toString());
                finish();
                break;
            case R.id.action_reset:
                editEmailBody.getText().clear();
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_email_cc, menu);
        return super.onCreateOptionsMenu(menu);
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
