package com.example.locationtracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.pm.PackageManager;

import android.location.Location;
import android.location.LocationListener;

import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements LocationListener {
    //DB
    final String FIREBASE_TAG = "firebase";
    final String INPUT_TAG = "input_tag";
    final String USERS_COLLECTION = "users";
    final String LOCATIONS_COLLECTION = "locations_samples";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usersCollectionRef = db.collection(USERS_COLLECTION);
    CollectionReference locationsCollectionRef = db.collection(LOCATIONS_COLLECTION);
    //Collections
    ArrayList<Map<String, Object>> usersCollectionData = new ArrayList<Map<String, Object>>();
    ArrayList<Map<String, Object>> locationsCollectionData = new ArrayList<Map<String, Object>>();

    //Layout
    EditText locationIDInput;
    EditText userId;
    TextView wrongLocationId;
    SwitchCompat onOffToggle;
    int location_counter = 0;
    boolean locationIdValid = false;
    boolean userIdValid = false;
    Button exitButton;

    //Location
    private LocationManager locationManager;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //init location
        initLocationServices();
        //init DB
        initUsersCollectionData();
        initLocationsCollectionData();
        //init layout
        locationIDInput = findViewById(R.id.locationIDInput);
        userId = findViewById(R.id.userId);

        wrongLocationId = findViewById(R.id.wrongLocationId);
        onOffToggle = findViewById(R.id.onOffToggle);

        exitButton = findViewById(R.id.exitBtn);
        //listeners
        setLocationIdTextListener();
        setExitBtnListener();
        setUserIdTextListener();
    }

    private void setExitBtnListener() {
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                System.exit(0);
            }
        });
    }

    private void initLocationServices() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(INPUT_TAG, "Missing permissions for location services.");
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 123);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000L, (float) 0, this);
    }

    private void initUsersCollectionData() {
        usersCollectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(INPUT_TAG, "Cant get collection from db => " + error);
                }
                usersCollectionData.clear();
                for (QueryDocumentSnapshot doc : value) {
                    Map<String, Object> data = doc.getData();
                    usersCollectionData.add(data);
                }
                Log.i(INPUT_TAG, "users collection => " + usersCollectionData);
            }
        });
    }

    private void initLocationsCollectionData() {
        locationsCollectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(INPUT_TAG, "Cant get collection from db => " + error);
                }
                locationsCollectionData.clear();
                for (QueryDocumentSnapshot doc : value) {
                    Map<String, Object> data = doc.getData();
                    locationsCollectionData.add(data);
                }
                Log.i(FIREBASE_TAG, "locations collection => " + locationsCollectionData);

            }
        });
    }

    private void setLocationIdTextListener() {
        locationIDInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String charSequence = editable.toString();
                boolean isValidEmail = Patterns.EMAIL_ADDRESS.matcher(charSequence).matches();
                if (TextUtils.isEmpty(charSequence) || isValidEmail) {
                    wrongLocationId.setVisibility(View.INVISIBLE);
                    userId.setVisibility(View.INVISIBLE);
                    locationIdValid = false;
                } else if (!isValidEmail) {
                    wrongLocationId.setVisibility(View.VISIBLE);
                    wrongLocationId.setText("Wrong Location Id");
                    userId.setVisibility(View.INVISIBLE);
                    locationIdValid = false;
                }
                if (isValidEmail && !getUsersNames().contains(charSequence)) {
                    wrongLocationId.setText("Unrecognized email !");
                    wrongLocationId.setVisibility(View.VISIBLE);
                    locationIdValid = false;
                }
                if (getUsersNames().contains(charSequence)) {
                    wrongLocationId.setVisibility(View.INVISIBLE);
                    userId.setVisibility(View.VISIBLE);
                    locationIdValid = true;
                }
                Log.w(INPUT_TAG, "logged users: => " + getUsersNames().toString());
                Log.w(INPUT_TAG, "input: => " + charSequence);

            }
        });
    }

    private void setUserIdTextListener() {
        userId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String charSequence = editable.toString();
                boolean isValidEmail = Patterns.EMAIL_ADDRESS.matcher(charSequence).matches();
                userIdValid = !TextUtils.isEmpty(charSequence) && isValidEmail;
                Log.w(INPUT_TAG, "userID: => " + charSequence);
                if (userIdValid && locationIdValid) {
                    onOffToggle.setVisibility(View.VISIBLE);

                } else {
                    onOffToggle.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public ArrayList<String> getUsersNames() {
        ArrayList<String> ids = new ArrayList<String>();
        for (Map<String, Object> user : usersCollectionData) {
            ids.add(user.get("email").toString());
        }
        return ids;
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        Log.d(INPUT_TAG, "Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude() + " counter=" + location_counter++);
        if (userIdValid && locationIdValid && onOffToggle.isChecked()) {
            insetNewLocation();


        }
    }

    private void insetNewLocation() {
        Query query = locationsCollectionRef.whereEqualTo("id", locationIDInput.getText().toString());
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean found = false;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserLocations doc = document.toObject(UserLocations.class);
                                Log.d(FIREBASE_TAG, document.getId() + " => " + doc);
                                LocationSample newSampleLocation = getLocationSample();
                                for (LocationSample sample : doc.locationSamples) {
                                    if (sample.userId.equals(newSampleLocation.userId)) {
                                        doc.locationSamples.remove(sample);
                                        break;
                                    }
                                }
                                doc.locationSamples.add(0, newSampleLocation);
                                document.getReference().set(doc);
                                Log.d(FIREBASE_TAG, "after change => " + document.getId() + " => " + doc);

                            }
                        } else {
                            Log.d(FIREBASE_TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    @NonNull
    private LocationSample getLocationSample() {
        LocationSample loc = new LocationSample();
        loc.userId = userId.getText().toString();
        loc.timestamp = Timestamp.now();
        loc.longitude = currentLocation.getLongitude();
        loc.latitude = currentLocation.getLatitude();
        return loc;
    }

}