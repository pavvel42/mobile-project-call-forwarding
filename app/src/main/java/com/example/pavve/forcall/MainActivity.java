package com.example.pavve.forcall;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    public static TextView wykonaniePolaczenia, wybranieGrupy;
    Button contact_list, group_list;
    public static Switch callBlocker,forwarding;
    public String gdy_zajety, ODgdy_zajety;
    public static String phoneNumber, daneKontaktowe, wybranaGrupa, finalSimOperatorName;

    public static ArrayList<String> grupaNumerowBlokowanych = new ArrayList<>();

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SWITCHF = "switchF";

    private boolean switchOnOffF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //checkPerm();
        initVariable();
        if(phoneNumber != null)
        {
            wykonaniePolaczenia.setText("Wybrano: "+daneKontaktowe+" Nr. Tel: "+phoneNumber);
        }
        if(wybranaGrupa != null)
        {
            wybranieGrupy.setText("Grupa: "+wybranaGrupa);
        }
        loadData();
        updateViews();
        acceptedNumer();

        contact_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)
                {
                    if(forwarding.isChecked() == false)
                    {
                        Intent intent = new Intent(getApplicationContext(), Contact_list.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Toasty("Wyłącz przekierowanie, aby wybrac nowy numer");
                    }
                }
                else
                {
                    checkPerm();
                }
            }
        });

        group_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)
                {
                    Intent intent = new Intent(getApplicationContext(), Group_list.class);
                    startActivity(intent);
                }
                else
                {
                    checkPerm();
                }
            }
        });

        forwarding.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(finalSimOperatorName != null)
                {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
                            && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
                    {
                        if(forwarding.isChecked())
                        {
                            if(phoneNumber == null || gdy_zajety == null)
                            {
                                Toasty("Wybierz numer telefonu na który mają być przekierowane połączenia przychodzące");
                                forwarding.setChecked(false);
                                saveData();
                            }
                            else
                            {
                                Uri call = Uri.parse("tel: " + gdy_zajety + phoneNumber + "%23");
                                saveData();
                                Intent dialIntent = new Intent(Intent.ACTION_CALL, call);
                                startActivity(dialIntent);
                            }
                        }
                        else
                        {
                            if (forwarding.isChecked() == false) {
                                Uri call = Uri.parse("tel: " + ODgdy_zajety);
                                saveData();
                                Intent dialIntent = new Intent(Intent.ACTION_CALL, call);
                                startActivity(dialIntent);
                            } else {
                                Toasty("Nie włączyłeś przekazywania połączeń");
                                forwarding.setChecked(false);
                                saveData();
                            }
                        }
                    }
                    else
                    {
                        forwarding.setChecked(false);
                        saveData();
                        checkPerm();
                    }
                }
                else
                {
                    forwarding.setChecked(false);
                    saveData();
                    Toasty("Brak twojego operatora w bazie danych!");
                }
            }
        });

        callBlocker.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ANSWER_PHONE_CALLS) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED)
                {
                    if(callBlocker.isChecked())
                    {
                        if(grupaNumerowBlokowanych.isEmpty() == false)
                        {
                            saveData();
                        }
                        else
                        {
                            callBlocker.setChecked(false);
                            Toasty("Wybierz grupę");
                            saveData();
                        }
                    }
                }
                else {
                    callBlocker.setChecked(false);
                    saveData();
                    checkPerm();
                }
            }
        });
    }

    public void startService(View v){
        String input = "Forwarding Call ON";

        Intent serviceIntent = new Intent(this,Service.class);
        serviceIntent.putExtra("inputExtra",input);

        ContextCompat.startForegroundService(this,serviceIntent);
    }

    public void stopService(View v){
        Intent serviceIntent = new Intent(this,Service.class);
        stopService(serviceIntent);
    }

    public void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SWITCHF, forwarding.isChecked());
        editor.apply();
    }

    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        switchOnOffF = sharedPreferences.getBoolean(SWITCHF,false);
    }

    public void updateViews(){
        forwarding.setChecked(switchOnOffF);
    }

    public void acceptedNumer(){
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String simOperatorName = telephonyManager.getNetworkOperatorName();

        String[] parts = simOperatorName.split(" ");
        simOperatorName = parts[0];
        finalSimOperatorName = simOperatorName;

        switch(simOperatorName) {
            case "PLAY":
                gdy_zajety = "*67*";
                ODgdy_zajety = "%2367%23";
                break;
            case "Orange":
                gdy_zajety = "**67*";
                ODgdy_zajety = "%2367%23";
                break;
            case "PLUS":
                gdy_zajety = "*67*";
                ODgdy_zajety = "%2367%23";
                break;
            case "T-Mobile.pl":
                gdy_zajety = "*67*";
                ODgdy_zajety = "%2367%23";
                break;
            case "nju":
                gdy_zajety = "**67*";
                ODgdy_zajety = "%2367%23";
                break;
            case "Android": //emulator
                gdy_zajety = "";
                ODgdy_zajety = "";
                break;
            default: //Operator nieznany
                finalSimOperatorName = null;
        }
    }

    public void Toasty(String komunikat){
        Toast.makeText(this, komunikat, Toast.LENGTH_LONG).show();
    }

    public void initVariable(){
        contact_list = findViewById(R.id.contacts);
        group_list = findViewById(R.id.groups);
        wykonaniePolaczenia = findViewById(R.id.wykonaniePolaczenia);
        callBlocker = findViewById(R.id.blockCall);
        forwarding = findViewById(R.id.forwardingOnOff);
        wybranieGrupy = findViewById(R.id.jakaGrupa);
    }

    @AfterPermissionGranted(123)
    private void checkPerm()
    {
        String[] perms = {Manifest.permission.READ_CONTACTS,Manifest.permission.READ_PHONE_STATE,Manifest.permission.CALL_PHONE,
                Manifest.permission.ANSWER_PHONE_CALLS,Manifest.permission.READ_CALL_LOG,/*Manifest.permission.FOREGROUND_SERVICE*/};
        if(EasyPermissions.hasPermissions(this,perms)){
            Toast.makeText(this, "Pomyślne przyznano uprawnienia. Wybierz działanie.", Toast.LENGTH_SHORT).show();
        }
        else {
            EasyPermissions.requestPermissions(this,"Potrzebujemy uprawnień do działania aplikacji",123,perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE){

        }
    }
}
