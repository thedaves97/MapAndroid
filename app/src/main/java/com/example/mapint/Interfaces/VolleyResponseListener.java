package com.example.mapint.Interfaces;

import com.example.mapint.Marker;

import java.util.ArrayList;

public interface VolleyResponseListener {
    void onError(String message);

    void onResponse(ArrayList<Marker> listOfMarker);
}
