package com.example.weather_application;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ForecastFragment extends Fragment {
    private final OkHttpClient client = new OkHttpClient();

    private static final String ARG_PARAM1 = "param1";
    private City mCity;


    public ForecastFragment() {
        // Required empty public constructor
    }


    public static ForecastFragment newInstance(City city) {
        ForecastFragment fragment = new ForecastFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, city);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCity = (City) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    TextView textViewCurrentLocation;
    ListView listView;
    ForecastAdapter adapter;
    ArrayList<Forecast> forecasts = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Weather Forecast");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forecast, container, false);
        textViewCurrentLocation = view.findViewById(R.id.textViewForecastCurrentLocation);
        textViewCurrentLocation.setText(mCity.getCity() + ", " + mCity.getState());
        listView = view.findViewById(R.id.listViewForecast);

        String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + mCity.getCity() + "&units=imperial&appid=d03bcf3901e633382f6787dd0f03ed58";

        Request request = new Request.Builder().url(url).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                Log.d("data", "onFailure: Forecast client failed");
            }


            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();


                if (response.isSuccessful()) {
                    JSONObject json = null;
                    try {
                        json = new JSONObject(responseData);
                        JSONArray array = json.getJSONArray("list");

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject weatherObject = array.getJSONObject(i);

                            String dateTime = weatherObject.getString("dt");
                            Long longDate = Long.parseLong(dateTime);
                            Date date = new java.util.Date(longDate * 1000L);
                            SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd h:mm:ss");
                            sdf.setTimeZone(java.util.TimeZone.getTimeZone("EST"));
                            String dateTimeString = sdf.format(date).toString();

                            JSONArray weatherArray = weatherObject.getJSONArray("weather");
                            JSONObject weatherArrayObject = weatherArray.getJSONObject(0);
                            JSONObject main = weatherObject.getJSONObject("main");

                            int temperature = (int) Math.round(main.getDouble("temp"));
                            String stringTemp = String.valueOf(temperature);

                            int temperatureMax = (int) Math.round(main.getDouble("temp_max"));
                            String stringTempMax = String.valueOf(temperatureMax);

                            int temperatureMin = (int) Math.round(main.getDouble("temp_min"));
                            String stringTempMin = String.valueOf(temperatureMin);

                            String description = weatherArrayObject.getString("description");

                            int humidity = (int) Math.round(main.getDouble("humidity"));
                            String stringHumidity = String.valueOf(humidity);

                            String stringIcon = weatherArrayObject.getString("icon");


                            Forecast forecast = new Forecast(dateTimeString, stringTemp, stringTempMax, stringTempMin, description, stringHumidity, stringIcon);
                            forecasts.add(forecast);
                        }


                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("data", "onCreateView: " + forecasts);
                                adapter = new ForecastAdapter(getContext(), R.layout.forecast_adapter, forecasts);
                                listView.setAdapter(adapter);
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        });


        return view;
    }

    class ForecastAdapter extends ArrayAdapter<Forecast> {

        public ForecastAdapter(@NonNull Context context, int resource, ArrayList<Forecast> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.forecast_adapter, parent, false);
            }

            Forecast forecast = getItem(position);
            TextView textViewTime = convertView.findViewById(R.id.textViewForecastTime);
            TextView textViewTemp = convertView.findViewById(R.id.textViewForecastTemp);
            TextView textViewTempMax = convertView.findViewById(R.id.textViewForecastTempMax);
            TextView textViewTempMin = convertView.findViewById(R.id.textViewForecastTempMin);
            TextView textViewDescription = convertView.findViewById(R.id.textViewForecastDescription);
            TextView textViewHumidity = convertView.findViewById(R.id.textViewForecastHumidity);
            ImageView imageView = convertView.findViewById(R.id.forecastImageView);

            textViewTime.setText(forecast.getTime());
            textViewTemp.setText(forecast.getTemp() + " F");
            textViewTempMax.setText("Max: " + forecast.getTempMax() + " F");
            textViewTempMin.setText("Min: " + forecast.getTempMin() + " F");
            textViewDescription.setText(forecast.getDescription());
            textViewHumidity.setText("Humidity: " + forecast.getHumidity() + "%");

            String iconURL = "https://openweathermap.org/img/wn/" + forecast.getIcon() + "@2x.png";
            Picasso.get().load(iconURL).into(imageView);

            return convertView;
        }
    }

}
