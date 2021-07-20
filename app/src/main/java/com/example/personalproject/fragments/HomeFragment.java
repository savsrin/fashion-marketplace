package com.example.personalproject.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.personalproject.R;
import com.example.personalproject.adapters.HomeItemsAdapter;
import com.example.personalproject.databinding.FragmentCreateBinding;
import com.example.personalproject.databinding.FragmentHomeBinding;
import com.example.personalproject.models.Item;
import com.example.personalproject.models.UserMeasurement;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;


import java.util.ArrayList;
import java.util.List;

import permissions.dispatcher.RuntimePermissions;
import permissions.dispatcher.NeedsPermission;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@RuntimePermissions
public class HomeFragment extends Fragment {

    public static final String TAG = "HomeFragment";

    Location currentLocation;
    FusedLocationProviderClient client;
    private List<Item> items;
    private HomeItemsAdapter adapter;
    private FragmentHomeBinding binding;
    private LocationRequest mLocationRequest;
    private final static String KEY_LOCATION = "location";
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Launching home fragment \n");
    }

    @Override
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION})
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "in on view created");
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.keySet().contains(KEY_LOCATION)) {
            // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
            // is not null.
            currentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }
        HomeFragmentPermissionsDispatcher.startLocationUpdatesWithPermissionCheck(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        HomeFragmentPermissionsDispatcher.startLocationUpdatesWithPermissionCheck(this);
        return binding.getRoot();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults);
       HomeFragmentPermissionsDispatcher.onRequestPermissionsResult(
               this,
               requestCode,
               grantResults);
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(KEY_LOCATION, currentLocation);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public  void onStart() {
        Log.i(TAG, "in on start");
        super.onStart();
    }

    @Override
    public void onStop() {
        Log.i(TAG, "in on stop");
        super.onStop();
    }

    @Override
    public void onResume() {
        Log.i(TAG, "in on resume");
        super.onResume();
        HomeFragmentPermissionsDispatcher.startLocationUpdatesWithPermissionCheck(this);
    }

    public void onLocationChanged(Location location) {
        // GPS may be turned off
        if (location == null) {
            return;
        }

        currentLocation = location;
        ParseGeoPoint buyerLocation = new ParseGeoPoint(
                currentLocation.getLatitude(),
                currentLocation.getLongitude());
        items = new ArrayList<>();
        adapter = new HomeItemsAdapter(getActivity(), items, buyerLocation);

        binding.rvItemsHome.setAdapter(adapter);
        binding.rvItemsHome.setLayoutManager(new LinearLayoutManager(getActivity()));

        String msg = "Updated Location: " +
                location.getLatitude() + "," +
               location.getLongitude();
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "calling query items");
        queryItems(buyerLocation);
    }

    public void queryItems(ParseGeoPoint buyerLocation) {
        // TODO: Finish Finalizing Parameters to Query By
        ParseQuery<Item> query = ParseQuery.getQuery(Item.class);
        query.include(Item.KEY_SELLER);
        query.whereWithinMiles("pickupLocation",buyerLocation, 20);
        query.setLimit(5);
        query.whereGreaterThanOrEqualTo("chest", 30);
        query.findInBackground(new FindCallback<Item>() {
            @Override
            public void done(List<Item> itemsFound, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                for (Item item: itemsFound) {
                    Log.i(TAG, "Item: " + item.getDescription() + "\n");
                }
                items.addAll(itemsFound);
                adapter.notifyDataSetChanged();
                Log.i(TAG, "finished querying posts");
            }
        });
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            onLocationChanged(locationResult.getLastLocation());
            if (locationResult.getLastLocation() != null ) {
                getFusedLocationProviderClient(getActivity()).removeLocationUpdates(locationCallback);
            }
        }
    };


    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(getActivity());
        settingsClient.checkLocationSettings(locationSettingsRequest);
        //noinspection MissingPermission
        getFusedLocationProviderClient(getActivity()).requestLocationUpdates(mLocationRequest, locationCallback,
                Looper.myLooper());
    }
}

