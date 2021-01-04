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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
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
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mQueue = Volley.newRequestQueue(this);

        button = (Button) findViewById(R.id.menu_button);
        LinearLayout li = (LinearLayout) findViewById(R.id.bottom);
        li.setBackgroundColor(Color.parseColor("#fbb324"));

        /*
         *READ JSON VIA URL
         */
        jsonParseLocali();

    }       //onCreate() ends

    private void jsonParseLocali() {
        String url = "http://10.0.2.2:1111/api/v1/locale";
        //String url = "http://192.168.1.157:1111/api/v1/locale";
        //final ArrayList<Marker> temp = new ArrayList<>();

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response)
            {
                try {

                    for(int i=0; i< response.length(); i++)
                    {
                        Marker m = new Marker(0,"name", "type", "address", 0.0, 0.0);
                        JSONObject locale = response.getJSONObject(i);

                        int id = locale.getInt("id");
                        String name = locale.getString("name");
                        String address = locale.getString("address");
                        String type = locale.getString("type");
                        double lat = locale.getDouble("lat");
                        double lon = locale.getDouble("lon");

                        m.setId(locale.getInt("id"));
                        m.setName(locale.getString("name"));
                        m.setAddress(locale.getString("address"));
                        m.setType(locale.getString("type"));
                        m.setLat(locale.getDouble("lat"));
                        m.setLon(locale.getDouble("lon"));
                        //Log.i("parse",  "--> " + m.getId() + " " + m.getName() + " " + m.getAddress() + " " + m.getType() + " " + m.getLat() + " " + m.getLon());

                        markers.add(m);
                    }

                    for (Marker m: markers)
                    {   Log.i("each",  "--> " + m.getId() + " " + m.getName() + " " + m.getAddress() + " " + m.getType() + " " + m.getLat() + " " + m.getLon());    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue.add(request);
        /*
        for (Marker m: temp)
        {   Log.i("outscope",  "--> " + m.getId() + " " + m.getName() + " " + m.getAddress() + " " + m.getType() + " " + m.getLat() + " " + m.getLon());    }
         */

    }     //FINE JSONPARSELOCALI// jsonParseLocali method ends

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.i("control", "sono in on map ready");
        String name, type, add;
        LatLng pos;
        double lat;
        double lon;
        Log.i("control", "ho creato le variabili");

        int cicli;
        cicli = markers.size();
        Log.i("control", "var cicli creata");

        if(markers.isEmpty())
        {
            Log.i("control", "Vuota ktm");
        }

        //CREAZIONE MARKER
        for(int i=0; i<=8;i++)
        {
            Log.i("control", "sto creando marker");
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
            case "pub":
                path = R.drawable.ic_beer;
                break;
            case "cocktail bar":
                path = R.drawable.ic_cocktail;
                break;
            case "wine Bar":
                path = R.drawable.ic_wine;
                break;
        }
        return path;
    }

}