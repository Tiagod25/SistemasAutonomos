package receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.independence.MainActivity;

import services.NotificationLService;

public class NotificationReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getStringExtra("type").equals("Chat")){
            MainActivity.personal_info.incNotifChatting();
            System.out.println("------- INFO: Notificação Chatting Recebida : " + MainActivity.personal_info.getnNotifChatting());
            /*
            //Intent i1 = new  Intent("com.example.independence.NOTIFICATION_LISTENER");
            //i1.putExtra("notification_event","=====================");
            //sendBroadcast(i1);
            int i=1;
            for (StatusBarNotification sbn : NLService.this.getActiveNotifications()) {
                Intent i2 = new  Intent("com.kpbird.nlsexample.NOTIFICATION_LISTENER");
                i2.putExtra("notification_event",i +" " + sbn.getPackageName() + "n");
                //sendBroadcast(i2);
                i++;
            }
            Intent i3 = new  Intent("com.kpbird.nlsexample.NOTIFICATION_LISTENER_EXAMPLE");
            i3.putExtra("notification_event","===== Notification List ====");
            sendBroadcast(i3);
            */
        }
        else{
            if (intent.getStringExtra("type").equals("Social")){
                MainActivity.personal_info.incNotifSocias();
                System.out.println("------- INFO: Notificação Social Recebida : " + MainActivity.personal_info.getnNotifSociais());
            }
            else{
                MainActivity.personal_info.incNotifOutrasApps();
                System.out.println("------- INFO: Notificação Outras Recebida : " + MainActivity.personal_info.getnNotifOutrasApps());
            }
        }

        MainActivity.personal_info.incNNotif();

    }
}
