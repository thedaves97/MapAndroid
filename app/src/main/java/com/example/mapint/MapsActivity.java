package com.example.mapint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<Marker> markers = new ArrayList<>();
    int cont = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //LETTURA JSON
        InputStream is = getResources().openRawResource(R.raw.marker);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];

        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            int n;
            while((n = reader.read(buffer)) !=-1)
            {
                writer.write(buffer, 0, n);
            } //FINE WHILE

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String marker_array = writer.toString();


        //FETCH JSON
        try {

            JSONObject json = new JSONObject(marker_array);
            JSONArray jArray =  json.getJSONArray("markers");
            for (int i = 0;i<jArray.length();i++)
            {
                JSONObject obj = jArray.getJSONObject(i);

                Marker m = new Marker("name", "type", "address", 0.0, 0.1);
                m.setName(obj.getString("name"));
                m.setType(obj.getString("type"));
                m.setAddress(obj.getString("address"));
                m.setLat(obj.getDouble("lat"));
                m.setLon(obj.getDouble("lon"));
                markers.add(m);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        String name, type, add;
        LatLng pos;
        double lat;
        double lon;


        //CREAZIONE MARKER
        for (Marker m: markers)
        {
            lat = m.getLat();
            lon = m.getLon();
            int path = R.drawable.ic_cocktail;         //INIZIALIZZAZIONE PER NON AVERE ERRORI A RUNTIME
            type = m.getType();
            add = m.getAddress();

            if(mMap!=null)
            {
                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(com.google.android.gms.maps.model.Marker marker) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(com.google.android.gms.maps.model.Marker marker) {

                        View row = getLayoutInflater().inflate(R.layout.custom_infowindow, null);

                        //PRENDIAMO DAL LAYOUT LA TEXT VIEW CON ID ADDRESS
                        TextView name = (TextView) row.findViewById(R.id.name);
                        //PRENDIAMO DAL LAYOUT LA TEXT VIEW CON ID ADDRESS
                        TextView address = (TextView) row.findViewById(R.id.address);
                        //PRENDIAMO DAL LAYOUT LA TEXT VIEW CON ID CROWDING
                        TextView crowding = (TextView) row.findViewById(R.id.crowding);

                        //ASSEGNAZIONE TESTO DA VISUALIZZARE
                        name.setText("Name: " + markers.get(cont).getName());
                        address.setText("Address: " + markers.get(cont).getAddress());
                        crowding.setText("Crowding: High/Medium/Low");

                        cont++;

                        return row;

                    }  //FINE getInfoContent

                }); //FINE setInfoWindowAdapter

            }  //FINE IF

            switch (type)
            {
                case "Pub":
                    path = R.drawable.ic_beer;
                    break;
                case "Cocktail bar":
                    path = R.drawable.ic_cocktail;
                    break;
                case "Wine Bar":
                    path = R.drawable.ic_wine;
                    break;
            }

            pos = new LatLng(lat, lon);
            //mMap.addMarker(new MarkerOptions().position(pos).title("Name: " + m.getName() + "\nType: " + m.getType() + "\nAddress: " + m.getAddress()));
            mMap.addMarker(new MarkerOptions().position(pos).title(m.getName())
                    .icon(bitmapDescriptorFromVector(getApplicationContext(), path)));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(25.0f));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));

        } //FINE FOR CREAZIONE MARKER

    } //FINE OnMapReady

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId)
    {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0,0, vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);

    }

}