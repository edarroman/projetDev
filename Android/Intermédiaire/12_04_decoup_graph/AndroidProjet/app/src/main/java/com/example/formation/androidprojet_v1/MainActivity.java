package com.example.formation.androidprojet_v1;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
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
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class MainActivity extends Activity {

    // Define ArcGIS Elements
    MapView mMapView;
    final String extern = Environment.getExternalStorageDirectory().getPath();
    //Hervé final String tpkPath = "/ArcGIS/samples/OfflineRouting/SanDiego.tpk";
    //final String tpkPath = "/Herve/ArcGIS/test_Forca.tpk";
    final String tpkPath = "/Android/data/com.example.formation.androidprojet_v1/ArcGIS/arcades.tpk";
    //final String tpkPath = "/projArcades/ArcGIS/arcades.tpk";
    TiledLayer mTileLayer = new ArcGISLocalTiledLayer(extern + tpkPath);
    GraphicsLayer mGraphicsLayer = new GraphicsLayer(RenderingMode.DYNAMIC);

    RouteTask mRouteTask = null;
    NAFeaturesAsFeature mStops = new NAFeaturesAsFeature();

    Locator mLocator = null;
    Spinner dSpinner;

    //Geometry lignes = null;
    Geometry projection_niv0 = null;
    Geometry projection_niv1 = null;
    Geometry projection_niv2 = null;
    Geometry geom_niveau0 = null;
    Geometry geom_niveau1 = null;
    Geometry geom_niveau2 = null;
    Geometry geometries_niveau0 = null;
    Geometry geometries_niveau1 = null;
    Geometry geometries_niveau2 = null;

    GeometryEngine geomen = new GeometryEngine();

    GraphicsLayer mGraphicsLayer2 = new GraphicsLayer(RenderingMode.DYNAMIC);
    //SpatialReference WKID_WGS84 = SpatialReference.create(4326);
    //SpatialReference WKID_WGS84_WEB_MERCATOR = SpatialReference.create(102113);
    //SpatialReference WKID_WGS84_WEB_MERCATOR_AUXILIARY_SPHERE = SpatialReference.create(102100);
    SpatialReference WKID_RGF93 = SpatialReference.create(102110);

    Geometry[] array_geom_niv0 = new Geometry[127];
    Geometry[] array_geom_niv1 = new Geometry[757];
    Geometry[] array_geom_niv2 = new Geometry[780];


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

    private void initializeRoutingAndGeocoding() {

        // We will spin off the initialization in a new thread
        new Thread(new Runnable() {

            @Override
            public void run() {
                // Get the external directory
                //Herve String locatorPath = "/ArcGIS/samples/OfflineRouting/Geocoding/SanDiego_StreetAddress.loc";
                //String locatorPath = "/Herve/ArcGIS/Geocoding/MGRS.loc";
                String locatorPath = "/Android/data/com.example.formation.androidprojet_v1/ArcGIS/Geocoding/MGRS_v3.loc";
                //Hervé String networkPath = "/ArcGIS/samples/OfflineRouting/Routing/RuntimeSanDiego.geodatabase";
                //String networkPath = "/Herve/ArcGIS/Routing/pays_forca.geodatabase";
                String networkPath = "/Android/data/com.example.formation.androidprojet_v1/ArcGIS/Routing/base_de_donnees_v3.geodatabase";
                //Hervé String networkName = "Streets_ND";
                //String networkName = "jdce_ND";
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
                    /*
                    Log.d("test_niveau0", "lst_types_niveau0 :" + lst_types_niveau0);
                    Log.d("test2_niveau0", "lst_mag_niveau0 :" + lst_mag_niveau0);
                    Log.d("test_niveau1", "lst_types_niveau1 :" + lst_types_niveau1);
                    Log.d("test2_niveau1", "lst_mag_niveau1 :" + lst_mag_niveau1);
                    Log.d("test_niveau2", "lst_types_niveau2 :" + lst_types_niveau2);
                    Log.d("test2_niveau2", "lst_mag_niveau2 :" + lst_mag_niveau2);
*/
                    //GeodatabaseFeatureTable tables = gdb.getGeodatabaseTables().get(11);
                    //GeodatabaseFeatureTable lignes = gdb.getGeodatabaseTables().get(13);//.getFeature(36).getGeometry();
                    //Log.d("geom_lig", ": " + lignes);
                    ////////////
                    //int nbr_features = new Long(gdb.getGeodatabaseTables().get(11).getNumberOfFeatures()).intValue();
                    //Geometry[] array_geom = new Geometry[nbr_features];
                    int i = array_geom_niv0.length;
                    int l = array_geom_niv1.length;
                    int m = array_geom_niv2.length;

                    for(int j=1; j<=i; j++){
                        if (gdb.getGeodatabaseTables().get(11).checkFeatureExists(j)) {
                            geom_niveau0 = gdb.getGeodatabaseTables().get(11).getFeature(j).getGeometry();
                            array_geom_niv0[j-1] = geom_niveau0;
                        }
                        else {
                            array_geom_niv0[j-1] = null;
                        }
                    }

                    for(int j=1; j<=l; j++){
                        if (gdb.getGeodatabaseTables().get(12).checkFeatureExists(j)) {
                            geom_niveau1 = gdb.getGeodatabaseTables().get(12).getFeature(j).getGeometry();
                            array_geom_niv1[j-1] = geom_niveau1;
                        }
                        else {
                            array_geom_niv1[j-1] = null;
                        }
                    }

                    for(int j=1; j<=m; j++){
                        if (gdb.getGeodatabaseTables().get(13).checkFeatureExists(j)) {
                            geom_niveau2 = gdb.getGeodatabaseTables().get(13).getFeature(j).getGeometry();
                            array_geom_niv2[j-1] = geom_niveau0;
                        }
                        else {
                            array_geom_niv2[j-1] = null;
                        }
                    }

                    ///////////
                    /*
                    Geometry ligne1 = gdb.getGeodatabaseTables().get(11).getFeature(36).getGeometry();
                    Geometry ligne2 = null;
                    Log.d("ok", "ok");
                    if (gdb.getGeodatabaseTables().get(11).checkFeatureExists(104)) {
                        ligne2 = gdb.getGeodatabaseTables().get(11).getFeature(104).getGeometry();
                        Log.d("toto5", "liste :" + ligne2);
                    }
                    Geometry[] array_geom = new Geometry[2];
                    array_geom[0] = ligne1;
                    array_geom[1] = ligne2;
                    lignes = geomen.union(array_geom,WKID_RGF93);*/
                    /*long nbr_features = gdb.getGeodatabaseTables().get(11).getNumberOfFeatures();
                    Geometry[] array_geom = new Geometry[127];
                    for(int j=1; j<=nbr_features; j++){
                        geom_niveau0 = gdb.getGeodatabaseTables().get(11).getFeature(j).getGeometry();
                        projection = geomen.project(geom_niveau0, WKID_RGF93, mapRef);
                        array_geom[j-1] = projection;
                    }
                    geometries_niveau0 = geomen.union(array_geom, mapRef);*/
                    // first table
                    //Boolean point = table.hasGeometry();
                    //Log.d("toto5", "liste :" + lignes);
                    //Log.d("geom_lig", ": " + lignes.getType());
                    //Log.d("geom_lig_length", ": " + lignes.calculateLength2D());


/*
                    // get the first table in the database
                    GeodatabaseFeatureTable table0 = gdb.getGeodatabaseFeatureTableByLayerId(1);
                    //get the fields of the table
                    List fields = table0.getFields();
                    Log.d("toto6", "liste :" + fields);

                    // get elements where TYPE == Services
                    Object colonne_type = fields.get(3);
                    Log.d("toto7", "liste :" + colonne_type);
*/

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

                //////////////////////////////

                geometries_niveau0 = geomen.union(array_geom_niv0, WKID_RGF93);
                projection_niv0 = geomen.project(geometries_niveau0, WKID_RGF93, mapRef);

                geometries_niveau1 = geomen.union(array_geom_niv1, WKID_RGF93);
                projection_niv1 = geomen.project(geometries_niveau1, WKID_RGF93, mapRef);

                geometries_niveau2 = geomen.union(array_geom_niv2, WKID_RGF93);
                projection_niv2 = geomen.project(geometries_niveau2, WKID_RGF93, mapRef);

                /////////////////////////////

                // Add the route shape to the graphics layer
                Geometry geom = result.getRouteGraphic().getGeometry();
                //projection = geomen.project(lignes, WKID_RGF93, mapRef);
                Geometry geom_intersect_niv0 = geomen.intersect(geom, projection_niv0, mapRef);
                Geometry geom_intersect_niv1 = geomen.intersect(geom, projection_niv1, mapRef);
                Geometry geom_intersect_niv2 = geomen.intersect(geom, projection_niv2, mapRef);

                //routeHandle = mGraphicsLayer.addGraphic(new Graphic(lignes, new SimpleLineSymbol(0x99990055, 5)));
                Log.d("geomEpt",": " + geom.isEmpty());
                //Log.d("geom",": " + geom.getType());
                //Log.d("geom_length",": " + geom.calculateLength2D());


                //routeHandle = mGraphicsLayer.addGraphic(new Graphic(geom, new SimpleLineSymbol(0x99990055, 5)));
                if (!geom_intersect_niv0.isEmpty()) {
                    routeHandle = mGraphicsLayer.addGraphic(new Graphic(geom_intersect_niv0, new SimpleLineSymbol(0x99990055, 5)));
                    Log.d("geom_inter", ": " + geom_intersect_niv0.getType());
                    Log.d("geom_inter_length",": " + geom_intersect_niv0.calculateLength2D());
                }
                /*
                if (!geom_intersect_niv1.isEmpty()) {
                    routeHandle = mGraphicsLayer.addGraphic(new Graphic(geom_intersect_niv1, new SimpleLineSymbol(0x99990055, 5)));
                    Log.d("geom_inter", ": " + geom_intersect_niv1.getType());
                    Log.d("geom_inter_length",": " + geom_intersect_niv1.calculateLength2D());
                }
                if (!geom_intersect_niv2.isEmpty()) {
                    routeHandle = mGraphicsLayer.addGraphic(new Graphic(geom_intersect_niv2, new SimpleLineSymbol(0x99990055, 5)));
                    Log.d("geom_inter", ": " + geom_intersect_niv2.getType());
                    Log.d("geom_inter_length",": " + geom_intersect_niv2.calculateLength2D());
                }
                */

                mMapView.getCallout().hide();

                // Get the list of directions from the result
                List<RouteDirection> directions = result.getRoutingDirections();

                // enable spinner to receive directions
                dSpinner.setEnabled(true);

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

                // Create a simple array adapter to visualize the directions in
                // the Spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_spinner_item, formattedDirections);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dSpinner.setAdapter(adapter);

                // Add a custom OnItemSelectedListener to the spinner to allow
                // panning to each directions item.
                dSpinner.setOnItemSelectedListener(new DirectionsItemListener(directions));

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

    class DirectionsItemListener implements OnItemSelectedListener {

        private List<RouteDirection> mDirections;

        public DirectionsItemListener(List<RouteDirection> directions) {
            mDirections = directions;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            // We have to account for the added summary String
            if (mDirections != null && pos > 0 && pos <= mDirections.size())
                mMapView.setExtent(mDirections.get(pos - 1).getGeometry());
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
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
