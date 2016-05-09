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
    Button arrivee;
    Button depart;
    String mag_dep;
    String mag_ar;
    String niv_ar;
    String niv_dep;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choix_magasin);

        Intent intent_choix = getIntent();
        final ArrayList lst_mag0 = intent_choix.getStringArrayListExtra("Liste_mag0");
        final ArrayList lst_mag1 = intent_choix.getStringArrayListExtra("Liste_mag1");
        final ArrayList lst_mag2 = intent_choix.getStringArrayListExtra("Liste_mag2");
        final ArrayList lst_type0 = intent_choix.getStringArrayListExtra("Liste_type0");
        final ArrayList lst_type1 = intent_choix.getStringArrayListExtra("Liste_type1");
        final ArrayList lst_type2 = intent_choix.getStringArrayListExtra("Liste_type2");

        Log.d("3",""+lst_type1);

        depart = (Button) findViewById(R.id.depart);


        arrivee = (Button) findViewById(R.id.arrivee);


        Button ok = (Button) findViewById(R.id.ok);
        ok.setText("OK");

        depart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_type = new Intent(Choix.this, Listetype.class);
                intent_type.putExtra("Liste_mag0", lst_mag0);
                intent_type.putExtra("Liste_mag1", lst_mag1);
                intent_type.putExtra("Liste_mag2", lst_mag2);
                intent_type.putExtra("Liste_type0", lst_type0);
                intent_type.putExtra("Liste_type1", lst_type1);
                intent_type.putExtra("Liste_type2", lst_type2);
                Log.d("!",""+lst_type1);
                startActivityForResult(intent_type, 3);

            }
        });



        arrivee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_type = new Intent(Choix.this, Listetype.class);
                intent_type.putExtra("Liste_mag0", lst_mag0);
                intent_type.putExtra("Liste_mag1", lst_mag1);
                intent_type.putExtra("Liste_mag2", lst_mag2);
                intent_type.putExtra("Liste_type0", lst_type0);
                intent_type.putExtra("Liste_type1", lst_type1);
                intent_type.putExtra("Liste_type2", lst_type2);
                startActivityForResult(intent_type, 2);
            }
        });

        ok.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (mag_dep!= null&& mag_ar!=null ){
                    Intent intent_req = new Intent(Choix.this, MainActivity.class);
                    intent_req.putExtra("Depart", mag_dep);
                    intent_req.putExtra("Arrivee", mag_ar);
                    intent_req.putExtra("Niv_ar", niv_ar);
                    intent_req.putExtra("Niv_dep", niv_dep);
                    setResult(RESULT_OK, intent_req);
                    finish();
                }
            }

        });

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent intent_req) {
        if (String.valueOf(requestCode)==String.valueOf(2)) {

            if (String.valueOf(resultCode) == String.valueOf(RESULT_OK)) {
                arrivee.setText(intent_req.getStringExtra("mag"));
                mag_ar = intent_req.getStringExtra("mag");
                niv_ar = intent_req.getStringExtra("niveau");
            }
        }
        if (String.valueOf(requestCode)==String.valueOf(3)) {
            if (String.valueOf(resultCode) == String.valueOf(RESULT_OK)) {
                depart.setText(intent_req.getStringExtra("mag"));
                mag_dep = intent_req.getStringExtra("mag");
                niv_dep = intent_req.getStringExtra("niveau");

            }
        }

    }

}
