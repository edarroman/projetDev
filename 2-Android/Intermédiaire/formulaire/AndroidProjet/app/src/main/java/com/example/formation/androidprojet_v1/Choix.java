package com.example.formation.androidprojet_v1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
        final ArrayList lst_mag = intent_choix.getStringArrayListExtra("Liste_mag");
        final ArrayList lst_type = intent_choix.getStringArrayListExtra("Liste_type");

        final String magasin = intent_choix.getStringExtra("mag_dep");

        Button depart = (Button) findViewById(R.id.depart);
        depart.setText(magasin);

        final String magasin_arrivee = intent_choix.getStringExtra("mag_ar");
        Log.d("", " " + magasin_arrivee);

        Button arrivee = (Button) findViewById(R.id.arrivee);

        arrivee.setText(magasin_arrivee);


            Log.d("!!",""+magasin_arrivee.isEmpty());


        Button ok = (Button) findViewById(R.id.ok);
        ok.setText("OK");

        depart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_type = new Intent(Choix.this, Listetype.class);
                final String choix = "0";
                intent_type.putExtra("Liste_mag", lst_mag);
                intent_type.putExtra("Liste_type", lst_type);
                intent_type.putExtra("mag_ar", magasin_arrivee);
                intent_type.putExtra("choix", choix);

                startActivity(intent_type);
                finish();
            }
        });



        arrivee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_type = new Intent(Choix.this, Listetype.class);
                final String choix="1";
                intent_type.putExtra("Liste_mag", lst_mag);
                intent_type.putExtra("Liste_type", lst_type);
                intent_type.putExtra("mag_dep", magasin);
                intent_type.putExtra("choix", choix);
                startActivity(intent_type);
                finish();
            }
        });

        ok.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (magasin==null || magasin_arrivee == null){
                    Log.d("coucou","");
                }
                if (!magasin.isEmpty()&& !magasin_arrivee.isEmpty() ){
                    Intent intent_req = new Intent(Choix.this, MainActivity.class);
                    intent_req.putExtra("Depart", magasin);
                    intent_req.putExtra("Arrivee", magasin_arrivee);
                    Log.d("dep", ""+intent_req.getStringExtra("Depart"));
                    Log.d("r",""+RESULT_OK);
                    setResult(0, intent_req);
                    finish();

                }
            }

        });

    }

}
