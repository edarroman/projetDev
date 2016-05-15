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


public class Listetype extends Activity {

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////// VARIABLES : ///////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private ListView mListView;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////// METHODES : ////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listetype);

        //////////////////////////////////// Import :  /////////////////////////////////////////////
        // Import des données transmises par l'activité choix
        List lst_types = new Vector();
        Intent intent_type = getIntent();
        final ArrayList lst_mag0 = intent_type.getStringArrayListExtra("Liste_mag0");
        final ArrayList lst_mag1 = intent_type.getStringArrayListExtra("Liste_mag1");
        final ArrayList lst_mag2 = intent_type.getStringArrayListExtra("Liste_mag2");
        final ArrayList lst_type0 = intent_type.getStringArrayListExtra("Liste_type0");
        final ArrayList lst_type1 = intent_type.getStringArrayListExtra("Liste_type1");
        final ArrayList lst_type2 = intent_type.getStringArrayListExtra("Liste_type2");

        // Création du liste contenant tout les types :
        for (int l=0; l<lst_type0.size(); l++) {
            Object obj = lst_type0.get(l);
            if (!lst_types.contains(obj)) {
                lst_types.add(obj);
            }
        }

        for (int m=0; m<lst_type1.size(); m++) {
            Object obj = lst_type1.get(m);
            if (!lst_types.contains(obj)) {
                lst_types.add(obj);
            }
        }

        for (int k=0; k<lst_type2.size(); k++) {
            Object obj = lst_type2.get(k);
            if (!lst_types.contains(obj)) {
                lst_types.add(obj);
            }
        }

        final List liste_type = lst_types;

        //////////////////////////////////// Affichage :  //////////////////////////////////////////
        // Affichage des types
        mListView = (ListView) findViewById(R.id.listetype);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Listetype.this,
                android.R.layout.simple_list_item_1, liste_type);
        mListView.setAdapter(adapter);

        //////////////////////////////////// Listeners :  //////////////////////////////////////////
        // Nous envoyons au click les inforamtions suivantes à l'activité Listemagasin :
        // le type du magasin recherché ainsi que l'ensemble des types et des noms des magasins
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object type = liste_type.get(position);
                Intent intent = new Intent(Listetype.this, Listemagasin.class);
                intent.putExtra("type", type.toString());
                intent.putExtra("Liste_mag0", lst_mag0);
                intent.putExtra("Liste_mag1", lst_mag1);
                intent.putExtra("Liste_mag2", lst_mag2);
                intent.putExtra("Liste_type0", lst_type0);
                intent.putExtra("Liste_type1", lst_type1);
                intent.putExtra("Liste_type2", lst_type2);
                startActivityForResult(intent, 1);
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // Retour des différentes activités et utilisation de leurs données
    protected void onActivityResult(int requestCode, int resultCode, Intent intent_req) {
        // On renvoie le magasin choisi à l'activité choix :
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
