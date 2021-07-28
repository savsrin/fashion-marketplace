package com.example.personalproject.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.personalproject.R;
import com.example.personalproject.databinding.ActivityMainBinding;
import com.example.personalproject.databinding.ActivityTransactionBinding;
import com.example.personalproject.models.Item;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import java.net.URI;
import java.net.URISyntaxException;

public class TransactionActivity extends AppCompatActivity {
    public static final String TAG = "TransactionActivity";

    ParseLiveQueryClient parseLiveQueryClient = null;
    SubscriptionHandling<Item> subscriptionHandling = null;
    ParseQuery<Item> subscribedQuery = null;

    ActivityTransactionBinding binding;
    ParseUser buyer;
    ParseUser seller;
    Item item;
    String buyerPhone;
    String buyerEmail;
    Boolean isStopped;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "in onCreate");
        super.onCreate(savedInstanceState);
        binding = ActivityTransactionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent().getExtras() != null) {
            buyer = getIntent().getParcelableExtra("buyer");
            item = getIntent().getParcelableExtra("item");
            seller = item.getSeller();
        } else {
            Log.i(TAG, "Bundle is null");
        }

        initView();
        
        try {
            parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient(new URI("wss://fashionmarketplace.b4a.io/"));
            Log.i(TAG, "parse query client created");
        } catch (URISyntaxException e) {
            Log.i(TAG, "Exception fom initializing parse Live Query Client.");
            e.printStackTrace();
        }

        if (parseLiveQueryClient != null) {
            ParseQuery<Item> parseQuery = ParseQuery.getQuery(Item.class);
            parseQuery.whereEqualTo("objectId", item.getObjectId());
            subscribedQuery = parseQuery;

            subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);
            Log.i(TAG, "Subscription of live query done.");
            subscriptionHandling.handleEvent(SubscriptionHandling.Event.UPDATE, new SubscriptionHandling.HandleEventCallback<Item>() {
                @Override
                public void onEvent(ParseQuery<Item> query, Item updatedItem) {
                    if (updatedItem.getStatus() == Item.AVAILABLE) {
                        Log.i(TAG, "Updated item is still available");
                        return;
                    }
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        public void run() {
                            Log.i(TAG, "in main looper, UPDATE event when item is not available");
                            binding.btnTrans.setEnabled(false);
                            binding.btnTrans.setBackgroundColor(Color.GRAY);
                            if (!isStopped) {
                                Toast.makeText(TransactionActivity.this,
                                        "Sorry this item is no longer available.",
                                        Toast.LENGTH_LONG).show();
                            }
                            Log.i(TAG, "Unsubscribing live query");
                            parseLiveQueryClient.unsubscribe(subscribedQuery);
                            subscribedQuery = null;
                        }
                    });
                }
            });
        }

        binding.btnTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyerPhone = binding.etPhoneBuy.getText().toString()
                        .replaceFirst("(\\d{3})(\\d{3})(\\d+)", "($1)-$2-$3");
                buyerEmail = binding.etEmailBuy.getText().toString();

                if (buyerPhone.isEmpty() || buyerEmail.isEmpty()) {
                    Toast.makeText(TransactionActivity.this,
                            "You have left one or more required fields empty. " +
                                    "Please enter all information.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG, buyerPhone);
                Log.i(TAG, buyerEmail);

                // TODO: update database with transaction information & change item status to pending
                // TODO: Then, navigate user to Purchases Tab of Dashboard
            }
        });
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "in onDestroy");
        if (parseLiveQueryClient != null && subscribedQuery != null) {
            Log.i(TAG, "Unsubscribing live query");
            parseLiveQueryClient.unsubscribe(subscribedQuery);
            subscribedQuery = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        isStopped = false;
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "in onStop");
        super.onStop();
        isStopped = true;
    }

    private void initView() {
        ParseFile image = item.getPhoto();
        if (image != null) {
            Glide.with(this).load(image.getUrl()).into(binding.ivTransPhoto);
        }
        binding.tvItemNameTrans.setText("Item: " + item.getDisplayName());
        binding.tvSellerTrans.setText("Seller: @" + seller.getUsername());
        binding.tvPriceTrans.setText("$" + item.getPrice().toString());
        binding.tvSizeTrans.setText("Size: " + item.getSize());
    }
}