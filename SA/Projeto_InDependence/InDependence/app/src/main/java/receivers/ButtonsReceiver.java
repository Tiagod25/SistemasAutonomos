package receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.independence.MainActivity;

public class ButtonsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final String SYSTEM_DIALOG_REASON_KEY = "reason";
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";

        String action = intent.getAction();
            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null) {
                    if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                        MainActivity.personal_info.incNClicksHome();
                        System.out.println("---- INFO BUTAO HOME = " + MainActivity.personal_info.getnClicksHome());
                    }
                    else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                        MainActivity.personal_info.incNClicksRecent();
                        System.out.println("---- INFO BUTAO RECENTES = " + MainActivity.personal_info.getnClicksRecentes());
                    }
                }
            }
    }
}
