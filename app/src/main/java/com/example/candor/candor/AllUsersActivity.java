package com.example.candor.candor;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersActivity extends AppCompatActivity {

    //widgets
    private Toolbar mToolbar;
    private RecyclerView mUsersList;

    //firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;

    private DatabaseReference mUserDatabase;
    private FirebaseUser mUser;
    private StorageReference mStorage;

    //variables
    private String mUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        //toolbar setting
        mToolbar = findViewById(R.id.all_users_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //firebase initialize
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUserID = mUser.getUid();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("users");
        mUserDatabase = mDatabase.getReference().child("users").child(mUserID);
        mStorage = FirebaseStorage.getInstance().getReference();

        //recycler view
        mUsersList = findViewById(R.id.users_recycler_view);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));
    }



    @Override
    protected void onStart() {
        super.onStart();

        mUserDatabase.child("online").setValue("true");//offline or online
        FirebaseRecyclerAdapter<Users ,UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(
                Users.class,
                R.layout.users_single_layoyt,
                UsersViewHolder.class,
                mDatabaseReference
        ) {
            @Override
            protected void populateViewHolder(final UsersViewHolder viewHolder, Users model, final int position) {
                viewHolder.setName(model.getName());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setImage(model.getProfile_thumb_image_url(),getApplicationContext());




                //online ase naki offline seta dekhtesi
                final String list_user_id = getRef(position).getKey();
                mDatabaseReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("online")){
                            String online_state = dataSnapshot.child("online").getValue().toString();
                            viewHolder.setUserOnline(online_state);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                //end
                final String currentUserID = getRef(position).getKey();
                viewHolder.mVIew.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent profileIntent  = new Intent(AllUsersActivity.this , ProfileActivity.class);
                        profileIntent.putExtra("userID" , currentUserID);
                        startActivity(profileIntent);
                    }
                });
            }
        };
        mUsersList.setAdapter(firebaseRecyclerAdapter);
    }
    @Override
    protected void onStop() {
        super.onStop();
        //mUserDatabase.child("online").setValue(false);//offline or online
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder{
        View mVIew;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mVIew =itemView;
        }

        public void setName(String name){
            TextView mUserNameView = mVIew.findViewById(R.id.user_single_name);
            mUserNameView.setText(name);
        }

        public void setStatus(String status){
            TextView mUsersStatusView = mVIew.findViewById(R.id.user_single_status);
            mUsersStatusView.setText(status);
        }

        //এখানে আমরা যেহেতু একটা সাবক্লাস ব্যাবহার করছি তাই পিকাসো তে কনটেক্সট নামের ভেরিএবল টা আমরা এই ক্লাস থেকে পাবনা
        //তাই আমরা কনটেক্সট তা কেও একটা ভেরিএবল হিসেবে পাস করে দিয়েছি আমাদের OnCreate() এই মেথড টা থেকে

        private void setImage(String ImageUrl , Context mContext){
            CircleImageView single_image = mVIew.findViewById(R.id.user_single_imagee);
            if(ImageUrl ==null){

            }
            else{
                Picasso.with(mContext).load(ImageUrl).placeholder(R.drawable.blank_profile).into(single_image);
            }
        }

        public void setUserOnline(String state){
            CircleImageView single_image = mVIew.findViewById(R.id.user_online_state);
            if(state.equals("true")){
                single_image.setVisibility(View.VISIBLE);
            }
            else{
                single_image.setVisibility(View.VISIBLE);
            }
        }
    }
}
