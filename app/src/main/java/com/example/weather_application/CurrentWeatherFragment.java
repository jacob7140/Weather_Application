package com.example.weather_application;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class CurrentWeatherFragment extends Fragment {
    private final OkHttpClient client = new OkHttpClient();
    private final String TAG = "data";

    private static final String CITY = "CITY";
    private static final String ARRAY_CITY = "ARRAY_CITY";

    private City mCity;

    public CurrentWeatherFragment() {
        // Required empty public constructor
    }

    public static CurrentWeatherFragment newInstance(City city) {
        CurrentWeatherFragment fragment = new CurrentWeatherFragment();
        Bundle args = new Bundle();
        args.putSerializable(CITY, city);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCity = (City) getArguments().getSerializable(CITY);
        }
    }

    TextView textViewCurrentLocation, textViewTemp, textViewTempMax, textViewTempMin, textViewDescription, textViewHumidity,
            textViewWindSpeed, textViewWindDegree, textViewCloudiness;
    ImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Current Weather");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_current_weather, container, false);
        textViewCurrentLocation = view.findViewById(R.id.textViewCurrentLocation);
        textViewCurrentLocation.setText(mCity.getCity() + ", " + mCity.getState());
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + mCity.getCity() + "&units=imperial&appid=d03bcf3901e633382f6787dd0f03ed58";
        Log.d(TAG, "onCreateView: " + url);

        Request request = new Request.Builder().url(url).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("data", "onFailure: Current Weather client failed");
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                if (response.isSuccessful()){
                    try {
                        JSONObject json = new JSONObject(responseData);
                        JSONArray array = json.getJSONArray("weather");
                        JSONObject object = array.getJSONObject(0);


                        JSONObject main = json.getJSONObject("main");
                        JSONObject wind = json.getJSONObject("wind");
                        JSONObject clouds = json.getJSONObject("clouds");

                        int temperature = (int)Math.round(main.getDouble("temp"));
                        String stringTemp = String.valueOf(temperature);

                        int temperatureMax = (int)Math.round(main.getDouble("temp_max"));
                        String stringTempMax = String.valueOf(temperatureMax);

                        int temperatureMin = (int)Math.round(main.getDouble("temp_min"));
                        String stringTempMin = String.valueOf(temperatureMin);

                        String description = object.getString("description");

                        int humidity = (int)Math.round(main.getDouble("humidity"));
                        String stringHumidity = String.valueOf(humidity);
//
                        int windSpeed = (int)Math.round(wind.getDouble("speed"));
                        String stringWindSpeed = String.valueOf(windSpeed);
//
                        int windDegree = (int)Math.round(wind.getDouble("deg"));
                        String stringWindDegree = String.valueOf(windDegree);

                        int cloudiness = (int)Math.round(clouds.getDouble("all"));
                        String stringClouds = String.valueOf(cloudiness);

                        String icon = object.getString("icon");
                        Log.d("data", "onResponse: " + icon);
                        String iconURL = "https://openweathermap.org/img/wn/" + icon + "@2x.png";
                        Log.d("data", "run: " + iconURL);

                        Weather weather = new Weather(stringTemp, stringTempMax, stringTempMin, description, stringHumidity, stringWindSpeed, stringWindDegree, stringClouds);



                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //textViewTemp.setText(printWeather);
                                //Log.d("data", "onResponse: " + weather.getTemp() + " Degrees");

                                imageView = view.findViewById(R.id.imageView);
                                Picasso.get().load(iconURL).into(imageView);

                                textViewTemp = view.findViewById(R.id.textDisplayTemp);
                                textViewTempMax = view.findViewById(R.id.textDisplayTempMax);
                                textViewTempMin = view.findViewById(R.id.textDisplayTempMin);
                                textViewDescription = view.findViewById(R.id.textDisplayDescription);
                                textViewHumidity = view.findViewById(R.id.textDisplayHumidity);
                                textViewWindSpeed = view.findViewById(R.id.textDisplayWindSpeed);
                                textViewWindDegree = view.findViewById(R.id.textDisplayWindDegree);
                                textViewCloudiness = view.findViewById(R.id.textDisplayCloudiness);

                                textViewTemp.setText(weather.getTemp() + " F");
                                textViewTempMax.setText(weather.getTempMax() + " F");
                                textViewTempMin.setText(weather.getTempMin() + " F");
                                textViewDescription.setText(weather.getDescription());
                                textViewHumidity.setText(weather.getHumidity() + " %");
                                textViewWindSpeed.setText(weather.getWindSpeed() + " miles/hr");
                                textViewWindDegree.setText(weather.getWindDegree() + " degrees");
                                textViewCloudiness.setText(weather.getCloudiness() + " %");

                            }
                        });

                        //Log.d("data", "onResponse: " + temperature);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        });

        view.findViewById(R.id.buttonCheckForecast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.goToForecast(mCity);

            }
        });

        view.findViewById(R.id.buttonCurrentWeatherBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.goToCityList();
            }
        });



        return view;
    }

    CurrentWeatherListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (CurrentWeatherFragment.CurrentWeatherListener) context;
    }

    interface CurrentWeatherListener{
        void goToForecast(City city);
        void goToCityList();
    }



}