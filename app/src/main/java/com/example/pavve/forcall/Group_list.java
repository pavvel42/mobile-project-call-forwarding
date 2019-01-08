package com.example.pavve.forcall;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;


public class Group_list extends AppCompatActivity {

    ListView listWithData;
    ArrayList<HashMap<String,String>> arrayAdapterList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        initVariables();
        grupyPokaz();
    }

    public void grupyPokaz() //pokazuje ID grupy oraz jej nazwe
    {
        Cursor groups_cursor= getContentResolver().query(
                ContactsContract.Groups.CONTENT_URI,
                new String[]{
                        ContactsContract.Groups._ID,
                        ContactsContract.Groups.TITLE
                }, null, null, null
        );

        while (groups_cursor.moveToNext())
        {
            final String nazwaGrupy = groups_cursor.getString(1);
            final String ID = groups_cursor.getString(0);
            arrayAdapterList.add(new HashMap<String, String>(){{ put("nazwa", nazwaGrupy); put("numer", ID); }  } );
        }
        groups_cursor.close();
        setListWithData();
    }

    public void setListWithData(){
        SimpleAdapter simpleAdapter = new SimpleAdapter(this,arrayAdapterList,android.R.layout.simple_list_item_1,
                new String [] {"nazwa", "numer"}, new int [] {android.R.id.text1, android.R.id.text2});

        listWithData.setAdapter(simpleAdapter);
        listWithData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),GroupView.class);
                MainActivity.wybranaGrupa = arrayAdapterList.get(position).get("nazwa");
                intent.putExtra("NazwaGrupy",arrayAdapterList.get(position).get("nazwa"));
                intent.putExtra("IDGrupy",arrayAdapterList.get(position).get("numer"));
                startActivity(intent);
            }
        });
    }

    public void initVariables(){
        listWithData = findViewById(R.id.listMapGroup);
        arrayAdapterList = new ArrayList<HashMap<String,String>>();
    }
}
