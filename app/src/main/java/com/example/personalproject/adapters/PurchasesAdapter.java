package com.example.personalproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.personalproject.R;
import com.example.personalproject.databinding.ItemPurchaseTransactionBinding;
import com.example.personalproject.databinding.ItemSaleTransactionBinding;
import com.example.personalproject.models.Item;
import com.example.personalproject.models.Transaction;
import com.parse.ParseFile;

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

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemPurchaseTransactionBinding itemPurchaseTransactionBinding;

        public ViewHolder(@NonNull ItemPurchaseTransactionBinding itemPurchaseTransactionBinding) {
            super(itemPurchaseTransactionBinding.getRoot());
            this.itemPurchaseTransactionBinding = itemPurchaseTransactionBinding;
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
    }
}
