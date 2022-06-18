package services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import androidx.annotation.Nullable;

import receivers.AtivacaoEcraReceiver;
import receivers.TempoEcraReceiver;

public class TempoEcraService extends Service {

    private TempoEcraReceiver tempoEcraRec = null;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Create an IntentFilter instance.
        IntentFilter intentFilter = new IntentFilter();

        // Add network connectivity change action.
        intentFilter.addAction("android.intent.action.SCREEN_OFF");

        // Set broadcast receiver priority.
        intentFilter.setPriority(100);

        // Create a network change broadcast receiver.
        tempoEcraRec = new TempoEcraReceiver();

        // Register the broadcast receiver with the intent filter object.
        registerReceiver(tempoEcraRec, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Unregister screenOnOffReceiver when destroy.
        if(tempoEcraRec!=null)
        {
            unregisterReceiver(tempoEcraRec);
        }
    }


}
