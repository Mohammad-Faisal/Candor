package com.example.candor.candor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.candor.candor.map.MapsActivity;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;


public class ReportFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{



    //constants
    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    public static final int RC_CAMERA_PICKER  = 1;
    public static final int RC_PHOTO_PICKER = 2;
    int flag=0;


    //location
    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private Location mLastKnownLocation;
    private double longitude;
    private double latitude;
    private GoogleApiClient googleApiClient;
    String address;



    //widgets
    EditText reportText,locationtext;
    ImageButton reportImageButton;
    Button reportSubmitButton;
    ImageView reportImage;
    ProgressDialog mProgress;
    ImageButton mCameraPicker , mGalleryPicker;
    FloatingActionButton mapbutton;

    private View mView;



    //related info
    private String mUserID;
    private String image_url;
    Uri selectedImageUri;
    private String caption;


    //firebase
    private DatabaseReference mReportsDatabaseReference , mUserDatabaseReference ,  mRootRef;
    private FirebaseUser mUser;
    private StorageReference mReportStorageRef;




    private OnFragmentInteractionListener mListener;
    public ReportFragment() {
        // Required empty public constructor
    }
    public static ReportFragment newInstance(String param1, String param2) {
        ReportFragment fragment = new ReportFragment();
        Bundle args = new Bundle();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_report, container, false);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .build();


        //map
        locationtext=(EditText)mView.findViewById(R.id.create_post_location_setter_text);



        //location of report

        reportImageButton=(ImageButton)mView.findViewById(R.id.report_fragment_location_setter_button);
        reportImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: reportimagebuttonclicked");
                datacheck();
                getLocationPermission();
                settingsRequest2(getActivity());



            }
        });



        // ------------ FIREBASE ------ //
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        if(mUser!=null){
            mUserID = mUser.getUid();  //je app e login kore ache
        }

        mReportsDatabaseReference = FirebaseDatabase.getInstance().getReference().child("reports");
        mUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        mReportsDatabaseReference.keepSynced(true);
        mUserDatabaseReference.keepSynced(true);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mReportStorageRef = FirebaseStorage.getInstance().getReference().child("reports");


        reportText = mView.findViewById(R.id.report_fragment_complain);
        reportSubmitButton = mView.findViewById(R.id.report_fragment_submit_button);
        reportImage = mView.findViewById(R.id.report_image);
        mCameraPicker = mView.findViewById(R.id.report_fragment_camera_picker_button);
        mGalleryPicker = mView.findViewById(R.id.report_fragment_gallery_picker_button);


        mCameraPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, RC_CAMERA_PICKER);
            }
        });

        mGalleryPicker.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,
                       android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
               startActivityForResult(photoPickerIntent, RC_PHOTO_PICKER);
           }
       });

        reportSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reportSubmitButton.setEnabled(false);
                if(selectedImageUri!=null){


                    mProgress = new ProgressDialog(getContext());
                    mProgress.setTitle("Uploading Report...");
                    mProgress.setMessage("please wait while we submit your report");
                    mProgress.show();

                    StorageReference photoRef = mReportStorageRef.child(selectedImageUri.getLastPathSegment());
                    photoRef.putFile(selectedImageUri).addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Uri downLoadURL = taskSnapshot.getDownloadUrl();
                                    image_url = downLoadURL.toString();
                                    String complain = reportText.getEditableText().toString();
                                    Reports report = new Reports(complain , image_url , mUserID);
                                    mRootRef.child("reports").push().setValue(report).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getContext(), "Success !", Toast.LENGTH_SHORT).show();
                                            reportText.setText("");
                                            reportImage.setImageResource(R.drawable.ic_add);
                                            mProgress.dismiss();

                                        }
                                    });
                                }
                            }
                    );
                    mProgress.dismiss();
                }
                else{
                    Toast.makeText(getContext(), "There must be an image  !! ", Toast.LENGTH_SHORT).show();
                }
                sendMessage();
                reportSubmitButton.setEnabled(true);
            }


        });
        return mView;

    }

    @Override
    public  void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK){
            Uri uri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            selectedImageUri = uri;
            reportImage.setImageBitmap(bitmap);
        }
        else if(requestCode==RC_CAMERA_PICKER && resultCode==RESULT_OK)//camera theke back korar pore
        {
            Bitmap bitmap = (Bitmap)data.getExtras().get("data");
            selectedImageUri  = data.getData();
            reportImage.setImageBitmap(bitmap);
        }
        else if(requestCode==REQUEST_CHECK_SETTINGS && resultCode==RESULT_CANCELED){
            settingsRequest2(getActivity());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }



    private void sendMessage() {

        Thread sender = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GmailSender sender = new GmailSender("candorappbd@gmail.com", "plan2018"); //what
                    sender.sendMail("EmailSender App",
                            "This is the message body",
                            "youremail",
                            "mohammadfaisal1011@gmail.com");
                   // dialog.dismiss();
                } catch (Exception e) {
                    Log.e("mylog", "Error: " + e.getMessage());
                }
            }
        });
        Log.d(TAG, "sendMessage: sending mail");
        sender.start();
    }

    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }



    //for location

    private String getaddress(double latitude,double longitude ){
        String add="";
        Geocoder gcd = new Geocoder(getActivity(), Locale.getDefault());
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

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            Log.d(TAG, "getLocationPermission: permission granted");
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public void settingsRequest2(final Activity activity) {
        Log.d(TAG, "settingsRequest2: setting chaitese");
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
                Log.d(TAG, "onSuccess: setting is ok");

                getDeviceLocation();
                getCurrentLocation();
                flag=1;
            }
        });

        task.addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                flag=1;
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


    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        Log.d(TAG, "getDeviceLocation: request for device location");

        try {
            if (mLocationPermissionGranted) {




                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Log.d(TAG, "onSuccess: device location paise");
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Log.d(TAG, "onSuccess: devicelocation null na");
                            
                            mLastKnownLocation=location;
                            address=getaddress(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
                            locationtext.setText(address);

                        }
                        else{
                            Log.d(TAG, "onSuccess: devicelocation null");

                        }
                    }
                });


            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getCurrentLocation() {
        Log.d(TAG, "getCurrentLocation: requesting fotr current locatino");
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            Log.d(TAG, "getCurrentLocation:  curreent location paise");
            //Getting longitude and latitude
            mLastKnownLocation=location;
            address=getaddress(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
            locationtext.setText(address);
        }
        else Log.d(TAG, "getCurrentLocation: locatio null");

    }

    private void datacheck(){
        if (!isDataAvailable()) { //check if data is enabled or not
            new AlertDialog.Builder(getActivity()).setTitle("Unable to connect")
                    .setMessage("Enable data?").setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // if user clicks ok then it will open network settings
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                }
            }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getActivity().finish();
                }
            }).show();
        } else {
            Toast.makeText(getActivity(), "DATA IS ON", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isDataAvailable() {
        // returns true or false based on whether data is enabled or not
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


}
