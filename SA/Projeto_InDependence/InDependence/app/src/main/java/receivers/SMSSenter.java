package receivers;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import com.example.independence.MainActivity;

public class SMSSenter extends ContentObserver {
    ContentResolver cnt;
    public SMSSenter(Handler handler,ContentResolver content) {
        super(handler);
        cnt=content;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Uri uriSMSURI = Uri.parse("content://sms/sent");
        Cursor cur = cnt.query(uriSMSURI, null, null, null, null);
        cur.moveToNext();
        String content = cur.getString(cur.getColumnIndex("body"));
        String smsNumber = cur.getString(cur.getColumnIndex("address"));
        cur.close();
        MainActivity.personal_info.incNSMSEnv();
        System.out.println("-------- MSG ENVIADA");
    }
}