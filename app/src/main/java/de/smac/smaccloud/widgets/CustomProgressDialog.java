package de.smac.smaccloud.widgets;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import de.smac.smaccloud.R;


public class CustomProgressDialog extends Dialog
{

    public CustomProgressDialog(Context context)
    {
        super(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_progress_dialog);
    }
}
