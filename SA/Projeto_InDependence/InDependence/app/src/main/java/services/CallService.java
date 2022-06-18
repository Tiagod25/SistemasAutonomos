package services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import androidx.annotation.Nullable;

import receivers.CallReceiver;
import receivers.SMSReceiver;

public class CallService extends Service {

    private CallReceiver callReceiver = null;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public void onCreate() {
        super.onCreate();

        // Create an IntentFilter instance.
        IntentFilter intentFilter = new IntentFilter();

        // Add network connectivity change action.
        intentFilter.addAction("android.intent.action.PHONE_STATE");

        // Set broadcast receiver priority.
        intentFilter.setPriority(100);

        // Create a network change broadcast receiver.
        callReceiver = new CallReceiver();

        // Register the broadcast receiver with the intent filter object.
        registerReceiver(callReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Unregister screenOnOffReceiver when destroy.
        if(callReceiver!=null)
        {
            unregisterReceiver(callReceiver);
        }
    }

}
