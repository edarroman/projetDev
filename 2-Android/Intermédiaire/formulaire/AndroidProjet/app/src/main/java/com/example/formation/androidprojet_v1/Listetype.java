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

    ListView mListView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listetype);

        List lst_types = new Vector();
        Intent intent_type = getIntent();
        final ArrayList lst_mag0 = intent_type.getStringArrayListExtra("Liste_mag0");
        final ArrayList lst_mag1 = intent_type.getStringArrayListExtra("Liste_mag1");
        final ArrayList lst_mag2 = intent_type.getStringArrayListExtra("Liste_mag2");
        final ArrayList lst_type0 = intent_type.getStringArrayListExtra("Liste_type0");
        final ArrayList lst_type1 = intent_type.getStringArrayListExtra("Liste_type1");
        final ArrayList lst_type2 = intent_type.getStringArrayListExtra("Liste_type2");

        for (int s=0; s<lst_type0.size(); s++) {
            Object t = lst_type0.get(s);
            if (!lst_types.contains(t)) {
                lst_types.add(t);
            }
        }

        //for (int p=0; p<lst_type1.size(); p++) {
        //    Object l = lst_type1.get(p);
        //    if (!lst_types.contains(l)) {
        //        lst_types.add(l);
        //    }
        //}
        for (int o=0; o<lst_type2.size(); o++) {
            Object q = lst_type2.get(o);
            if (!lst_types.contains(q)) {
                lst_types.add(q);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent intent_req) {
        if (String.valueOf(requestCode)==String.valueOf(1)) {
            if (String.valueOf(resultCode)==String.valueOf(RESULT_OK)) {
                Intent intent = new Intent(Listetype.this, Choix.class);
                intent.putExtra("mag", intent_req.getStringExtra("mag"));
                intent.putExtra("niveau", intent_req.getStringExtra("niveau"));
                setResult(RESULT_OK, intent);
                finish();
            }
        }

    }

}
