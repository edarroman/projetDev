## Pour envoyer les données :

ArrayList<Double> all = mEnregistrementDataAccessObject.getLatLng();

 /*
for(Double j:all){
    Log.d("LatLng",j.toString());
}
*/

// Ouverture de la Gmap (lancement de MapACtivity)
Intent i = new Intent(getApplicationContext(), MapsActivity.class);
i.putExtra("LatLng",all); // Pour zfficher les marqueurs sur la carte

startActivity(i);


## Pour récupérer les données :

Intent i = getIntent();
ArrayList<Integer> latlng = i.getIntegerArrayListExtra("LAT_LNG");