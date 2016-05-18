package com.example.formation.androidprojet_v1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

/**
 * Vue du choix de l'itinéraire
 */
public class Choix extends Activity {
    // Initialisation des variables
    private TextView t_dep;
    private TextView t_arr;
    private Button type_arr;
    private Button type_dep;
    private Button qr;
    private Button ok;
    private String mag_dep;
    private String mag_ar;
    private String niv_ar;
    private String niv_dep;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choix_magasin);

        // import des données transmises par l'activité principale
        Intent intent_choix = getIntent();
        final ArrayList lst_mag0 = intent_choix.getStringArrayListExtra("Liste_mag0");
        final ArrayList lst_mag1 = intent_choix.getStringArrayListExtra("Liste_mag1");
        final ArrayList lst_mag2 = intent_choix.getStringArrayListExtra("Liste_mag2");
        final ArrayList lst_type0 = intent_choix.getStringArrayListExtra("Liste_type0");
        final ArrayList lst_type1 = intent_choix.getStringArrayListExtra("Liste_type1");
        final ArrayList lst_type2 = intent_choix.getStringArrayListExtra("Liste_type2");

        // Affichage du texte
        t_dep=(TextView)findViewById(R.id.mag_dep);
        t_dep.setText("Magasin de départ");
        t_arr=(TextView)findViewById(R.id.mag_arr);
        t_arr.setText("Magasin d'arrivée");
        // Boutons
        type_dep = (Button) findViewById(R.id.depart);
        type_dep.setText("Par type");
        qr = (Button) findViewById(R.id.qr);
        qr.setText("Par Qr Code");
        type_arr = (Button) findViewById(R.id.arrivee);
        type_arr.setText("Par type");
        ok = (Button) findViewById(R.id.ok);
        ok.setText("OK");


        // Fonctions des boutons
        type_dep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on lance la liste des types de magasins lors du clic
                Intent intent_type = new Intent(Choix.this, Listetype.class);
                intent_type.putExtra("Liste_mag0", lst_mag0);
                intent_type.putExtra("Liste_mag1", lst_mag1);
                intent_type.putExtra("Liste_mag2", lst_mag2);
                intent_type.putExtra("Liste_type0", lst_type0);
                intent_type.putExtra("Liste_type1", lst_type1);
                intent_type.putExtra("Liste_type2", lst_type2);
                startActivityForResult(intent_type, 3);

            }
        });
        qr.setOnClickListener(new View.OnClickListener() {
            //QR code
            @Override
            public void onClick(View v) {
                //QR code
                // on lance le scanner au clic sur notre bouton
                IntentIntegrator integrator = new IntentIntegrator(Choix.this);
                integrator.initiateScan();
            }
        });
        type_arr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on lance la liste des types de magasins lors du clic
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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Retour des différentes activités et utilisation de leurs données
    protected void onActivityResult(int requestCode, int resultCode, Intent intent_req) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent_req);
        if (scanningResult != null) {
            // Nous récupérons le contenu du code barre
            String scanContent = scanningResult.getContents();

            // Nous récupérons le format du code barre
            String scanFormat = scanningResult.getFormatName();

            // Test sur le different QR code scanné
            // On utilise ces tests pour définir le point de départ ou des points intermédiaires par exemple
            if(scanContent.equals( "QR code 01" ) )
            {
                t_dep.setText(intent_req.getStringExtra("La grande recre"));
                mag_dep = "La grande récré";
            }
            if(scanContent.equals( "QR code 02" ) ) {Log.d("QR_code","QR code 02");}
            if(scanContent.equals( "QR code 03" ) ) {Log.d("QR_code","QR code 03");}
        }
        else {
            if (String.valueOf(requestCode) == String.valueOf(2)) {
                if (String.valueOf(resultCode) == String.valueOf(RESULT_OK)) {
                    t_arr.setText(intent_req.getStringExtra("mag"));
                    mag_ar = intent_req.getStringExtra("mag");
                    niv_ar = intent_req.getStringExtra("niveau");
                }
            }
            if (String.valueOf(requestCode) == String.valueOf(3)) {
                if (String.valueOf(resultCode) == String.valueOf(RESULT_OK)) {
                    t_dep.setText(intent_req.getStringExtra("mag"));
                    mag_dep = intent_req.getStringExtra("mag");
                    niv_dep = intent_req.getStringExtra("niveau");

                }
            }
        }

    }

}
