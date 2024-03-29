package com.example.personalproject.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.personalproject.BitmapScaler;
import com.example.personalproject.R;
import com.example.personalproject.databinding.FragmentCreateBinding;
import com.example.personalproject.models.Item;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import es.dmoral.toasty.Toasty;

/**
 * This fragment contains the implementation for a user to upload a clothing item
 * that they wish to sell to the marketplace.
 */
public class CreateFragment extends Fragment {

    public static final String TAG = "CreateFragment";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    public static final String photoFileName = "photo.jpg";
    public static final String resizedFileName = "photo_resized.jpg";

    private FragmentCreateBinding binding;
    private AutocompleteSupportFragment autocompleteFragment;
    private String size;
    private String condition;
    private String type;
    private File photoFile;
    private String address;
    // this is cached after each submission
    private ParseGeoPoint pickupLocation;

    public CreateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "Launching on create fragment \n");
        super.onCreate(savedInstanceState);
        Places.initialize(getContext(), getString(R.string.google_maps_api_key));
        PlacesClient placesClient = Places.createClient(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       binding = FragmentCreateBinding.inflate(getLayoutInflater());
       return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // set up location autocomplete fragment
        autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() +"," + place.getLatLng());
                pickupLocation = new ParseGeoPoint(place.getLatLng().latitude, place.getLatLng().longitude);
                address = place.getAddress();
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i(TAG, "An error occurred: " + status);
                pickupLocation = null;
            }
        });

       Spinner itemSizeSpinner = binding.itemSizeSpinner;
       ArrayAdapter<CharSequence> itemSizeAdapter = new ArrayAdapter<CharSequence>(
               getContext(),
               android.R.layout.simple_spinner_item,
               getContext().getResources().getStringArray(R.array.sizing_array)){
            @Override
            public boolean isEnabled(int position){
                // first item disabled; used as hint
                return  position != 0;
            }

            @Override
            public View getDropDownView(int position,
                                        View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        itemSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemSizeSpinner.setAdapter(itemSizeAdapter);
        itemSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 ) {
                    Log.i(TAG, parent.getItemAtPosition(position).toString());
                    size = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i(TAG, "No item size selected");
                size = "";

            }
        });

        Spinner itemConditionSpinner = binding.conditionSpinner;
        ArrayAdapter<CharSequence> itemConditionAdapter = new ArrayAdapter<CharSequence>(
                getContext(),
                android.R.layout.simple_spinner_item,
                getContext().getResources().getStringArray(R.array.condition_array)){
            @Override
            public boolean isEnabled(int position){
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        itemConditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemConditionSpinner.setAdapter(itemConditionAdapter);
        itemConditionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 ) {
                    Log.i(TAG, parent.getItemAtPosition(position).toString());
                    condition = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i(TAG, "No item condition selected");
                condition = "";
            }
        });

        Spinner itemTypeSpinner = binding.itemTypeSpinner;
        ArrayAdapter<CharSequence> itemTypeAdapter = new ArrayAdapter<CharSequence>(
                getContext(),
                android.R.layout.simple_spinner_item,
                getContext().getResources().getStringArray(R.array.item_type)){
            @Override
            public boolean isEnabled(int position){
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        itemTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemTypeSpinner.setAdapter(itemTypeAdapter);
        itemTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 ) {
                    Log.i(TAG, parent.getItemAtPosition(position).toString());
                    type = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i(TAG, "No item type selected");
                type = "";

            }
        });

        binding.btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = binding.etDescription.getText().toString();
                String  displayName = binding.etItemName.getText().toString();
                String brand = binding.etBrand.getText().toString();
                String priceString = binding.etPrice.getText().toString();

                if (description.isEmpty()
                        || displayName.isEmpty()
                        || brand.isEmpty()
                        || priceString.isEmpty()
                        || condition.isEmpty()
                        || type.isEmpty()
                        || size.isEmpty()
                        || pickupLocation == null
                 ) {
                    Toasty.error(getContext(),
                            "You have left one or more required fields empty. " +
                                    "Please enter all information.",
                            Toast.LENGTH_SHORT, true).show();
                    return;
                }

                Double price = Double.parseDouble(priceString);
                if(price <= 0 ) {
                    Toasty.error(getContext(),
                            "You have entered an invalid price",
                            Toast.LENGTH_SHORT, true).show();
                    return;
                }

                if (photoFile == null || binding.ivItemImage.getDrawable() == null) {
                    Toasty.error(getContext(), "You must submit an image!", Toast.LENGTH_SHORT).show();
                    return;
                }

                ParseUser currentUser = ParseUser.getCurrentUser();
                saveItem(currentUser,
                        brand,
                        description,
                        displayName,
                        price);
            }
        });
    }

    private void saveItem(ParseUser currentUser,
                          String brand,
                          String description,
                          String displayName,
                          Double price) {
        Item item = new Item();
        item.setBrand(brand);
        item.setCondition(condition);
        item.setDescription(description);
        item.setDisplayName(displayName);
        item.setPhoto(new ParseFile(photoFile));
        item.setItemType(type);
        item.setPickupLocation(pickupLocation);
        item.setSize(size);
        item.setPrice(price);
        item.setSeller(currentUser);
        item.setStatus(Item.AVAILABLE);
        item.setAddress(address);
        item.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e !=null) {
                    Log.e(TAG, "Error while saving", e);
                    Toasty.error(getContext(), "error while saving.", Toast.LENGTH_SHORT, true).show();
                    return;
                }
                Log.i(TAG, "Item save was successful!");
                binding.etDescription.setText("");
                binding.ivItemImage.setImageResource(0);
                binding.etItemName.setText("");
                binding.etPrice.setText("");
                binding.etBrand.setText("");
                binding.etDescription.setText("");
                binding.conditionSpinner.setSelection(0);
                binding.itemSizeSpinner.setSelection(0);
                binding.itemTypeSpinner.setSelection(0);
            }
        });

        // reset globals so that error checking for next submit works as expected
        condition = "";
        type = "";
        size = "";
        photoFile = null;
    }

    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.personalproject.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    private File getPhotoFileUri(String photoFileName) {
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }
        return new File(mediaStorageDir.getPath() + File.separator + photoFileName);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == android.app.Activity.RESULT_OK) {
                // by this point we have the camera photo on disk
                photoFile = getPhotoFileUri(photoFileName);
                Bitmap takenImage = rotateBitmapOrientation(photoFile.getAbsolutePath());
                Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(takenImage, 500);
                resizedBitmap = cropSquare(resizedBitmap);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
                // Create a new file for the resized bitmap (`getPhotoFileUri` defined above)
                File resizedFile = getPhotoFileUri(resizedFileName);
                try {
                    resizedFile.createNewFile();
                    FileOutputStream fos = null;
                    fos = new FileOutputStream(resizedFile);
                    fos.write(bytes.toByteArray());
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Load the taken image into a preview
                photoFile = resizedFile;
                binding.ivItemImage.setImageBitmap(resizedBitmap);
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Bitmap cropSquare(Bitmap bitmap) {
        // Tutorial: https://stackoverflow.com/questions/6908604/android-crop-center-of-bitmap
        int minLength = Math.min(bitmap.getHeight(), bitmap.getWidth());
        return Bitmap.createBitmap(bitmap, 0, 0, minLength, minLength);
    }

    private Bitmap rotateBitmapOrientation(String photoFilePath) {
        // Create and configure BitmapFactory
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFilePath, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(photoFilePath, opts);
        // Read EXIF Data
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        // TODO: add switch statement here
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        // Rotate Bitmap
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
        return rotatedBitmap;
    }
}