package com.example.candor.candor;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.candor.candor.home.*;
import com.example.candor.candor.map.MapsActivity;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Arrays;
import java.util.HashMap;

import static com.example.candor.candor.HomeFragment.RC_SIGN_IN;

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
    FloatingActionButton mAddPost;

    public int mLastDy = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAppBarLayout = findViewById(R.id.appBarLayout);
        //adding toolbar
        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("Candor");
        getSupportActionBar().setTitle("Candor");

        mapbutton=(FloatingActionButton)findViewById(R.id.mapaction);

        mapbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });




        //adding tabs
        mViewPager = findViewById(R.id.tabPager);
        mSectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionPagerAdapter);

        //setting tabs
        mTabLayout = findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);


        /*mAddPost= findViewById(R.id.home_post_edit_button);
        mAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createPostIntent = new Intent(MainActivity.this, CreatePostActivity.class);
                createPostIntent.putExtra("userID" , mUserID);
                startActivity(createPostIntent);
            }
        });
*/

        /*mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if(position == 0){
                    mAddPost.setVisibility(View.VISIBLE);
                } else if(position == 1){
                    mAddPost.setVisibility(View.INVISIBLE);
                }else{
                    mAddPost.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });*/



        //firebase
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null){
            mUserID = mAuth.getCurrentUser().getUid();
            mFirebaseDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mUserID);
        }
        else{
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(true)
                            .setAvailableProviders(
                                    Arrays.asList(
                                            new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                            .build(),
                    RC_SIGN_IN);
        }


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


        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser mUser = firebaseAuth.getCurrentUser();
                if (mUser != null) {
                    mUserID = mUser.getUid();

                } else {
                    //user is not signed in
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    //.setIsSmartLockEnabled(true)
                                    .setAvailableProviders(
                                            Arrays.asList(
                                                    new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };


    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if(response==null) Log.d(TAG, "onActivityResult: eikhanevull");
            // Successfully signed in
            if (resultCode == RESULT_OK && response!=null) {
                mUserID = response.getIdpToken();


                //if an user is logged in for the first time i am inserting some default information about him
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("users");
                rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(mUserID)){

                        }
                        else{

                            //i want to store some data before moving on to next page
                            DatabaseReference mFirebaseDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mUserID);
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



                            mFirebaseDatabase.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
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
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setAvailableProviders(
                                    Arrays.asList(
                                            new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                            .build(),
                    RC_SIGN_IN);
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser mUser = firebaseAuth.getCurrentUser();
                if (mUser != null) {
                    mUserID = mUser.getUid();

                } else {
                    //user is not signed in
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(
                                            Arrays.asList(
                                                    new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                                                    new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    public void hideEditPostButton() {
        mAddPost.setVisibility(View.GONE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFirebaseUser = mAuth.getCurrentUser();
        if(mFirebaseUser!=null){
        }
    }

    private  void update_ui(FirebaseUser user)
    {
        if(user==null) {
            //mUserDatabase.child("online").setValue(ServerValue.TIMESTAMP);
            Intent startIntent= new Intent(MainActivity.this , StartActivity.class);
            startActivity(startIntent);
            finish();
        }else{
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
