package com.yeehungchong.p09_gettingmylocationenhanced;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileWriter;

public class MainActivity extends AppCompatActivity {

    TextView tvTitle, tvLat, tvLng;
    Button btnGetLocationUpdate, btnRemoveLocationUpdate, btnCheckRecords;
    private GoogleMap map;
    private FusedLocationProviderClient client;
    private LocationCallback mLocationCallback;
    private String folderLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTitle = findViewById(R.id.tvTitle);
        tvLat = findViewById(R.id.tvLat);
        tvLng = findViewById(R.id.tvLng);
        btnGetLocationUpdate = findViewById(R.id.btnGetLocationUpdate);
        btnRemoveLocationUpdate = findViewById(R.id.btnRemoveLocationUpdate);
        btnCheckRecords = findViewById(R.id.btnCheckRecords);

        tvTitle.setText("Last known location:");
        checkPermission();

        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);

        client = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        folderLocation = getFilesDir().getAbsolutePath() + "/Folder";
        File targetFile = new File(folderLocation, "data.txt");

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult!= null) {
                    Location data = locationResult.getLastLocation();
                    double lat = data.getLatitude();
                    double lng = data.getLongitude();

                    LatLng poi_currLocation = new LatLng(lat, lng);
                    mapFragment.getMapAsync(new OnMapReadyCallback(){
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            tvLat.setText("Latitude: " + lat);
                            tvLng.setText("Longitude: " + lng);

                            map = googleMap;
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(poi_currLocation, 15));
                            map.clear();
                            Marker currLocation = map.addMarker(new
                                    MarkerOptions()
                                    .position(poi_currLocation)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                            UiSettings ui = map.getUiSettings();
                            ui.setCompassEnabled(true);
                            ui.setZoomControlsEnabled(true);
                            ui.setMyLocationButtonEnabled(true);

                            File folder = new File(folderLocation);
                            if (folder.exists() == false) {
                                boolean result = folder.mkdir();
                                if (result == true) {
                                    Log.d("File Read/Write", "Folder created");
                                }
                            }
                            try {
                                FileWriter writer = new FileWriter(targetFile, true);
                                writer.write(lat + ", " + lng + "\n");
                                writer.flush();
                                writer.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    Toast.makeText(MainActivity.this,"New Location Detected\n" + "Lat: " + lat + ", " + " Lat: " + lng, Toast.LENGTH_SHORT).show();
                }
            }
        };

        btnGetLocationUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
                Toast.makeText(MainActivity.this, "Location update started", Toast.LENGTH_SHORT).show();
                LocationRequest mLocationRequest = LocationRequest.create();
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                mLocationRequest.setInterval(10000);
                mLocationRequest.setFastestInterval(5000);
                mLocationRequest.setSmallestDisplacement(100);
                client.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
            }
        });

        btnRemoveLocationUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.removeLocationUpdates(mLocationCallback);
                map.clear();
                Toast.makeText(MainActivity.this, "Location update ended", Toast.LENGTH_SHORT).show();
            }
        });

        btnCheckRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(i);
            }
        });
    }

    private boolean checkPermission() {
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED || permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    btnGetLocationUpdate.performClick();
                    Toast.makeText(MainActivity.this, "Location update started", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Permission not granted", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}