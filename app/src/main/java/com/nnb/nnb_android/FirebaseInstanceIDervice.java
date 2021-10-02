package com.nnb.nnb_android;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseInstanceIDervice extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIDService";


    @Override
    public void onTokenRefresh() {

        String token = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, token);

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

    private  void sendRegistrationToServer( String token){

    }

}
