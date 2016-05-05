package com.example.formation.androidprojet_v1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.io.File;

import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import android.content.Context;
import android.graphics.Color;

import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Feature;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol.STYLE;
import com.esri.core.tasks.geocode.Locator;
import com.esri.core.tasks.geocode.LocatorReverseGeocodeResult;
import com.esri.core.tasks.na.NAFeaturesAsFeature;
import com.esri.core.tasks.na.Route;
import com.esri.core.tasks.na.RouteDirection;
import com.esri.core.tasks.na.RouteParameters;
import com.esri.core.tasks.na.RouteResult;
import com.esri.core.tasks.na.RouteTask;
import com.esri.core.tasks.na.StopGraphic;


public class MainActivity extends AppCompatActivity {

    // Define ArcGIS Elements
    MapView mMapView;
    final String extern = "/mnt/ext_card";
    //Hervé final String tpkPath = "/ArcGIS/samples/OfflineRouting/SanDiego.tpk";
    //final String tpkPath = "/Herve/ArcGIS/test_Forca.tpk";
    final String tpkPath = "/ProjetArcades/ArcGIS/niveau_1.tpk";
    //private final String extern = Environment.getExternalStorageDirectory().getPath();
    //final String tpkPath = "/ProjArcades/ArcGIS/niveau_1.tpk";


    TiledLayer mTileLayer = new ArcGISLocalTiledLayer(extern + tpkPath);
    GraphicsLayer mGraphicsLayer = new GraphicsLayer(RenderingMode.DYNAMIC);

    RouteTask mRouteTask = null;
    NAFeaturesAsFeature mStops = new NAFeaturesAsFeature();

    Locator mLocator = null;

    ArrayList<String> lst_mag = new ArrayList<>();
    ArrayList<String> lst_type = new ArrayList<>();
    List lst_geo = new Vector();

    public final static int request_code = 1;
    Object geo_dep;
    Object geo_arr;

    // Définiton géométrie engine :
    private GeometryEngine geomen = new GeometryEngine();

    // Référence spatiale :
    private SpatialReference WKID_RGF93 = SpatialReference.create(102110);

    // Variables utiles pour la récupération des magasins :
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        File tpk = new File(extern + tpkPath);
        Log.d("RoutingAndGeocoding", "Find tpk: " + tpk.exists());
        Log.d("RoutingAndGeocoding", "Initialized tpk: " + mTileLayer.isInitialized());
        // Retrieve the map and initial extent from XML layout
        mMapView = (MapView) findViewById(R.id.map);
        // Set the tiled map service layer and add a graphics layer
        mMapView.addLayer(mTileLayer);
        mMapView.addLayer(mGraphicsLayer);
        // Initialize the RouteTask and Locator with the local data
        initializeRoutingAndGeocoding();
        mMapView.setOnTouchListener(new TouchListener(MainActivity.this, mMapView));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent_req) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK){
                final String mag_dep = intent_req.getStringExtra("Depart");
                final String mag_arr = intent_req.getStringExtra("Arrivee");
                final String niv_ar = intent_req.getStringExtra("Niv_ar");
                final String niv_dep = intent_req.getStringExtra("Niv_dep");
                if (niv_dep == "0") {
                    for (int s = 0; s < lst_mag_niveau0.size(); s++) {
                        if (lst_mag_niveau0.get(s).toString().equals(mag_dep)) {
                            geo_dep = mag_niv0_geom[s];
                        }
                    }
                }
                if (niv_dep == "1") {
                    for (int s = 0; s < lst_mag_niveau1.size(); s++) {
                        if (lst_mag_niveau1.get(s).toString().equals(mag_dep)) {
                            geo_dep = mag_niv1_geom[s];
                        }
                    }
                }
                if (niv_dep == "2") {
                    for (int s = 0; s < lst_mag_niveau2.size(); s++) {
                        if (lst_mag_niveau2.get(s).toString().equals(mag_dep)) {
                            geo_dep = mag_niv2_geom[s];
                        }
                    }
                }
                if (niv_ar == "0") {
                    for (int s = 0; s < lst_mag_niveau0.size(); s++) {
                        if (lst_mag_niveau0.get(s).toString().equals(mag_arr)) {
                            geo_arr = mag_niv0_geom[s];
                        }
                    }
                }
                if (niv_ar == "1") {
                    for (int s = 0; s < lst_mag_niveau1.size(); s++) {
                        if (lst_mag_niveau1.get(s).toString().equals(mag_arr)) {
                            geo_arr = mag_niv1_geom[s];
                        }
                    }
                }
                if (niv_ar == "2") {
                    for (int s = 0; s < lst_mag_niveau2.size(); s++) {
                        if (lst_mag_niveau2.get(s).toString().equals(mag_arr)) {
                            geo_arr = mag_niv2_geom[s];
                        }
                    }
                }

            }
        }
    }

    private void initializeRoutingAndGeocoding() {
        // We will spin off the initialization in a new thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Get the external directory
                String locatorPath = "/ProjetArcades/ArcGIS/Geocoding/MGRS.loc";
                String networkPath = "/ProjetArcades/ArcGIS/Routing/base_de_donnees.geodatabase";
                String networkName = "GRAPH_Final_ND";
                // Attempt to load the local geocoding and routing data
                try {
                    mLocator = Locator.createLocalLocator(extern + locatorPath);
                    mRouteTask = RouteTask.createLocalRouteTask(extern + networkPath, networkName);
                    // open a local geodatabase
                    Geodatabase gdb = new Geodatabase(extern + networkPath);

                    ////////////////////////////////////////////////////////////////////////////////

                    // Récupération des magasins :
                    for(int v=0; v<=2; v++){

                        GeodatabaseFeatureTable mag = gdb.getGeodatabaseTables().get(v);

                        long nbr_lignes = mag.getNumberOfFeatures();
                        for(int l=1; l<=nbr_lignes; l++){
                            if (v==0) {
                                if (mag.checkFeatureExists(l)) {
                                    mag_niv0[l-1] = mag.getFeature(l);
                                } else {
                                    mag_niv0[l-1] = null;
                                }
                            } else if (v==1) {
                                if (mag.checkFeatureExists(l)) {
                                    mag_niv1[l-1] = mag.getFeature(l);
                                } else {
                                    mag_niv1[l-1] = null;
                                }
                            } else if (v==2) {
                                if (mag.checkFeatureExists(l)) {
                                    mag_niv2[l-1] = mag.getFeature(l);
                                } else {
                                    mag_niv2[l-1] = null;
                                }
                            }
                        }
                    }

                    // Récupération de la référence spatiale :
                    SpatialReference mapRef = mMapView.getSpatialReference();

                    ////////////////////////////////////////////////////////////////////////////////

                    // Récupération des géométries, noms type :
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
                    }

                    ////////////////////////////////////////////////////////////////////////////////

                    // Union des magasins :

                    mag_niveau0 = geomen.union(mag_niv0_geom, WKID_RGF93);
                    mag_niveau1 = geomen.union(mag_niv1_geom, WKID_RGF93);
                    mag_niveau2 = geomen.union(mag_niv2_geom, WKID_RGF93);

                    ////////////////////////////////////////////////////////////////////////////////

                    // On projete les magasins en RGF93 :

                    projection_mag_niv0 = geomen.project(mag_niveau0, WKID_RGF93, mapRef);
                    projection_mag_niv1 = geomen.project(mag_niveau1, WKID_RGF93, mapRef);
                    projection_mag_niv2 = geomen.project(mag_niveau2, WKID_RGF93, mapRef);

                    ////////////////////////////////////////////////////////////////////////////////
                    Log.d("1",""+lst_types_niveau1);
                } catch (Exception e) {
                    popToast("Error while initializing :" + e.getMessage(), true);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    class TouchListener extends MapOnTouchListener {
        private int routeHandle = -1;
        @Override
        public void onLongPress(MotionEvent point) {
            // Our long press will clear the screen
            mStops.clearFeatures();
            mGraphicsLayer.removeAll();
            mMapView.getCallout().hide();
        }
        @Override
        public boolean onSingleTap(MotionEvent point) {
            if (mLocator == null) {
                popToast("Locator uninitialized", true);
                return super.onSingleTap(point);
            }
            // Add a graphic to the screen for the touch event
            Point mapPoint = mMapView.toMapPoint(point.getX(), point.getY());
            Graphic graphic = new Graphic(mapPoint, new SimpleMarkerSymbol(Color.BLUE, 10, STYLE.DIAMOND));
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
                SpatialReference mapRef = mMapView.getSpatialReference();
                RouteParameters params = mRouteTask.retrieveDefaultRouteTaskParameters();
                params.setOutSpatialReference(mapRef);
                mStops.setSpatialReference(mapRef);

                // Set the stops and since we want driving directions,
                // returnDirections==true
                params.setStops(mStops);
                params.setReturnDirections(true);

                // Perform the solve
                RouteResult results = mRouteTask.solve(params);

                // Grab the results; for offline routing, there will only be one
                // result returned on the output.
                Route result = results.getRoutes().get(0);

                // Remove any previous route Graphics
                if (routeHandle != -1)
                    mGraphicsLayer.removeGraphic(routeHandle);

                // Add the route shape to the graphics layer
                Geometry geom = result.getRouteGraphic().getGeometry();
                routeHandle = mGraphicsLayer.addGraphic(new Graphic(geom, new SimpleLineSymbol(0x99990055, 5)));
                mMapView.getCallout().hide();
                // Get the list of directions from the result
                List<RouteDirection> directions = result.getRoutingDirections();
                // Iterate through all of the individual directions items and
                // create a nicely formatted string for each.
                List<String> formattedDirections = new ArrayList<String>();
                for (int i = 0; i < directions.size(); i++) {
                    RouteDirection direction = directions.get(i);
                    formattedDirections.add(String.format("%s\nGo %.2f %s For %.2f Minutes", direction.getText(),
                            direction.getLength(), params.getDirectionsLengthUnit().name(), direction.getMinutes()));
                }
                // Add a summary String
                formattedDirections.add(0, String.format("Total time: %.2f Mintues", result.getTotalMinutes()));
            } catch (Exception e) {
                popToast("Solve Failed: " + e.getMessage(), true);
                e.printStackTrace();
            }
            return true;
        }
        public TouchListener(Context context, MapView view) {
            super(context, view);
        }
    }
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
}


