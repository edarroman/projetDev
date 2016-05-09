package com.example.formation.androidprojet_v1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

/**
 * Created by formation on 13/04/2016.
 */
public class Choix extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choix_magasin);

        Intent intent_choix = getIntent();
        final ArrayList lst_mag = intent_choix.getIntegerArrayListExtra("Liste_magasin");

        Intent mag = getIntent();
        String magasin = mag.getStringExtra("mag");

        Button depart = (Button) findViewById(R.id.depart);
        depart.setText("Départ");
        if (mag != null) {
            depart.setText(magasin);
        }

        depart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_type = new Intent(Choix.this, Listetype.class);
                intent_type.putExtra("Liste_mag", lst_mag);
                startActivity(intent_type);
            }
        });



        Button arrivee = (Button) findViewById(R.id.arrivee);
        arrivee.setText("Arrivée");

    }

}
