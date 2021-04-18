package com.example.weather_application;

/**
 * @author Jacob Smith
 * @title Weather Forecast App
 */

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements CitiesListFragment.CitiesListListener, CurrentWeatherFragment.CurrentWeatherListener {

    private String CITIES_FRAGMENT = "CITIES_FRAGMENT";
    private String CURRENT_WEATHER_FRAGMENT = "CURRENT_WEATHER_FRAGMENT";
    private String FORECAST_FRAGMENT = "FORECAST_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().add(R.id.rootView, new CitiesListFragment()).commit();
    }

    @Override
    public void goToCurrentWeather(City city) {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, CurrentWeatherFragment.newInstance(city), CURRENT_WEATHER_FRAGMENT).commit();
    }

    @Override
    public void goToForecast(City city) {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, ForecastFragment.newInstance(city), FORECAST_FRAGMENT).commit();
    }

    @Override
    public void goToCityList() {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new CitiesListFragment()).commit();
    }
}