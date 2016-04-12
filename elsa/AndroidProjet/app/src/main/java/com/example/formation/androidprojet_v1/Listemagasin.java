package com.example.formation.androidprojet_v1;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64InputStream;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.esri.core.geodatabase.Geodatabase;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import android.util.Base64;
import android.widget.Toast;


/**
 * Created by formation on 06/04/2016.
 */
public class Listemagasin extends Activity {

    ListView mListView;

    final String extern = "/mnt/ext_card";
    String networkPath = "/ProjetArcades/ArcGIS/Routing/base_de_donnees.geodatabase";
    List lst_mag = new Vector();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listemagasin);
        Intent intent = getIntent();
        String Type = intent.getStringExtra("type");
        if (intent != null) {
            Log.d("type", "type:" + intent);
        }
        try {
            Geodatabase gdb = new Geodatabase(extern + networkPath);
            for(int i=0; i<=2; i++){
                long nbr_lignes = gdb.getGeodatabaseTables().get(i).getNumberOfFeatures();
                for(int l=1; l<=nbr_lignes; l++){
                    Map<String, Object> lignes = gdb.getGeodatabaseTables().get(i).getFeature(l).getAttributes();
                    Object type = lignes.get("TYPE");
                    Object nom_mag = lignes.get("NOM");
                    Log.d("cc", "alors: " + type + Type);
                    if (type.toString().equals(Type)){
                        Log.d("cc", "yes" );
                        lst_mag.add(nom_mag);}
                }
            }
            Log.d("Coucou", "Fini:" + lst_mag);
        } catch (Exception e) {
            popToast("Error while initializing :" + e.getMessage(), true);
            e.printStackTrace();
        }

        mListView = (ListView) findViewById(R.id.listmag);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Listemagasin.this,
                android.R.layout.simple_list_item_1, lst_mag);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object mag = lst_mag.get(position);
                Intent intent = new Intent(Listemagasin.this, MainActivity.class);
                intent.putExtra("mag", mag.toString());
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
