package com.example.personalproject.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.personalproject.databinding.ItemClothingBinding;
import com.example.personalproject.models.Item;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class HomeItemsAdapter extends RecyclerView.Adapter<HomeItemsAdapter.ViewHolder>
                                                                implements  Filterable {
    private List<Item> items;
    private List<Item> itemsFiltered;
    private Context context;
    private ParseGeoPoint currentBuyerLocation;

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
                String query = constraint.toString().toLowerCase().trim();
                if (query.isEmpty()) {
                    itemsFiltered = items;
                } else {
                    List<Item> filteredList = new ArrayList<>();
                    for (Item item : items) {
                        if (item.getItemBrand().toLowerCase().contains(query)
                            || item.getItemType().toLowerCase().contains(query)
                            || item.getCondition().toLowerCase().contains(query)
                            || item.getDescription().toLowerCase().contains(query)
                            || item.getDisplayName().toLowerCase().contains(query)) {
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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
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
        }

        @Override
        public void onClick(View v) {
            // TODO: navigate to item details activity
        }
    }
}
