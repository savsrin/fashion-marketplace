package com.example.personalproject.fragments;

import android.content.Context;
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
import com.example.personalproject.adapters.PurchasesAdapter;
import com.example.personalproject.adapters.SalesAdapter;
import com.example.personalproject.databinding.FragmentPurchasesBinding;
import com.example.personalproject.databinding.FragmentSalesBinding;
import com.example.personalproject.models.Item;
import com.example.personalproject.models.Transaction;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PurchasesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PurchasesFragment extends Fragment {
    public static final String TAG = "PurchasesFragment";

    private FragmentPurchasesBinding binding;
    private List<Transaction> transactions = new ArrayList<>();;
    private PurchasesAdapter adapter;

    public PurchasesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPurchasesBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new PurchasesAdapter(getActivity(), transactions);
        binding.rvTransactionsPurchases.setAdapter(adapter);
        binding.rvTransactionsPurchases.setLayoutManager(new LinearLayoutManager(getActivity()));
        queryPurchases();
    }

    public void queryPurchases() {
        ParseQuery<Transaction> query = ParseQuery.getQuery(Transaction.class);
        query.include("item.seller");
        query.whereEqualTo("buyer", ParseUser.getCurrentUser());
        query.orderByAscending("paid");
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Transaction>() {
            @Override
            public void done(List<Transaction> transactionsFound, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting purchases", e);
                    return;
                }
                transactions.clear();
                transactions.addAll(transactionsFound);
                adapter.notifyDataSetChanged();
                Log.i(TAG, "finished querying purchases");
            }
        });
    }
}