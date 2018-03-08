package de.smac.smaccloud.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.smac.smaccloud.R;
import de.smac.smaccloud.base.Activity;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.service.FCMMessagingService;
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

    public static Drawable convertTextToDrawable(Context context, String text, int color)
    {
        TextView txtActionAdd = new TextView(context);
        txtActionAdd.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        txtActionAdd.setText(text);
        txtActionAdd.setTextColor(color);

        txtActionAdd.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.title_small));

        txtActionAdd.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        txtActionAdd.layout(0, 0, txtActionAdd.getMeasuredWidth(), txtActionAdd.getMeasuredHeight());

        txtActionAdd.setDrawingCacheEnabled(true);
        txtActionAdd.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(txtActionAdd.getDrawingCache());
        txtActionAdd.setDrawingCacheEnabled(false);

        return new BitmapDrawable(context.getResources(), bitmap);
    }

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
        Helper.setupTypeface(parentLayout, Helper.robotoRegularTypeface);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle(getString(R.string.label_signature));
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(PreferenceHelper.getAppBackColor(context))));
            final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back_material_vector);
            upArrow.setColorFilter(Color.parseColor(PreferenceHelper.getAppColor(context)), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            toolbar.setTitleTextColor(Color.parseColor(PreferenceHelper.getAppColor(context)));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_set_signature_activity, menu);
        menu.findItem(R.id.action_save).setIcon(convertTextToDrawable(context, getString(R.string.label_save), Color.parseColor(PreferenceHelper.getAppColor(context))));
        menu.findItem(R.id.action_reset).setIcon(convertTextToDrawable(context, getString(R.string.label_reset), Color.parseColor(PreferenceHelper.getAppColor(context))));//   menu.findItem(R.id.action_reset).setVisible(true);
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

    @Override
    protected void onResume()
    {
        super.onResume();
        applyThemeColor();
    }
}