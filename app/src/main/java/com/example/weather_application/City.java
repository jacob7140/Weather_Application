package com.example.weather_application;

import java.io.Serializable;

public class City implements Serializable {

    private String state;
    private String city;


    public City(String state, String city) {
        this.state = state;
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "City{" +
                "country='" + state + '\'' +
                ", city='" + city + '\'' +
                '}';
    }

}