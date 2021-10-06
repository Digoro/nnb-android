package com.nnb.nnb_android;

import android.content.SharedPreferences;
import android.util.Log;
import android.webkit.WebView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class FirebaseInstanceIDervice extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIDService";
    WebView webview2 = null;

    @Override
    public void onTokenRefresh() {

        webview2 = new WebView(getBaseContext());

        String token = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, token);

        String url = "https://nonunbub.com/api/fcm/token";
        String postData = "";
        try {
            postData = "frm_token=" + URLEncoder.encode(getToken(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        byte[] byteArray=postData.getBytes();

        webview2.postUrl(url, byteArray);

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

    private  void sendRegistrationToServer( String token){

    }
}
