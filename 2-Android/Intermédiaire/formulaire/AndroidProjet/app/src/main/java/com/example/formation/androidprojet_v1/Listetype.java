package com.example.formation.androidprojet_v1;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.widget.Toast;


public class Listetype extends Activity {

    ListView mListView;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listetype);

        List lst_types = new Vector();
        Intent intent_type = getIntent();
        final ArrayList lstmag = intent_type.getStringArrayListExtra("Liste_mag");
        final ArrayList lsttype = intent_type.getStringArrayListExtra("Liste_type");

        for (int s=0; s<lsttype.size(); s++) {
            Object t = lsttype.get(s);
            if (!lst_types.contains(t)) {
                lst_types.add(t);
            }
        }
        final List liste_type = lst_types;

        mListView = (ListView) findViewById(R.id.listetype);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Listetype.this,
                android.R.layout.simple_list_item_1, liste_type);
        mListView.setAdapter(adapter);


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Object type = liste_type.get(position);
                Intent intent = new Intent(Listetype.this, Listemagasin.class);
                intent.putExtra("type", type.toString());
                intent.putExtra("Liste_mag", lstmag);
                intent.putExtra("Liste_type", lsttype);
                startActivityForResult(intent, 1);
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent intent_req) {
        if (String.valueOf(requestCode)==String.valueOf(1)) {
            if (String.valueOf(resultCode)==String.valueOf(RESULT_OK)) {
                Intent intent = new Intent(Listetype.this, Choix.class);
                intent.putExtra("mag", intent_req.getStringExtra("mag"));
                setResult(RESULT_OK, intent);
                finish();
            }
        }

    }

}
