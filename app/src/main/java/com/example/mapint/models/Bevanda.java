package com.example.mapint.models;

import java.util.ArrayList;

public class Bevanda {
    String name;
    String type;

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Bevanda(String name, String type){

        this.name = name;
        this.type = type;
    }

    private static int lastContactId = 0;

    public static ArrayList<Bevanda> createContactsList(int numContacts) {
        ArrayList<Bevanda> contacts = new ArrayList<Bevanda>();

        for (int i = 1; i <= numContacts; i++) {
            contacts.add(new Bevanda("Person " + ++lastContactId, "m"));
        }

        return contacts;
    }

}
