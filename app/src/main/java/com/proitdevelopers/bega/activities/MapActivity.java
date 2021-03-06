package com.proitdevelopers.bega.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.SphericalUtil;
import com.proitdevelopers.bega.utilsClasses.CustomInfoWindow;
import com.proitdevelopers.bega.R;
import com.proitdevelopers.bega.helper.Common;
import com.proitdevelopers.bega.utilsClasses.MapInfoWindowAdapter;

import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    SupportMapFragment mapFragment;

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;


    //Location

    private GoogleMap mMap;

    //Play Services
    private static final int MY_PERMISSION_REQUEST_CODE = 7192;
    private static final int PLAY_SERVICE_RES_REQUEST = 300193;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private static int UPDATE_INTERVAL = 5000; // 5 secs
    private static int FATEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;


    Marker mUserMarker, markerDestination;





    int radius = 1; // 1km
    int distance = 1; // 3km
    private static final int LIMIT = 3;






    String mPlaceLocation, mPlaceDestination;
    String getMyEndereco,getMyDestination;
    String toolbarTitle;

    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        toolbarTitle = getIntent().getStringExtra("toolbarTitle");
        if (toolbarTitle==null){
            toolbarTitle = "Mapa";
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(toolbarTitle);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }








        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        //Maps
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        setUpLocation();



    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == MY_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //The External Storage Write Permission is granted to you... Continue your left job...
                setUpLocation();
            } else {
                Toast.makeText(this, "This app needs Location permission to continue!", Toast.LENGTH_SHORT).show();
            }
        }


    }

    private void setUpLocation() {

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //Request runtime permission
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);
        } else {


            buildLocationCallBack();
            createLocationRequest();
            displayLocation();

        }
    }

    private void buildLocationCallBack() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Common.mLastLocation = locationResult.getLocations().get(locationResult.getLocations().size() - 1); //Get last location
                displayLocation();
            }
        };
    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Common.mLastLocation = location;

                if (Common.mLastLocation != null) {


                    //Create LatLng from mLastLocation and this is center point
                    LatLng center = new LatLng(Common.mLastLocation.getLatitude(), Common.mLastLocation.getLongitude());
                    // distance in metters
                    // heading 0 is northSide, 90 is east, 180 is south and 270 is west
                    // base on compact :)
                    LatLng northSide = SphericalUtil.computeOffset(center, 100000, 0);
                    LatLng southSide = SphericalUtil.computeOffset(center, 100000, 180);

                    getMyEndereco = getMyAddress(center);

                    LatLngBounds bounds = LatLngBounds.builder()
                            .include(northSide)
                            .include(southSide)
                            .build();


//                    place_location.setBoundsBias(bounds);
//                    place_location.setFilter(typeFilter);
//
//                    place_destination.setBoundsBias(bounds);
//                    place_destination.setFilter(typeFilter);
//
//
//                    //Presence system
//                    driversAvailable = FirebaseDatabase.getInstance().getReference(Common.driver_tbl);
//                    driversAvailable.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            //If have any change from Drivers table, we will reload all drivers available
//                            loadAllAvailableDriver(new LatLng(Common.mLastLocation.getLatitude(), Common.mLastLocation.getLongitude()));
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });

                    final double latitude = Common.mLastLocation.getLatitude();
                    final double longitude = Common.mLastLocation.getLongitude();


                    loadAllAvailableDriver(new LatLng(Common.mLastLocation.getLatitude(), Common.mLastLocation.getLongitude()));


                    Log.d("EDMTDEV", String.format("Your location was changed : %f / %f",
                            latitude, longitude));

                } else {
                    Log.d("EDMTDEV", "Can not get your location");
                }
            }
        });

    }

    private void loadAllAvailableDriver(final LatLng location) {

        //Add Marker
        //Here we will clear all map to delete old position of driver


        mMap.clear();
        mUserMarker = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                .position(location)
                .title(String.format("Eu"))
                .snippet(getMyEndereco));
//        mUserMarker.setTag(Common.mCurrentUser.imagem);

        //Move camera to this position
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15.0f));










    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

//        try {
//
//            boolean isSucess = googleMap.setMapStyle(
//                    MapStyleOptions.loadRawResourceStyle(this, R.raw.uber_style_map)
//            );
//
//            if (!isSucess)
//                Log.e("ERROR", "Map style load failed !!!");
//
//        } catch (Resources.NotFoundException ex) {
//            ex.printStackTrace();
//        }

        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
//        mMap.setInfoWindowAdapter(new CustomInfoWindow(this));

        mMap.setInfoWindowAdapter(new MapInfoWindowAdapter(this));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {


                if (toolbarTitle.equals("Local de entrega")){
                    getMyDestination = getMyAddress(latLng);
                    //First, check markerDestination
                    //IF is not null, just remove available marker
                    if (markerDestination != null)
                        markerDestination.remove();
                    markerDestination = mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_marker))
                            .position(latLng)
                            .title("Local de entrega")
                            .snippet(getMyDestination));


                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
                }




            }
        });


        mMap.setOnInfoWindowClickListener(this);

        if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());

    }


    private String getMyAddress(LatLng location) {
        String address="";
        try {
            Geocoder geo = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(location.latitude, location.longitude, 1);
            if (addresses.isEmpty()) {
                Log.d("EDMTDEV", "Waiting for Location");
//                Toast.makeText(mActivity.getApplicationContext(), "Waiting for Location", Toast.LENGTH_SHORT).show();

            }
            else {
                if (addresses.size() > 0) {

                    address = addresses.get(0).getAddressLine(0);

                }
            }
        }
        catch (Exception e) {
            e.printStackTrace(); // getFromLocation() may sometimes fail
        }

        return address;
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        //If marker info windows is your location, don't apply this event

        String latitude = String.valueOf(marker.getPosition().latitude);
        String longitude = String.valueOf(marker.getPosition().longitude);

        if (toolbarTitle.equals("Local de entrega")){
            if (marker.getTitle().equals("Eu")){
//                Toast.makeText(this, ""+marker.getTitle(), Toast.LENGTH_SHORT).show();

                alertaUsarLocalizacao(getMyEndereco,latitude,longitude);
            }else if (marker.getTitle().equals("Local de entrega")){

                alertaUsarLocalizacao(getMyDestination,latitude,longitude);


            }
        }


    }

    private void alertaUsarLocalizacao(String endereco, String latitude, String longitude) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(endereco);
        dialog.setMessage("Deseja utilizar esta localização?");
        dialog.setCancelable(false);

        //Set button
        dialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {


                dialogInterface.dismiss();

                Intent data = new Intent();

                data.putExtra("endereco", endereco);
                data.putExtra("latitude", latitude);
                data.putExtra("longitude", longitude);
                setResult(RESULT_OK, data);
                finish();

            }
        });

        dialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });



        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        if (item.getItemId() == R.id.menu_my_location) {
            setUpLocation();
            return true;
        }


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, ConfiguracoesActivity.class);
            startActivity(intent);

            return true;
        }



        return super.onOptionsItemSelected(item);
    }


}
