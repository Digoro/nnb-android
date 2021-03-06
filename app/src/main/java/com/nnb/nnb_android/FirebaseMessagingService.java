package com.nnb.nnb_android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "FirebaseMsgService";

    private String redirectUrl, body, title;
    private boolean isAlarm;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.e(TAG, "onMessageReceived");

        try {
            JSONObject jsonObject = new JSONObject(new JSONObject(remoteMessage.getData()).getString("data"));
            title = jsonObject.getString("title");
            body = jsonObject.getString("body");
            redirectUrl = jsonObject.getString("redirectUrl");
            isAlarm = jsonObject.getBoolean("isAlarm");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        int requestID = (int) System.currentTimeMillis();

        Intent intent = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("url", redirectUrl);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, requestID, intent, PendingIntent.FLAG_ONE_SHOT);

        Notification mBuilder = null;

        if (isAlarm) {
            mBuilder = new NotificationCompat.Builder(this, "MY_channel").setSmallIcon(R.mipmap.nnb_logo_round)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setVibrate(new long[]{1, 1000})
                    .build();
        } else {
            mBuilder = new NotificationCompat.Builder(this, "MY_channel").setSmallIcon(R.mipmap.nnb_logo_round)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .build();
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // ?????? ??????: ????????? ?????? ID(ex: 1002), ?????? ??????
        notificationManager.notify(1002, mBuilder);
    }
}
