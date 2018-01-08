package de.smac.smaccloud.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import de.smac.smaccloud.R;
import de.smac.smaccloud.base.Activity;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.widgets.SquareImageView;

public class SetSignatureActivity extends Activity
{
    public static int PERMISSION_REQUEST_CODE = 101;
    private static int CAPTURE_IMAGE_REQUEST_CODE = 1001;
    public LinearLayout parentLayout;
    Button btnPickImage;
    MenuInflater inflater;
    ImageView imgCancel;
    SquareImageView imgUserSign;
    EditText editSignature;
    String path;
    String localPath;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_signature);
        Helper.retainOrientation(SetSignatureActivity.this);
        editSignature = (EditText) findViewById(R.id.edit_set_signature);
        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);

        editSignature.setText(PreferenceHelper.getSignature(context));
        editSignature.setSelection(editSignature.getText().length());

        Helper.setupTypeface(parentLayout, Helper.robotoRegularTypeface);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle(getString(R.string.label_signature));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_set_signature_activity, menu);

        menu.findItem(R.id.action_reset).setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_save:
                PreferenceHelper.storeSetSignature(context, editSignature.getText().toString());
                if (localPath != null && !localPath.isEmpty())
                    PreferenceHelper.storeSetSignatureImage(context, localPath);
                setResult(Activity.RESULT_OK);
                finish();
                break;

            case R.id.action_reset:
                editSignature.getText().clear();
                break;

            case android.R.id.home:
                onBackPressed();
                return true;

        }
        return super.onOptionsItemSelected(item);
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