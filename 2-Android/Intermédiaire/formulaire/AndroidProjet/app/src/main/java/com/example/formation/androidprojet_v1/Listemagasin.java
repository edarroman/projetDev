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
        final String choix = intent_magasin.getStringExtra("choix");
        final String mag = intent_magasin.getStringExtra("mag");
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
            Intent intent = new Intent(Listemagasin.this, Choix.class);
            if(choix.equals("0")) {
                intent.putExtra("mag_dep", magasin.toString());
                intent.putExtra("mag_ar", mag);
            }
            if(choix.equals("1")) {
                intent.putExtra("mag_ar", magasin.toString());
                intent.putExtra("mag_dep", mag);
            }
            intent.putExtra("Liste_mag", lstmag);
            intent.putExtra("Liste_type", lsttype);

            startActivity(intent);
            finish();
        }
    });
    }

    private void popToast(final String message, final boolean show) {
        // Simple helper method for showing toast on the main thread
        if (!show)
            return;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Listemagasin.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
