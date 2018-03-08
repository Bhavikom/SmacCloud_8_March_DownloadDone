package de.smac.smaccloud.activity;

import android.content.Context;
import android.content.Intent;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.smac.smaccloud.R;
import de.smac.smaccloud.base.Activity;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.fragment.MediaAttachmentFragment;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.model.Channel;

public class MediaAttachmentActivity extends Activity
{
    public static final String EXTRA_CHANNEL = "extra_channel";
    public static final String EXTRA_PARENT = "extra_parent";
    public static final String EXTRA_VIEW = "extra_view";
    public static final String EXTRA_MEDIA = "extra_media";
    MediaAttachmentFragment mediaFragment;
    private MenuInflater inflater;
    private Channel channel;
    private int parentId = -1;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_attach);
        Helper.retainOrientation(MediaAttachmentActivity.this);
        Intent extras = getIntent();
        if (extras != null)
        {
            channel = extras.getExtras().getParcelable(EXTRA_CHANNEL);
            parentId = extras.getExtras().getInt(EXTRA_PARENT);
            if (getSupportActionBar() != null)
            {
                getSupportActionBar().setTitle(channel.name);
                final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back_material_vector);
                upArrow.setColorFilter(Color.parseColor(PreferenceHelper.getAppColor(context)), PorterDuff.Mode.SRC_ATOP);
                getSupportActionBar().setHomeAsUpIndicator(upArrow);
                toolbar.setTitleTextColor(Color.parseColor(PreferenceHelper.getAppColor(context)));

            }
        }


        mediaFragment = new MediaAttachmentFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable(EXTRA_CHANNEL, channel);
        arguments.putInt(EXTRA_PARENT, parentId);
        mediaFragment.setArguments(arguments);
        navigateToFragment(R.id.layoutDynamicFrame, mediaFragment, true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(PreferenceHelper.getAppBackColor(context))));
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_media_attachment_activity, menu);

        menu.findItem(R.id.action_add).setIcon(convertTextToDrawable(context, getString(R.string.label_add), Color.parseColor(PreferenceHelper.getAppColor(context))));

        /*Spannable bodySpannableString = new SpannableString(getString(R.string.label_add));
        bodySpannableString.setSpan(new ForegroundColorSpan(Color.BLUE), 0, bodySpannableString.toString().length(), 0);
        menu.findItem(R.id.action_add).setTitle(bodySpannableString);*/

        /*View view = findViewById(R.id.action_add);
        if (view != null && view instanceof TextView)
        {
            ((TextView) view).setTextColor(Color.parseColor(PreferenceHelper.getAppColor(context))); // Make text colour blue
            //((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_SP, 24); // Increase font size
        }*/

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_add:
                Intent intentReturn = new Intent();
                intentReturn.putExtra(ShareActivity.KEY_SELECTED_MEDIA, ShareAttachmentActivity.selectedAttachmentList);
                intentReturn.setFlags(Activity.RESULT_OK);
                setResult(Activity.RESULT_OK, intentReturn);
                finish();
                break;
        }
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        if (fragmentManager.getBackStackEntryCount() == 1)
            finish();
        else
            super.onBackPressed();

    }

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
}
