package com.example.personalproject.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.personalproject.fragments.PurchasesFragment;
import com.example.personalproject.fragments.SalesFragment;

public class PagerAdapter extends FragmentPagerAdapter {

    private Integer numOfTabs;
    private Context context;

    public PagerAdapter(Context context, FragmentManager fm, Integer numOfTabs) {
        super(fm);
        this.context = context;
        this.numOfTabs = numOfTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new SalesFragment();
            case 1:
                return new PurchasesFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
