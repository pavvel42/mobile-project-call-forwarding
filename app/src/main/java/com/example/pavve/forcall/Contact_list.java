package com.example.pavve.forcall;

import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;


public class Contact_list extends AppCompatActivity {

    ListView listWithData;
    ArrayList<HashMap<String,String>> arrayAdapterList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        initVariables();
        kontakty();
    }

    public void kontakty(){
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);

        while (phones.moveToNext())
        {
            final String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            final String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER));
            arrayAdapterList.add(new HashMap<String, String>(){{ put("nazwa", name); put("numer", phoneNumber); }  } );
        }
        phones.close();
        setListWithData();
    }

    public void setListWithData(){
        SimpleAdapter simpleAdapter = new SimpleAdapter(this,arrayAdapterList,android.R.layout.simple_list_item_2,
                new String [] {"nazwa", "numer"}, new int [] {android.R.id.text1, android.R.id.text2});

        listWithData.setAdapter(simpleAdapter);
        listWithData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(Contact_list.this, "Pozycja: " + position + " Nr: " + arrayAdapterList.get(position).get("numer"), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                MainActivity.phoneNumber = arrayAdapterList.get(position).get("numer");
                MainActivity.daneKontaktowe = arrayAdapterList.get(position).get("nazwa");
                startActivity(intent);
            }
        });
    }

    public void initVariables(){
        listWithData = findViewById(R.id.listMap);
        arrayAdapterList = new ArrayList<HashMap<String,String>>();
    }
}
