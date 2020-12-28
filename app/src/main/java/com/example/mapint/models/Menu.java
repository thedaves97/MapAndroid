package com.example.mapint.models;

public class Menu {

    private Locale locale;
    private Bevanda bevanda;

    private float price;


    public Bevanda getBevanda() {
        return bevanda;
    }


    public Locale getLocale() {
        return locale;
    }


    public void setBevanda(Bevanda bevanda) {
        this.bevanda = bevanda;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public float getPrice(){
        return price;
    }

    public void setPrice (float price){
        this.price = price;
    }



    public Menu(Locale locale, Bevanda bevanda, float price){
        this.locale = locale;
        this.bevanda = bevanda;
        this.price = price;
    }




}

