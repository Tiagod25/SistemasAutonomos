package com.example.independence;

import android.util.Log;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimerTask;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.example.independence.MainActivity.inst_ult_init;
import static com.example.independence.MainActivity.personal_info;

public class TimeTask extends TimerTask {
    String androidID;

    public TimeTask(String id){
        androidID = id;
    }

    @Override
    public void run() {
        try {
            Log.i(TAG, "--------------- EXECUTAR AÇÃO SEND ---------------------- "+ new Date(System.currentTimeMillis()));// Debug
            SendInformation();
            Log.i(TAG, "--------------- FIM AÇÃO SEND ----------------- ");// Debug
        } catch (Exception e) {
        }
    }

    private void SendInformation(){

        //get Android ID
        String android_id =androidID;

        // Calculations needed
        long now = System.currentTimeMillis();

        if(inst_ult_init != -1 ) {
            long intervalo = now - inst_ult_init;
            double intervalo_minutos = (intervalo / 1000.0) / 60.0;
            personal_info.adicionaTempoEcra(intervalo_minutos);
            inst_ult_init = now;
        }

        //Get personal information for the register
        personal_info.setWifi(MainActivity.getStatusWifi());
        personal_info.setDadosMoveis(MainActivity.getStatusDados());
        Log.i(TAG, "*************** "+ personal_info.getTempo_ecra());// Debug


        // Send Register to Database
        MainActivity.sendToFirebase(personal_info,android_id);

        // Reset the user
        MainActivity.personal_info = new Registo();



    }

}
