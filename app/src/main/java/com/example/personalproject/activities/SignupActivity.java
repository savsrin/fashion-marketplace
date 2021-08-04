package com.example.personalproject.activities;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.personalproject.models.UserMeasurement;
import com.example.personalproject.databinding.ActivitySignupBinding;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;


public class SignupActivity extends AppCompatActivity {

    public static final String TAG = "SignupActivity";

    ActivitySignupBinding binding;
    String name;
    String email;
    String username;
    String password;
    String chest_measurement;
    String height_measurement;
    String waist_measurement;
    String weight_measurement;
    String hip_measurement;
    UserMeasurement measurement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("instathrift");

        if (getIntent().getExtras() != null) {
            username = getIntent().getStringExtra("username");
            password = getIntent().getStringExtra("password");
        } else {
            Log.i(TAG, "Bundle is null");
        }

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = binding.etEmailSignup.getText().toString();
                name = binding.etNameSignup.getText().toString();
                chest_measurement = binding.etChestSignup.getText().toString();
                height_measurement = binding.etHeightSignup.getText().toString();
                waist_measurement = binding.etWaistSignup.getText().toString();
                weight_measurement = binding.etWeightSignup.getText().toString();
                hip_measurement = binding.etHipSignup.getText().toString();

                if (username.isEmpty()
                        || password.isEmpty()
                        || email.isEmpty()
                        || name.isEmpty()
                        || chest_measurement.isEmpty()
                        || height_measurement.isEmpty()
                        || waist_measurement.isEmpty()
                        || weight_measurement.isEmpty()
                        || hip_measurement.isEmpty()
                ) {
                    Toast.makeText(SignupActivity.this,
                            "You have left one or more required fields empty. " +
                                    "Please enter all information.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                measurement = new UserMeasurement();

                try {
                    measurement.setChest(Double.parseDouble(chest_measurement));
                    measurement.setHeight(Double.parseDouble(height_measurement));
                    measurement.setWaist(Double.parseDouble(waist_measurement));
                    measurement.setWeight(Double.parseDouble(weight_measurement));
                    measurement.setHip(Double.parseDouble(hip_measurement));
                } catch (Exception e)  {
                    Toast.makeText(SignupActivity.this,
                            "One or more of the measurements you have entered are invalid.",
                            Toast.LENGTH_SHORT).show();
                }
                measurement.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e !=null) {
                            Log.e(TAG, "Error while saving measurement", e);
                            Toast.makeText(SignupActivity.this, "error while saving.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Log.i(TAG, "Measurement save was successful!");
                        signupUser();
                    }
                });
            }
        });
    }

    private void signupUser() {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.put("measurement", measurement);
        user.put("name", name);
       user.signUpInBackground(new SignUpCallback() {
           @Override
           public void done(ParseException e) {
               if (e == null) {
                   goMainActivity();
                   Toast.makeText(SignupActivity.this, "Success!", Toast.LENGTH_SHORT).show();
               } else {
                   // Sign up didn't succeed
                   Log.e(TAG, "Issue with signup", e);
                   // TODO: better error message
                   Toast.makeText(SignupActivity.this,
                           e.toString(),
                           Toast.LENGTH_SHORT).show();
               }
           }
       });
    }

    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        // user can't go back to signup screen
        finish();
    }
}