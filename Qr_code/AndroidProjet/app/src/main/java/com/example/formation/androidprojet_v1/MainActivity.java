package com.example.formation.androidprojet_v1;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
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
//QR code
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

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


// on importe les classes IntentIntegrator et IntentResult de la librairie zxing
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends Activity implements OnItemSelectedListener, View.OnClickListener {

    // Define ArcGIS Elements
    MapView mMapView; //init carte
    ArcGISLocalTiledLayer mTileLayer; //init couche à ajouter
    String extern = Environment.getExternalStorageDirectory().getPath(); //initialisation de l'environement de travail
    String tpkPath = ""; //initialisation chemin tpkPath
    GraphicsLayer mGraphicsLayer = new GraphicsLayer(RenderingMode.DYNAMIC);

    RouteTask mRouteTask = null;
    NAFeaturesAsFeature mStops = new NAFeaturesAsFeature();

    Locator mLocator = null;
    Spinner dSpinner;

    //QR_code
    Geometry geom_QR_code = null;
    Geometry projection = null;
    GeometryEngine geomen = new GeometryEngine();
    SpatialReference WKID_RGF93 = SpatialReference.create(102110);
    //SpatialReference mapRef = mMapView.getSpatialReference();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Spinner element pour changement etage
        Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        // Spinner click listener pour changement etage
        spinner.setOnItemSelectedListener(this);

        //initialisation du tpk lors du demarrage de l'appplication
        tpkPath = "/ProjArcades/ArcGIS/arcades.tpk"; // graphe général
        // create the local tpk
        mTileLayer = new ArcGISLocalTiledLayer(extern + tpkPath);

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


        // QR code
        Button mybutton = (Button) findViewById(R.id.scan_button);
        mybutton.setOnClickListener(this);

    }



    private void initializeRoutingAndGeocoding() {

        // We will spin off the initialization in a new thread
        new Thread(new Runnable() {

            @Override
            public void run() {
                // Get the external directory
                //Herve String locatorPath = "/ArcGIS/samples/OfflineRouting/Geocoding/SanDiego_StreetAddress.loc";
                //String locatorPath = "/Herve/ArcGIS/Geocoding/MGRS.loc";
                String locatorPath = "/ProjArcades/ArcGIS/Geocoding/MGRS.loc";

                //Hervé String networkPath = "/ArcGIS/samples/OfflineRouting/Routing/RuntimeSanDiego.geodatabase";
                //String networkPath = "/Herve/ArcGIS/Routing/pays_forca.geodatabase";
                String networkPath = "/ProjArcades/ArcGIS/Routing/base_de_donnees.geodatabase";

                //Hervé String networkName = "Streets_ND";
                //String networkName = "jdce_ND";
                String networkName = "GRAPH_Final_ND";

                // Attempt to load the local geocoding and routing data
                try {
                    mLocator = Locator.createLocalLocator(extern + locatorPath);
                    mRouteTask = RouteTask.createLocalRouteTask(extern + networkPath, networkName);



                    // open a local geodatabase
                    //Geodatabase gdb = new Geodatabase(extern + networkPath);
                    Geodatabase gdb = new Geodatabase(extern + networkPath);

                    //on prend un point connu pour tester QR code, en utilisant un magasin existant
                    //ensuite on integrera directement les QR code dans la geodatabase
                    // il sera judicieux de l'integrer par Android pour éviter à l'utilisateur programmeur de recréer la geodatabase

                    // magasin test : La grande recre
                    geom_QR_code = gdb.getGeodatabaseTables().get(0).getFeature(1).getGeometry(); //point_joint0 : get(0) , ligne 1 de point_joint0 : getFeature(1)




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

                //QR code
                projection = geomen.project(geom_QR_code, WKID_RGF93, mapRef);
                //routeHandle = mGraphicsLayer.addGraphic(new Graphic(projection, new SimpleMarkerSymbol(Color.BLUE, 10, STYLE.DIAMOND ) ));
                //mMapView.getCallout().hide();

                // Add the route shape to the graphics layer
                //Geometry geom = result.getRouteGraphic().getGeometry();
                //routeHandle = mGraphicsLayer.addGraphic(new Graphic(geom, new SimpleLineSymbol(0x99990055, 5)));
                //mMapView.getCallout().hide();

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



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();

        //on recupere les noms des etages qui sont stockés dans ressources.strings.values
        String[] nom_etage = getResources().getStringArray(R.array.etage_array);

        // test suivant la selection de l'utilisateur
        Log.d("test",item);
        mMapView.removeLayer(mTileLayer);
        if(item.equals( nom_etage[0] ) )
        {
            tpkPath = "/ProjArcades/ArcGIS/niveau_0_v2.tpk";
            Log.d("etage","la!!! 0 ");
            Log.d("nom_etage0", nom_etage[0]);
        }
        if(item.equals( nom_etage[1] ))
        {
            tpkPath = "/ProjArcades/ArcGIS/niveau_1.tpk";
            Log.d("etage","la!!! 1");
            Log.d("nom_etage1", nom_etage[1]);
        }
        if(item.equals( nom_etage[2]))
        {
            tpkPath = "/ProjArcades/ArcGIS/niveau_2.tpk";
            Log.d("etage","la!!! 2");
            Log.d("nom_etage2", nom_etage[2]);
        }
        if(item.equals( nom_etage[3]))
        {
            tpkPath = "/ProjArcades/ArcGIS/arcades.tpk";
            Log.d("etage","la!!! complet");
            Log.d("nom_etage3", nom_etage[3]);
        }

        // create the local tpk
        mTileLayer = new ArcGISLocalTiledLayer(extern + tpkPath);
        //on ajoute sur la carte la couche selectionnée
        mMapView.addLayer(mTileLayer);
        mMapView.addLayer(mGraphicsLayer);
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    //QR code
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.scan_button){
            // on lance le scanner au clic sur notre bouton
            new IntentIntegrator(this).initiateScan();
        }
    }

    //QR code
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        // nous utilisons la classe IntentIntegrator et sa fonction parseActivityResult pour parser le résultat du scan
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {

            // nous récupérons le contenu du code barre
            String scanContent = scanningResult.getContents();

            // nous récupérons le format du code barre
            String scanFormat = scanningResult.getFormatName();

            TextView scan_format = (TextView) findViewById(R.id.scan_format);
            TextView scan_content = (TextView) findViewById(R.id.scan_content);

            // nous affichons le résultat dans nos TextView
            scan_format.setText("FORMAT: " + scanFormat);
            scan_content.setText("CONTENT: " + scanContent);
            Log.d("toto", scanContent);

            //test sur le different QR code scanné
            //on utilise ces tests pour définir le point de départ ou des points intermédiaires par exemple
            if(scanContent.equals( "QR code 01" ) )
            {
                Log.d("QR_code","QR code 01");
                // on marque la geometrie du QR code sur la carte
                // rappel,on test avec le magasin "La grande recre"
                mGraphicsLayer.addGraphic(new Graphic(projection, new SimpleMarkerSymbol(Color.RED, 10, STYLE.CROSS ) ));
                mMapView.getCallout().hide();



            }
            if(scanContent.equals( "QR code 02" ) )
            {
                Log.d("QR_code","QR code 02");
            }
            if(scanContent.equals( "QR code 03" ) )
            {
                Log.d("QR_code","QR code 03");
            }


        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Aucune donnée reçu!", Toast.LENGTH_SHORT);
            toast.show();
        }



    }





}
