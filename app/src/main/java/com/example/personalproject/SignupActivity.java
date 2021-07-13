package com.example.personalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.personalproject.databinding.ActivityLoginBinding;
import com.example.personalproject.databinding.ActivitySignupBinding;

public class SignupActivity extends AppCompatActivity {

    public static final String TAG = "SignupActivity";
    ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
    }

}