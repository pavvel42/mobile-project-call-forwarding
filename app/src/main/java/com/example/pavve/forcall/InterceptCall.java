package com.example.pavve.forcall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class InterceptCall extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onReceive(Context context, Intent intent) {
       try{
           String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
           if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)) {
               String numer = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
               Toast.makeText(context,"Dzwoni "+numer,Toast.LENGTH_SHORT).show();
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
               }
           }
           if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
               String numer = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
               Toast.makeText(context,"Połączenie z "+numer,Toast.LENGTH_SHORT).show();
           }
           if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_IDLE)) {
               Toast.makeText(context,"IDLE",Toast.LENGTH_SHORT).show();
           }
       } catch (Exception  e){
           e.printStackTrace();
       }
    }
}
