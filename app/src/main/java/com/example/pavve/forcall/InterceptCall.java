package com.example.pavve.forcall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.telecom.TelecomManager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class InterceptCall extends BroadcastReceiver {

    private final String TAG = InterceptCall.class.getSimpleName();

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onReceive(Context context, Intent intent) {
       try{
           String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
           if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)) {
               String numer = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
               //Toast.makeText(context,"Dzwoni "+numer,Toast.LENGTH_SHORT).show();
               Log.d(TAG, "Dzwoni numer: "+numer);
               if(numer.startsWith("+"))
               {
                   numer = numer.substring(3);
               }
               TelecomManager tm = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
               if(MainActivity.grupaNumerowBlokowanych.isEmpty() == false)
               {
                   for(int i=0;i<MainActivity.grupaNumerowBlokowanych.size();i++)
                   {
                       if(MainActivity.grupaNumerowBlokowanych.get(i).equals(numer))
                       {
                           if (tm != null && MainActivity.callBlocker.isChecked()) {
                               boolean success = tm.endCall();
                           }
                       }
                   }
               } else if(tm != null && MainActivity.getCallBlocker.isChecked()) {
                   boolean success = tm.endCall();
                   Log.d(TAG, "MainActivity.messageSMS "+MainActivity.messageSMS);
                   sendSMS(numer, MainActivity.messageSMS);
               }
           }
           if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
               String numer = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
               //Toast.makeText(context,"Połączenie z "+numer,Toast.LENGTH_SHORT).show();
               Log.d(TAG, "Połączenie z "+numer);
           }
           if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_IDLE)) {
               Log.d(TAG, "IDLE");
           }
       } catch (Exception  e){
           e.printStackTrace();
       }
    }

    public void sendSMS(String phoneNo,String message) {
        try {
            //String message = "Hello World!";
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> parts = smsManager.divideMessage(message);
            //smsManager.sendTextMessage(phoneNo, null, message, null, null);
            smsManager.sendMultipartTextMessage(phoneNo, null, parts, null, null);
            Log.d(TAG, "Wiadomość została wysłana na numer "+phoneNo);
        } catch (Exception ex) {
            Log.d(TAG, "nie wysłano wiadomości ("+phoneNo+")"+ex.toString());
            ex.printStackTrace();
        }
    }
}
