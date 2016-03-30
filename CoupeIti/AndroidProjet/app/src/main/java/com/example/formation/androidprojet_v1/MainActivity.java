package com.example.formation.androidprojet_v1;

import android.os.Bundle;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

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
import com.esri.core.geometry.Geometry;
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

public class MainActivity extends Activity {

    // Define ArcGIS Elements
    MapView mMapView;
    final String extern = Environment.getExternalStorageDirectory().getPath();
    final String tpkPath = "/ProjArcades/ArcGIS/arcades.tpk";
    TiledLayer mTileLayer = new ArcGISLocalTiledLayer(extern + tpkPath);
    GraphicsLayer mGraphicsLayer = new GraphicsLayer(RenderingMode.DYNAMIC);

    RouteTask mRouteTask = null;
    NAFeaturesAsFeature mStops = new NAFeaturesAsFeature();

    Locator mLocator = null;
    Spinner dSpinner;

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
                String locatorPath = "/ProjArcades/ArcGIS/Geocoding/MGRS.loc";
                String networkPath = "/ProjArcades/ArcGIS/Routing/base_de_donnees.geodatabase";
                String networkName = "GRAPH_Final_ND";

                // Attempt to load the local geocoding and routing data
                try {
                    mLocator = Locator.createLocalLocator(extern + locatorPath);
                    mRouteTask = RouteTask.createLocalRouteTask(extern + networkPath, networkName);
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
            // Add  graphics to the screen
            addPoint(441, 233);
            addPoint(200, 300);
            addPoint(297, 513);

            // Lorsque l'on touche deux fois l'écran rapidement le calcul d'itinéraire est lancé

            // Return default behavior if we did not initialize properly.
            if (mRouteTask == null) {
                popToast("RouteTask uninitialized.", true);
                return super.onSingleTap(point);
            }

            try {

                // Set the correct input spatial reference on the stops and the
                // desired output spatial reference on the RouteParameters object.
                SpatialReference mapRef = mMapView.getSpatialReference();
                RouteParameters params = mRouteTask.retrieveDefaultRouteTaskParameters();
                params.setOutSpatialReference(mapRef);
                mStops.setSpatialReference(mapRef);

                //Log.d("stops", mStops.toJson());

                // Set the stops and since we want driving directions,
                // returnDirections==true
                params.setStops(mStops);
                params.setReturnDirections(true);

                //Log.d("Params", params.toString());

                // Perform the solve
                RouteResult results = mRouteTask.solve(params);

                //Log.d("results", results.toString());

                // Grab the results; for offline routing, there will only be one
                // result returned on the output.
                Route result = results.getRoutes().get(0);

                //Log.d("result", result.toString());

                // Remove any previous route Graphics
                if (routeHandle != -1)
                    mGraphicsLayer.removeGraphic(routeHandle);

                // Add the route shape to the graphics layer
                Geometry geom = result.getRouteGraphic().getGeometry();
                routeHandle = mGraphicsLayer.addGraphic(new Graphic(geom, new SimpleLineSymbol(0x99990055, 5)));
                mMapView.getCallout().hide();

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

    /** Fonctions : **/

    public boolean addPoint(float X, float Y) {
        // Add a graphic to the screen for the touch event
        Point mapPoint = mMapView.toMapPoint(X, Y);

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
}
