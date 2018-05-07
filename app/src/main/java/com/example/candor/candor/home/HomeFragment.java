package com.example.candor.candor.home;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.candor.candor.R;
import com.example.candor.candor.profile.ProfileActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class HomeFragment extends Fragment {


    private OnFragmentInteractionListener mListener;

    //constants
    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    public static final int RC_SIGN_IN  = 1;
    public static final int RC_PHOTO_PICKER = 2;
    public static final int RC_CAMERA_PICKER = 3;

    FloatingActionButton mapbutton;



    //related info
    private String mUserID;
    private String image_url;
    Uri selectedImageUri;
    private String caption;
    boolean likeFunction = false;


    //firebase
    private DatabaseReference mPostsDatabaseReference , mUserDatabaseReference ,  mRootRef;
    private Query mQuery;
    private FirebaseUser mUser;
    private StorageReference mPostStorageRef;


    //widgets
    RecyclerView mHomeFragmentList;
    private View mMainView;




    public HomeFragment() {
        // Required empty public constructor
    }
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView  = inflater.inflate(R.layout.fragment_home ,container ,  false);

        // ------------ FIREBASE ------ //
        mUserID = "default";
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mUser!=null){
            mUserID = mUser.getUid();  //je app e login kore ache
        }


       //firebase.database().ref('user-posts/' + myUserId).orderByChild('starCount');

        mPostsDatabaseReference = FirebaseDatabase.getInstance().getReference().child("posts");
        mUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        mPostsDatabaseReference.keepSynced(true);
        mUserDatabaseReference.keepSynced(true);

        mQuery = mPostsDatabaseReference.orderByChild("time_stamp");

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mPostStorageRef = FirebaseStorage.getInstance().getReference().child("posts");



        //recycler view
        mHomeFragmentList = mMainView.findViewById(R.id.home_fragment_recyclerView);
        mHomeFragmentList.setHasFixedSize(true);
        mHomeFragmentList.setLayoutManager(new LinearLayoutManager(getContext()));
        return mMainView;
    }

    @Override
    public  void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK){
            Log.d(TAG , "result code is RC_PHOTO_PICKER");/////
            selectedImageUri = data.getData();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Posts,HomeFragment.HomeViewHolder > firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Posts,HomeFragment.HomeViewHolder>(
                Posts.class,
                R.layout.post_single,
                HomeFragment.HomeViewHolder.class,
                mQuery
        ) {
            @Override
            protected void populateViewHolder(final HomeFragment.HomeViewHolder viewHolder,final Posts model, final int position) {
                viewHolder.setTimeDate(model.getTime_and_date());
                viewHolder.setPostImage(model.getPost_image_url() , getContext());
                viewHolder.setCaption(model.getCaption());
                viewHolder.setLocation(model.getLocation());
                viewHolder.setLikeCount(model.getThumbs_up_cnt());
                Log.d("caption is     " ,"caption is  " +model.getCaption());

                viewHolder.like_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        likeFunction = true;
                        final String postPushID = model.getPost_push_id();
                            mRootRef.child("likes").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(likeFunction){
                                        if(dataSnapshot.child(postPushID).hasChild(mUserID)){ //like already exists
                                            mRootRef.child("likes").child(postPushID).child(mUserID).removeValue();
                                            //like count changing
                                            String current_likes =String.valueOf(dataSnapshot.getChildrenCount());
                                            likeFunction = false;
                                            Log.d("Home Fragment    ", "like count is    "+ current_likes);
                                            int number = Integer.parseInt(current_likes);
                                            number--;
                                            current_likes = String.valueOf(number);
                                            viewHolder.setLikeCount(current_likes);
                                            //like count end
                                            viewHolder.like_button.setImageResource(R.drawable.ic_love_empty);
                                        }
                                        else{
                                            mRootRef.child("likes").child(postPushID).child(mUserID).setValue("y");
                                            String current_likes =String.valueOf(dataSnapshot.getChildrenCount());
                                            likeFunction = false;
                                            int number = Integer.parseInt(current_likes);
                                            number++;
                                            current_likes = String.valueOf(number);
                                            viewHolder.setLikeCount(current_likes);
                                            //like count end
                                            viewHolder.like_button.setImageResource(R.drawable.ic_love_full);
                                        }
                                    }
                                    else{
                                        Log.d("inside else    " , "hups");
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                    }
                });
                //to populate users details
                final String list_user_id = model.getUid();
                mUserDatabaseReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("name").getValue().toString();
                        final String thumb_image_url = dataSnapshot.child("thumb_image").getValue().toString();
                        final Context mContext =getContext();
                        viewHolder.setName(name);
                        viewHolder.setProfileImage( thumb_image_url ,mContext  ); //Image(thumb_image_url,getContext());
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                final String postPushID = model.getPost_push_id();
                mRootRef.child("likes").child(postPushID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String current_likes =String.valueOf(dataSnapshot.getChildrenCount());
                        viewHolder.setLikeCount(current_likes);
                        if(dataSnapshot.hasChild(mUserID)){ //like already exists
                            viewHolder.like_button.setImageResource(R.drawable.ic_love_full);
                        }
                        else{
                            viewHolder.like_button.setImageResource(R.drawable.ic_love_empty);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                viewHolder.single_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent profileIntent  = new Intent(getContext() , ProfileActivity.class);
                        profileIntent.putExtra("userID" , list_user_id);
                        startActivity(profileIntent);
                    }
                });
            }
        };
        mHomeFragmentList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class HomeViewHolder extends RecyclerView.ViewHolder{

        View mVIew;
        ImageButton like_button;
        CircleImageView single_image;


        public HomeViewHolder(View itemView) {
            super(itemView);
            mVIew =itemView;
            like_button = itemView.findViewById(R.id.post_like_button);
            single_image = mVIew.findViewById(R.id.post_user_single_imagee);
        }

        public void setName(String name){
            TextView mUserNameView = mVIew.findViewById(R.id.txt_name);
            mUserNameView.setText(name);
        }

        public void setTimeDate(String time_date){
            TextView mUsersStatusView = mVIew.findViewById(R.id.txt_time_date);
            mUsersStatusView.setText(time_date);
        }

        public void setCaption(String caption){
            TextView mPostCaption = mVIew.findViewById(R.id.post_caption);
            mPostCaption.setText(caption);
        }

        public void setLocation(String location){
            TextView mPostLocation = mVIew.findViewById(R.id.txt_location);
            mPostLocation.setText(location);
        }

        public void setLikeCount(String LikeCount){
            TextView mPostLikeCount = mVIew.findViewById(R.id.post_like_number);
            mPostLikeCount.setText(LikeCount);
        }


        //এখানে আমরা যেহেতু একটা সাবক্লাস ব্যাবহার করছি তাই পিকাসো তে কনটেক্সট নামের ভেরিএবল টা আমরা এই ক্লাস থেকে পাবনা
        //তাই আমরা কনটেক্সট তা কেও একটা ভেরিএবল হিসেবে পাস করে দিয়েছি আমাদের OnCreate() এই মেথড টা থেকে

        private void setProfileImage(final String ImageUrl ,final Context mContext){

            final CircleImageView single_image = mVIew.findViewById(R.id.post_user_single_imagee);
            if(ImageUrl.equals("")){

            }
            else{
                Picasso.with(mContext).load(ImageUrl).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.blank_profile).into(single_image, new Callback() {
                    @Override
                    public void onSuccess() {
                        //do nothing if an image is found offline
                    }
                    @Override
                    public void onError() {
                        Picasso.with(mContext).load(ImageUrl).placeholder(R.drawable.blank_profile).into(single_image);
                    }
                });
            }
        }

        private void setPostImage(final String ImageUrl , final Context mContext){
            final ImageView single_image = mVIew.findViewById(R.id.img_post);
            if(ImageUrl.equals("")){
            }
            else{
                Picasso.with(mContext).load(ImageUrl).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.blank_profile).into(single_image, new Callback() {
                    @Override
                    public void onSuccess() {
                        //do nothing if an image is found offline
                    }
                    @Override
                    public void onError() {
                        Picasso.with(mContext).load(ImageUrl).placeholder(R.drawable.blank_profile).into(single_image);
                    }
                });

            }
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
