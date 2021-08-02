package com.example.personalproject.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.personalproject.R;
import com.example.personalproject.adapters.SalesAdapter;
import com.example.personalproject.databinding.FragmentDashboardBinding;
import com.example.personalproject.databinding.FragmentHomeBinding;
import com.example.personalproject.databinding.FragmentSalesBinding;
import com.example.personalproject.models.Item;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SalesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SalesFragment extends Fragment {

    public static final String TAG = "SalesFragment";

    ParseLiveQueryClient parseLiveQueryClient = null;
    SubscriptionHandling<Item> subscriptionHandling = null;
    ParseQuery<Item> subscribedQuery = null;

    private FragmentSalesBinding binding;
    private List<Item> items = new ArrayList<>();;
    private SalesAdapter adapter;

    public SalesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        Log.i(TAG, "in on resume");
        super.onResume();
        if (parseLiveQueryClient == null) {
            try {
                parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient(
                        new URI("wss://fashionmarketplace.b4a.io/"));
                Log.i(TAG, "parse query client created");
            } catch (URISyntaxException e) {
                Log.i(TAG, "Exception from initializing parse Live Query Client.");
                e.printStackTrace();
            }
        }
        querySales();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "in on destroy");
        if (parseLiveQueryClient != null && subscribedQuery != null) {
            Log.i(TAG, "Unsubscribing live query");
            parseLiveQueryClient.unsubscribe(subscribedQuery);
            subscribedQuery = null;
        }
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSalesBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "in on view created");
        super.onViewCreated(view, savedInstanceState);
        adapter = new SalesAdapter(getActivity(), items);
        binding.rvItemsSales.setAdapter(adapter);
        binding.rvItemsSales.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    public void querySales() {
        ParseQuery<Item> query = ParseQuery.getQuery(Item.class);
        query.include("transaction.buyer");
        query.include(Item.KEY_SELLER);
        query.whereEqualTo("seller", ParseUser.getCurrentUser());
        subscribeToLiveQuery(query);
        query.orderByAscending("status");
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Item>() {
            @Override
            public void done(List<Item> itemsFound, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting sales", e);
                    return;
                }
                items.clear();
                items.addAll(itemsFound);
                adapter.notifyDataSetChanged();
                Log.i(TAG, "finished querying sales");
            }
        });
    }

    private void subscribeToLiveQuery(ParseQuery<Item> parseQuery) {
        if (parseLiveQueryClient != null) {
            if (subscribedQuery != null) {
                Log.i(TAG, "Unsubscribing live query");
                parseLiveQueryClient.unsubscribe(subscribedQuery);
                subscribedQuery = null;
            }
            subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);
            Log.i(TAG, "Subscription of live query done.");

            subscriptionHandling.handleEvent(SubscriptionHandling.Event.UPDATE, new SubscriptionHandling.HandleEventCallback<Item>() {
                @Override
                public void onEvent(ParseQuery<Item> query, Item updatedItem) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        public void run() {
                            Log.i(TAG, "in main looper, UPDATE event");
                            if (updatedItem.getStatus() == Item.PENDING){
                                updatedItem.getParseObject("transaction")
                                        .fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                                    @Override
                                    public void done(ParseObject object, ParseException e) {
                                        Log.i(TAG, "Seller fetched in enter event.");
                                        updatedItem.getTransaction().getParseObject("buyer")
                                                .fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                                            @Override
                                            public void done(ParseObject object, ParseException e) {
                                                adapter.handleItemUpdate(updatedItem);
                                            }
                                        });
                                    }
                                });
                            } else {
                                adapter.handleItemUpdate(updatedItem);
                            }
                        }
                    });
                }
            });

            subscriptionHandling.handleEvent(SubscriptionHandling.Event.ENTER, new SubscriptionHandling.HandleEventCallback<Item>() {
                @Override
                public void onEvent(ParseQuery<Item> query, Item addedItem) {
                    /* Note that this will only happen if the seller is using two devices
                    * and adds an item on one while keeping the dashboard open in the other.*/
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        public void run() {
                            Log.i(TAG, "in main looper, ENTER event");
                            adapter.handleItemAdded(addedItem);
                        }
                    });
                }
            });
        }
        subscribedQuery = parseQuery;
    }
}