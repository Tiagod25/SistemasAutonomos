package receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.independence.MainActivity;

public class AtivacaoEcraReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(Intent.ACTION_SCREEN_ON.equals(action)) {
            System.out.println(" -------> Ecra ON");
            MainActivity.inst_ult_init=System.currentTimeMillis();
            MainActivity.personal_info.incAtivEcra();
            //System.out.println(" -------- INFO ECRA ATIVO = " + MainActivity.personal_info.getnAtivacoesEcra());
        }
    }

}
