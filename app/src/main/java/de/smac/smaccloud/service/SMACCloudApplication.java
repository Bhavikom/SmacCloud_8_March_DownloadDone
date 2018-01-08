package de.smac.smaccloud.service;

import android.content.Intent;
import android.support.multidex.MultiDexApplication;

import java.util.ArrayList;

import de.smac.smaccloud.base.NetworkService;
import de.smac.smaccloud.model.Media;
import de.smac.smaccloud.model.ThreadModel;

/**
 * Start network service
 */
public class SMACCloudApplication extends MultiDexApplication
{
    public ArrayList<ThreadModel> arrayListThread;
    public ArrayList<Media> arrayListMediaTemp;
    private static SMACCloudApplication _instance;

    public static SMACCloudApplication getInstance()
    {
        return _instance;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        arrayListThread = new ArrayList<>();
        arrayListMediaTemp = new ArrayList<>();
        _instance = this;
        Intent networkServiceIntent = new Intent(this, NetworkService.class);
        startService(networkServiceIntent);
    }
}
