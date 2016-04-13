package com.example.formation.androidprojet_v1;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.widget.Toast;


/**
 * Created by formation on 06/04/2016.
 */
public class Listemagasin extends Activity {

    ListView maliste;
    List lst_magasin = new Vector();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listemagasin);


        Intent i = getIntent();
        ArrayList lstmag = i.getIntegerArrayListExtra("Liste_mag");
        String Type = i.getStringExtra("type");
        final ArrayList<ArrayList<String>> lst_mag = new ArrayList<>();
        for (int s=0; s<lstmag.size(); s++) {
            Object t = lstmag.get(s);
            ArrayList type = (ArrayList) t;
            if (type.get(0).toString().equals(Type)){
                lst_magasin.add(type.get(1));}
            }


    maliste = (ListView) findViewById(R.id.listmag);
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(Listemagasin.this,
            android.R.layout.simple_list_item_1, lst_magasin);
    maliste.setAdapter(adapter);


    maliste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Object magasin = lst_magasin.get(position);
            Intent intent = new Intent(Listemagasin.this, MainActivity.class);
            intent.putExtra("Magasin", magasin.toString());
            intent.putExtra("Liste_mag", lst_mag);
            startActivity(intent);
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
