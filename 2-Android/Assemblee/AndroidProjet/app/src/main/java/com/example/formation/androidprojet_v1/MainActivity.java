package com.example.formation.androidprojet_v1;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.GraphicsLayer.RenderingMode;
import com.esri.android.map.MapView;
import com.esri.android.map.TiledLayer;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.core.geodatabase.Geodatabase;
import com.esri.core.geodatabase.GeodatabaseFeatureTable;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.LinearUnit;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.geometry.Unit;
import com.esri.core.map.Feature;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol.STYLE;
import com.esri.core.symbol.Symbol;
import com.esri.core.symbol.TextSymbol;
import com.esri.core.tasks.na.NAFeaturesAsFeature;
import com.esri.core.tasks.na.Route;
import com.esri.core.tasks.na.RouteParameters;
import com.esri.core.tasks.na.RouteResult;
import com.esri.core.tasks.na.RouteTask;
import com.esri.core.tasks.na.StopGraphic;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////// VARIABLES : ///////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Déclaration des variables globales :
     */

    //////////////////////////////////// ArcGIS Elements : /////////////////////////////////////////
    private MapView mMapView;

    private final String extern = Environment.getExternalStorageDirectory().getPath();

    // TODO : chemin qui change en fonction SD card ou non : trouver automatiquement

    /*
    // Sd card :
    private final String chTpk = "/ProjArcades/ArcGIS/";*/

    // Sans sd card :
    private final String chTpk = "/Android/data/com.example.formation.androidprojet_v1/ArcGIS/";


    //////////////////////////////////// Image de fond  : //////////////////////////////////////////
    private String tpkPath  = chTpk +"arcades.tpk";
    private String tpkPath0 = chTpk +"niveau_0.tpk";
    private String tpkPath1 = chTpk +"niveau_1.tpk";
    private String tpkPath2 = chTpk +"niveau_2.tpk";
    private String tpkPath3 = chTpk +"logo.tpk";

    private TiledLayer mTileLayer = new ArcGISLocalTiledLayer(extern + tpkPath);
    private TiledLayer mTileLayer0 = new ArcGISLocalTiledLayer(extern + tpkPath0);
    private TiledLayer mTileLayer1 = new ArcGISLocalTiledLayer(extern + tpkPath1);
    private TiledLayer mTileLayer2 = new ArcGISLocalTiledLayer(extern + tpkPath2);
    private TiledLayer mTileLayer3 = new ArcGISLocalTiledLayer(extern + tpkPath3);

    private GraphicsLayer mGraphicsLayer = new GraphicsLayer(RenderingMode.DYNAMIC);

    private RouteTask mRouteTask = null;
    private NAFeaturesAsFeature mStops = new NAFeaturesAsFeature();

    //////////////////////////////////// Symbole départ/arrivée : ///////////////////////////////////
    private Drawable marqueur;
    private Symbol symStop;

    //////////////////////////////////// Gestion du multi-étage : //////////////////////////////////
    private Spinner spinnerEtgSel;
    private boolean etgsSelected = false;
    private boolean etg0Selected = false;
    private boolean etg1Selected = false;
    private boolean etg2Selected = false;
    private boolean logoSelected = false;

    //////////////////////////////////// Gestion du QR code : //////////////////////////////////////
    private Geometry geom_QR_code = null;

    //////////////////////////////////// Récupération des arcs : ///////////////////////////////////
    // Géometries :
    private Geometry[] array_geom_niv0 = new Geometry[127];
    private Geometry[] array_geom_niv1_1 = new Geometry[380];
    private Geometry[] array_geom_niv1_2 = new Geometry[377];
    private Geometry[] array_geom_niv2_1 = new Geometry[380];
    private Geometry[] array_geom_niv2_2 = new Geometry[400];
    private Geometry[] array_geom_niv1 = new Geometry[2];
    private Geometry[] array_geom_niv2 = new Geometry[2];
    // Projection
    private Geometry projection_niv0 = null;
    private Geometry projection_niv1 = null;
    private Geometry projection_niv2 = null;
    // Geometrie union :
    private Geometry geometries_niveau0 = null;
    private Geometry geometries_niveau1 = null;
    private Geometry geometries_niveau2 = null;
    // Geometrie intersections :
    private Geometry geom = null;
    private Geometry geom_intersect_niv0 = null;
    private Geometry geom_intersect_niv1 = null;
    private Geometry geom_intersect_niv2 = null;

    //////////////////////////////////// Géométrie engine : ////////////////////////////////////////
    private GeometryEngine geomen = new GeometryEngine();

    //////////////////////////////////// Référence spatiale : //////////////////////////////////////
    private SpatialReference WKID_RGF93 = SpatialReference.create(102110);

    //////////////////////////////////// Récupération des magasins : ///////////////////////////////
    // Features :
    private Feature[] mag_niv0 = new Feature[12];
    private Feature[] mag_niv1 = new Feature[66];
    private Feature[] mag_niv2 = new Feature[64];
    // Geometries :
    private Geometry[] mag_niv0_geom = new Geometry[12];
    private Geometry[] mag_niv1_geom = new Geometry[66];
    private Geometry[] mag_niv2_geom = new Geometry[64];
    // Geometries projetees :
    private Geometry projection_mag_niv0 = null;
    private Geometry projection_mag_niv1 = null;
    private Geometry projection_mag_niv2 = null;
    // Geometries d'un magasin :
    private Geometry mag_niveau0 = null;
    private Geometry mag_niveau1 = null;
    private Geometry mag_niveau2 = null;
    // Liste nom  :
    private ArrayList lst_mag_niveau0 = new ArrayList();
    private ArrayList lst_mag_niveau1 = new ArrayList();
    private ArrayList lst_mag_niveau2 = new ArrayList();
    // Liste type  :
    private ArrayList lst_types_niveau0 = new ArrayList();
    private ArrayList lst_types_niveau1 = new ArrayList();
    private ArrayList lst_types_niveau2 = new ArrayList();
    // Test
    private Geometry pt_fnac = null;

    //////////////////////////////////// Gestion itinéraire  : /////////////////////////////////////
    private int routeHandle = -1;

    //////////////////////////////////// Variable de restrictions : ////////////////////////////////
    private CheckBox checkBoxRes = null;
    private boolean estRestreint = false;

    //////////////////////////////////// Saisie automatique  :  ////////////////////////////////////
    private List lst_nom_mag = new ArrayList();
    private AutoCompleteTextView textViewArr;
    private AutoCompleteTextView textViewDep;

    //////////////////////////////////// Plus proche vosisin  :  ///////////////////////////////////
    private int niveau_dep = 0;
    private int niveau_arr = 0;

    //////////////////////////////////// Points de départ et d'arrivée :  ///////////////////////////
    private Geometry depart;
    private Geometry arrive;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////// METHODES : ////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //////////////////////////////////// Element graphique :  //////////////////////////////////
        // Toolbar pour l'option itinéraire
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Spinner element pour changement etage
        spinnerEtgSel = (Spinner) findViewById(R.id.spinnerEtgSelc);
        // Spinner click listener pour changement etage
        spinnerEtgSel.setOnItemSelectedListener(new BoutonEtageListener());

        // Checkbox sur la restriction :
        checkBoxRes = (CheckBox)findViewById(R.id.handicap);
        checkBoxRes.setOnClickListener(new checkedListener());

        // QR code
        ImageView qrButton = (ImageView) findViewById(R.id.scan_button);
        qrButton.setOnClickListener(new BoutonQRcodeListener());

        // Saisie automatique
        // Liste magasin :
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, lst_nom_mag);
        // Bouton :
        textViewDep = (AutoCompleteTextView)
                findViewById(R.id.dep_magasin);
        String depTxt = getResources().getString(R.string.dep);
        textViewDep .setHint(depTxt);
        textViewDep .setAdapter(adapter);
        textViewDep .setThreshold(1); // on commence la recherche automatique dès la première lettre ecrite
        textViewDep .setOnItemClickListener(new BoutonSaisieAutomatiqueDepListener());

        textViewArr = (AutoCompleteTextView)
                findViewById(R.id.arr_magasin);
        String arrTxt = getResources().getString(R.string.arr);
        textViewArr .setHint(arrTxt);
        textViewArr .setAdapter(adapter);
        textViewArr .setThreshold(1); // on commence la recherche automatique dès la première lettre ecrite
        textViewArr .setOnItemClickListener(new BoutonSaisieAutomatiqueArrListener());

        // Image de fond :
        File tpk = new File(extern + tpkPath);
        Log.d("RoutingAndGeocoding", "Find tpk: " + tpk.exists());
        Log.d("RoutingAndGeocoding", "Initialized tpk: " + mTileLayer.isInitialized());

        //////////////////////////////////// Carte de fond :  //////////////////////////////////////
        // Retrieve the map and initial extent from XML layout
        mMapView = (MapView) findViewById(R.id.map);

        // Mise en place des fonds et visibilité = false :
        mMapView.addLayer(mTileLayer0);
        mTileLayer0.setVisible(false);

        mMapView.addLayer(mTileLayer1);
        mTileLayer1.setVisible(false);

        mMapView.addLayer(mTileLayer2);
        mTileLayer2.setVisible(false);

        mMapView.addLayer(mTileLayer3);
        mTileLayer3.setVisible(false);

        mMapView.addLayer(mTileLayer);
        mTileLayer.setVisible(false);

        // Ajout couche graphique :
        mMapView.addLayer(mGraphicsLayer);

        //////////////////////////////////// Symbole :  ////////////////////////////////////////////
        // Création symbole point départ/arrivée :
        marqueur = getResources().getDrawable(R.drawable.ic_action_marqueur);
        symStop = new PictureMarkerSymbol(marqueur);

        //////////////////////////////////// Base de données :  ////////////////////////////////////
        // Récupération des élémenst dans la bdd :
        accesBdd();

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Méthode de retour d'activité permettant de gèrer le formulaire et le QR_code
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        // Nous utilisons la classe IntentIntegrator et sa fonction parseActivityResult pour parser le résultat du scan
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        // Récupération de la référence spatiale de  la vue :
        SpatialReference mapRef = mMapView.getSpatialReference();

        //////////////////////////////////// QR code :  ////////////////////////////////////////////

        if (scanningResult != null) {
            // Nous récupérons le contenu du code barre
            String scanContent = scanningResult.getContents();

            // Nous récupérons le format du code barre
            String scanFormat = scanningResult.getFormatName();

            // Nous modifions les vues créées pour contenir les informations liées à ce QR code :
            TextView scan_format = (TextView) findViewById(R.id.scan_format);
            TextView scan_content = (TextView) findViewById(R.id.scan_content);

            // Nous affichons le résultat dans nos TextView
            scan_format.setText("FORMAT: " + scanFormat);
            scan_content.setText("CONTENT: " + scanContent);
            Log.d("Scan content", scanContent);

            // Test sur le different QR code scanné
            // On utilise ces tests pour définir le point de départ ou des points intermédiaires par exemple
            // TODO : Pas encore utilisé dans notre cas, on peut se servir de la fonction ajouterPoint()
            if(scanContent.equals( "QR code 01" ) )
            {
                Log.d("QR_code","QR code 01");
                // On marque la geometrie du QR code sur la carte
                // Rappel,on test avec le magasin "La grande recre"
                Geometry projection = geomen.project(geom_QR_code, WKID_RGF93, mapRef);
                mGraphicsLayer.addGraphic(new Graphic(projection, new SimpleMarkerSymbol(Color.RED, 10, STYLE.CROSS)));
                //mMapView.getCallout().hide();
            }
            if(scanContent.equals( "QR code 02" ) ) {Log.d("QR_code","QR code 02");}
            if(scanContent.equals( "QR code 03" ) ) {Log.d("QR_code","QR code 03");}

        //////////////////////////////////// Formulaire :  /////////////////////////////////////////

        } else{
            if (requestCode == 0) {
                if (resultCode == RESULT_OK){
                    // Récupération des noms du magasin de départ et d'arrivée
                    final String mag_dep = intent.getStringExtra("Depart");
                    final String mag_arr = intent.getStringExtra("Arrivee");

                    // On compte le nombre de points présents dans mStops :
                    int tStop = mStops.getFeatures().size();

                    // Si il y en a plus de deux on réinistialise les stops :
                    if( tStop >=2 ) {
                        mStops.clearFeatures();
                        clearAffich();
                    }

                    // On retrouve les points de départ et d'arrivée à l'aide de leurs noms dans la liste de magasin
                    Geometry ptDep = trouverPtSel(mag_dep, true);
                    depart = geomen.project(ptDep, WKID_RGF93, mapRef);
                    ajouterPoint(depart, symStop);

                    Geometry ptArr =trouverPtSel(mag_arr, false);
                    arrive = geomen.project(ptArr, WKID_RGF93, mapRef);
                    ajouterPoint(arrive, symStop);

                    // On récupére à nouveau le nombre de stop :
                    tStop = mStops.getFeatures().size();

                    // Si on a 2 stops on calcule et on affiche l'itinéraire
                    if( tStop >= 2) {
                        calculerIti(mapRef);
                        afficherPpv(mapRef);
                    }
                }
            }
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////// LISTENERS : ///////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Listener du bouton de la restriction.
     * */
    class checkedListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // On récupère la référence sptaiale :
            SpatialReference mapRef = mMapView.getSpatialReference();

            // Si la checkbox est selectionnés on met le booléen estRestraint à true, sinon à false
            if (((CheckBox) v).isChecked()) {estRestreint = true;}
            else {estRestreint = false;}

            // On clear ce qui est affiché et on recalcule l'itinéraire avec la restriction
            clearAffich();
            calculerIti(mapRef);
        }
    }


    /**
     * Listener du bouton de choix d'étage :
     */
    class BoutonEtageListener implements OnItemSelectedListener {

        /**
         * Définition des évenements :
         */
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // On selecting a spinner item
            String etageSelec = parent.getItemAtPosition(position).toString();

            // Showing selected spinner item
            Toast.makeText(parent.getContext(), etageSelec + " sélectionné", Toast.LENGTH_LONG).show();

            // On recupere les noms des etages qui sont stockés dans ressources.strings.values
            String[] nom_etage = getResources().getStringArray(R.array.etage_array);

            // Test suivant la selection de l'utilisateur:
            if (etageSelec.equals(nom_etage[0])) {
                etgsSelected = false;
                etg0Selected = false;
                etg1Selected = false;
                etg2Selected = false;
                logoSelected = true;
            }
            if (etageSelec.equals(nom_etage[1])) {
                etgsSelected = false;
                etg0Selected = true;
                etg1Selected = false;
                etg2Selected = false;
                logoSelected = false;
            }
            if (etageSelec.equals(nom_etage[2])) {
                etgsSelected = false;
                etg0Selected = false;
                etg1Selected = true;
                etg2Selected = false;
                logoSelected = false;
            }
            if (etageSelec.equals(nom_etage[3])) {
                etgsSelected = false;
                etg0Selected = false;
                etg1Selected = false;
                etg2Selected = true;
                logoSelected = false;
            }
            if (etageSelec.equals(nom_etage[4])) {
                etgsSelected = true;
                etg0Selected = false;
                etg1Selected = false;
                etg2Selected = false;
                logoSelected = false;
            }

            // On affiche l'étage sélectionné :
            mTileLayer.setVisible(etgsSelected);
            mTileLayer0.setVisible(etg0Selected);
            mTileLayer1.setVisible(etg1Selected);
            mTileLayer2.setVisible(etg2Selected);
            mTileLayer3.setVisible(logoSelected);

            ////////////////////////////////////////////////////////////////////////////////////////

            // Gestion de l'affichage de l'itinéraire au moment du changement d'étage :
            afficherIti();
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    class BoutonQRcodeListener implements View.OnClickListener {

        //QR code
        @Override
        public void onClick(View v) {
            //QR code
            if (v.getId() == R.id.scan_button) {
                // On lance le scanner au clic sur notre bouton
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.initiateScan();
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Listener bouton saise auto
     * Il y en a deux car aucun moyen de récupérere facilement l'id de l(AutoCompleteTextView sur laquelle on clique
     */

    //////////////////////////////////// Départ :  /////////////////////////////////////////////////
    class BoutonSaisieAutomatiqueDepListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view,int position, long id) {

            //////////////////////////////////// Initialisation :  /////////////////////////////////

            // Référence spatiale :
            SpatialReference mapRef = mMapView.getSpatialReference();

            // Booléen (vrai si un point est selectionné, faux sinon) :
            boolean trouve = false;

            // Définition de la géométrie :
            Geometry ptTest = null;

            // Nombre de point sélectionnés :
            int tStop = mStops.getFeatures().size();

            //////////////////////////////////// Initinéraire :  ///////////////////////////////////

            // Remise à zero des stops :
            // Si il y a plus de deus stops au départ
            // On réinistialise la vue et on remet en fonction du bouton sélectionné le départ
            // ou l'arrivée (on remet le départ si on modifie l'arrivée et inversement)
            if( tStop >=2 ) {
                mStops.clearFeatures();
                clearAffich();
                ajouterPoint(arrive, symStop);
            }

            // On selectionne le magasin dans la liste de saisie automatique
            String item = parent.getItemAtPosition(position).toString();
            Log.v("mag_selectionne",item);

            ptTest = trouverPtSel(item, true);
            if (ptTest !=null){
                trouve = true;
            }

            // Lorsque qu'on a trouvé un point
            // On gère le fait que ce soit le départ ou l'arrivée
            // Dans tous les cas on l'ajoute au stop et on l'affiche
            if(trouve){
                depart = geomen.project(ptTest, WKID_RGF93, mapRef);
                ajouterPoint(depart, symStop);
            }

            // On récupére à nouveau le nombre de stops
            tStop = mStops.getFeatures().size();

            // Si on a 2 stops on calcule et on affiche l'itinéraire
            if( tStop >= 2) {
                calculerIti(mapRef);
                afficherPpv(mapRef);
            }
        }
    }

    //////////////////////////////////// Arrivée :  ////////////////////////////////////////////////
    class BoutonSaisieAutomatiqueArrListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view,int position, long id) {

            //////////////////////////////////// Initialisation :  /////////////////////////////////

            // Référence spatiale :
            SpatialReference mapRef = mMapView.getSpatialReference();

            // Booléen (vrai si un point est selectionné, faux sinon) :
            boolean trouve = false;

            // Définition de la géométrie :
            Geometry ptTest = null;

            // Nombre de point sélectionnés :
            int tStop = mStops.getFeatures().size();

            //////////////////////////////////// Initinéraire :  ///////////////////////////////////

            // Remise à zero des stops :
            // Si il y a plus de deux stops au départ
            // On réinistialise la vue et on remet en fonction du bouton sélectionné le départ
            // ou l'arrivée (on remet le départ si on modifie l'arrivée et inversement)
            if( tStop >=2 ) {
                mStops.clearFeatures();
                clearAffich();
                ajouterPoint(depart, symStop);
            }

            // On selectionne le magasin dans la liste de saisie automatique
            String item = parent.getItemAtPosition(position).toString();
            Log.v("mag_selectionne",item);

            ptTest = trouverPtSel(item, false);
            if (ptTest !=null){
                trouve = true;
            }

            // Lorsque qu'on a trouvé un point
            // On gére le fait que ce soit le départ ou l'arrivée
            // Dans touts les cas on l'ajoute au stop et on l'affiche
            if(trouve){
                Log.d("if_arr", "OK");
                arrive = geomen.project(ptTest, WKID_RGF93, mapRef);
                ajouterPoint(arrive, symStop);
            }

            // On récupére à nouveau le nombre de stops
            tStop = mStops.getFeatures().size();

            Log.d("nStop","" + tStop );

            // Si on a 2 stops on calcule et on affiche l'itinéraire
            if( tStop >= 2) {
                calculerIti(mapRef);
                afficherPpv(mapRef);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////// FONCTIONS : ///////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Fonction qui récupère les données dans la base de données
     */
    public void accesBdd(){
        // Get the external directory

        String locatorPath = chTpk + "/Geocoding/MGRS.loc";
        String networkPath = chTpk + "/Routing/base_de_donnees.geodatabase";

        String networkName = "GRAPH_Final_ND";

        // Attempt to load the local geocoding and routing data
        try {
            mRouteTask = RouteTask.createLocalRouteTask(extern + networkPath, networkName);

            //////////////////////////////////// Ouverture bdd : ///////////////////////////////////

            // open a local geodatabase
            Geodatabase gdb = new Geodatabase(extern + networkPath);

            //////////////////////////////////// Test Qr code : ////////////////////////////////////

            // On prend un point connu pour tester QR code, en utilisant un magasin existant
            // Ensuite on integrera directement les QR code dans la geodatabase
            // Il sera judicieux de l'integrer par Android pour éviter à l'utilisateur
            // programmeur de recréer la geodatabase

            // Magasin test : La grande recre
            geom_QR_code = gdb.getGeodatabaseTables().get(0).getFeature(1).getGeometry();

            //////////////////////////////////// Référence spatiale : //////////////////////////////
            // Récupération de la référence spatiale :
            SpatialReference mapRef = mMapView.getSpatialReference();

            //////////////////////////////////// Récupération par arcs : ///////////////////////////

            // TODO : un arc par niveau ? Gain temps et efficacité ?

            GeodatabaseFeatureTable tab_niv0 = gdb.getGeodatabaseTables().get(11);
            GeodatabaseFeatureTable tab_niv1 = gdb.getGeodatabaseTables().get(12);
            GeodatabaseFeatureTable tab_niv2 = gdb.getGeodatabaseTables().get(13);

            Log.d("tab1", tab_niv1.getTableName());

            // Récupération des arcs dans la géodatabase  :
            // Comme nous avons plus de 512 arcs sur les étages 1 et 2
            // Nous avons dû procéder en deux fois pour récupérer les arcs de ces étages

            // Définition du nombre d'arcs :
            int i = array_geom_niv0.length;
            int l1 = array_geom_niv1_1.length;
            int l2 = array_geom_niv1_2.length;
            int m1 = array_geom_niv2_1.length;
            int m2 = array_geom_niv2_2.length;

            Geometry poubelle = new Polyline(); // Varaible utile si pas d'objet dans la base

            // Etage 0 :
            for(int j=1; j<=i; j++){
                if (tab_niv0.checkFeatureExists(j)) {
                    array_geom_niv0[j-1] = tab_niv0.getFeature(j,WKID_RGF93).getGeometry();
                } else {array_geom_niv0[j-1] = poubelle;}
            }

            // Etage 1_1 :
            for(int j=1; j<=l1; j++){
                if (tab_niv1.checkFeatureExists(j)) {
                    array_geom_niv1_1[j-1] = tab_niv1.getFeature(j,WKID_RGF93).getGeometry();
                } else {array_geom_niv1_1[j-1] = poubelle;}
            }

            // Etage 1_2 :
            int k1 = 0;
            double longTot = 0;
            for(int j=l1+1; j<=l1+l2; j++){
                if (tab_niv1.checkFeatureExists(j)) {
                    array_geom_niv1_2[k1] = tab_niv1.getFeature(j,WKID_RGF93).getGeometry();
                } else {array_geom_niv1_2[k1] = poubelle;}
                k1 = k1+1;
            }

            // Etage 2_1 :
            for(int j=1; j<=m1; j++){
                if (tab_niv2.checkFeatureExists(j)) {
                    array_geom_niv2_1[j-1] = tab_niv2.getFeature(j,WKID_RGF93).getGeometry();
                } else {array_geom_niv2_1[j-1] = poubelle;}
            }

            // Etage 2_2 :
            int k2 = 0;
            for(int j=m1+1; j<=m1+m2; j++){
                if (tab_niv2.checkFeatureExists(j)) {
                    array_geom_niv2_2[k2] = tab_niv2.getFeature(j,WKID_RGF93).getGeometry();
                } else {array_geom_niv2_2[k2] = poubelle;}
                k2 = k2+1;
            }

            //////////////////////////////////// Union des arcs : //////////////////////////////////

            // Niveau 0:
            geometries_niveau0 = geomen.union(array_geom_niv0, WKID_RGF93);

            // Niveau 1 :
            array_geom_niv1[0] = geomen.union(array_geom_niv1_1, WKID_RGF93);
            array_geom_niv1[1] = geomen.union(array_geom_niv1_2, WKID_RGF93);
            geometries_niveau1 = geomen.union(array_geom_niv1, WKID_RGF93);

            // Niveau 2 :
            array_geom_niv2[0] = geomen.union(array_geom_niv2_1, WKID_RGF93);
            array_geom_niv2[1] = geomen.union(array_geom_niv2_2, WKID_RGF93);
            geometries_niveau2 = geomen.union(array_geom_niv2, WKID_RGF93);

            // logs :
            Log.d("geometries_niveau0", "" + geometries_niveau0.calculateLength2D());
            Log.d("geometries_niveau1", "" + geometries_niveau1.calculateLength2D());
            Log.d("geometries_niveau2", "" + geometries_niveau2.calculateLength2D());

            //////////////////////////////////// Récupération des magasins : ///////////////////////
            for(int v=0; v<=2; v++){
                GeodatabaseFeatureTable mag = gdb.getGeodatabaseTables().get(v);

                long nbr_lignes = mag.getNumberOfFeatures();
                for(int l=1; l<=nbr_lignes; l++){
                    if (v==0) {
                        if (mag.checkFeatureExists(l)) {
                            mag_niv0[l-1] = mag.getFeature(l);
                        } else {mag_niv0[l-1] = null;}
                    } else if (v==1) {
                        if (mag.checkFeatureExists(l)) {
                            mag_niv1[l-1] = mag.getFeature(l);
                        } else {mag_niv1[l-1] = null;}
                    } else if (v==2) {
                        if (mag.checkFeatureExists(l)) {
                            mag_niv2[l-1] = mag.getFeature(l);
                        } else {mag_niv2[l-1] = null;}
                    }
                }
            }

            //////////////////////////////////// Récupération des géométries, noms type : //////////

            // Etage 0
            int len0 = mag_niv0.length;
            for(int k=0; k<len0; k++) {

                Feature Mag =  mag_niv0[k];

                // Récupération géométrie :
                mag_niv0_geom[k] = mag_niv0[k].getGeometry();

                // Récupération nom et type :
                Map<String, Object> lignes = Mag.getAttributes();
                Object type = lignes.get("TYPE");
                Object nom_mag = lignes.get("NOM");
                lst_types_niveau0.add(type);
                lst_mag_niveau0.add(nom_mag);
                lst_nom_mag.add(nom_mag);
            }

            // Etage 1
            int len1 = mag_niv1.length;
            for(int k=0; k<len1; k++) {

                Feature Mag =  mag_niv1[k];

                // Récupération géométrie :
                mag_niv1_geom[k] = mag_niv1[k].getGeometry();

                // Récupération nom et type :
                Map<String, Object> lignes = Mag.getAttributes();
                Object type = lignes.get("TYPE");
                Object nom_mag = lignes.get("NOM");
                lst_types_niveau1.add(type);
                lst_mag_niveau1.add(nom_mag);
                lst_nom_mag.add(nom_mag);
            }

            // Etage 2
            int len2 = mag_niv0.length;
            for(int k=0; k<len2; k++) {

                Feature Mag =  mag_niv2[k];

                // Récupération géométrie :
                mag_niv2_geom[k] = mag_niv2[k].getGeometry();

                // Récupération nom et type :
                Map<String, Object> lignes = Mag.getAttributes();
                Object type = lignes.get("TYPE");
                Object nom_mag = lignes.get("NOM");
                lst_types_niveau2.add(type);
                lst_mag_niveau2.add(nom_mag);
                lst_nom_mag.add(nom_mag);
            }

            //////////////////////////////////// Union des magasins : //////////////////////////////

            mag_niveau0 = geomen.union(mag_niv0_geom, WKID_RGF93);
            mag_niveau1 = geomen.union(mag_niv1_geom, WKID_RGF93);
            mag_niveau2 = geomen.union(mag_niv2_geom, WKID_RGF93);

        } catch (Exception e) {
            popToast("Error while initializing :" + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Fonction qui retrouve un magasin item dans la liste de magasins
     * @param item
     * @param estDepart
     * @return
     */
    public Geometry trouverPtSel(String item, boolean estDepart){

        // Booléen (vrai si un point est selectionné, faux sinon) :
        boolean trouve = false;

        // Définition de la géométrie :
        Geometry ptTest = null;

        // On parcourt la liste récupérer au début dans la geodatabase et on récupère la geometrie
        // correspondant au magasin choisie par l'utilisateur
        int len0 = mag_niv0.length;
        for(int k=0; k<len0; k++) {
            Feature Mag =  mag_niv0[k];
            Map<String, Object> lignes = Mag.getAttributes();
            Object nom_mag = lignes.get("NOM");
            if(nom_mag.equals(item)){
                ptTest = mag_niv0[k].getGeometry();
                trouve = true;
                if(estDepart) {niveau_dep = 0;}
                else{niveau_arr = 0;}
                afficherNom(nom_mag, ptTest);
            }
        }
        if(!trouve){
            int len1 = mag_niv1.length;
            for(int k=0; k<len1; k++) {
                Feature Mag =  mag_niv1[k];
                Map<String, Object> lignes = Mag.getAttributes();
                Object nom_mag = lignes.get("NOM");
                if(nom_mag.equals(item)){
                    ptTest = mag_niv1[k].getGeometry();
                    trouve = true;
                    if(estDepart) {niveau_dep = 1;}
                    else{niveau_arr = 1;}
                    afficherNom(nom_mag, ptTest);
                }
            }
        }
        if(!trouve){
            int len2 = mag_niv2.length;
            for(int k=0; k<len2; k++) {
                Feature Mag =  mag_niv2[k];
                Map<String, Object> lignes = Mag.getAttributes();
                Object nom_mag = lignes.get("NOM");
                if(nom_mag.equals(item)){
                    ptTest = mag_niv2[k].getGeometry();
                    if(estDepart) {niveau_dep = 2;}
                    else{niveau_arr = 2;}
                    afficherNom(nom_mag, ptTest);
                }
            }
        }
        return ptTest;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Fonction qui ajoute une géométrie point aux stops et sur le graphe avec le symbole symbol
     * @param point
     * @param symbol
     */
    public void ajouterPoint(Geometry point, Symbol symbol){
        mGraphicsLayer.addGraphic(new Graphic(point, symbol));
        StopGraphic stop = new StopGraphic(point);
        mStops.addFeature(stop);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Fonction qui réinitialise l'affichage :
     */
    public void clearAffich(){
        mGraphicsLayer.removeAll();
        mMapView.getCallout().hide();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Fonction qui calcule l'tinéraire
     */
    public void calculerIti(SpatialReference mapRef){
        // Return default behavior if we did not initialize properly.
        if (mRouteTask == null) {
            popToast("RouteTask uninitialized.", true);
        }

        try {
            // Set the correct input spatial reference on the stops and the
            // desired output spatial reference on the RouteParameters object.
            RouteParameters params = mRouteTask.retrieveDefaultRouteTaskParameters();
            params.setOutSpatialReference(mapRef);
            mStops.setSpatialReference(mapRef);

            // Si l'utilisateur est à mobilité réduite, on ajoute la restriction au paramètre
            if(estRestreint){
                String[] restrictions = {"Restriction"};
                params.setRestrictionAttributeNames(restrictions);
            } else{
                String[] restrictions = {""};
                params.setRestrictionAttributeNames(restrictions);
            }

            // Set the stops and since we want driving directions,
            // returnDirections==true
            params.setStops(mStops);
            params.setReturnDirections(true);

            // Perform the solve
            RouteResult results = mRouteTask.solve(params);

            // Grab the results; for offline routing, there will only be one
            // result returned on the output.
            Route result = results.getRoutes().get(0);

            //////////////////////////////////// Projection : //////////////////////////////////////

            // On projete les arcs dans le repère local :

            projection_niv0 = geomen.project(geometries_niveau0, WKID_RGF93, mapRef);
            projection_niv1 = geomen.project(geometries_niveau1, WKID_RGF93, mapRef);
            projection_niv2 = geomen.project(geometries_niveau2, WKID_RGF93, mapRef);

            //////////////////////////////////// Intersection : ////////////////////////////////////

            geom = result.getRouteGraphic().getGeometry();

            // On intersecte l'itinéraire avec les arcs :
            geom_intersect_niv0 = geomen.intersect(geom, projection_niv0, mapRef);
            geom_intersect_niv1 = geomen.intersect(geom, projection_niv1, mapRef);
            geom_intersect_niv2 = geomen.intersect(geom, projection_niv2, mapRef);

            //////////////////////////////////// Gestion : /////////////////////////////////////////

            // Affichage de l’étage du point de départ :
            spinnerEtgSel.setSelection(niveau_dep+1);
            //Gestion affichage au moment du calcul d'itinéraire :
            afficherIti();

            ////////////////////////////////////////////////////////////////////////////////////////
            mMapView.getCallout().hide();

        } catch (Exception e) {
            popToast("Solve Failed: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Fonction pour afficher l'itinéraire en fonction de l'étage sélectionné
     */
    public void afficherIti(){
        // Remove any previous route Graphics
        mGraphicsLayer.removeGraphic(routeHandle);

        // Défintion symbole pour l'itinéraire :
        SimpleLineSymbol ligSym = new SimpleLineSymbol(0x99990055, 5, SimpleLineSymbol.STYLE.fromString("DASH"));

        // On ne visualise que l'itinéraire au niveau selectionné :
        if(geom_intersect_niv0 != null && etg0Selected) {
            if (!geom_intersect_niv0.isEmpty()) {
                routeHandle = mGraphicsLayer.addGraphic(new Graphic(geom_intersect_niv0, ligSym));
                Log.d("geom0_inter_length", ": " + geom_intersect_niv0.calculateLength2D());
            }
        }

        if(geom_intersect_niv1 != null && etg1Selected) {
            if (!geom_intersect_niv1.isEmpty()) {
                routeHandle = mGraphicsLayer.addGraphic(new Graphic(geom_intersect_niv1, ligSym));
                Log.d("geom1_inter_length", ": " + geom_intersect_niv1.calculateLength2D());
            }
        }

        if(geom_intersect_niv2 != null  && etg2Selected) {
            if (!geom_intersect_niv2.isEmpty()) {
                routeHandle = mGraphicsLayer.addGraphic(new Graphic(geom_intersect_niv2, ligSym));
                Log.d("geom2_inter_length", ": " + geom_intersect_niv2.calculateLength2D());
            }
        }

        if(geom!= null && etgsSelected) {
            if (!geom.isEmpty()) {
                routeHandle = mGraphicsLayer.addGraphic(new Graphic(geom, ligSym));
                Log.d("geom_inter_length", ": " + geom.calculateLength2D());
            }
        }
    }


    /**
     * Fonction qui affiche le magasin le plus proche du point de départ :
     * @param mapRef
     */
    public void afficherPpv(SpatialReference mapRef){
        //////////////////////////////////// Projection  : /////////////////////////////////////////

        // On projete les magasins en mapRef :
        projection_mag_niv0 = geomen.project(mag_niveau0, WKID_RGF93, mapRef);
        projection_mag_niv1 = geomen.project(mag_niveau1, WKID_RGF93, mapRef);
        projection_mag_niv2 = geomen.project(mag_niveau2, WKID_RGF93, mapRef);

        //////////////////////////////////// Différence : //////////////////////////////////////////

        // Différence entre le point et les autres magasins
        Geometry diff_niv0 = geomen.difference(projection_mag_niv0, depart, mapRef);
        Geometry diff_niv1 = geomen.difference(projection_mag_niv1, depart, mapRef);
        Geometry diff_niv2 = geomen.difference(projection_mag_niv2, depart, mapRef);


        //////////////////////////////////// Distance géométrique : ////////////////////////////////

        double distance_niv0 = geomen.distance(depart, diff_niv0, mapRef);
        double distance_niv1 = geomen.distance(depart, diff_niv1, mapRef);
        double distance_niv2 = geomen.distance(depart, diff_niv2, mapRef);


        //////////////////////////////////// Initialisation : //////////////////////////////////////

        // Définition de l'unité :
        Unit meter = Unit.create(LinearUnit.Code.METER);

        // Initialisation des variables utile au calcul du ppv :
        String texte = null;
        Geometry mag = null;
        int taille = 14;
        double dist_ref = 1000;
        int color = Color.rgb(255, 1, 1); // Couleur texte d'affichage du nom du magasin

        if (niveau_dep == 0){
            Polygon buff_niv0 = geomen.buffer(depart, mapRef, distance_niv0, meter);
            Geometry magasin = geomen.intersect(buff_niv0, projection_mag_niv0, mapRef);

            // On cherhce le magasin le plus proche
            // c'est-à-dire à la distance minimale du point de départ
            for (int r=0; r<lst_mag_niveau0.size(); r++){
                Geometry mag_niv0_r = geomen.project(mag_niv0_geom[r], WKID_RGF93, mapRef);
                double dist_mag0 = geomen.distance(mag_niv0_r,magasin, mapRef);

                if (dist_mag0 < dist_ref && dist_mag0!=0){
                    texte = lst_mag_niveau0.get(r).toString();
                    mag = geomen.project(mag_niv0_geom[r], WKID_RGF93, mapRef);
                    dist_ref = dist_mag0;
                }
            }
            // Affichage du ppv :
            if (mag != null) {
                mGraphicsLayer.addGraphic(new Graphic(mag, new TextSymbol(taille, texte, color)));
            }

        } else if (niveau_dep == 1){
            Polygon buff_niv1 = geomen.buffer(depart, mapRef, distance_niv1, meter);
            Geometry magasin = geomen.intersect(buff_niv1, projection_mag_niv1, mapRef);

            for (int r=0; r<lst_mag_niveau1.size(); r++){
                Geometry mag_niv1_r = geomen.project(mag_niv1_geom[r], WKID_RGF93, mapRef);
                double dist_mag1 = geomen.distance(mag_niv1_r, magasin, mapRef);

                // On cherhce le magasin le plus proche
                // c'est-à-dire à la distance minimale du point de départ
                if (dist_mag1 < dist_ref && dist_mag1!=0){
                    texte = lst_mag_niveau1.get(r).toString();
                    mag = geomen.project(mag_niv1_geom[r], WKID_RGF93, mapRef);
                    dist_ref = dist_mag1;
                }
            }
            // Affichage du ppv :
            if (mag != null) {
                mGraphicsLayer.addGraphic(new Graphic(mag, new TextSymbol(taille, texte, color)));
            }

        } else if (niveau_dep == 2){
            Polygon buff_niv2 = geomen.buffer(depart, mapRef, distance_niv2, meter);
            Geometry magasin = geomen.intersect(buff_niv2, projection_mag_niv2, mapRef);

            // On cherhce le magasin le plus proche
            // c'est-à-dire à la distance minimale du point de départ
            for (int r=0; r<lst_mag_niveau2.size(); r++){
                Geometry mag_niv2_r = geomen.project(mag_niv2_geom[r], WKID_RGF93, mapRef);
                double dist_mag2 = geomen.distance(mag_niv2_r, magasin, mapRef);

                if (dist_mag2 < dist_ref && dist_mag2!=0){
                    texte = lst_mag_niveau2.get(r).toString();
                    mag = geomen.project(mag_niv2_geom[r], WKID_RGF93, mapRef);
                    dist_ref = dist_mag2;
                }
            }
            // Affichage du ppv :
            if (mag != null) {
                mGraphicsLayer.addGraphic(new Graphic(mag, new TextSymbol(taille, texte, color)));
            }
        }
    }

    /////////////////////////////////////////////////////////////////////

    /**
     * Fonction qui affiche le nom des points selectionnés:
     * @param nom_mag
     * @param PtTest
     */
    public void afficherNom(Object nom_mag, Geometry PtTest) {
        SpatialReference mapRef = mMapView.getSpatialReference();
        String nom = nom_mag.toString();
        int taille_nom = 16;
        int color_nom = Color.rgb(255, 1, 1);
        Geometry point = geomen.project(PtTest, WKID_RGF93, mapRef);
        mGraphicsLayer.addGraphic(new Graphic(point, new TextSymbol(taille_nom, nom, color_nom,
                TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.MIDDLE)));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Toolbar:
     */

     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         // Inflate the menu; this adds items to the action bar if it is present.
         getMenuInflater().inflate(R.menu.menu_main, menu);
         return true;
     }

     private void choix(){
         Intent intent_choix = new Intent(MainActivity.this, Choix.class);
         intent_choix.putExtra("Liste_mag0", lst_mag_niveau0);
         intent_choix.putExtra("Liste_mag1", lst_mag_niveau1);
         intent_choix.putExtra("Liste_mag2", lst_mag_niveau2);
         intent_choix.putExtra("Liste_type0", lst_types_niveau0);
         intent_choix.putExtra("Liste_type1", lst_types_niveau1);
         intent_choix.putExtra("Liste_type2", lst_types_niveau2);

         startActivityForResult(intent_choix, 0);
     }

     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
         // Handle action bar item clicks here. The action bar will
         // automatically handle clicks on the Home/Up button, so long
         // as you specify a parent activity in AndroidManifest.xml.
         int id = item.getItemId();

         //noinspection SimplifiableIfStatement
         if (id == R.id.action_choix) {
             choix();
             return true;
         }

     return super.onOptionsItemSelected(item);
     }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void popToast(final String message, final boolean show) {
        // Simple helper method for showing toast on the main thread
        if (!show)
            return;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
