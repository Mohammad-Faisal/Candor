package com.example.candor.candor.map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.candor.candor.profile.Users;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.example.candor.candor.R;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
 GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private GoogleMap mMap;

    Button setvalue;
    Button confirm;
    private String water="";
    Button ok1;

    String usernameglobal="";


    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private Location mLastKnownLocation;
    private static final String TAG = MapsActivity.class.getSimpleName();
    private CameraPosition mCameraPosition;


    private final LatLng mDefaultLocation = new LatLng(23,90);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;




    LocationSettingsRequest.Builder builder;
    LatLng dhaka = new LatLng(23.7256, 90.3925);
    LatLng usa = new LatLng(40.273502, -86.126976);
    LatLng latLng1final;

    LocationRequest mLocationRequest;
    Task<LocationSettingsResponse> task;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;



    //for getting current location
    private double longitude;
    private double latitude;
    private GoogleApiClient googleApiClient;





    //Database is here

    private DatabaseReference markerdata;

    MyDialog dialog;
    private  int setvaluechecked=0;
    String address;

    List<Marker> markerList = new ArrayList<Marker>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate: suru");

        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        setContentView(R.layout.activity_maps);

        setvalue=(Button) findViewById(R.id.setvalue);
        confirm=(Button) findViewById(R.id.confirm);
        confirm.setVisibility(View.GONE);



        datacheck();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //database is here
        markerdata=FirebaseDatabase.getInstance().getReference();

        dialog = new MyDialog(this);




        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: suru");
        

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        getDeviceLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        settingsRequest2(MapsActivity.this);//keep asking if imp or do whatever
                        break;
                }
                break;
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: suru");
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(false);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dhaka, DEFAULT_ZOOM));

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapsActivity.this));
        getLocationPermission();
        updateLocationUI();
        settingsRequest2(MapsActivity.this);
        changeinlocationdata();
        setvalue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //settingsRequest2(MapsActivity.this);
                Marker marker;
                Log.d(TAG,"click1");
                getDeviceLocation();
                openMyDialog();

                Log.d(TAG,"click4");
                confirm.setVisibility(View.VISIBLE);
                setvalue.setVisibility(View.GONE);


            }
        });



        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                getinfo();
                Log.d(TAG, "onClick: confirm" +water );
                setvalue.setVisibility(View.GONE);

                if(mLastKnownLocation!=null){
                    LatLng latLng=new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude());
                    Log.d(TAG,"click2");
                }
                else {
                    Log.d(TAG,"click3");
                    getCurrentLocation();
                    mLastKnownLocation=new Location("");
                    mLastKnownLocation.setLatitude(latitude);
                    mLastKnownLocation.setLongitude(longitude);
                }
                address=getaddress(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
                latLng1final=new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
                addtodatabase();

                Toast.makeText(getApplicationContext(), address,
                        Toast.LENGTH_LONG).show();


                confirm.setVisibility(View.GONE);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker markerfinal) {
                try{
                    if(markerfinal.isInfoWindowShown()){
                        markerfinal.hideInfoWindow();
                    }else{
                        markerfinal.showInfoWindow();
                    }
                }catch (NullPointerException e){
                    Log.e(TAG, "onClick: NullPointerException: " + e.getMessage() );
                }

                return false;
            }
        });



    }

    private void getCurrentLocation() {
        mMap.clear();
        Log.d(TAG,"current1");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            Log.d(TAG,"current2");
            //Getting longitude and latitude
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            moveMap();
        }
        else Log.d(TAG,"current3");

    }


    private void changeinlocationdata(){
        Log.d(TAG, "changeinlocationdata: kebol1");

        Long timestamp1;

        DatabaseReference ttlRef=FirebaseDatabase.getInstance().getReference().
                child("location");

        long cutoff = new Date().getTime() - TimeUnit.MILLISECONDS.convert(5, TimeUnit.HOURS);
        Query oldItems = ttlRef.orderByChild("timestamp").endAt(cutoff);
        oldItems.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot: snapshot.getChildren()) {
                    itemSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });

        markerdata.child("location").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG,"kebol2");

                        for(DataSnapshot key1 : dataSnapshot.getChildren()){
                            LatLng latLng1=usa;
                            String author1="";
                            String water1="";
                            String place1="";
                            String uid1="";


                            // Locationdetail ll1= key1.getValue(Locationdetail.class);

                            for(DataSnapshot key2: key1.getChildren()){


                                if(key2.getKey().equals("latlng")){
                                    Double d1 =  key2.child("latitude").getValue(Double.class);
                                    Double d2 =  key2.child("longitude").getValue(Double.class);
                                    latLng1=new LatLng(d1,d2);

                                }
                                else if(key2.getKey().equals("waterlevel")){
                                    water1=key2.getValue(String.class);
                                }
                                else if(key2.getKey().equals("placename")){
                                    place1=key2.getValue(String.class);
                                }
                                else if(key2.getKey().equals("author")){
                                    author1=key2.getValue(String.class);
                                }
                              //  else if()
                                else if(key2.getKey().equals("uid")) uid1=key2.getValue(String.class);
                               // else timestamp1=key2.getValue(Long.class);





                            }
                            if(uid1!=""){
                                Log.d(TAG, "onDataChange: kebol3");
                                String snippet = "\n"+"Social hero: "+author1 +"\n"+
                                        "\n"+"Address: " + place1 + "\n" +"\n" +
                                        "Water level: " + water1 + "\n";

                                MarkerOptions options = new MarkerOptions()
                                        .position(latLng1)
                                        .title("Information:")
                                        .snippet(snippet)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_markerdefault));

                                if(water1.equals(getString(R.string.gorali))){
                                    options = new MarkerOptions()
                                            .position(latLng1)
                                            .title("Information:")
                                            .snippet(snippet)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker0));
                                }
                                else if(water1.equals(getString(R.string.hatu))){
                                    Log.d(TAG, "onDataChange: hatucheck");
                                    options = new MarkerOptions()
                                            .position(latLng1)
                                            .title("Information:")
                                            .snippet(snippet)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker1));
                                }

                                else if(water1.equals(getString(R.string.komor))){
                                    Log.d(TAG, "onDataChange: komorcheck");
                                    options = new MarkerOptions()
                                            .position(latLng1)
                                            .title("Information:")
                                            .snippet(snippet)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker2));
                                }
                                if(mMap==null) Log.d(TAG, "onDataChange: kebolbujhlam");
                                else Log.d(TAG, "onDataChange: kebolmapthik ase");

                                mMap.addMarker(options);
                            }
                            else Log.d(TAG, "onDataChange: kebol4");

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());

                    }
                }
        );
    }


    private void addtodatabase() {
        final LatLng latLng=new LatLng(mLastKnownLocation.getLatitude(),
                mLastKnownLocation.getLongitude());





        Toast.makeText(this, "Adding...", Toast.LENGTH_SHORT).show();

        // [START single_value_read]
        final String userId = getUid();







        markerdata.child("users").child(userId).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        Users user = dataSnapshot.getValue(Users.class);

                        Log.d("vetore1", dataSnapshot.getKey());

                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(MapsActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new post
                            addmarkertodata(userId, user.name, latLng,water,address);
                            usernameglobal=user.name;
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        // [START_EXCLUDE]

                    }
                });
    }






    private void getDeviceLocation() {
        Log.d(TAG,"getdevice1");

        try {
            if (mLocationPermissionGranted) {
                Log.d(TAG,"getdevice2");
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Log.d(TAG,"getdevice3");
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Log.d(TAG,"getdevice4");
                            mLastKnownLocation=location;
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));

                        }
                        else{
                            Log.d(TAG,"getdevice5");
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dhaka, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });


            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }




    public void settingsRequest2(final Activity activity) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        SettingsClient client = LocationServices.getSettingsClient(activity);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                getDeviceLocation();
            }
        });

        task.addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(activity ,REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });
    }


    private void datacheck(){
        if (!isDataAvailable()) { //check if data is enabled or not
            new AlertDialog.Builder(this).setTitle("Unable to connect")
                    .setMessage("Enable data?").setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // if user clicks ok then it will open network settings
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                }
            }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).show();
        } else {
            Toast.makeText(this, "DATA IS ON", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isDataAvailable() {
        // returns true or false based on whether data is enabled or not
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


    private void addmarkertodata(String userId, String username,LatLng latlng,String waterlevel,String  placename) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = markerdata.child("location").push().getKey();
        Locationdetail locationdetail = new Locationdetail(userId, username,latlng,waterlevel,placename);
        Log.d(TAG, "addmarkertodata: "+ username);
        usernameglobal=username;
        Log.d(TAG, "addmarkertodata: "+ usernameglobal);
        markerinfo(mMap,address,water,latLng1final);
        Map<String, Object> postValues = locationdetail.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/location/" + key, postValues);
        markerdata.updateChildren(childUpdates);
    }


    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getCurrentLocation();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
        Log.d(TAG, "onStart: suru");
//        getLocationPermission();
//
//        updateLocationUI();
//
//        settingsRequest2(MapsActivity.this);
//
//        changeinlocationdata();
    }

    @Override
    protected void onStop() {
       // googleApiClient.disconnect();
        super.onStop();
    }

    private void moveMap() {
        /**
         * Creating the latlng object to store lat, long coordinates
         * adding marker to map
         * move the camera with animation
         */
        LatLng latLng = new LatLng(latitude, longitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));


    }

    private void openMyDialog() {

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    private void getinfo() {
        water=dialog.getWaterinfo();
        dialog.dismiss();
        Log.d(TAG, "getinfo: vetore" +water);
    }


    private String getaddress(double latitude,double longitude ){
        String add="";
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null && addresses.size() > 0) {
            add = addresses.get(0).getAddressLine(0);
        }
        return add;
    }

    private void markerinfo(GoogleMap googleMap ,String addr,String waterinfo,LatLng latLng){

        Log.d(TAG, "markerinfo: "+usernameglobal);
        if(addr != null){
            Log.d(TAG, "markerinfo: insideif");
            try{
                String snippet = "\n"+"The social hero: "+usernameglobal +"\n"+
                        "\n"+"Address: " + addr + "\n" +
                        "Water level: " + waterinfo + "\n" ;

                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .title("Information:")
                        .snippet(snippet)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_markerdefault));

                if(waterinfo.equals(getString(R.string.gorali))){
                    Log.d(TAG, "onDataChange: goralicheck1");
                    options = new MarkerOptions()
                            .position(latLng)
                            .title("Information:")
                            .snippet(snippet)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker0));
                }
                else if(waterinfo.equals(getString(R.string.hatu))){
                    Log.d(TAG, "onDataChange: hatucheck");
                    options = new MarkerOptions()
                            .position(latLng)
                            .title("Information:")
                            .snippet(snippet)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker1));
                }

                else if(waterinfo.equals(getString(R.string.komor))){
                    Log.d(TAG, "onDataChange: komorcheck");
                    options = new MarkerOptions()
                            .position(latLng)
                            .title("Information:")
                            .snippet(snippet)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker2));
                }

                googleMap.addMarker(options);


            }catch (NullPointerException e){
                Log.e(TAG, "moveCamera: NullPointerException: " + e.getMessage() );
            }
        }else{
            String snippet1 ="\n"+"The social hero: "+usernameglobal +"\n"+
                    "\n"+"Address: " + "null" + "\n" +
                    "Water level: " + waterinfo + "\n" ;
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title("Information:")
                    .snippet(snippet1)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_markerdefault));

            if(waterinfo.equals(getString(R.string.gorali))){
                Log.d(TAG, "onDataChange: goralicheck2");
                options = new MarkerOptions()
                        .position(latLng)
                        .title("Information:")
                        .snippet(snippet1)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker0));
            }
            else if(waterinfo.equals(getString(R.string.hatu))){
                Log.d(TAG, "onDataChange: hatucheck");
                options = new MarkerOptions()
                        .position(latLng)
                        .title("Information:")
                        .snippet(snippet1)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker1));
            }

            else if(waterinfo.equals(getString(R.string.komor))){
                Log.d(TAG, "onDataChange: komorcheck");
                options = new MarkerOptions()
                        .position(latLng)
                        .title("Information:")
                        .snippet(snippet1)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker2));
            }
            googleMap.addMarker(options);
        }

    }


}

