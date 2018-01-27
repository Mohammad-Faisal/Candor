package com.example.candor.candor;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;

import com.example.candor.candor.R;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Mohammad Faisal on 11/28/2017.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String notificationTitle = remoteMessage.getNotification().getTitle();
        String notificatoinMesssage = remoteMessage.getNotification().getBody();
        String click_action = remoteMessage.getNotification().getClickAction();

        String from_user = remoteMessage.getData().get("from_user");

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark_normal_background)
                        .setContentTitle(notificationTitle)
                        .setContentText(notificatoinMesssage);

        //on click er jonnno

        Intent resultIntent = new Intent (click_action) ;
        resultIntent.putExtra("userID" , from_user);


        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);





        //default
        int mNotificationId = (int) System.currentTimeMillis();  //eita korle unique hobe
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());


        //all codes ar from
        //https://developer.android.com/training/notify-user/build-notification.html#notify

    }
}

