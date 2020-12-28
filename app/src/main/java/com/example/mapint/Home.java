package com.example.mapint;

import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.Request;
import com.example.mapint.adapter.DrinkAdapter;
import com.example.mapint.models.Bevanda;
import com.example.mapint.models.Locale;
import com.example.mapint.models.Menu;

import java.util.ArrayList;

public class Home extends AppCompatActivity {

    private RequestQueue mQueue;
    private String localName;
    ArrayList<Menu> menus;
    RecyclerView rvBevande;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        final Button birre = findViewById(R.id.beer);
        final Button vini = findViewById(R.id.wine);
        final Button cocktail = findViewById(R.id.cocktail);

        VolleyCallback callback = null;

        mQueue = Volley.newRequestQueue(this);

        menus = new ArrayList<Menu>();

        if(getIntent() != null){
            localName = getIntent().getStringExtra("name");
            System.out.println(localName);
        }

        birre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String n = birre.getText().toString();
                jsonParse(n);
            }
        });

        vini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String n = vini.getText().toString();
                jsonParse(n);
            }
        });

        cocktail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String n = cocktail.getText().toString();
                jsonParse(n);
            }
        });

        rvBevande = (RecyclerView) findViewById(R.id.recyclermenu);


        InitJsonParsing(new VolleyCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                for(int i=0; i< response.length(); i++)
                {
                    try {
                        JSONObject r  = response.getJSONObject(i);
                        String drinkName = r.getJSONObject("bevanda").getString("name");
                        String drinkType = r.getJSONObject("bevanda").getString("type");

                        String localName = r.getJSONObject("locale").getString("name");
                        String localAddress = r.getJSONObject("locale").getString("name");
                        String localType = r.getJSONObject("locale").getString("name");
                        String localLat = r.getJSONObject("locale").getString("name");
                        String localLon = r.getJSONObject("locale").getString("name");

                        float price =(float) r.getLong("price");


                        Bevanda bevandaAtt = new Bevanda(drinkName, drinkType);
                        Locale localeAtt = new Locale(localName, localAddress, localType, localLat, localLon);

                        menus.add(new Menu(localeAtt, bevandaAtt, price));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                DrinkAdapter adapter = new DrinkAdapter(menus);
                // Attach the adapter to the recyclerview to populate items
                rvBevande.setAdapter(adapter);
            }
        });

        rvBevande.setLayoutManager(new LinearLayoutManager(this));
    }


    public void InitJsonParsing (final VolleyCallback callback){
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, "http://192.168.1.90:1111/api/v1/menu?nameLocale=" + localName, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }

    private void jsonParse(String buttonName)
    {
        String url = setUrl(buttonName);
        ArrayList<Bevanda> bev = new ArrayList<Bevanda>();

        menus.clear();

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {

                    for(int i=0; i< response.length(); i++)
                    {

                        JSONObject r  = response.getJSONObject(i);
                        String drinkName = r.getJSONObject("bevanda").getString("name");
                        String drinkType = r.getJSONObject("bevanda").getString("type");

                        String localName = r.getJSONObject("locale").getString("name");
                        String localAddress = r.getJSONObject("locale").getString("name");
                        String localType = r.getJSONObject("locale").getString("name");
                        String localLat = r.getJSONObject("locale").getString("name");
                        String localLon = r.getJSONObject("locale").getString("name");

                        float price =(float) r.getLong("price");

                        Bevanda bevandaAtt = new Bevanda(drinkName, drinkType);
                        Locale localeAtt = new Locale(localName, localAddress, localType, localLat, localLon);

                        menus.add(new Menu(localeAtt, bevandaAtt, price));


                    }

                    DrinkAdapter adapter = new DrinkAdapter(menus);
                    // Attach the adapter to the recyclerview to populate items
                    rvBevande.setAdapter(adapter);

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

    }

    public String setWelcomeText(String defaultName)
    {
        String name = "";
        String complete = "";

        Bundle extras = getIntent().getExtras();

        if(extras!=null)
        { name = extras.getString("key"); }

        if(!defaultName.equals(""))
        { complete = defaultName.replace("name", name); }

        return complete;
    }

    public String setUrl(String buttonName)
    {
        String url = "";
        switch (buttonName)
        {
            case "Beers":
                url = "http://192.168.1.90:1111/api/v1/specificdrinktype?nameLocale=" + localName + "&typeBevanda=beer";
                break;
            case "Wines":
                url = "http://192.168.1.90:1111/api/v1/specificdrinktype?nameLocale=" + localName + "&typeBevanda=wine";
                break;
            case "Cocktails":
                url = "http://192.168.1.90:1111/api/v1/specificdrinktype?nameLocale=" + localName + "&typeBevanda=cocktail";
                break;
        }   //FINE SWITCH

    return url;
    }
}