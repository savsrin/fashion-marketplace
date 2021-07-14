package com.example.personalproject.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("UserMeasurement")
public class UserMeasurement extends ParseObject {
    public static final String KEY_HEIGHT = "height";
    public static final String KEY_HIP = "hip";
    public static final String KEY_WAIST = "waist";
    public static final String KEY_CHEST = "chest";
    public static final String KEY_WEIGHT = "weight";

    public Double getHeight() {return getDouble(KEY_HEIGHT); }
    public Double getHip() {return getDouble(KEY_HIP); }
    public Double getWaist() {return getDouble(KEY_WAIST); }
    public Double getChest() {return getDouble(KEY_CHEST); }
    public Double getWeight() {return getDouble(KEY_WEIGHT); }

    public void setHeight(Double height) {put(KEY_HEIGHT, height); }
    public void setHip(Double hip) {put(KEY_HIP, hip); }
    public void setChest(Double chest) {put(KEY_CHEST, chest); }
    public void setWaist(Double waist) {put(KEY_WAIST, waist); }
    public void setWeight(Double weight) {put(KEY_WEIGHT, weight); }

}

