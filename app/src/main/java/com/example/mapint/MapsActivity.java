package com.example.mapint;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.InfoWindowAdapter, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private static final String EXTRA_TEXT = "com.example.mapint.MapsActivity.EXTRA_TEXT";

    ArrayList<Marker> markers = new ArrayList<>();
    int cont = 0;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        button = (Button) findViewById(R.id.menu_button);
        LinearLayout li = (LinearLayout) findViewById(R.id.bottom);
        li.setBackgroundColor(Color.parseColor("#fbb324"));

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

                Marker m = new Marker(0,"name", "type", "address", 0.0, 0.1);
                m.setId(obj.getInt("id"));
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
        for (int i=0; i<markers.size();i++)
        {
            lat = markers.get(i).getLat();
            lon = markers.get(i).getLon();
            type = markers.get(i).getType();

            pos = new LatLng(lat, lon);
            //mMap.addMarker(new MarkerOptions().position(pos).title("Name: " + m.getName() + "\nType: " + m.getType() + "\nAddress: " + m.getAddress()));
            mMap.addMarker(new MarkerOptions().position(pos).title(markers.get(i).getName())
                    .icon(bitmapDescriptorFromVector(getApplicationContext(), setCustomIcon(type))));

            //SETTING FOCUS TO CITY CENTER
            float zoomLevel = 16.0f;
            LatLng mapCenter;
            //Mole Antonelliana 45.0690113  7.6910275
            //Porta Nuova/San Salvario 45.062055    7.6763373
            //Piazza Vittorio Veneto
            double latCenter = 45.0647992, lngCenter = 7.6930788;
            mapCenter = new LatLng(latCenter, lngCenter);
            mMap.moveCamera((CameraUpdateFactory.newLatLngZoom(mapCenter, zoomLevel)));

            final int finalI1 = i;
            mMap.setOnMarkerClickListener(this);
            mMap.setInfoWindowAdapter(this);
            mMap.setOnMapClickListener(this);

        } //FINE FOR CREAZIONE MARKER

    } //FINE OnMapReady

    @Override
    public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {
        TextView selectedMarkerLocalName = findViewById(R.id.local_name);

        final String localName = marker.getTitle();
        selectedMarkerLocalName.setText(localName);

        TextView selectedMarker = (TextView) findViewById(R.id.local_name);

        selectedMarker.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        selectedMarker.setTextColor(Color.parseColor("#000000"));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMenu(localName);
            }
        });

        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {

        Toast.makeText(this, "Choose a marker on the map", Toast.LENGTH_LONG).show();
    }

    @Override
    public View getInfoWindow(com.google.android.gms.maps.model.Marker marker) {

        String title = marker.getTitle();

        View row = getLayoutInflater().inflate(R.layout.custom_infowindow, null);

        for (int i=0;i<markers.size();i++)
        {
            if(title.equals(markers.get(i).getName()))
            {
                //PRENDIAMO DAL LAYOUT LA TEXT VIEW CON ID ADDRESS
                TextView name = (TextView) row.findViewById(R.id.name);
                //PRENDIAMO DAL LAYOUT LA TEXT VIEW CON ID ADDRESS
                TextView address = (TextView) row.findViewById(R.id.address);
                //PRENDIAMO DAL LAYOUT LA TEXT VIEW CON ID CROWDING
                TextView crowding = (TextView) row.findViewById(R.id.crowding);

                //SETTING COLORI TESTO/INFOWINDOW
                String cName = markers.get(i).getName() + " ";
                //Log.i("ciclo", "val="+cont);
                String cAdd = markers.get(i).getAddress() + " ";
                String cCrow = "High/Medium/Low ";

                //ASSEGNAZIONE TESTO DA VISUALIZZARE
                name = setTextAndColor(name, cName, " Name");

                address = setTextAndColor(address, cAdd, " Address");

                setTextAndColor(crowding, cCrow, " Crowding");
                row.setBackgroundColor(Color.parseColor("#fbb324"));

            }
        }

        return row;
    }

    @Override
    public View getInfoContents(com.google.android.gms.maps.model.Marker marker) {
        return null;
    } //FINE getInfoContent

    public void bindInfoWindowToMarker()
    {}

    //ANDIAMO A DEFINIRE LA PROCEDURA PER APRIRE IL MENU
    public void openMenu(String localName) {
        Intent intent = new Intent(this, Menu.class);
        intent.putExtra("key", localName);

        startActivity(intent);

    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId)
    {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0,0, vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);

    }

    public TextView setTextAndColor(TextView tv, String str, String att)
    {
        tv.setText(att + ": " + str);
        tv.setTextColor(Color.parseColor("#000000"));

        return tv;
    }

    public int setCustomIcon(String type)
    {
        int path = R.drawable.ic_cocktail;         //INIZIALIZZAZIONE PER NON AVERE ERRORI A RUNTIME

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
        return path;
    }

}