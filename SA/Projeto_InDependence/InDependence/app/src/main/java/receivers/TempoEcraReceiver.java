package receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.independence.MainActivity;

public class TempoEcraReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(Intent.ACTION_SCREEN_OFF)) {
            System.out.println(" -------> Ecra OFF");
            long agora= System.currentTimeMillis();
            long ultima_vez = MainActivity.inst_ult_init;
            long tempo_passado = agora-ultima_vez;
            double intervalo_minutos = (tempo_passado / 1000.0) / 60.0;
            MainActivity.personal_info.adicionaTempoEcra(intervalo_minutos);
            //System.out.println(" -------- Ecra off INFO TEMPO PASSADO  = " + tempo_passado);
            //System.out.println(" -------- INFO TEMPO ECRA = " + MainActivity.personal_info.getTempo_ecra());
            MainActivity.inst_ult_init = -1;
        }
    }
}
