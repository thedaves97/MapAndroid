package com.example.mapint.Interfaces;

import com.example.mapint.CrowdingDto;
import com.example.mapint.Marker;

import java.util.ArrayList;

public interface CrowdingVolleyResponseListener {
    void onError(String message);

    void onResponse(ArrayList<CrowdingDto> drinkToDoQuantity);
}
