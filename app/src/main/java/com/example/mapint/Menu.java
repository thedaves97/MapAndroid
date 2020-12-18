package com.example.mapint;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class Menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        String defaultName = "";

        TextView welcomeView = (TextView) findViewById(R.id.welcome);

        defaultName = (String) welcomeView.getText();

        welcomeView.setText(setWelcomeText(defaultName));

    }

    public String setWelcomeText(String defaultName)
    {
        String name = "";
        String complete = "";

        Bundle extras = getIntent().getExtras();

        if(extras!=null)
        { name = extras.getString("key"); }

        if(defaultName != "")
        { complete = defaultName.replace("name", name); }

        return complete;
    }
}