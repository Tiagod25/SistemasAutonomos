package receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.independence.MainActivity;

public class CallReceiver extends BroadcastReceiver {


    private static long inst_lastCall = 0;


    @Override
    public void onReceive(Context context, Intent intent) {

        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        // Se tiver passado menos 5 segundos, provavelmente é uma chamada repetida. Por isso, podemos não considerar
        if(System.currentTimeMillis() < inst_lastCall + 5000) return;


        if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
            // save last outgoing call time to avoid duplicates
            inst_lastCall = System.currentTimeMillis();
            // increase counter for outgoing calls
            MainActivity.personal_info.incNChamReceb();
            System.out.println(" /////// INFO CHAMADAS RECEBIDAS = " + MainActivity.personal_info.getnChamadasRecebidas());

        }



    }
}
