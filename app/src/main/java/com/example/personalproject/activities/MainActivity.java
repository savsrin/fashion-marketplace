package com.example.personalproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.personalproject.R;
import com.example.personalproject.databinding.ActivityMainBinding;
import com.example.personalproject.fragments.CreateFragment;
import com.example.personalproject.fragments.DashboardFragment;
import com.example.personalproject.fragments.HomeFragment;
import com.example.personalproject.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        final FragmentManager fragmentManager = getSupportFragmentManager();
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("instathrift");

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        Log.i(TAG, "action_home switch case \n");
                        fragment = new HomeFragment();
                        break;
                    case R.id.action_create:
                        Log.i(TAG, "action_create switch case \n");
                        fragment = new CreateFragment();
                        break;
                    case R.id.action_dashboard:
                        Log.i(TAG, "action_dashboard switch case \n");
                        fragment = new DashboardFragment();
                        break;
                    case R.id.action_profile:
                        Log.i(TAG, "action_profile switch case \n");
                        fragment = new ProfileFragment();
                        break;
                    default:
                        fragment = new HomeFragment();
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        // Set default selection
        binding.bottomNavigationView.setSelectedItemId(R.id.action_home);
    }
}