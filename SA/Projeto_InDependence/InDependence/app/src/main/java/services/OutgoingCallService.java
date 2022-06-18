package services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import androidx.annotation.Nullable;

import receivers.OutgoingCallReceiver;

public class OutgoingCallService extends Service {
    private OutgoingCallReceiver makingCall=null;


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

        intentFilter.addAction("Intent.ACTION_NEW_OUTGOING_CALL");

        // Set broadcast receiver priority.
        intentFilter.setPriority(100);

        makingCall = new OutgoingCallReceiver();

        registerReceiver(makingCall,intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (makingCall!=null) unregisterReceiver(makingCall);
    }

}
