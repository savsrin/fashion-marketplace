package com.example.personalproject.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.personalproject.R;
import com.example.personalproject.adapters.HomeItemsAdapter;
import com.example.personalproject.adapters.PagerAdapter;
import com.example.personalproject.databinding.FragmentDashboardBinding;
import com.example.personalproject.databinding.FragmentHomeBinding;
import com.google.android.material.tabs.TabLayout;


public class DashboardFragment extends Fragment {

    public static final String TAG = "DashboardFragment";

    private FragmentDashboardBinding binding;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Launching on create fragment \n");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull  View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.tabBarDashboard.addTab( binding.tabBarDashboard.newTab().setText("Sales"));
        binding.tabBarDashboard.addTab( binding.tabBarDashboard.newTab().setText("Purchases"));
        binding.tabBarDashboard.setTabGravity(TabLayout.GRAVITY_FILL);

        PagerAdapter pagerAdapter = new PagerAdapter(getContext(), getChildFragmentManager(), 2);
        binding.vpDashboard.setAdapter(pagerAdapter);
        
        int defaultValue = 0;
        int page = getActivity().getIntent().getIntExtra("page", defaultValue);
        binding.vpDashboard.setCurrentItem(page);
        binding.vpDashboard.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabBarDashboard));
        binding.tabBarDashboard.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.vpDashboard.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}