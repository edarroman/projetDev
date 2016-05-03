package com.example.formation.androidprojet_v1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.GraphicsLayer.RenderingMode;
import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;
import com.esri.android.map.TiledLayer;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.core.geodatabase.Geodatabase;
import com.esri.core.geodatabase.GeodatabaseFeatureTable;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol.STYLE;
import com.esri.core.tasks.geocode.Locator;
import com.esri.core.tasks.geocode.LocatorReverseGeocodeResult;
import com.esri.core.tasks.na.NAFeaturesAsFeature;
import com.esri.core.tasks.na.Route;
import com.esri.core.tasks.na.RouteParameters;
import com.esri.core.tasks.na.RouteResult;
import com.esri.core.tasks.na.RouteTask;
import com.esri.core.tasks.na.StopGraphic;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;

// TODO en fonction carte sd ou non changer chemin (Fonction à coder) :

/*

// SD card :

private final String chTpk = "/ProjArcades/ArcGIS/";

String locatorPath = "/ProjArcades/ArcGIS/Geocoding/MGRS.loc";
String networkPath = "/ProjArcades/ArcGIS/Routing/base_de_donnees.geodatabase";
String networkName = "GRAPH_Final_ND";

OU

// Autre :
private final String chTpk = "/Android/data/com.example.formation.androidprojet_v1/ArcGIS/";

String locatorPath = "/Android/data/com.example.formation.androidprojet_v1/ArcGIS/Geocoding/MGRS.loc";
String networkPath = "/Android/data/com.example.formation.androidprojet_v1/ArcGIS/Routing/base_de_donnees.geodatabase";
String networkName = "GRAPH_Final_ND";

*/

public class MainActivity extends Activity implements OnItemSelectedListener, View.OnClickListener {

    /**
     * Déclaration des variables globales :
     */

    // Define ArcGIS Elements
    private MapView mMapView;

    private final String extern = Environment.getExternalStorageDirectory().getPath();

    // Sd card :
    private final String chTpk = "/ProjArcades/ArcGIS/";
     /*
    // Sans sd card :
    private final String chTpk = "/Android/data/com.example.formation.androidprojet_v1/ArcGIS/"; // TODO chemin qui change en fonction SD card ou non : trouver automatiquement
    */

    // Variable pour image de fond :
    private String tpkPath = chTpk +"arcades.tpk";
    private String tpkPath0 = chTpk +"niveau_0.tpk";
    private String tpkPath1 = chTpk +"niveau_1.tpk";
    private String tpkPath2 = chTpk +"niveau_2.tpk";

    private TiledLayer mTileLayer = new ArcGISLocalTiledLayer(extern + tpkPath);
    private TiledLayer mTileLayer0 = new ArcGISLocalTiledLayer(extern + tpkPath0);
    private TiledLayer mTileLayer1 = new ArcGISLocalTiledLayer(extern + tpkPath1);
    private TiledLayer mTileLayer2 = new ArcGISLocalTiledLayer(extern + tpkPath2);

    private GraphicsLayer mGraphicsLayer = new GraphicsLayer(RenderingMode.DYNAMIC);

    private RouteTask mRouteTask = null;
    private NAFeaturesAsFeature mStops = new NAFeaturesAsFeature();

    private Locator mLocator = null;
    private Spinner dSpinner;

    // Variables utiles pour la gestion du multi-étage :
    private boolean etgsSelected = false;
    private boolean etg0Selected = false;
    private boolean etg1Selected = false;
    private boolean etg2Selected = false;

    // Variables utiles à la gestion du QR_code :
    private Geometry geom_QR_code = null;

    private Geometry projection_niv0 = null;
    private Geometry projection_niv1 = null;
    private Geometry projection_niv2 = null;
    // Geometrie union :
    private Geometry geometries_niveau0 = null;
    private Geometry geometries_niveau1 = null;
    private Geometry geometries_niveau1_2 = null;
    private Geometry geometries_niveau2 = null;
    // Géomtrie intersections :
    private Geometry geom = null;
    private Geometry geom_intersect_niv0 = null;
    private Geometry geom_intersect_niv1 = null;
    private Geometry geom_intersect_niv2 = null;

    // Définiton géométrie engine :
    private GeometryEngine geomen = new GeometryEngine();

    // Référence spatiale :
    private SpatialReference WKID_RGF93 = SpatialReference.create(102110);

    // Variables utiles à la récupérations des arcs :
    private Geometry[] array_geom_niv0 = new Geometry[127];
    private Geometry[] array_geom_niv1_1 = new Geometry[380];
    private Geometry[] array_geom_niv1_2 = new Geometry[377];
    private Geometry[] array_geom_niv2_1 = new Geometry[380];
    private Geometry[] array_geom_niv2_2 = new Geometry[400];
    private Geometry[] array_geom_niv1 = new Geometry[2];
    private Geometry[] array_geom_niv2 = new Geometry[2];

    // Gestion itinéraire :
    private int routeHandle = -1;

    // Variable de restrictions :
    private CheckBox checkBoxRes = null;
    private String resTxt;
    private boolean estRestreint = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Spinner element pour changement etage
        Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        // Spinner click listener pour changement etage
        spinner.setOnItemSelectedListener(this);

        File tpk = new File(extern + tpkPath);
        Log.d("RoutingAndGeocoding", "Find tpk: " + tpk.exists());
        Log.d("RoutingAndGeocoding", "Initialized tpk: " + mTileLayer.isInitialized());

        // Find the directions spinner
        dSpinner = (Spinner) findViewById(R.id.directionsSpinner);
        dSpinner.setEnabled(false);

        // Retrieve the map and initial extent from XML layout
        mMapView = (MapView) findViewById(R.id.map);

        // Mise en place des fonds et visibilité = false :
        mMapView.addLayer(mTileLayer0);
        mTileLayer0.setVisible(false);

        mMapView.addLayer(mTileLayer1);
        mTileLayer1.setVisible(false);

        mMapView.addLayer(mTileLayer2);
        mTileLayer2.setVisible(false);

        mMapView.addLayer(mTileLayer);
        mTileLayer.setVisible(false);

        // Ajout couche graphique :
        mMapView.addLayer(mGraphicsLayer);

        //Restriction :
        checkBoxRes = (CheckBox)findViewById(R.id.checkBoxRes);
        resTxt = getResources().getString(R.string.rest);
        checkBoxRes.setText(resTxt);
        checkBoxRes.setOnClickListener(checkedListener);

        // Initialize the RouteTask and Locator with the local data
        initializeRoutingAndGeocoding();
        mMapView.setOnTouchListener(new TouchListener(MainActivity.this, mMapView));

        // QR code
        Button mybutton = (Button) findViewById(R.id.scan_button);
        mybutton.setOnClickListener(this);
    }

    /**
     * Initilialisation de l'itinéraire et du géocodage
     */

    private void initializeRoutingAndGeocoding() {

        // We will spin off the initialization in a new thread
        new Thread(new Runnable() {

            @Override
            public void run() {

                // TODO : Modification automatique en fonction du type d'appareil (SD ou non)

                // Get the external directory

                // SdCard
                String locatorPath = "/ProjArcades/ArcGIS/Geocoding/MGRS.loc";
                String networkPath = "/ProjArcades/ArcGIS/Routing/base_de_donnees.geodatabase";

                /*
                // Sans carte Sd :
                String locatorPath = "/Android/data/com.example.formation.androidprojet_v1/ArcGIS/Geocoding/MGRS.loc";
                String networkPath = "/Android/data/com.example.formation.androidprojet_v1/ArcGIS/Routing/base_de_donnees.geodatabase";
                */

                String networkName = "GRAPH_Final_ND";

                // Attempt to load the local geocoding and routing data
                try {
                    mLocator = Locator.createLocalLocator(extern + locatorPath);
                    mRouteTask = RouteTask.createLocalRouteTask(extern + networkPath, networkName);

                    ////////////////////////////////////////////////////////////////////////////////

                    // open a local geodatabase
                    Geodatabase gdb = new Geodatabase(extern + networkPath);

                    // On prend un point connu pour tester QR code, en utilisant un magasin existant
                    // Ensuite on integrera directement les QR code dans la geodatabase
                    // Il sera judicieux de l'integrer par Android pour éviter à l'utilisateur
                    // programmeur de recréer la geodatabase

                    // Magasin test : La grande recre
                    geom_QR_code = gdb.getGeodatabaseTables().get(0).getFeature(1).getGeometry();

                    ////////////////////////////////////////////////////////////////////////////////

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

                    ////////////////////////////////////////////////////////////////////////////////

                    // Union des arcs :

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


                } catch (Exception e) {
                    popToast("Error while initializing :" + e.getMessage(), true);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Définition des évenements :
     */

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String etageSelec = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + etageSelec, Toast.LENGTH_LONG).show();

        // On recupere les noms des etages qui sont stockés dans ressources.strings.values
        String[] nom_etage = getResources().getStringArray(R.array.etage_array);

        // Test suivant la selection de l'utilisateur:
        if(etageSelec.equals(nom_etage[0])) {
            etgsSelected = false;
            etg0Selected = true;
            etg1Selected = false;
            etg2Selected = false;
        }
        if(etageSelec.equals(nom_etage[1])) {
            etgsSelected = false;
            etg0Selected = false;
            etg1Selected = true;
            etg2Selected = false;
        }
        if(etageSelec.equals(nom_etage[2])) {
            etgsSelected = false;
            etg0Selected = false;
            etg1Selected = false;
            etg2Selected = true;
        }
        if(etageSelec.equals(nom_etage[3])) {
            etgsSelected = true;
            etg0Selected = false;
            etg1Selected = false;
            etg2Selected = false;
        }

        mTileLayer.setVisible(etgsSelected);
        mTileLayer0.setVisible(etg0Selected);
        mTileLayer1.setVisible(etg1Selected);
        mTileLayer2.setVisible(etg2Selected);

        ////////////////////////////////////////////////////////////////////////////////////////////

        // Gestion affichage au moment du changement d'étage :
        afficherIti();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO : Auto-generated method stub
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gestion des QR codes :
     */

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.scan_button){
            // On lance le scanner au clic sur notre bouton
            new IntentIntegrator(this).initiateScan();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // Nous utilisons la classe IntentIntegrator et sa fonction parseActivityResult pour parser le résultat du scan
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            // Récupération référence spatiale de  la vue :
            SpatialReference mapRef = mMapView.getSpatialReference();

            // Nous récupérons le contenu du code barre
            String scanContent = scanningResult.getContents();

            // Nous récupérons le format du code barre
            String scanFormat = scanningResult.getFormatName();

            TextView scan_format = (TextView) findViewById(R.id.scan_format);
            TextView scan_content = (TextView) findViewById(R.id.scan_content);

            // Nous affichons le résultat dans nos TextView
            scan_format.setText("FORMAT: " + scanFormat);
            scan_content.setText("CONTENT: " + scanContent);
            Log.d("Scan content", scanContent);

            // Test sur le different QR code scanné
            // On utilise ces tests pour définir le point de départ ou des points intermédiaires par exemple
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
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Aucune donnée reçu!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gestion itinéraire :
     */

    class TouchListener extends MapOnTouchListener {
        /**
         * Evenement sur une longue pression du doigt supprsesions des stops et de l'itinéraire
         * @param point
         */
        @Override
        public void onLongPress(MotionEvent point) {
            // Our long press will clear the screen
            mStops.clearFeatures();
            mGraphicsLayer.removeAll();
            mMapView.getCallout().hide();
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        /**
         * Evenement zvec une tape on ajoute un point (= un stop)
         * @param point
         * @return
         */
        @Override
        public boolean onSingleTap(MotionEvent point) {

            if (mLocator == null) {
                popToast("Locator uninitialized", true);
                return super.onSingleTap(point);
            }
            // Add a graphic to the screen for the touch event
            Point mapPoint = mMapView.toMapPoint(point.getX(), point.getY());
            //Graphic graphic = new Graphic(mapPoint, new SimpleMarkerSymbol(Color.BLUE, 10, STYLE.DIAMOND));

            // TODO : se renseigner sur getDrawable()

            Drawable marqueur = getResources().getDrawable(R.drawable.ic_action_marqueur);
            //Log.d("Pic", "" + new PictureMarkerSymbol(marqueur));
            Graphic graphic = new Graphic(mapPoint, new PictureMarkerSymbol(marqueur));

            mGraphicsLayer.addGraphic(graphic);

            try {
                // Attempt to reverse geocode the point.
                // Our input and output spatial reference will
                // be the same as the map.
                SpatialReference mapRef = mMapView.getSpatialReference();
                LocatorReverseGeocodeResult result = mLocator.reverseGeocode(mapPoint, 50, mapRef, mapRef);

            } catch (Exception e) {
                Log.v("Reverse Geocode", e.getMessage());
            }

            // Add the touch event as a stop
            StopGraphic stop = new StopGraphic(graphic);
            mStops.addFeature(stop);

            return true;
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        /**
         * Evenement au double tape : calcule de l'itinéraire et affichage
         * @param point
         * @return
         */
        @Override
        public boolean onDoubleTap(MotionEvent point) {

            // Return default behavior if we did not initialize properly.
            if (mRouteTask == null) {
                popToast("RouteTask uninitialized.", true);
                return super.onDoubleTap(point);
            }

            try {

                // Set the correct input spatial reference on the stops and the
                // desired output spatial reference on the RouteParameters object.
                RouteParameters params = mRouteTask.retrieveDefaultRouteTaskParameters();
                SpatialReference mapRef = mMapView.getSpatialReference();
                params.setOutSpatialReference(mapRef);
                mStops.setSpatialReference(mapRef);

                if(estRestreint){
                    String[] restrictions = {"Restriction"};
                    params.setRestrictionAttributeNames(restrictions);
                } else{
                    String[] restrictions = {""};
                    params.setRestrictionAttributeNames(restrictions);
                }



                Log.d("Restrictions",""+ params.getRestrictionAttributeNames());

                // Set the stops and since we want driving directions,
                // returnDirections==true
                params.setStops(mStops);
                params.setReturnDirections(true);

                // Perform the solve
                RouteResult results = mRouteTask.solve(params);

                // Grab the results; for offline routing, there will only be one
                // result returned on the output.
                Route result = results.getRoutes().get(0);

                ////////////////////////////////////////////////////////////////////////////////////

                // On projete les arcs en RGF93 :

                //geometries_niveau0 = geomen.union(array_geom_niv0, WKID_RGF93);
                projection_niv0 = geomen.project(geometries_niveau0, WKID_RGF93, mapRef);

                //geometries_niveau1 = geomen.union(array_geom_niv1, WKID_RGF93);
                projection_niv1 = geomen.project(geometries_niveau1, WKID_RGF93, mapRef);

                //geometries_niveau2 = geomen.union(array_geom_niv2, WKID_RGF93);
                projection_niv2 = geomen.project(geometries_niveau2, WKID_RGF93, mapRef);

                ////////////////////////////////////////////////////////////////////////////////////

                // On intersecte l'itinéraire avec les arcs :

                // Add the route shape to the graphics layer
                geom = result.getRouteGraphic().getGeometry();

                geom_intersect_niv0 = geomen.intersect(geom, projection_niv0, mapRef);
                geom_intersect_niv1 = geomen.intersect(geom, projection_niv1, mapRef);
                geom_intersect_niv2 = geomen.intersect(geom, projection_niv2, mapRef);

                ////////////////////////////////////////////////////////////////////////////////////

                //Gestion affichage au moment du calcul d'itinéraire :

                afficherIti();
                /*
                ////////////////////////////////////////////////////////////////////////////////////

                //QR code
                projection = geomen.project(geom_QR_code, WKID_RGF93, mapRef);
                */
                mMapView.getCallout().hide();

            } catch (Exception e) {
                popToast("Solve Failed: " + e.getMessage(), true);
                e.printStackTrace();
            }
            return true;
        }

        public TouchListener(Context context, MapView view) {
            super(context, view);}
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
    * Listener du bouton de la restriction.
     * */
    private View.OnClickListener checkedListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(((CheckBox)v).isChecked()) {
                estRestreint = true;
            }else{
                estRestreint = false;
            }
        }
    };
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Fonction pour afficher l'itinéraire en fonction de l'étage sélectionné
     */
    public void afficherIti(){
        // Défintion symbole pour l'itinéraire :
        SimpleLineSymbol ligSym = new SimpleLineSymbol(0x99990055, 5);

        // Remove any previous route Graphics
        mGraphicsLayer.removeGraphic(routeHandle);

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
