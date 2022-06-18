package com.example.independence;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import receivers.ButtonsReceiver;
import receivers.SMSSenter;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.DatabaseMetaData;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import services.AtivacaoEcraService;
import services.CallService;
import services.NotificationLService;
import services.SMSService;
import services.OutgoingCallService;
import services.TempoEcraService;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static ArrayList<Calendar> ultimos15=new ArrayList<Calendar>();
    protected static DatabaseReference reference = database.getReference("registos");
    protected static DatabaseReference ref = database.getReference();
    public static ArrayList<Integer> pontuacao =new ArrayList<Integer>();
    public static ArrayList<Registo> dados = new ArrayList<Registo>();
    public static double classificacao = 0;
    public static int num = 0;

    public static ConnectivityManager cm;
    public static Registo personal_info = new Registo();

    public static long inst_ult_init;

    private static String CHANNEL_ID = "independence";
    private static final String TAG = "MyActivity";


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ultimos15=last15days();
        inst_ult_init = System.currentTimeMillis();
        askForPermissions();
        startupConnectivityService();
        initSMSService();
        initCallService();
        initOutgoingCallService();
        NavigationView NV=findViewById((R.id.NV));
        NV.setNavigationItemSelectedListener(this);


        inst_ult_init = System.currentTimeMillis();

        initNotificationService(this);

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);
        if (ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_SMS") == PackageManager.PERMISSION_GRANTED) {
            initOutgoingSms();
        }
        initButtonsService();
        initAtivacaoEcraService();
        initTempoEcraService();
        if(pontuacao.size()<15 && dados.size()<15) {
            recdados();
        }
        calcClassificacao();
        // Create Timer to send information
        TimeTask task = new TimeTask(getAndroidId());
        long two_hours = 7200000; // 2h - 7200000ms
        long minute = 60000; // 1min - 60000ms
        Date cur = new Date(System.currentTimeMillis() + (minute/2)); // Start half minute after start of app

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(cur);
        calendar.add(Calendar.DAY_OF_MONTH,1);
        Calendar start_day = new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),00,00,00);
        Date start_date = start_day.getTime();

        //Log.i(TAG, "********* START AT ************ "+ cur);// Debug
        /*
        * Iniciar Timer para enviar os dados para a Firebase
        * Para "produção" comentar o DEV e descomentar o PROD
         */
        new Timer().scheduleAtFixedRate(task, cur, (minute)); //  DEV - Inicia 30s depois da app inicializar
        //new Timer().scheduleAtFixedRate(task, start_date, two_hours); // PROD -  Inicia no dia seguinte

        // Aviso de inicio de contagem no dia seguinte
        sendNotification("Aviso Importante " + "\u26A0", "A aplicação irá iniciar a sua atividade a partir das 23h59min!\nObrigado por usar In-Dependence!" + "\uD83D\uDCAA");
    }

    public void visualizeData(View view) {
        Intent intent = new Intent(this, ViewDataActivity.class);
        startActivity(intent);
    }

    public ArrayList last15days() {
        ArrayList<Calendar> list = new ArrayList<Calendar>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ");
        Calendar cal = Calendar.getInstance();
// get starting date
        cal.add(Calendar.DAY_OF_YEAR, -15);

// loop adding one day in each iteration
        for (int i = 0; i < 15; i++) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
            list.add((Calendar) cal.clone());
            //System.out.println(sdf.format(cal.getTime()));
        }
        return list;
    }

    public void recdados(){
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        //DatabaseReference ref = reference.child(getAndroidId()).child("2020").child(""+month).child(""+day);//funciona com child tempo e 1 ciclo
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dados.size() < 15 && pontuacao.size() < 15) {
                    int flag = 0;
                    DataSnapshot save = dataSnapshot;
                    ArrayList<Calendar> dias = last15days();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ");
                    for (Calendar dia : dias) {
                        Registo reg = new Registo();
                        System.out.println(sdf.format(dia.getTime()) + "   " + (dia.get(Calendar.MONTH) + 1) + "  " + (dia.get(Calendar.DAY_OF_MONTH)) + " " + dia.get(Calendar.YEAR));
                        dataSnapshot = save.child("registos").child(getAndroidId()).child("" + dia.get(Calendar.YEAR)).child("" + (dia.get(Calendar.MONTH) + 1)).child("" + dia.get(Calendar.DAY_OF_MONTH));

                        for (DataSnapshot snap1 : dataSnapshot.getChildren()) {
                            //   i = 0;
                            //for (DataSnapshot snap2 : snap1.getChildren()) {
                            if (snap1.exists()) {
                                Long k = snap1.child("nChamadasEfet").getValue(Long.class);
                                reg.setnChamadasEfet((snap1.child("nChamadasEfet").getValue(Long.class).intValue()) + reg.getnChamadasEfet());
                                reg.setnSMSEnviadas((snap1.child("nSMSEnviadas").getValue(Long.class).intValue()) + reg.getnSMSEnviadas());
                                reg.setnSMSRecebidas((snap1.child("nSMSRecebidas").getValue(Long.class).intValue()) + reg.getnSMSRecebidas());
                                reg.setnChamadasRecebidas((snap1.child("nChamadasRecebidas").getValue(Long.class).intValue()) + reg.getnChamadasRecebidas());
                                reg.setnClicksRecentes((snap1.child("nClicksRecentes").getValue(Long.class).intValue()) + reg.getnClicksRecentes());
                                reg.setnNotificaces((snap1.child("nNotificaces").getValue(Long.class).intValue()) + reg.getnNotificaces());
                                reg.setTempo_ecra((snap1.child("tempo_ecra").getValue(Long.class).intValue()) + reg.getTempo_ecra());
                                reg.setnNotifChatting((snap1.child("nNotifChatting").getValue(Long.class).intValue()) + reg.getnNotifChatting());
                                reg.setnNotifSociais((snap1.child("nNotifSociais").getValue(Long.class).intValue()) + reg.getnNotifSociais());
                                reg.setnAtivacoesEcra((snap1.child("nAtivacoesEcra").getValue(Long.class).intValue()) + reg.getnAtivacoesEcra());
                                reg.setnNotifOutrasApps((snap1.child("nNotifOutrasApps").getValue(Long.class).intValue()) + reg.getnNotifOutrasApps());
                                reg.setnClicksHome((snap1.child("nClicksHome").getValue(Long.class).intValue()) + reg.getnClicksHome());
                                reg.setTempo_ecra((snap1.child("tempo_ecra").getValue(Long.class).doubleValue()) + reg.getTempo_ecra());
                                flag = 1;
                            }
                        }
                        dados.add(reg);
                        System.out.println("" + reg.getnChamadasEfet() + " " + reg.getnSMSEnviadas());
                        DataSnapshot refPont = save.child("pontuacoes").child(getAndroidId()).child("" + dia.get(Calendar.YEAR)).child("" + (dia.get(Calendar.MONTH) + 1)).child("" + dia.get(Calendar.DAY_OF_MONTH));
                        if (refPont.exists()) {
                            pontuacao.add(refPont.getValue(Long.class).intValue());
                            System.out.println("pontuacao é " + refPont.getValue(Long.class).intValue());
                        } else {
                            pontuacao.add(0);
                            System.out.println("pontuacao é ZERO");
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }


    public void calcClassificacao() {

        //DatabaseReference ref = reference.child(getAndroidId()).child("2020").child(""+month).child(""+day);//funciona com child tempo e 1 ciclo
        DatabaseReference ref = database.getReference("classificacoes").child(getAndroidId());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int flag = 0;
                Date date = new Date();
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(date);
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                dataSnapshot = dataSnapshot.child("" + year).child("" + month).child("" + day);
                double firebaseClassicacao = 0;
                int firebaseNum = 0;
                for (DataSnapshot snap1 : dataSnapshot.getChildren()) {
                    //   i = 0;
                    //for (DataSnapshot snap2 : snap1.getChildren()) {
                    if (snap1.exists()) {
                        firebaseNum++;
                        Object value = snap1.child("classificacao").getValue();
                        firebaseClassicacao += (double) ((long) value);
                    }
                }
                num = firebaseNum;
                classificacao =  firebaseClassicacao;
                if (num == 12) sendDayClassif();
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    public void sendDayClassif() {
        try {
            //prepare to send
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            ;
            String time = (dateFormat.format(date));
            Log.i(TAG, "--------------- " + time);//2016/11/16 12:08:43
            database.getReference("pontuacoes").child(getAndroidId()).child("" + year).child("" + month).child("" + day).setValue(classificacao);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int title_int = R.string.string_title;
        String title = getString(title_int) + "\uD83D\uDE0E";
        sendNotification(title ,"A sua classifição do último dia foi " + classificacao +"!\nVeja mais detalhes." );
        classificacao = 0;
        num = 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void askForPermissions() {

        // The request code used in ActivityCompat.requestPermissions()
        // and returned in the Activity's onRequestPermissionsResult()
        int PERMISSION_ALL = 1;
        Boolean isAns = false;
        String[] PERMISSIONS = {
                android.Manifest.permission.PROCESS_OUTGOING_CALLS,
                android.Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.SEND_SMS,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET
        };

        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        while (!isAns) {
            List<String> permissionNeeded = new ArrayList<>();
            for (String permission : PERMISSIONS)
                if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED)
                    permissionNeeded.add(permission);
            if (permissionNeeded.size() == PERMISSIONS.length) {
                isAns = true;
            }
        }

    }

    private void startupConnectivityService() {

        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    }


    public void question_2(View view) {
        Intent intent = new Intent(this, ThanksActivity.class);
        startActivity(intent);
    }

    public String getAndroidId() {
        String deviceUniqueIdentifier = null;
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (null != tm) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                deviceUniqueIdentifier = tm.getDeviceId();
                return deviceUniqueIdentifier;
            }
        }
        if (null == deviceUniqueIdentifier || 0 == deviceUniqueIdentifier.length()) {
            deviceUniqueIdentifier = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceUniqueIdentifier;
    }



    /**
     * Retorna estado da ligação WiFi
     */
    public static int getStatusWifi() {

        if (cm.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {
            System.out.println(" ------  WIFI_STATUS: ON");
            return 1;
        } else {
            System.out.println("----  WIFI_STATUS: OFF");
            return 0;
        }
    }

    /**
     * Get the wifi status on snapshot time
     */
    public static int getStatusDados() {
        if (cm.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED) {
            System.out.println("----- DADOS_MOVEIS: ON");
            return 1;
        } else {
            System.out.println("----- DADOS_MOVEIS: OFF");
            return 0;
        }
    }


    public void initSMSService() {

        // check and ask for sms read permissions


        // Start the service to collect data
        Intent SMSIntent = new Intent(this, SMSService.class);
        startService(SMSIntent);
    }


    public void initCallService() {

        // Start the service to collect data
        Intent SMSIntent = new Intent(this, CallService.class);
        startService(SMSIntent);
    }

    public void initOutgoingCallService() {

        // Start the service to collect data
        Intent SMSIntent = new Intent(this, OutgoingCallService.class);
        startService(SMSIntent);
    }


    public void initOutgoingSms() {
        ContentResolver contentResolver = this.getApplicationContext().getContentResolver();
        SMSSenter myObserver = new SMSSenter(new Handler(), contentResolver);
        contentResolver.registerContentObserver(Uri.parse("content://sms/sent"), true, myObserver);
    }


    private void initButtonsService() {

        // register to keys receiver
        ButtonsReceiver buttonReceiver = new ButtonsReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(buttonReceiver, intentFilter);

    }


    private void initAtivacaoEcraService() {
        // Start the service to collect data
        Intent screenBackgroundIntent = new Intent(this, AtivacaoEcraService.class);
        startService(screenBackgroundIntent);
    }

    private void initTempoEcraService() {

        // Start the service to collect data
        Intent screenTimeIntent = new Intent(this, TempoEcraService.class);
        startService(screenTimeIntent);
    }

    private void initNotificationService(Activity activity) {
        String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
        if (android.os.Build.VERSION.SDK_INT >= 22) {
            ACTION_NOTIFICATION_LISTENER_SETTINGS = Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS;
        }
        Intent intent = new Intent();
        intent.setAction(ACTION_NOTIFICATION_LISTENER_SETTINGS);
        startActivityForResult(intent, 0);


        Intent notificationIntent = new Intent(this, NotificationLService.class);
        startService(notificationIntent);
    }

    public void sendNotification(String title, String content) {
        CharSequence textTitle = (CharSequence) title;
        CharSequence textContent = (CharSequence) content;
        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_icon_3)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(textContent).setBigContentTitle(textTitle))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());

    }

    public static int sendToFirebase(Registo r, String android_id) {
        try {
            //prepare to send
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            String seg;String hour;String min;
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int minutos = calendar.get(Calendar.MINUTE);
            int hora = calendar.get(Calendar.HOUR_OF_DAY);
            int segundos = calendar.get(Calendar.SECOND);
            if (segundos<10) { seg="0"+segundos;} else {seg=""+segundos;}
            if (minutos<10) { min="0"+minutos;} else {min=""+minutos;}
            if (hora<10) { hour="0"+hora;} else {hour=""+hora;}
            String time = (dateFormat.format(date));
            Log.i(TAG, "--------------- " + time);//2016/11/16 12:08:43
            MainActivity.reference.child(android_id).child("" + year).child("" + month).child("" + day).child(hour + ":" + min + ":" + seg).setValue(r);
            MainActivity.database.getReference("resultados_input").child(android_id).child("" + year).child("" + month).child("" + day).child("" + hora + ":" + minutos + ":" + segundos).setValue(r);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            //String description = getString(R.string.app_name);
            String description = "Dedicated_Channel_Independence";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        CharSequence a = item.getTitle();
        if (a.toString().equals("Ver Dados Estatísticos")) {
            System.out.println(item.getTitle());
            Intent intent = new Intent(this, ViewDataActivity.class);
            startActivity(intent);
        }
        return false;
    }

}
