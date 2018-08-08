package tasu.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Dialog;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;


public class MapsActivity extends FragmentActivity implements LocationListener{

    GoogleMap mGoogleMap;
    Spinner mSprPlaceType;

    String[] mPlaceType=null;
    String[] mPlaceTypeName=null;

    double mLatitude=0;
    double mLongitude=0;

    /*
    public void showMenuDrawer(View v) {
        Intent intent2 = new Intent(getApplicationContext(), DrawerActivity.class);
        startActivity(intent2);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mPlaceType = getResources().getStringArray(R.array.place_type);
        mPlaceTypeName = getResources().getStringArray(R.array.place_type_name);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, mPlaceTypeName);
        mSprPlaceType = (Spinner) findViewById(R.id.spr_place_type);
        mSprPlaceType.setAdapter(adapter);

        Button btnMenu;
        btnMenu = (Button)findViewById(R.id.btn_menu_drawer);
        btnMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), DrawerActivity.class);
                startActivity(i);
            }
        });


        Button btnFind;
        btnFind = ( Button ) findViewById(R.id.btn_find);

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        if(status!=ConnectionResult.SUCCESS){

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        }else {

            SupportMapFragment fragment = ( SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            mGoogleMap = fragment.getMap();

            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);

            // Getting Current Location From GPS
            Location location = locationManager.getLastKnownLocation(provider);

            if(location!=null){
                onLocationChanged(location);
            }

            locationManager.requestLocationUpdates(provider, 20000, 0, this);

            btnFind.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {


                    int selectedPosition = mSprPlaceType.getSelectedItemPosition();
                    String type = mPlaceType[selectedPosition];


                    final SharedPreferences mSharedPreference = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

                    String range = (mSharedPreference.getString("range", "5000"));
                    ;

                    if (range == null) range = "5000";
                    String data = "Distance: " + range + "\nPlaces: ";
                    if (mSharedPreference.getBoolean("c1", false) == true) data += "Doctor ";
                    if (mSharedPreference.getBoolean("c2", false) == true) data += "Hospital ";
                    if (mSharedPreference.getBoolean("c3", false) == true) data += "Restaurant ";
                    if (mSharedPreference.getBoolean("c4", false) == true) data += "Museum ";
                    if (mSharedPreference.getBoolean("c5", false) == true) data += "Stadium ";
                    if (mSharedPreference.getBoolean("c6", false) == true) data += "Library ";
                    if (mSharedPreference.getBoolean("c7", false) == true) data += "Zoo";

                    Toast tasu = Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG);
                    tasu.show();


                    StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                    sb.append("location=" + mLatitude + "," + mLongitude);
                    sb.append("&radius=" + range);
                    //sb.append("&types="+type);
                    sb.append("&types=");

                    if (data.contains("Doctor")) sb.append("|doctor");
                    if (data.contains("Hospital")) sb.append("|hospital");
                    if (data.contains("Restaurant")) sb.append("|restaurant");
                    if (data.contains("Museum")) sb.append("|museum");
                    if (data.contains("Stadium")) sb.append("|stadium");
                    if (data.contains("Library")) sb.append("|library");
                    if (data.contains("Zoo")) sb.append("|zoo");

                    sb.append("&sensor=true");
                    sb.append("&key=AIzaSyDwq3T-OK9nXByWLgkewAQnK3EeIkG6D7Q\n");

                    // Creating a new non-ui thread task to download json data
                    PlacesTask placesTask = new PlacesTask();

                    // Invokes the "doInBackground()" method of the class PlaceTask
                    placesTask.execute(sb.toString());

                }
            });

            mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    String name = marker.getTitle();
                    String vicinity = marker.getSnippet();
                    LatLng position = marker.getPosition();

                    /*
                    Toast.makeText(getApplicationContext(),
                            name + " \n" + vicinity + " \n" + position, Toast.LENGTH_SHORT)
                            .show();
                    */

                    Intent info = new Intent(getApplicationContext(), InfoActivity.class);
                    info.putExtra("name", name);
                    info.putExtra("vicinity", vicinity);
                    info.putExtra("latitude", position.latitude);
                    info.putExtra("longitude", position.longitude);
                    startActivity(info);
                }
            });

            //mGoogleMap.getUiSettings().setCompassEnabled(true);
            mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
            mGoogleMap.setMyLocationEnabled(true);

            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(12));

        }

    }


    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("error downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }

        return data;
    }

    /** A class, to download Google Places */
    private class PlacesTask extends AsyncTask<String, Integer, String>{

        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try{
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result){
            ParserTask parserTask = new ParserTask();

            // Start parsing the Google places in JSON format
            // Invokes the "doInBackground()" method of the class ParseTask
            parserTask.execute(result);
        }

    }

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{

        JSONObject jObject;

        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String,String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;
            JSONParser placeJsonParser = new JSONParser();

            try{
                jObject = new JSONObject(jsonData[0]);

                /** Getting the parsed data as a List construct */
                places = placeJsonParser.parse(jObject);

            }catch(Exception e){
                Log.d("Exception",e.toString());
            }
            return places;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String,String>> list){

            // Clears all the existing markers
            mGoogleMap.clear();

            for(int i=0;i<list.size();i++){

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Getting a place from the places list
                HashMap<String, String> hmPlace = list.get(i);

                // Getting latitude of the place
                double lat = Double.parseDouble(hmPlace.get("lat"));

                // Getting longitude of the place
                double lng = Double.parseDouble(hmPlace.get("lng"));

                // Getting name
                String name = hmPlace.get("place_name");

                // Getting vicinity
                String vicinity = hmPlace.get("vicinity");

                LatLng latLng = new LatLng(lat, lng);

                // Setting the position for the marker
                markerOptions.position(latLng);


                // Setting the title for the marker.
                //This will be displayed on taping the marker
                markerOptions.title(name);
                markerOptions.snippet(vicinity);


                String types = hmPlace.get("types");
                if(types.contains("doctor")) markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.doctor));
                else if(types.contains("hospital")) markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.hospital));
                else if(types.contains("restaurant")) markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant));
                else if(types.contains("museum")) markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.museum));
                else if(types.contains("stadium")) markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.stadium));
                else if(types.contains("library")) markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.library));
                else if(types.contains("zoo")) markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.zoo));


                // Placing a marker on the touched position
                mGoogleMap.addMarker(markerOptions);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.activity_maps, menu);
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        LatLng latLng = new LatLng(mLatitude, mLongitude);

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }
}