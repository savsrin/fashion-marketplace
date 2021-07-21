package com.example.personalproject.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Item")
public class Item extends ParseObject {
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
}
