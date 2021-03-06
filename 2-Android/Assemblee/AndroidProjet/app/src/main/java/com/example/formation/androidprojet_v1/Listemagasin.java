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


public class Listemagasin extends Activity {

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////// VARIABLES : ///////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private ListView maliste;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////// METHODES : ////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listemagasin);

        /////////////////////////////////// Import :  /////////////////////////////////////////////
        // Import des données transmises par l'activité Listetype
        List lst_magasin = new Vector();
        Intent intent_magasin = getIntent();
        final ArrayList lst_mag0 = intent_magasin.getStringArrayListExtra("Liste_mag0");
        final ArrayList lst_mag1 = intent_magasin.getStringArrayListExtra("Liste_mag1");
        final ArrayList lst_mag2 = intent_magasin.getStringArrayListExtra("Liste_mag2");
        final ArrayList lst_type0 = intent_magasin.getStringArrayListExtra("Liste_type0");
        final ArrayList lst_type1 = intent_magasin.getStringArrayListExtra("Liste_type1");
        final ArrayList lst_type2 = intent_magasin.getStringArrayListExtra("Liste_type2");
        String Type = intent_magasin.getStringExtra("type");

        for (int s=0; s<lst_type0.size(); s++) {
            if (lst_type0.get(s).equals(Type)){
                lst_magasin.add(lst_mag0.get(s));
            }
        }
        for (int o=0; o<lst_type1.size(); o++) {
            if (lst_type1.get(o).equals(Type)){
                lst_magasin.add(lst_mag1.get(o));
            }
        }
        for (int t=0; t<lst_type2.size(); t++) {
            if (lst_type2.get(t).equals(Type)){
                lst_magasin.add(lst_mag2.get(t));
            }
        }
        final List liste_magasin = lst_magasin;

        //////////////////////////////////// Affichage :  //////////////////////////////////////////
        // Affichage des magaisns ayant le type Type
        maliste = (ListView) findViewById(R.id.listmag);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Listemagasin.this,
                android.R.layout.simple_list_item_1, liste_magasin);
        maliste.setAdapter(adapter);

        //////////////////////////////////// Listeners :  //////////////////////////////////////////
        // Nous envoyons au click les informations suivantes à l'activité Listetype :
        // le magasin chosi
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
