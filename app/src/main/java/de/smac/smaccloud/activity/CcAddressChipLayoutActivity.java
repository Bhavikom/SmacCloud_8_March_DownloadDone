package de.smac.smaccloud.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.model.ChipInterface;
import com.pchmn.materialchips.views.ChipsInputEditText;

import java.util.ArrayList;
import java.util.List;

import de.smac.smaccloud.R;
import de.smac.smaccloud.base.Activity;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.service.FCMMessagingService;

public class CcAddressChipLayoutActivity extends Activity
{
    MenuInflater inflater;
    ChipsInput chips_input_email_cc;
    RecyclerView chipRecyclerView;
    ChipsInputEditText chipsInputEditText;
    LinearLayout parentLayout;

    public static Drawable convertTextToDrawable(Context context, String text, int color)
    {
        TextView txtActionAdd = new TextView(context);
        txtActionAdd.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        txtActionAdd.setText(text);
        txtActionAdd.setTextColor(color);
        Helper.setupTypeface(txtActionAdd, Helper.robotoRegularTypeface);
        txtActionAdd.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.title_large));

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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cc_address_chiplayout);

        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);
        chips_input_email_cc = (ChipsInput) findViewById(R.id.chips_input_email_cc);

        chips_input_email_cc.addChipsListener(new ChipsInput.ChipsListener()
        {
            @Override
            public void onChipAdded(ChipInterface chipInterface, int i)
            {

            }

            @Override
            public void onChipRemoved(ChipInterface chipInterface, int i)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence)
            {
                if (charSequence.toString().endsWith(" ") || charSequence.toString().endsWith(","))
                {
                    String strEmail = String.valueOf(charSequence.subSequence(0, charSequence.length() - 1));
                    if (Helper.isEmailValid(strEmail))
                    {
                        chips_input_email_cc.addChip(strEmail, "");
                    }
                }
            }
        });

        final List<String> emails = new Gson().fromJson(PreferenceHelper.getEmailCcAddress(context), List.class);
        if (emails != null && !emails.isEmpty())
        {
            for (String email : emails)
            {
                chips_input_email_cc.addChip(email, "");
            }
        }

        chipRecyclerView = (RecyclerView) chips_input_email_cc.findViewById(R.id.chips_recycler);
        chipsInputEditText = (ChipsInputEditText) chipRecyclerView.getChildAt(chipRecyclerView.getChildCount() - 1);
        chipRecyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener()
        {
            @Override
            public void onChildViewAttachedToWindow(View view)
            {

                if (chipsInputEditText != null)
                {
                    Helper.setupTypeface(chipsInputEditText, Helper.robotoRegularTypeface);
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(View view)
            {

            }
        });
        applyThemeColor();
        FCMMessagingService.themeChangeNotificationListener=new FCMMessagingService.ThemeChangeNotificationListener()
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
            getSupportActionBar().setTitle(getString(R.string.label_cc));
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(PreferenceHelper.getAppBackColor(context))));
            final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back_material_vector);
            upArrow.setColorFilter(Color.parseColor(PreferenceHelper.getAppColor(context)), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            toolbar.setTitleTextColor(Color.parseColor(PreferenceHelper.getAppColor(context)));
        }
        Helper.setupTypeface(chips_input_email_cc, Helper.robotoRegularTypeface);

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
                boolean isValidEmail = true;

                List<String> enteredEmails = new ArrayList<>();
                for (ChipInterface chipInterface : chips_input_email_cc.getSelectedChipList())
                {
                    enteredEmails.add(chipInterface.getLabel());
                }
                chipRecyclerView = (RecyclerView) chips_input_email_cc.findViewById(R.id.chips_recycler);

                chipsInputEditText = (ChipsInputEditText) chipRecyclerView.getChildAt(chipRecyclerView.getChildCount() - 1);
                //chipsInputEditText.setTypeface(Helper.robotoBlackTypeface);
                //   Helper.setupTypeface(chipsInputEditText, Helper.robotoRegularTypeface);
                if (chipsInputEditText != null && chipsInputEditText.getEditableText() != null && !chipsInputEditText.getEditableText().toString().isEmpty())
                {
                    enteredEmails.add(chipsInputEditText.getEditableText().toString());
                }

                isValidEmail = Helper.checkEmailAddresses(enteredEmails);
                if (isValidEmail)
                {
                    PreferenceHelper.storeEmailCCAddress(context, new Gson().toJson(enteredEmails));
                    finish();

                }
                else
                    Helper.showMessage(CcAddressChipLayoutActivity.this, false, getString(R.string.msg_please_check_your_email));

//                activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                break;
            case R.id.action_reset:
                chips_input_email_cc.removeChipByInfo("");
                chipRecyclerView = (RecyclerView) chips_input_email_cc.findViewById(R.id.chips_recycler);
                chipsInputEditText = (ChipsInputEditText) chipRecyclerView.getChildAt(chipRecyclerView.getChildCount() - 1);
                if (chipsInputEditText != null)
                {
                    chipsInputEditText.getEditableText().clear();
                    Helper.setupTypeface(chipsInputEditText, Helper.robotoRegularTypeface);
                }
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_email_cc, menu);
        menu.findItem(R.id.action_done).setIcon(convertTextToDrawable(context, getString(R.string.label_save), Color.parseColor(PreferenceHelper.getAppColor(context))));
        menu.findItem(R.id.action_reset).setIcon(convertTextToDrawable(context, getString(R.string.label_reset), Color.parseColor(PreferenceHelper.getAppColor(context))));
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

    @Override
    protected void onResume()
    {
        super.onResume();
        applyThemeColor();
    }
}
