package de.smac.smaccloud.service;

import android.content.Context;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import de.smac.smaccloud.helper.PreferenceHelper;

public class FCMInstanceIdService extends FirebaseInstanceIdService
{
    String TAG = "SMAC CLOUD";
    Context context;

    public FCMInstanceIdService()
    {
    }

    public FCMInstanceIdService(Context context)
    {
        this.context = context;
    }

    @Override
    public void onTokenRefresh()
    {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "Refreshed token: " + refreshedToken);
        if (context != null)
        {
            PreferenceHelper.storeFCMTokenId(context, refreshedToken);
        }
    }

    public void deleteInstanceId()
    {
        try
        {
            FirebaseInstanceId.getInstance().deleteInstanceId();
            if (context != null)
            {
                PreferenceHelper.storeFCMTokenId(context, "");
                Log.e("TEST>>", "FCM ID Deleted!!!");
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
