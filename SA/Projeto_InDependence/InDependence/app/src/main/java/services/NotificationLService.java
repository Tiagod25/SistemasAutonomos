package services;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.independence.MainActivity;


@SuppressLint("OverrideAbstract")
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationLService extends NotificationListenerService {

    private String TAG = this.getClass().getSimpleName();


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.i(TAG,"**********  onNotificationPosted");
        Log.i(TAG,"ID :" + sbn.getId() + "t" + sbn.getNotification().tickerText + "t" + sbn.getPackageName());
        String code = matchNotificationCode(sbn);
        atualizaNotificacao(code);
    }


    private void atualizaNotificacao(String code) {
        if (code.equals("Chat")) {
            MainActivity.personal_info.incNotifChatting();
            System.out.println("------- INFO: Notificação Chatting Recebida : " + MainActivity.personal_info.getnNotifChatting());
        } else {
            if (code.equals("Social")) {
                MainActivity.personal_info.incNotifSocias();
                System.out.println("------- INFO: Notificação Social Recebida : " + MainActivity.personal_info.getnNotifSociais());
            } else {
                MainActivity.personal_info.incNotifOutrasApps();
                System.out.println("------- INFO: Notificação Outras Recebida : " + MainActivity.personal_info.getnNotifOutrasApps());
            }
        }
    }

    private String matchNotificationCode(StatusBarNotification sbn) {
        String FACEBOOK_PACKAGE_NAME = "com.facebook.katana";
        String FACEBOOK_MESSENGER_PACKAGE_NAME = "com.facebook.orca";
        String WHATSAPP_PACKAGE_NAME = "com.whatsapp";
        String INSTAGRAM_PACKAGE_NAME = "com.instagram.android";
        String FACEBOOK_LITE_PACK_NAME = "com.facebook.lite";
        String MESSENGER_LITE_PACKAGE_NAME = "com.facebook.mlite";
        String TWITTER_LITE_PACKAGE_NAME = "com.twitter.android";


        String packageName = sbn.getPackageName();


        if(packageName.equals(FACEBOOK_PACKAGE_NAME)
                || packageName.equals(INSTAGRAM_PACKAGE_NAME) || packageName.equals(FACEBOOK_LITE_PACK_NAME)|| packageName.equals(TWITTER_LITE_PACKAGE_NAME) ){
            return "Social";
        }

        else if(packageName.equals(FACEBOOK_MESSENGER_PACKAGE_NAME)
                || packageName.equals(WHATSAPP_PACKAGE_NAME) || packageName.equals(MESSENGER_LITE_PACKAGE_NAME) ){
            return "Chat";
        }
        // if none of the above
        else{
            return "Outras";
        }
    }

}
