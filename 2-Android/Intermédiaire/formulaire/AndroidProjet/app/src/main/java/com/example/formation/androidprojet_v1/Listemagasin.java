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



public class Listemagasin extends Activity {

    ListView maliste;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listemagasin);


        List lst_magasin = new Vector();
        Intent intent_magasin = getIntent();
        final ArrayList lstmag = intent_magasin.getStringArrayListExtra("Liste_mag");
        final ArrayList lsttype = intent_magasin.getStringArrayListExtra("Liste_type");
        String Type = intent_magasin.getStringExtra("type");

        for (int s=0; s<lsttype.size(); s++) {
            if (lsttype.get(s).equals(Type)){
                lst_magasin.add(lstmag.get(s));}
        }
        final List liste_magasin = lst_magasin;


    maliste = (ListView) findViewById(R.id.listmag);
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(Listemagasin.this,
            android.R.layout.simple_list_item_1, liste_magasin);
    maliste.setAdapter(adapter);


    maliste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Object magasin = liste_magasin.get(position);
            Intent intent = new Intent(Listemagasin.this, Listetype.class);
            intent.putExtra("mag", magasin.toString());
            setResult(RESULT_OK, intent);
            finish();
        }
    });
    }


}
