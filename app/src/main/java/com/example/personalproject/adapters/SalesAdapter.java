package com.example.personalproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.gesture.Gesture;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.personalproject.R;
import com.example.personalproject.activities.TransactionActivity;
import com.example.personalproject.databinding.ItemSaleTransactionBinding;
import com.example.personalproject.fragments.DashboardFragment;
import com.example.personalproject.models.Item;
import com.example.personalproject.models.Transaction;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;
import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;

import org.parceler.Parcels;

import java.util.List;

import bolts.Continuation;
import bolts.Task;

public class SalesAdapter extends RecyclerView.Adapter<SalesAdapter.ViewHolder>{
    public final String TAG = "SalesAdapter";
    private List<Item> items;
    private Context context;

    public SalesAdapter(Context context, List<Item> items){
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSaleTransactionBinding binding = ItemSaleTransactionBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SalesAdapter.ViewHolder holder, int position) {
        Item item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void handleItemAdded(Item addedItem) {
        addItem(addedItem);
        notifyDataSetChanged();
    }

    public void handleItemUpdate(Item updatedItem) {
        if (removeItem(updatedItem)) {
            addItem(updatedItem);
            Log.i(TAG, "Number of items: " + items.size());
            notifyDataSetChanged();
        }
    }

    private boolean removeItem(Item itemToRemove) {
        for (int i = 0 ; i < items.size(); i++) {
            Item curItem = items.get(i);
            if (curItem.getObjectId().equals(itemToRemove.getObjectId())) {
                items.remove(i);
                Log.i(TAG, "Removed item from list");
                return true;
            }
        }
        return false;
    }

    private void addItem(Item itemToAdd) {
        for (int i = 0; i < items.size(); i++) {
            Item curItem = items.get(i);
            if (itemToAdd.getStatus() < curItem.getStatus() ||
                (itemToAdd.getStatus() == curItem.getStatus() &&
                 itemToAdd.getCreatedAt().compareTo(curItem.getCreatedAt()) >= 0)) {
                items.add(i, itemToAdd);
                return;
            }
        }
        items.add(itemToAdd);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
        ItemSaleTransactionBinding itemSaleTransactionBinding;
        Balloon balloon;

        public ViewHolder(@NonNull ItemSaleTransactionBinding itemSaleTransactionBinding) {
            super(itemSaleTransactionBinding.getRoot());
            this.itemSaleTransactionBinding = itemSaleTransactionBinding;
            itemSaleTransactionBinding.getRoot().setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            int position = getAdapterPosition();
            Log.i(TAG, "In on long Click");
            if (position != -1) {
                Item item = items.get(position);
                if (item.getStatus() == Item.PENDING) {
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
                            .setText("Buyer Contact Information: \n" +
                                     "Name: " +   item.getTransaction().getBuyer().get("name") + "\n" +
                                     "Phone Number: " + item.getTransaction().getBuyerPhone() + "\n" +
                                    "Email: " + item.getTransaction().getBuyerEmail())
                            .setTextColor(ContextCompat.getColor(context.getApplicationContext(),
                                    R.color.white))
                            .setBackgroundColor(ContextCompat.getColor(context.getApplicationContext(),
                                    R.color.dark_salmon))
                            .setBalloonAnimation(BalloonAnimation.FADE)
                            .build();
                    balloon.showAlignBottom(itemSaleTransactionBinding.cardViewSalesTransac);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            balloon.dismiss();
                        }
                    }, 5000);
                }
            }
            return true;
        }

        public void bind(Item item) {
            itemSaleTransactionBinding.tvSalesTransacTitle.setText(item.getDisplayName());
            itemSaleTransactionBinding.tvSalesPrice.setText("List Price: $"
                                                                    + item.getPrice().toString());
            ParseFile image = item.getPhoto();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(itemSaleTransactionBinding.ivSalesTransacImage);
            }
            if (item.getStatus() == Item.PENDING) {
                initPendingItemView(item);
            } else {
                initNonPendingItemView(item);
            }

            itemSaleTransactionBinding.btnConfirmPayment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.completeSale().continueWith(new Continuation<Item, Void>() {
                        @Override
                        public Void then(Task<Item> task) throws Exception {
                            if (task.isCancelled()) {
                                // this task can't be cancelled so we don't need to handle this
                            } else if (task.isFaulted()) {
                                Exception error = task.getError();
                                Log.i(TAG, "Error while saving: " + task.getError());
                                Toast.makeText(context,
                                        "Error updating sale status. Please try again.",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                /* Note that the UI is updated when the live subscription gets an
                                   update notification for the item.*/
                                Log.i(TAG, "Save was successful");
                            }
                            return null;
                        }
                    });
                }
            });

            itemSaleTransactionBinding.btnCancelTransaction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.cancelSale().continueWith(new Continuation<Item, Void>() {
                        @Override
                        public Void then(Task<Item> task) throws Exception {
                            if (task.isCancelled()) {
                                // this task can't be cancelled so we don't need to handle this
                            } else if (task.isFaulted()) {
                                Exception error = task.getError();
                                Log.i(TAG, "Error while saving: " + task.getError());
                                Toast.makeText(context,
                                        "Error updating cancellation status. Please try again.",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                /* Note that the UI is updated when the live subscription gets an
                                   update notification for the item.*/
                                Log.i(TAG, "Cancellation was successful");
                            }
                            return null;
                        }
                    });
                }
            });
        }

        private void initPendingItemView(Item item) {
            itemSaleTransactionBinding.btnConfirmPayment.setVisibility(View.VISIBLE);
            itemSaleTransactionBinding.btnConfirmPayment.setVisibility(View.VISIBLE);
            itemSaleTransactionBinding.tvStatusSalesTransac.setText("PENDING");
            Glide.with(context).load(R.drawable.bell_alert).into(itemSaleTransactionBinding.ivIconStatus);
        }

        private void initNonPendingItemView(Item item) {
            itemSaleTransactionBinding.btnCancelTransaction.setVisibility(View.INVISIBLE);
            itemSaleTransactionBinding.btnConfirmPayment.setVisibility(View.INVISIBLE);
            itemSaleTransactionBinding.btnConfirmPayment.setVisibility(View.INVISIBLE);
            if (item.getStatus() == Item.AVAILABLE) {
                itemSaleTransactionBinding.tvStatusSalesTransac.setText("AVAILABLE");
                Glide.with(context).load(R.drawable.cart).into(itemSaleTransactionBinding.ivIconStatus);
            } else{
                itemSaleTransactionBinding.tvStatusSalesTransac.setText("SOLD");
                Glide.with(context).load(R.drawable.check_circle).into(itemSaleTransactionBinding.ivIconStatus);
            }
        }

    }
}
