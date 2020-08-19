package com.example.pavve.forcall;

import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;

public class GroupView extends AppCompatActivity {

    ListView listWithData;

    HashMap<String, String> hashMapToGetData;
    ArrayList<HashMap<String,String>> arrayAdapterList;
    Button wybierzGrupe;
    ArrayList<String> grupaNumerowDoWyslania = new ArrayList<>();

    String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_view);
        initVariables();

        Intent intent = getIntent();
        int id = Integer.parseInt(intent.getStringExtra("IDGrupy"));
        getlistofcontacts(id);
        wybierzGrupe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.grupaNumerowBlokowanych = grupaNumerowDoWyslania;
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void getlistofcontacts( int groupId ) {
        String[] cProjection = { ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID };

        Cursor groupCursor = getContentResolver().query(ContactsContract.Data.CONTENT_URI, cProjection,
                ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID + "= ?" + " AND "
                        + ContactsContract.CommonDataKinds.GroupMembership.MIMETYPE + "='"
                        + ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE + "'", new String[] { String.valueOf(groupId) }, null);
        if (groupCursor != null && groupCursor.moveToFirst())
        {
            do
            {

                int nameCoumnIndex = groupCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

                final String name = groupCursor.getString(nameCoumnIndex);
                hashMapToGetData.put("nazwa",name);
                long contactId = groupCursor.getLong(groupCursor.getColumnIndex(ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID));

                Cursor numberCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER }, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);

                if (numberCursor.moveToFirst())
                {
                    int numberColumnIndex = numberCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    do
                    {
                        phoneNumber = numberCursor.getString(numberColumnIndex);
                        String[] test = phoneNumber.split(" ");
                        phoneNumber = String.join("",test);
                        arrayAdapterList.add(new HashMap<String, String>(){{ put("nazwa", name); put("numer", phoneNumber); }  } );
                        grupaNumerowDoWyslania.add(phoneNumber);
                    } while (numberCursor.moveToNext());
                    numberCursor.close();
                }
                else
                {
                    Toast.makeText(this,"Brak kontaktów w tej grupie",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            } while (groupCursor.moveToNext());
            groupCursor.close();
        }
        else
        {
            Toast.makeText(this,"Brak kontaktów w tej grupie!",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        setListWithData();
    }

    public void setListWithData(){
        SimpleAdapter simpleAdapter = new SimpleAdapter(this,arrayAdapterList,android.R.layout.simple_list_item_2,
                new String [] {"nazwa", "numer"}, new int [] {android.R.id.text1, android.R.id.text2});

        listWithData.setAdapter(simpleAdapter);
    }

    public void initVariables(){
        listWithData = findViewById(R.id.listMapGroupView);
        hashMapToGetData = new HashMap<String, String>();
        arrayAdapterList = new ArrayList<HashMap<String,String>>();
        wybierzGrupe = findViewById(R.id.chooseGroup);
    }
}
