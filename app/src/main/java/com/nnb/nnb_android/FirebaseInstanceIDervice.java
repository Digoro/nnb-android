package com.nnb.nnb_android;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.View;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextKt;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class FirebaseInstanceIDervice extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIDService";

    public static boolean frm_delete = false;

    public static String beforeToken = "";

    @Override
    public void onTokenRefresh() {

        frm_delete = true;

        beforeToken = getToken();

        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, token);

        String url = "https://nonunbub.com/api/fcm/token";
        String postData = "";
        try {
            postData = "frm_token=" + URLEncoder.encode(getToken(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        byte[] byteArray=postData.getBytes();

        //webview2.postUrl(url, byteArray);

        setToken(token);

        sendRegistrationToServer(token);
    }

    // 값 저장하기
    public void setToken(String token) {
        SharedPreferences pref = getSharedPreferences("token", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("token", token);
        editor.commit();
    }

    // 값 불러오기
    public String getToken(){
         SharedPreferences pref = getSharedPreferences("token", MODE_PRIVATE);
        return pref.getString("token", "");
    }

    // 값 저장하기
    public void setBeforeToken(String token) {
        SharedPreferences pref = getSharedPreferences("token", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("before_token", token);
        editor.commit();
    }

    // 값 불러오기
    public String getBeforeToken(){
        SharedPreferences pref = getSharedPreferences("token", MODE_PRIVATE);
        return pref.getString("before_token", "");
    }

    private  void sendRegistrationToServer( String token){

    }
}
