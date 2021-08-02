package com.example.personalproject.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.personalproject.R;
import com.example.personalproject.activities.TransactionActivity;
import com.example.personalproject.databinding.ItemClothingBinding;
import com.example.personalproject.models.Item;
import com.example.personalproject.models.Transaction;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class HomeItemsAdapter extends RecyclerView.Adapter<HomeItemsAdapter.ViewHolder>
                                                                implements  Filterable {
    private static final String TAG = "HomeItemsAdapter";

    private List<Item> items;
    private List<Item> itemsFiltered;
    private Context context;
    private ParseGeoPoint currentBuyerLocation;
    private String filterQuery = "";

    public HomeItemsAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
        this.itemsFiltered = items;
    }

    public void setCurrentBuyerLocation(ParseGeoPoint currentBuyerLocation) {
        this.currentBuyerLocation = currentBuyerLocation;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemClothingBinding binding = ItemClothingBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemsFiltered.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return itemsFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                filterQuery = constraint.toString().toLowerCase().trim();
                if (filterQuery.isEmpty()) {
                    itemsFiltered = items;
                } else {
                    List<Item> filteredList = new ArrayList<>();
                    for (Item item : items) {
                        if (isFiltered(item)) {
                            filteredList.add(item);
                        }
                    }
                    itemsFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = itemsFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                itemsFiltered = (ArrayList<Item>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public void onItemRemoved(String removedItemObjectId) {
        boolean updateNeeded = false;
        if (removeItem(items, removedItemObjectId)) {
            updateNeeded = true;
            Log.i(TAG, "Item removed from items");
        }

        if (removeItem(itemsFiltered, removedItemObjectId)) {
            updateNeeded = true;
            Log.i(TAG, "Item removed from itemsFiltered");
        }

        if (updateNeeded) {
            notifyDataSetChanged();
        }
    }

    public void onItemAdded(Item addedItem) {
        addItem(items, addedItem);
        Log.i(TAG, "Item added to items");
        if (!filterQuery.isEmpty() && isFiltered(addedItem)) {
            addItem(itemsFiltered, addedItem);
            Log.i(TAG, "Item added to itemsFiltered");
        }
        notifyDataSetChanged();
    }

    private Boolean isFiltered(Item item) {
        return (item.getItemBrand().toLowerCase().contains(filterQuery)
                || item.getItemType().toLowerCase().contains(filterQuery)
                || item.getCondition().toLowerCase().contains(filterQuery)
                || item.getDescription().toLowerCase().contains(filterQuery)
                || item.getDisplayName().toLowerCase().contains(filterQuery));
    }

    private void addItem(List<Item> items, Item addedItem) {
        Double addedItemDistance = addedItem.getPickupLocation()
                .distanceInMilesTo(currentBuyerLocation);

        for (int i = 0; i < items.size(); i++) {
            Double curItemDistance = items.get(i).getPickupLocation()
                    .distanceInMilesTo(currentBuyerLocation);
            if (addedItemDistance <= curItemDistance) {
                items.add(i, addedItem);
                return;
            }
        }
        items.add(addedItem);
        return;
    }

    private Boolean removeItem(List<Item> items, String removedItemObjectId) {
        for (Item item : items) {
            if (item.getObjectId().equals(removedItemObjectId)) {
                items.remove(item);
                return true;
            }
        }
        return  false;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemClothingBinding itemClothingBinding;

        public ViewHolder(@NonNull ItemClothingBinding itemClothingBinding) {
            super(itemClothingBinding.getRoot());
            this.itemClothingBinding = itemClothingBinding;
        }

        public void bind(Item item) {
            itemClothingBinding.tvItemNameRV.setText(item.getDisplayName());
            itemClothingBinding.tvPriceRV.setText("$" + item.getPrice());
            itemClothingBinding.tvConditionRV.setText(item.getCondition());
            itemClothingBinding.tvSizeRV.setText(item.getSize());
            itemClothingBinding.tvTypeRV.setText(item.getItemType());
            itemClothingBinding.tvBrandRV.setText("Brand: " + item.getItemBrand());
            itemClothingBinding.tvSellerNameRV.setText("Seller: @"
                                                        + item.getSeller().getUsername());

            ParseFile image = item.getPhoto();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(itemClothingBinding.ivItemImageRV);
            }

            Double distanceToSeller = item.getPickupLocation()
                                            .distanceInMilesTo(currentBuyerLocation);
            distanceToSeller = round(distanceToSeller, 2);
            itemClothingBinding.tvSellerDistanceRV.setText(distanceToSeller.toString() + " miles");

            itemClothingBinding.btnBuyRv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != -1) {
                        Item curItem = itemsFiltered.get(position);
                        ParseUser buyer = ParseUser.getCurrentUser();
                        Intent intent = new Intent(context, TransactionActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("buyer", buyer);
                        bundle.putParcelable("item", curItem);
                        intent.putExtras(bundle);
                        Activity activity = (Activity) context;
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            // TODO: navigate to item details activity
        }
    }
}