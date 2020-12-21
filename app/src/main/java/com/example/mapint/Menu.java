package com.example.mapint;

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
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;               //Allinea gli elementi nella lista


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mTextViewResult = findViewById(R.id.text_view_result);
        Button birre = findViewById(R.id.beer);

        mQueue = Volley.newRequestQueue(this);

        birre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jsonParse();
            }
        });

        String defaultName;

        TextView welcomeView = (TextView) findViewById(R.id.welcome);

        defaultName = (String) welcomeView.getText();

        welcomeView.setText(setWelcomeText(defaultName));

    }  //FINE ON CREATE

    private void jsonParse()
    {
        //Si usa il 10.0.2.2 così facciamo riferimento all'IP del computer su cui l'emulatore sta girando
        String url = "http://10.0.2.2:1111/drink/cocktail";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {

                    for(int i=0; i< response.length(); i++)
                    {
                        JSONObject beer = response.getJSONObject(i);
                        String drinkName = beer.getString("name");
                        double price = beer.getDouble("price");
                        //items.add(new Item(drinkName, price));

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
}