package de.smac.smaccloud.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

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

public class CcAddressChipLayoutActivity extends Activity
{
    MenuInflater inflater;

    ChipsInput chips_input_email_cc;
    RecyclerView chipRecyclerView;
    ChipsInputEditText chipsInputEditText;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cc_address_chiplayout);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle(getString(R.string.label_cc));
        }

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
