package com.example.personalproject.models;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import java.net.URI;
import java.net.URISyntaxException;

import bolts.Continuation;
import bolts.Task;
import bolts.TaskCompletionSource;

@ParseClassName("Item")
public class Item extends ParseObject {
    // Order in which the items on the Sales tab of the Dashboard will be sorted for the seller.
    public static final Integer PENDING = 0;
    public static final Integer AVAILABLE = 1;
    public static final Integer SOLD = 2;

    public static final String TAG = "ItemClass";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_SIZE = "size";
    public static final String KEY_LOCATION = "pickupLocation";
    public static final String KEY_DISPLAY = "display_name";
    public static final String KEY_BRAND = "brand";
    public static final String KEY_CONDITION = "condition";
    public static final String KEY_TYPE = "type";
    public static final String KEY_PRICE = "price";
    public static final String KEY_PHOTO = "photo";
    public static final String KEY_SELLER = "seller";
    public static final String KEY_STATUS = "status";
    public static final String KEY_TRANSACTION = "transaction";

    public String getDescription() {return getString(KEY_DESCRIPTION);}
    public String getSize() {return getString(KEY_SIZE);}
    public ParseGeoPoint getPickupLocation() {return getParseGeoPoint(KEY_LOCATION);}
    public String getDisplayName() {return getString(KEY_DISPLAY);}
    public String getItemBrand() {return getString(KEY_BRAND);}
    public String getCondition() {return getString(KEY_CONDITION);}
    public String getItemType() {return getString(KEY_TYPE);}
    public Double getPrice() {return getDouble(KEY_PRICE);}
    public ParseFile getPhoto() {return getParseFile(KEY_PHOTO);}
    public ParseUser getSeller() {return getParseUser(KEY_SELLER);}
    public @Nullable Transaction getTransaction() {return (Transaction) getParseObject(KEY_TRANSACTION);}
    public Integer getStatus() {return getInt(KEY_STATUS);}

    public void setDescription(String description) {put(KEY_DESCRIPTION, description );}
    public void setSize(String size) {put(KEY_SIZE, size);}
    public void setDisplayName(String displayName) {put(KEY_DISPLAY, displayName);}
    public void setItemType(String type) {put(KEY_TYPE, type);}
    public void setCondition(String condition) {put(KEY_CONDITION, condition);}
    public void setPrice(Double price) {put(KEY_PRICE, price);}
    public void setPickupLocation(ParseGeoPoint pickupLocation) {put(KEY_LOCATION, pickupLocation);}
    public void setBrand(String brand) {put(KEY_BRAND, brand);}
    public void setPhoto(ParseFile parseFile) { put(KEY_PHOTO, parseFile);}
    public void setSeller(ParseUser seller) { put(KEY_SELLER,seller); }
    public void setTransaction(@Nullable Transaction transaction) {put(KEY_TRANSACTION, transaction);}
    public void setStatus(Integer status) {put(KEY_STATUS, status);}

    public Task<Item> purchase(String buyerEmail, String buyerPhone) {
        Transaction transaction = new Transaction();
        transaction.setCurItem(Item.this);
        transaction.setBuyer(ParseUser.getCurrentUser());
        transaction.setBuyerEmail(buyerEmail);
        transaction.setBuyerPhone(buyerPhone);
        transaction.setSeller(Item.this.getSeller());
        transaction.setPaymentStatus(false);

        Item.this.setTransaction(transaction);
        Item.this.setStatus(PENDING);

        final TaskCompletionSource<Item> tcs = new TaskCompletionSource<>();
        Item.this.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    tcs.setResult(Item.this);
                } else {
                    tcs.setError(e);
                }
            }
        });
        return tcs.getTask();
    }

    public Task<Item> completeSale() {
        Item.this.getTransaction().setPaymentStatus(true);
        Item.this.setStatus(SOLD);

        final TaskCompletionSource<Item> tcs = new TaskCompletionSource<>();
        Item.this.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    tcs.setResult(Item.this);
                } else {
                    tcs.setError(e);
                }
            }
        });
        return tcs.getTask();
    }

    public Task<Item> cancelSale() {
        Item.this.setStatus(Item.AVAILABLE);
        Transaction transaction = Item.this.getTransaction();
        Item.this.remove("transaction");

        final TaskCompletionSource<Item> tcs = new TaskCompletionSource<>();
        Item.this.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.i(TAG, "error saving item for available status");
                    tcs.setError(e);
                } else {
                    Log.i(TAG, "save successful for available status.");
                    transaction.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.i(TAG, "error deleting transaction object");
                            }  else {
                                Log.i(TAG, "deleted transaction object");
                            }
                            tcs.setResult(Item.this);
                        }
                    });
                }
            }
        });
        return tcs.getTask();
    }
}


