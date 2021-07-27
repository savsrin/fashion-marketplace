package com.example.personalproject.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
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
import com.parse.ParseUser;

public class TransactionActivity extends AppCompatActivity {
    private static final String TAG = "TransactionActivity";
    ActivityTransactionBinding binding;
    ParseUser buyer;
    ParseUser seller;
    Item item;
    String buyerPhone;
    String buyerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

                // TODO: Implement purchase method
            }
        });
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