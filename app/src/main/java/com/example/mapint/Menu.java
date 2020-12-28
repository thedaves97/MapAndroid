package com.example.mapint;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.Request;

import java.util.ArrayList;

public class Menu extends AppCompatActivity {

    private TextView mTextViewResult;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mTextViewResult = findViewById(R.id.text_view_result);

        final Button birre = findViewById(R.id.beer);
        final Button vini = findViewById(R.id.wine);
        final Button cocktail = findViewById(R.id.cocktail);

        mQueue = Volley.newRequestQueue(this);

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

        String defaultName;

        TextView welcomeView = (TextView) findViewById(R.id.welcome);

        defaultName = (String) welcomeView.getText();

        welcomeView.setText(setWelcomeText(defaultName));

    }  //FINE ON CREATE

    private void jsonParse(String buttonName)
    {
        String localName ="";
        localName = getLocalNamePassed();

        //Si usa il 10.0.2.2 così facciamo riferimento all'IP del computer su cui l'emulatore sta girando
        String url = setUrl(buttonName, localName);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                try {


                    for(int i=0; i< response.length(); i++)
                    {
                        JSONObject drink = response.getJSONObject(i);
                        String drinkName = drink.getString("name");
                        double price = drink.getDouble("price");

                        mTextViewResult.append("\n" + drinkName + " " + price + " €");


                    }

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

    public String getLocalNamePassed()
    {
        String localName = "";

        Bundle extras = getIntent().getExtras();

        if(extras!=null)
        { localName = extras.getString("key"); }

        return localName;
    }

    public String setWelcomeText(String defaultName)
    {
        String name = "";
        String complete = "";
        /*
        Bundle extras = getIntent().getExtras();

        if(extras!=null)
        { name = extras.getString("key"); }
         */
        name = getLocalNamePassed();

        if(!defaultName.equals(""))
        { complete = defaultName.replace("name", name); }

        return complete;
    }

    public String setUrl(String buttonName, String localName)
    {
        String url = "";
        switch (buttonName)
        {
            case "Beers":
                url = "http://10.0.2.2:1111/api/v1/specificdrinktype?nameLocale="+ localName +"&typeBevanda=beer";
                Log.i("url", url);
                break;
            case "Wines":
                url = "http://10.0.2.2:1111/api/v1/bevanda";
                break;
            case "Cocktails":
                url = "http://10.0.2.2:1111/api/v1/bevanda";
                break;
        }   //FINE SWITCH

    return url;
    }

}