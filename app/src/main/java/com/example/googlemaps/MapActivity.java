package com.example.googlemaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final float DEFAULT_ZOOM = 15f;
    private Boolean locatinPermissionGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private GoogleMap mMap;
    //device location
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        getLocationPermision();
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Toast.makeText(MapActivity.this, "Map is ready", Toast.LENGTH_SHORT).show();
        mMap = googleMap;
        if (locatinPermissionGranted)
            getDeviceLocation();
        //add point to your location
        mMap.setMyLocationEnabled(true);
        //hide reocate button
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting current location");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (locatinPermissionGranted) {
                final Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: completed");
                            Location currentLocation = (Location) task.getResult();
                            if (currentLocation !=null)
                                 movingCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);
                        } else {
                            Toast.makeText(MapActivity.this, "cann't get location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: securityException " + e.getMessage());
        }
    }

    private void movingCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "movingCamera: moving camera on " + latLng.latitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void getLocationPermision() {
        String[] permision = {FINE_LOCATION, COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locatinPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permision, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permision, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locatinPermissionGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; ++i) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            locatinPermissionGranted = false;
                            return;
                        }
                    }
                    locatinPermissionGranted = true;
                    //init our map
                    initMap();
                }
            }
        }
    }
}
