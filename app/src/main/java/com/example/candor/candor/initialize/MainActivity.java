package com.example.candor.candor.initialize;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;


import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.candor.candor.friends.FriendsFragment;
import com.example.candor.candor.R;
import com.example.candor.candor.game.GamesFragment;
import com.example.candor.candor.home.HomeFragment;
import com.example.candor.candor.map.MapsActivity;
import com.example.candor.candor.profile.ProfileActivity;
import com.example.candor.candor.profile.SettingsActivity;
import com.example.candor.candor.report.ReportFragment;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.candor.candor.home.HomeFragment.RC_SIGN_IN;

public class MainActivity extends AppCompatActivity implements FriendsFragment.OnFragmentInteractionListener,
        HomeFragment.OnFragmentInteractionListener,ReportFragment.OnFragmentInteractionListener,GamesFragment.OnFragmentInteractionListener
{
    private static final String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference mFirebaseDatabase;

    private ViewPager mViewPager;
    private SectionPagerAdapter mSectionPagerAdapter;
    private TabLayout mTabLayout;
    private FirebaseUser  mFirebaseUser;
    private DatabaseReference mUserDatabase;
    private AppBarLayout mAppBarLayout;
    private ImageView profile_logo_main_toolbar;
    FloatingActionButton mapbutton;
    private  String mUserID;

    private CircleImageView mProfilePicToolbar;

    public int mLastDy = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAppBarLayout = findViewById(R.id.appBarLayout);
        //adding toolbar
        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Candor");
        mapbutton=findViewById(R.id.mapaction);
        mapbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
        mProfilePicToolbar = findViewById(R.id.main_toolbar_logo_image);

        //adding tabs
        mViewPager = findViewById(R.id.tabPager);
        mSectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionPagerAdapter);

        //setting tabs
        mTabLayout = findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null){
            mUserID = mAuth.getCurrentUser().getUid();
            mFirebaseDatabase = FirebaseDatabase.getInstance().getReference().child("users");
            mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.hasChild(mUserID)){
                        final String thumb_image = dataSnapshot.child(mUserID).child("thumb_image").getValue().toString();
                        if(thumb_image.equals("")){
                        }
                        else{
                            Picasso.with(getApplicationContext()).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE)
                                    .placeholder(R.drawable.blank_profile).into(mProfilePicToolbar, new Callback() {
                                @Override
                                public void onSuccess() {
                                    //do nothing if an image is found offline
                                }
                                @Override
                                public void onError() {
                                    Picasso.with(getApplicationContext()).load(thumb_image).placeholder(R.drawable.blank_profile).into(mProfilePicToolbar);
                                }
                            });
                        }
                    }else{

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    //user is not signed in
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(
                                            Arrays.asList(
                                                    new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);
                } else {
                    mUserID = user.getUid();
                    //i want to store some data before moving on to next page





                    FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(mUserID)){

                            }
                            else{

                                String deviceTokenID = FirebaseInstanceId.getInstance().getToken();
                                HashMap<String  , String> hashMap = new HashMap<>();
                                hashMap.put("name"  , "Name");
                                hashMap.put("image" , "default");
                                hashMap.put("thumb_image" , "default");
                                hashMap.put("device_id" , deviceTokenID);
                                hashMap.put("phone_number" , "default");
                                hashMap.put("email" , "default");
                                hashMap.put("date_of_birth" , "default");
                                hashMap.put("blood_group" , "default");
                                hashMap.put("bio" , "default");
                                hashMap.put("location" , "location");

                                mFirebaseDatabase.child(mUserID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                                            View mVIew = getLayoutInflater().inflate(R.layout.dialog , null);
                                            mBuilder.setView(mVIew);
                                            final AlertDialog dialog = mBuilder.create();
                                            dialog.show();

                                            Button mDialogEdit = mVIew.findViewById(R.id.dialog_profile_edit);
                                            Button mDialogCancel = mVIew.findViewById(R.id.dialog_profile_cancel);

                                            mDialogEdit.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                    Intent settingsIntent = new Intent (MainActivity.this , SettingsActivity.class);
                                                    startActivity(settingsIntent);
                                                }
                                            });

                                            mDialogCancel.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    dialog.dismiss();
                                                }
                                            });
                                        }
                                        else{
                                        }
                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }
            }
        };

        profile_logo_main_toolbar = findViewById(R.id.main_toolbar_logo_image);
        profile_logo_main_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Intent intent = new Intent(MainActivity.this , ProfileActivity.class);
                intent.putExtra("userID" , mUserID);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if(response==null){
            }
            else if (resultCode == RESULT_OK && response!=null) {
               // mUserID = response.getIdpToken();









                return;
            }
            else if(resultCode == RESULT_CANCELED)
            {
                finish();
                return;
            }
            else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                   // Toast.makeText(this, "Sign in cancelled !", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "no internet Connection :/ ", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(this, "\"unknown error ! :( \"", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(this, "unknown sign in response", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.main_menu_sign_out_btn)
        {
            FirebaseAuth.getInstance().signOut();
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(mAuthStateListener);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //to communicate between fragments
    }
}