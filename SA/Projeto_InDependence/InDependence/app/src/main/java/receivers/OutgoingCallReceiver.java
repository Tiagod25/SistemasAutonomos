package receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.independence.MainActivity;

public class OutgoingCallReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        System.out.println("Número Ligado é: " + number);
        MainActivity.personal_info.incNChamEfet();
        }
    }
