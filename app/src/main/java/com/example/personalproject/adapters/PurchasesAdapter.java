package com.example.personalproject.adapters;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.personalproject.R;
import com.example.personalproject.databinding.ItemPurchaseTransactionBinding;
import com.example.personalproject.databinding.ItemSaleTransactionBinding;
import com.example.personalproject.models.Item;
import com.example.personalproject.models.Transaction;
import com.parse.ParseFile;
import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;

import java.util.List;

public class PurchasesAdapter extends RecyclerView.Adapter<PurchasesAdapter.ViewHolder> {
    public final String TAG = "PurchasesAdapter";
    private List<Transaction> transactions;
    private Context context;

    public PurchasesAdapter(Context context, List<Transaction> transactions){
        this.context = context;
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPurchaseTransactionBinding binding = ItemPurchaseTransactionBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false);
        return new PurchasesAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.bind(transaction);

    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
        ItemPurchaseTransactionBinding itemPurchaseTransactionBinding;

        public ViewHolder(@NonNull ItemPurchaseTransactionBinding itemPurchaseTransactionBinding) {
            super(itemPurchaseTransactionBinding.getRoot());
            this.itemPurchaseTransactionBinding = itemPurchaseTransactionBinding;
            itemPurchaseTransactionBinding.getRoot().setOnLongClickListener(this);
        }

        public void bind(Transaction transaction) {
            Item item = transaction.getCurItem();
            itemPurchaseTransactionBinding.tvPurchaseTransacTitle
                                            .setText(item.getDisplayName());
            itemPurchaseTransactionBinding.tvPricePurchaseTransac
                                            .setText("List Price: $" + item.getPrice().toString());
            itemPurchaseTransactionBinding.tvSellerPurchaseTransac
                                            .setText("Seller: " + item.getSeller().get("name"));

            ParseFile image = item.getPhoto();
            if (image != null) {
                Glide.with(context).load(image.getUrl())
                        .into(itemPurchaseTransactionBinding.ivPurchaseTransacImage);
            }
            if (transaction.isPaid()) {
                itemPurchaseTransactionBinding.tvStatusPurchase.setText("PURCHASED");
                Glide.with(context).load(R.drawable.check_circle).into(itemPurchaseTransactionBinding.ivPurchaseStatus);
            } else  {
                itemPurchaseTransactionBinding.tvStatusPurchase.setText("PENDING");
                Glide.with(context).load(R.drawable.bell_alert).into(itemPurchaseTransactionBinding.ivPurchaseStatus);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            int position = getAdapterPosition();
            Balloon balloon;
            Log.i(TAG, "In on long Click");
            if (position != -1) {
                Transaction transaction = transactions.get(position);
                balloon = new Balloon.Builder(context.getApplicationContext())
                        .setArrowSize(20)
                        .setArrowOrientation(ArrowOrientation.TOP)
                        .setIsVisibleArrow(true)
                        .setArrowPosition(0.9f)
                        .setWidthRatio(1.0f)
                        .setHeight(200)
                        .setTextSize(20f)
                        .setCornerRadius(4f)
                        .setAlpha(0.9f)
                        .setText("Pickup Address: " + transaction.getCurItem().getAddress())
                        .setTextColor(ContextCompat.getColor(context.getApplicationContext(),
                                R.color.white))
                        .setBackgroundColor(ContextCompat.getColor(context.getApplicationContext(),
                                R.color.dark_salmon))
                        .setBalloonAnimation(BalloonAnimation.FADE)
                        .build();
                balloon.showAlignBottom(itemPurchaseTransactionBinding.cardViewPurchasesTransac);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        balloon.dismiss();
                    }
                }, 5000);
            }
            return true;
        }
    }
}
