package com.blisscom.gourava.jaiho.infra;

/**
 * Created by gourava on 1/6/17.
 */

import com.blisscom.gourava.jaiho.R;
import com.blisscom.gourava.jaiho.model.PojoPreferredStrings;
import com.google.firebase.iid.FirebaseInstanceIdService;

import android.content.SharedPreferences;
import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;


public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    private SharedPreferences sharedPreferences;
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token:>>>>>>>>>>>>>>>>>>>>>>>>>> " + refreshedToken);
        sharedPreferences = getSharedPreferences(getString(R.string.shared_preference_name), MODE_PRIVATE);
        String savedFcmToken = sharedPreferences.getString(PojoPreferredStrings.SAVED_FCM_TOKEN.toString(), null);
        if(refreshedToken != null && !refreshedToken.equalsIgnoreCase(savedFcmToken)){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(PojoPreferredStrings.SAVED_FCM_TOKEN.toString(), refreshedToken);
            editor.commit();
            //todo make an async call
            sendRegistrationToServer(refreshedToken);
        }

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

    }

    public String getToken(){
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token:>>>>>>>>>>>>>>>>>>>>>>>>>> " + refreshedToken);
        return refreshedToken;
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }
}
