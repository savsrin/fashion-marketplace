package com.example.personalproject.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Transaction")
public class Transaction extends ParseObject {
    public static final String KEY_PICKUP_TIME = "buyerPickupTime";
    public static final String KEY_BUYER = "buyer";
    public static final String KEY_ITEM = "item";
    public static final String KEY_BUYER_EMAIL = "buyerEmail";
    public static final String KEY_BUYER_PHONE = "buyerPhone";
    public static final String KEY_PAID = "paid";
    public static final String KEY_DELIVERED = "delivered";
    public static final String KEY_CONTACTED = "buyerContacted";

    public Date getPickupTime () {return getDate(KEY_PICKUP_TIME);}
    public ParseUser getBuyer () {return getParseUser(KEY_BUYER);}
    public Item getCurItem () {return (Item) getParseObject(KEY_ITEM);}
    public String getBuyerEmail () { return getString(KEY_BUYER_EMAIL);}
    public String getBuyerPhone () { return getString(KEY_BUYER_PHONE);}
    public Boolean isPaid () { return getBoolean(KEY_PAID);}
    public Boolean isDelivered () { return getBoolean(KEY_DELIVERED);}
    public Boolean isBuyerContacted () { return getBoolean(KEY_CONTACTED);}

    public void setPickupTime(Date time) { put(KEY_PICKUP_TIME, time);}
    public void setBuyer(ParseUser buyer) { put(KEY_BUYER, buyer);}
    public void setCurItem(Item item) { put(KEY_ITEM, item);}
    public void setBuyerEmail(String email) { put(KEY_BUYER_EMAIL, email);}
    public void setPaymentStatus(Boolean paid) { put(KEY_PAID, paid);}
    public void setDeliveryStatus(Boolean delivered) { put(KEY_PAID, delivered);}
    public void setContactedStatus(Boolean contacted) { put(KEY_CONTACTED, contacted);}

}
