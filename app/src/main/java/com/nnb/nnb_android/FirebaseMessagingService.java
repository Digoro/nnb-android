package com.nnb.nnb_android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
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

    private  String body ,url,message , title;
    private boolean isAlarm;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.e(TAG, "onMessageReceived");

        title = remoteMessage.getNotification().getTitle();


        body = remoteMessage.getNotification().getBody();
        try {
            JSONObject jsonObject = new JSONObject(body);
            url = jsonObject.getString("redirectUrl");
            message = jsonObject.getString("message");
            isAlarm = jsonObject.getBoolean("isAlarm");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Intent intent = new Intent(this,MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("url",url);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);

        Notification mBuilder = null;

        if(isAlarm) {
            mBuilder = new NotificationCompat.Builder(this, "MY_channel").setSmallIcon(R.drawable.splash)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setVibrate(new long[]{1, 1000})
                    .build();
        }else {
            mBuilder = new NotificationCompat.Builder(this, "MY_channel").setSmallIcon(R.drawable.splash)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .build();
        }


        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        // 알림 표시: 알림의 고유 ID(ex: 1002), 알림 결과
        notificationManager.notify(1002, mBuilder);
    }
}
