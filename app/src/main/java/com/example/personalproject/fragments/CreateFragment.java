package com.example.personalproject.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.personalproject.R;
import com.example.personalproject.databinding.FragmentCreateBinding;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    public static final String TAG = "CreateFragment";

    private FragmentCreateBinding binding;
    private AutocompleteSupportFragment autocompleteFragment;







    public CreateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Places.initialize(getContext(), getString(R.string.google_maps_api_key));
        PlacesClient placesClient = Places.createClient(getContext());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       binding = FragmentCreateBinding.inflate(getLayoutInflater());

       return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // set up location autocomplete fragment
        autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() +"," + place.getLatLng());

            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        Spinner itemConditionSpinner = binding.conditionSpinner;

        ArrayAdapter<CharSequence> itemConditionAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.condition_array, android.R.layout.simple_spinner_item);
        itemConditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemConditionSpinner.setAdapter(itemConditionAdapter);

        itemConditionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 ) {
                    Log.i(TAG, parent.getItemAtPosition(position).toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i(TAG, "No item condition selected");

            }
        });



        Spinner itemSizeSpinner = binding.itemSizeSpinner;

        ArrayAdapter<CharSequence> itemSizeAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.sizing_array, android.R.layout.simple_spinner_item);
        itemConditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemSizeSpinner.setAdapter(itemSizeAdapter);

        itemSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 ) {
                    Log.i(TAG, parent.getItemAtPosition(position).toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i(TAG, "No item size selected");

            }
        });



        Spinner itemTypeSpinner = binding.itemTypeSpinner;

        ArrayAdapter<CharSequence> itemTypeAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.item_type, android.R.layout.simple_spinner_item);
        itemConditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemTypeSpinner.setAdapter(itemTypeAdapter);

        itemSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 ) {
                    Log.i(TAG, parent.getItemAtPosition(position).toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i(TAG, "No item type selected");

            }
        });



    }
}