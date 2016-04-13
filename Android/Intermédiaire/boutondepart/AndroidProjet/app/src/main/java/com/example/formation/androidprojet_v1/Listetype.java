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
public class Listetype extends Activity {

    ListView mListView;
    List lst_types = new Vector();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listetype);

        Intent i = getIntent();
        ArrayList lstmag = i.getIntegerArrayListExtra("Liste_mag");
        final ArrayList<ArrayList<String>> lst_mag = new ArrayList<>();
        for (int s=0; s<lstmag.size(); s++) {
            Object t = lstmag.get(s);
            ArrayList type = (ArrayList) t;
            lst_mag.add(type);
            if (!lst_types.contains(type.get(0))) {
                lst_types.add(type.get(0));
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
                intent.putExtra("Liste_mag", lst_mag);
                startActivity(intent);
            }
        });
    }

}
