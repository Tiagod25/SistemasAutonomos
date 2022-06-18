package receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.independence.MainActivity;

public class SMSReceiver extends BroadcastReceiver {

    private static final String TAG = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            MainActivity.personal_info.incNSMSReceb();
            System.out.println("------- INFO: SMS RECEBIDA : " + MainActivity.personal_info.getnSMSRecebidas());
        }
    }
}
