package com.nnb.nnb_android;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseInstanceIDervice extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIDService";


    @Override
    public void onTokenRefresh() {

        String token = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, token);

        sendRegistrationToServer(token);

    }

    private  void sendRegistrationToServer( String token){

    }

}
