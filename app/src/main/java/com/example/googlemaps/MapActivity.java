package com.example.googlemaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private Boolean locatinPermissionGranted = false;
    private static final   int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private  GoogleMap mMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        getLocationPermision();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locatinPermissionGranted = false;
        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if (grantResults.length >0 ){
                    for (int i = 0 ; i<grantResults.length;++i){
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
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

    private void getLocationPermision(){

        String[] permision = {FINE_LOCATION,COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION )== PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),COARSE_LOCATION )== PackageManager.PERMISSION_GRANTED){
                locatinPermissionGranted = true;
            }else {
                ActivityCompat.requestPermissions(this,permision,LOCATION_PERMISSION_REQUEST_CODE);
            }
        }

    }

    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Toast.makeText(MapActivity.this,"Map is ready",Toast.LENGTH_SHORT).show();
        mMap = googleMap;
    }
}
