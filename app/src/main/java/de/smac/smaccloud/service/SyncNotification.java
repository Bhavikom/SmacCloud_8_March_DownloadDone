package de.smac.smaccloud.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;

import de.smac.smaccloud.R;
import de.smac.smaccloud.activity.LoginActivity;

/**
 * Show sync notification
 */
public class SyncNotification extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String message = intent.getStringExtra("message");
        String title = intent.getStringExtra("title");
        Intent notIntent = new Intent(context, LoginActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        Drawable myDrawable = context.getResources().getDrawable(R.drawable.chip_delete);
        Bitmap myLogo = ((BitmapDrawable) myDrawable).getBitmap();
        WearableExtender wearableExtender = new WearableExtender().setBackground(myLogo);
        Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_delete_white)
                .setContentTitle(title)
                .setContentText(message)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .extend(wearableExtender);
        Notification notification = builder.build();
        manager.notify(0, notification);
    }
}