package com.example.weather_application;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;

import java.util.ArrayList;

public class CitiesListFragment extends Fragment {


    public CitiesListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    ListView listView;
    EditText editTextCity, editTextState;
    CitiesAdapter adapter;
    ArrayList<City> cities = new ArrayList<>();
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Location");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cities_list, container, false);

        listView = view.findViewById(R.id.listView);
        editTextCity = view.findViewById(R.id.editTextCityName);
        editTextState = view.findViewById(R.id.editTextState);

        adapter = new CitiesAdapter(getContext(), R.layout.cities_adapter, cities);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("data", "onItemClick: " + position);
                Log.d("data", "onItemClick: " + cities.get(position).toString());
                mListener.goToCurrentWeather(cities.get(position));
            }
        });

        view.findViewById(R.id.buttonAddCity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = editTextCity.getText().toString();
                String state = editTextState.getText().toString();

                String cityLower = city.toLowerCase();
                String cityCap = cityLower.substring(0, 1).toUpperCase() + cityLower.substring(1);
                String stateCap = state.toUpperCase();

                if (city.isEmpty() | state.isEmpty()){
                    Toast.makeText(getActivity(), "City/State can not be empty.", Toast.LENGTH_SHORT).show();
                } else if (state.length() != 2){
                    Toast.makeText(getActivity(), "State code must be 2 characters", Toast.LENGTH_SHORT).show();
                } else {
                    City newCity = new City(stateCap, cityCap);
                    cities.add(newCity);

                    editTextCity.setText("");
                    editTextState.setText("");

                    InputMethodManager inputManager = (InputMethodManager)
                            getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }

            }
        });


        return view;
    }


    class CitiesAdapter extends ArrayAdapter<City>{

        public CitiesAdapter(@NonNull Context context, int resource, ArrayList<City> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.cities_adapter, parent, false);
            }

            City city = getItem(position);
            TextView textViewCities = convertView.findViewById(R.id.textViewCities);

            textViewCities.setText(city.getCity() + ", " + city.getState());

            return convertView;
        }
    }

    CitiesListListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (CitiesListFragment.CitiesListListener) context;
    }

    interface CitiesListListener{
        void goToCurrentWeather(City city);
    }
}