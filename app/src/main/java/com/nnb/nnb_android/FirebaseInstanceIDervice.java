package com.nnb.nnb_android;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

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
            postData = "token=" + URLEncoder.encode(getToken(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        byte[] byteArray = postData.getBytes();

        setToken(token);
    }

    public void setToken(String token) {
        SharedPreferences pref = getSharedPreferences("token", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("token", token);
        editor.commit();
    }

    public String getToken() {
        SharedPreferences pref = getSharedPreferences("token", MODE_PRIVATE);
        return pref.getString("token", "");
    }
}
