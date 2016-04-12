package com.example.formation.androidprojet_v1;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.esri.core.geodatabase.Geodatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import android.widget.Toast;

/**
 * Created by formation on 06/04/2016.
 */
public class Listetype extends Activity {

    ListView mListView;
    final String extern = "/mnt/ext_card";
    String networkPath = "/ProjetArcades/ArcGIS/Routing/base_de_donnees.geodatabase";
    List lst_types = new Vector();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listetype);

        Intent i = getIntent();
        ArrayList lstmag = i.getIntegerArrayListExtra();

        for (int s=0; s<lstmag.size(); s++) {
            Object mag = lstmag.get(s);
            Object type = mag;
            if (!lst_types.contains(type)) {
                lst_types.add(type);
            }
        }

        mListView = (ListView) findViewById(R.id.listetype);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Listetype.this,
                android.R.layout.simple_list_item_1, lst_types);
        mListView.setAdapter(adapter);


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object type = lst_types.get(position);
                Intent intent = new Intent(Listetype.this, Listemagasin.class);
                intent.putExtra("type", type.toString());
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
                Toast.makeText(Listetype.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
