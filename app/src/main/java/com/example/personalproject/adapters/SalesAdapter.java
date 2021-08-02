package com.example.personalproject.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemSaleTransactionBinding itemSaleTransactionBinding;

        public ViewHolder(@NonNull ItemSaleTransactionBinding itemSaleTransactionBinding) {
            super(itemSaleTransactionBinding.getRoot());
            this.itemSaleTransactionBinding = itemSaleTransactionBinding;
        }

        public void bind(Item item) {
            setBackgroundColor(item);
            itemSaleTransactionBinding.tvSalesTransacTitle.setText(item.getDisplayName());
            itemSaleTransactionBinding.tvPriceSalesTransac.setText("List Price: $"
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
                    /* TODO: change item status to available, remove transaction from item, &
                            delete transaction; Note that the UI is updated when the live subscription
                            gets an update notification.
                     */
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
            itemSaleTransactionBinding.tvBuyerEmail.setText("Email"
                    + item.getTransaction().getBuyerEmail());
            itemSaleTransactionBinding.tvBuyerName.setText("Name: "
                    + item.getTransaction().getBuyer().get("name"));
            itemSaleTransactionBinding.tvBuyerPhoneSalesTransac.setText("Phone: "
                    + item.getTransaction().getBuyerPhone());
            itemSaleTransactionBinding.btnConfirmPayment.setVisibility(View.VISIBLE);
            itemSaleTransactionBinding.btnConfirmPayment.setVisibility(View.VISIBLE);
            itemSaleTransactionBinding.tvBuyerEmail.setVisibility(View.VISIBLE);
            itemSaleTransactionBinding.tvBuyerName.setVisibility(View.VISIBLE);
            itemSaleTransactionBinding.tvBuyerPhoneSalesTransac.setVisibility(View.VISIBLE);
        }

        private void initNonPendingItemView(Item item) {
            itemSaleTransactionBinding.btnCancelTransaction.setVisibility(View.INVISIBLE);
            itemSaleTransactionBinding.btnConfirmPayment.setVisibility(View.INVISIBLE);
            itemSaleTransactionBinding.btnConfirmPayment.setVisibility(View.INVISIBLE);
            itemSaleTransactionBinding.tvBuyerDetails.setVisibility(View.INVISIBLE);
            itemSaleTransactionBinding.tvBuyerEmail.setVisibility(View.INVISIBLE);
            itemSaleTransactionBinding.tvBuyerName.setVisibility(View.INVISIBLE);
            itemSaleTransactionBinding.tvBuyerPhoneSalesTransac.setVisibility(View.INVISIBLE);
        }

        private void setBackgroundColor(Item item) {
            if (item.getStatus() == Item.AVAILABLE) {
                itemSaleTransactionBinding.cardViewSalesTransac
                        .setCardBackgroundColor(Color.parseColor("#C7F09D"));
                itemSaleTransactionBinding.tvStatusSalesTransac.setText("AVAILABLE");
            } else if (item.getStatus() == Item.PENDING) {
                itemSaleTransactionBinding.cardViewSalesTransac
                        .setCardBackgroundColor(Color.parseColor("#F0F09D"));
                itemSaleTransactionBinding.tvStatusSalesTransac.setText("PENDING");
            } else {
                itemSaleTransactionBinding.cardViewSalesTransac
                        .setCardBackgroundColor(Color.parseColor("#F09D9D"));
                itemSaleTransactionBinding.tvStatusSalesTransac.setText("SOLD");

            }
        }
    }
}
