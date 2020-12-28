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
import java.util.Collections;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String EXTRA_TEXT = "com.example.mapint.MapsActivity.EXTRA_TEXT";

    ArrayList<Marker> markers = new ArrayList<>();
    //ArrayList<Marker> temp = new ArrayList<>();
    String mt;
    int cont = 0;
    private RequestQueue mQueue;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mQueue = Volley.newRequestQueue(this);

        button = findViewById(R.id.menu_button);
        LinearLayout li = findViewById(R.id.bottom);
        li.setBackgroundColor(Color.parseColor("#fbb324"));

        /*
        //Collections.copy(markers, jsonParseLocali());
        //jsonParseLocali();
        ArrayList<Marker> mk = new ArrayList<>();
        //STAMPA NEL LOG PER VERIFICARE LA LETTURA DEI MARKER
        for (Marker m: markers)
        {
            Log.i("da markers",  "--> " + m.getId() + " " + m.getName() + " " + m.getAddress() + " " + m.getType() + " " + m.getLat() + " " + m.getLon());
        }
        */

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

                Marker m = new Marker(1,"name", "type", "address", 0.0, 0.1);
                m.setId(obj.getInt("id"));
                m.setName(obj.getString("name"));
                m.setType(obj.getString("type"));
                m.setAddress(obj.getString("address"));
                m.setLat(obj.getDouble("lat"));
                m.setLon(obj.getDouble("lon"));
                markers.add(m);

            }

            for (int i=0; i<markers.size();i++)
            {
                Log.i("prova", "val " + " " + markers.get(i));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }           //FINE ONCREATE

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
            type = m.getType();

            if(mMap!=null)
            {
                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(com.google.android.gms.maps.model.Marker marker)
                    {

                        View row = getLayoutInflater().inflate(R.layout.custom_infowindow, null);

                        //PRENDIAMO DAL LAYOUT LA TEXT VIEW CON ID ADDRESS
                        TextView name = (TextView) row.findViewById(R.id.name);
                        //PRENDIAMO DAL LAYOUT LA TEXT VIEW CON ID ADDRESS
                        TextView address = (TextView) row.findViewById(R.id.address);
                        //PRENDIAMO DAL LAYOUT LA TEXT VIEW CON ID CROWDING
                        TextView crowding = (TextView) row.findViewById(R.id.crowding);

                        //SETTING COLORI TESTO/INFOWINDOW
                        String cName = markers.get(cont).getName() + " ";
                        String cAdd = markers.get(cont).getAddress() + " ";
                        //String cName = temp.get(cont).getName() + " ";
                        //String cAdd = temp.get(cont).getAddress() + " ";
                        String cCrow = "High/Medium/Low ";

                        //ASSEGNAZIONE TESTO DA VISUALIZZARE
                        name = setTextAndColor(name, cName, " Name");

                        address = setTextAndColor(address, cAdd, " Address");

                        setTextAndColor(crowding, cCrow, " Crowding");

                        cont++;

                        row.setBackgroundColor(Color.parseColor("#fbb324"));

                        return row;
                    }

                    @Override
                    public View getInfoContents(com.google.android.gms.maps.model.Marker marker) {

                        return null;
                    }  //FINE getInfoContent

                }); //FINE setInfoWindowAdapter

            }  //FINE IF

            //pos = new LatLng(45.0646803, 7.6955659);    //Prova esecuzione dopo GET request
            pos = new LatLng(lat, lon);
            //mMap.addMarker(new MarkerOptions().position(pos).title("Name: " + m.getName() + "\nType: " + m.getType() + "\nAddress: " + m.getAddress()));
            mMap.addMarker(new MarkerOptions().position(pos).title(m.getName())
                    .icon(bitmapDescriptorFromVector(getApplicationContext(), setCustomIcon(type))));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 17.0f));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {
                    //Controllare se il get(cont) serve o no
                    TextView selectedMarker = (TextView) findViewById(R.id.local_name);
                    selectedMarker.setText(markers.get(cont).getName());
                    selectedMarker.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                    selectedMarker.setTextColor(Color.parseColor("#000000"));

                    final String localName = (String) selectedMarker.getText();

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openMenu(localName);
                        }
                    });

                    return false;
                }
            });

        } //FINE FOR CREAZIONE MARKER

    } //FINE OnMapReady

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

    public void jsonParseLocali()
    {
        String url = "http://10.0.2.2:1111/api/v1/locale";
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
                        Log.i("parse",  "--> " + m.getId() + " " + m.getName() + " " + m.getAddress() + " " + m.getType() + " " + m.getLat() + " " + m.getLon());
                        //Log.i("temp", String.valueOf(temp.get(i)));
                        //temp.add(m);
                        //markers.add(m);
                    }
                    /*
                    for (Marker m: temp)
                    {   Log.i("each",  "--> " + m.getId() + " " + m.getName() + " " + m.getAddress() + " " + m.getType() + " " + m.getLat() + " " + m.getLon());    }
                     */

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

    }     //FINE JSONPARSELOCALI

    /*
    public void setList(ArrayList<Marker> temp)
    {
        ArrayList<Marker> mk = new ArrayList<>();
        Collections.copy(temp, mk);

        getList(mk);
    }

    public ArrayList<Marker> getList(ArrayList<Marker> mk)
    {
        return mk;
    }
     */
}