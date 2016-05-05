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
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choix_magasin);

        Intent intent_choix = getIntent();
        final ArrayList lst_mag = intent_choix.getStringArrayListExtra("Liste_mag");
        final ArrayList lst_type = intent_choix.getStringArrayListExtra("Liste_type");

        final String magasin = intent_choix.getStringExtra("mag_dep");

        depart = (Button) findViewById(R.id.depart);

        final String magasin_arrivee = intent_choix.getStringExtra("mag_ar");

        arrivee = (Button) findViewById(R.id.arrivee);

        Log.d("mag",""+mag_dep+" "+mag_ar);

        Button ok = (Button) findViewById(R.id.ok);
        ok.setText("OK");

        depart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_type = new Intent(Choix.this, Listetype.class);
                intent_type.putExtra("Liste_mag", lst_mag);
                intent_type.putExtra("Liste_type", lst_type);
                startActivityForResult(intent_type, 3);

            }
        });



        arrivee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_type = new Intent(Choix.this, Listetype.class);
                intent_type.putExtra("Liste_mag", lst_mag);
                intent_type.putExtra("Liste_type", lst_type);
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
            }
        }
        if (String.valueOf(requestCode)==String.valueOf(3)) {
            if (String.valueOf(resultCode) == String.valueOf(RESULT_OK)) {
                depart.setText(intent_req.getStringExtra("mag"));
                mag_dep = intent_req.getStringExtra("mag");
            }
        }

    }

}
