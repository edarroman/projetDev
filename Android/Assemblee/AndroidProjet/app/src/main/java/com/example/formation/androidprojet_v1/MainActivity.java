package com.example.formation.androidprojet_v1;

import android.os.Bundle;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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

/*
final String tpkPath = "/ProjArcades/ArcGIS/arcades.tpk";

String locatorPath = "/ProjArcades/ArcGIS/Geocoding/MGRS.loc";
String networkPath = "/ProjArcades/ArcGIS/Routing/base_de_donnees.geodatabase";
String networkName = "GRAPH_Final_ND";
*/


public class MainActivity extends Activity {

    /**
     * Déclaration des variables globales :
     */

    // Define ArcGIS Elements
    private MapView mMapView;
    private final String extern = Environment.getExternalStorageDirectory().getPath();
    private final String tpkPath = "/ProjArcades/ArcGIS/arcades.tpk";

    private TiledLayer mTileLayer = new ArcGISLocalTiledLayer(extern + tpkPath);
    private GraphicsLayer mGraphicsLayer = new GraphicsLayer(RenderingMode.DYNAMIC);

    private RouteTask mRouteTask = null;
    private NAFeaturesAsFeature mStops = new NAFeaturesAsFeature();

    private Locator mLocator = null;
    private Spinner dSpinner;

    // Variables utiles pour la gestion des géométries;
    private Geometry projection_niv0 = null;
    private Geometry projection_niv1 = null;
    private Geometry projection_niv2 = null;
    private Geometry geom_niveau0 = null;
    private Geometry geom_niveau1 = null;
    private Geometry geom_niveau2 = null;
    private Geometry geometries_niveau0 = null;
    private Geometry geometries_niveau1_1 = null;
    private Geometry geometries_niveau1_2 = null;
    private Geometry geometries_niveau2_1 = null;
    private Geometry geometries_niveau2_2 = null;
    private Geometry geometries_niveau1_all = null;
    private Geometry geometries_niveau2_all = null;

    private GeometryEngine geomen = new GeometryEngine();

    private GraphicsLayer mGraphicsLayer2 = new GraphicsLayer(RenderingMode.DYNAMIC);
    //private SpatialReference WKID_WGS84 = SpatialReference.create(4326);
    //private SpatialReference WKID_WGS84_WEB_MERCATOR = SpatialReference.create(102113);
    //private SpatialReference WKID_WGS84_WEB_MERCATOR_AUXILIARY_SPHERE = SpatialReference.create(102100);
    private SpatialReference WKID_RGF93 = SpatialReference.create(102110);


    // Variables utiles à la récupérations des arcs :
    private Geometry[] array_geom_niv0 = new Geometry[127];
    private Geometry[] array_geom_niv1_1 = new Geometry[380];
    private Geometry[] array_geom_niv1_2 = new Geometry[377];
    private Geometry[] array_geom_niv2_1 = new Geometry[380];
    private Geometry[] array_geom_niv2_2 = new Geometry[400];
    private Geometry[] array_geom_niv1_all = new Geometry[2];
    private Geometry[] array_geom_niv2_all = new Geometry[2];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File tpk = new File(extern + tpkPath);
        Log.d("RoutingAndGeocoding", "Find tpk: " + tpk.exists());
        Log.d("RoutingAndGeocoding", "Initialized tpk: " + mTileLayer.isInitialized());

        // Find the directions spinner
        dSpinner = (Spinner) findViewById(R.id.directionsSpinner);
        dSpinner.setEnabled(false);

        // Retrieve the map and initial extent from XML layout
        mMapView = (MapView) findViewById(R.id.map);

        // Set the tiled map service layer and add a graphics layer
        mMapView.addLayer(mTileLayer);
        mMapView.addLayer(mGraphicsLayer);

        // Initialize the RouteTask and Locator with the local data
        initializeRoutingAndGeocoding();
        mMapView.setOnTouchListener(new TouchListener(MainActivity.this, mMapView));
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
                String locatorPath = "/ProjArcades/ArcGIS/Geocoding/MGRS.loc";
                String networkPath = "/ProjArcades/ArcGIS/Routing/base_de_donnees.geodatabase";
                String networkName = "GRAPH_Final_ND";

                // Attempt to load the local geocoding and routing data
                try {
                    mLocator = Locator.createLocalLocator(extern + locatorPath);
                    mRouteTask = RouteTask.createLocalRouteTask(extern + networkPath, networkName);

                    // open a local geodatabase
                    Geodatabase gdb = new Geodatabase(extern + networkPath);

                    List lst_types_niveau0 = new Vector();
                    List lst_mag_niveau0 = new Vector();
                    List lst_types_niveau1 = new Vector();
                    List lst_mag_niveau1 = new Vector();
                    List lst_types_niveau2 = new Vector();
                    List lst_mag_niveau2 = new Vector();

                    for(int i=0; i<=2; i++){
                        long nbr_lignes = gdb.getGeodatabaseTables().get(i).getNumberOfFeatures();
                        for(int l=1; l<=nbr_lignes; l++){
                            Map<String, Object> lignes = gdb.getGeodatabaseTables().get(i).getFeature(l).getAttributes();
                            Object type = lignes.get("TYPE");
                            Object nom_mag = lignes.get("NOM");
                            if (i == 0) {
                                lst_types_niveau0.add(type);
                                lst_mag_niveau0.add(nom_mag);
                            }
                            else if (i == 1) {
                                lst_mag_niveau1.add(nom_mag);
                                lst_types_niveau1.add(type);
                            }
                            else if (i == 2) {
                                lst_mag_niveau2.add(nom_mag);
                                lst_types_niveau2.add(type);
                            }
                        }
                    }

                    ////////////////////////////////////////////////////////////////////////////////

                    // Définition du nombre d'arcs :
                    int i = array_geom_niv0.length;
                    int l1 = array_geom_niv1_1.length;
                    int l2 = array_geom_niv1_1.length;
                    int m1 = array_geom_niv2_1.length;
                    int m2 = array_geom_niv2_1.length;

                    GeodatabaseFeatureTable tab_niv0 = gdb.getGeodatabaseTables().get(11);
                    GeodatabaseFeatureTable tab_niv1 = gdb.getGeodatabaseTables().get(12);
                    GeodatabaseFeatureTable tab_niv2 = gdb.getGeodatabaseTables().get(13);

                    Geometry poubelle = new Polyline(); // Varaible utile si pas d'objet dans la base

                    // Récupération des arcs dans la géodatabase :
                    for(int j=1; j<=i; j++){
                        if (tab_niv0.checkFeatureExists(j)) {

                            geom_niveau0 = tab_niv0.getFeature(j).getGeometry();
                            array_geom_niv0[j-1] = geom_niveau0;
                        }
                        else {array_geom_niv0[j-1] = poubelle;}
                    }

                    for(int j=1; j<=l1; j++){
                        if (tab_niv1.checkFeatureExists(j)) {
                            geom_niveau1 = tab_niv1.getFeature(j).getGeometry();
                            array_geom_niv1_1[j-1] = geom_niveau1;
                        }
                        else {array_geom_niv1_1[j-1] = poubelle;}
                    }

                    int k1 = 0;
                    for(int j=(l1+1); j<=l2; j++){
                        if (tab_niv1.checkFeatureExists(j)) {
                            geom_niveau1 = tab_niv1.getFeature(j).getGeometry();
                            array_geom_niv1_2[k1] = geom_niveau1;

                            k1 = k1+1;
                        }
                        else {
                            array_geom_niv1_1[i] = poubelle;
                            k1 = k1+1;
                        }
                    }

                    for(int j=1; j<=m1; j++){
                        if (tab_niv2.checkFeatureExists(j)) {
                            geom_niveau2 = tab_niv2.getFeature(j).getGeometry();
                            array_geom_niv2_1[j-1] = geom_niveau2;
                        }
                        else {array_geom_niv2_1[j-1] = poubelle;}
                    }

                    int k2 = 0;
                    for(int j=(m1+1); j<=m2; j++){
                        if (tab_niv2.checkFeatureExists(j)) {
                            geom_niveau2 = tab_niv2.getFeature(j).getGeometry();
                            array_geom_niv2_2[k2] = geom_niveau2;

                            k2 = k2+1;
                        }
                        else {
                            array_geom_niv2_2[i] = poubelle;
                            k2 = k2+1;
                        }
                    }

                    ////////////////////////////////////////////////////////////////////////////////

                    // Union des arcs :
                    geometries_niveau0 = geomen.union(array_geom_niv0, WKID_RGF93);

                    geometries_niveau1_1 = geomen.union(array_geom_niv1_1, WKID_RGF93);
                    geometries_niveau1_2 = geomen.union(array_geom_niv1_2, WKID_RGF93);
                    array_geom_niv1_all[0] = geometries_niveau1_1;
                    array_geom_niv1_all[1] = geometries_niveau1_2;
                    geometries_niveau1_all = geomen.union(array_geom_niv1_all, WKID_RGF93);


                    geometries_niveau2_1 = geomen.union(array_geom_niv2_1, WKID_RGF93);
                    geometries_niveau2_2 = geomen.union(array_geom_niv2_2, WKID_RGF93);
                    array_geom_niv2_all[0] = geometries_niveau2_1;
                    array_geom_niv2_all[1] = geometries_niveau2_2;
                    geometries_niveau2_all = geomen.union(array_geom_niv2_all, WKID_RGF93);

                    Log.d("geometries_niveau0", "" + geometries_niveau0.calculateLength2D());
                    Log.d("geometries_niveau1", "" + geometries_niveau1_all.calculateLength2D());
                    Log.d("geometries_niveau2", "" + geometries_niveau2_all.calculateLength2D());

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
                RouteParameters params = mRouteTask.retrieveDefaultRouteTaskParameters();
                SpatialReference mapRef = mMapView.getSpatialReference();
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

                ////////////////////////////////////////////////////////////////////////////////////

                // On projete les arcs en RGF93 :

                //geometries_niveau0 = geomen.union(array_geom_niv0, WKID_RGF93);
                projection_niv0 = geomen.project(geometries_niveau0, WKID_RGF93, mapRef);

                //geometries_niveau1 = geomen.union(array_geom_niv1, WKID_RGF93);
                projection_niv1 = geomen.project(geometries_niveau1_all, WKID_RGF93, mapRef);

                //geometries_niveau2 = geomen.union(array_geom_niv2, WKID_RGF93);
                projection_niv2 = geomen.project(geometries_niveau2_all, WKID_RGF93, mapRef);

                ////////////////////////////////////////////////////////////////////////////////////

                // On intersecte l'itinéraire avec les arcs :

                // Add the route shape to the graphics layer
                Geometry geom = result.getRouteGraphic().getGeometry();

                Geometry geom_intersect_niv0 = geomen.intersect(geom, projection_niv0, mapRef);
                Geometry geom_intersect_niv1 = geomen.intersect(geom, projection_niv1, mapRef);
                Geometry geom_intersect_niv2 = geomen.intersect(geom, projection_niv2, mapRef);

                ////////////////////////////////////////////////////////////////////////////////////

                // On ne visulalise que l'itinéraire au niveau selectionné :

                /*
                if (!geom_intersect_niv0.isEmpty()) {
                    routeHandle = mGraphicsLayer.addGraphic(new Graphic(geom_intersect_niv0, new SimpleLineSymbol(0x99990055, 5)));
                    Log.d("geom_inter", ": " + geom_intersect_niv0.getType());
                    Log.d("geom_inter_length",": " + geom_intersect_niv0.calculateLength2D());
                }

                if (!geom_intersect_niv1.isEmpty()) {
                    routeHandle = mGraphicsLayer.addGraphic(new Graphic(geom_intersect_niv1, new SimpleLineSymbol(0x99990055, 5)));
                    Log.d("geom_inter", ": " + geom_intersect_niv1.getType());
                    Log.d("geom_inter_length",": " + geom_intersect_niv1.calculateLength2D());
                }
                */
                if (!geom_intersect_niv2.isEmpty()) {
                    routeHandle = mGraphicsLayer.addGraphic(new Graphic(geom_intersect_niv2, new SimpleLineSymbol(0x99990055, 5)));
                    Log.d("geom_inter", ": " + geom_intersect_niv2.getType());
                    Log.d("geom_inter_length",": " + geom_intersect_niv2.calculateLength2D());
                }

                mMapView.getCallout().hide();

                // Get the list of directions from the result
                List<RouteDirection> directions = result.getRoutingDirections();


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


}
