package com.example.candor.candor.friends;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.candor.candor.R;
import com.example.candor.candor.chat.ChatActivity;
import com.example.candor.candor.profile.ProfileActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class FriendsFragment extends Fragment {


    private OnFragmentInteractionListener mListener;


    //variables
    private String mUserID;
    //firebase
    private DatabaseReference mFriendsDatabaseReference;
    private DatabaseReference mUsersDatabaseReference;
    private FirebaseUser mUser;
    //widgets
    RecyclerView mFriendsFragmentList;
    private View mMainView;



    public FriendsFragment() {
        // Required empty public constructor
    }


    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
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


        mMainView  = inflater.inflate(R.layout.fragment_friends ,container ,  false);


        //firebase
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mUser != null)mUserID = mUser.getUid();  //je app e login kore ache


        mFriendsDatabaseReference = FirebaseDatabase.getInstance().getReference().child("friends").child(mUserID);
        mFriendsDatabaseReference.keepSynced(true);
        mUsersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        mUsersDatabaseReference.keepSynced(true);


        //widgets
        mFriendsFragmentList = mMainView.findViewById(R.id.friends_fragment_recyclerView);
        mFriendsFragmentList.setHasFixedSize(true);
        mFriendsFragmentList.setLayoutManager(new LinearLayoutManager(getContext()));


        return mMainView;
    }




    @Override
     public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Friends,FriendsViewHolder > firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends,FriendsViewHolder>(
                Friends.class,
                R.layout.users_single_layoyt,
                FriendsViewHolder.class,
                mFriendsDatabaseReference
        ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder viewHolder, Friends model, final int position) {


                viewHolder.setDate(model.getDate());
                final String users_name = "";

                //to populate users details
                final String list_user_id = getRef(position).getKey();
                mUsersDatabaseReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("name").getValue().toString();
                        String status = dataSnapshot.child("status").getValue().toString();
                        String thumb_image_url = dataSnapshot.child("thumb_image").getValue().toString();

                        if(dataSnapshot.hasChild("online")){
                            String online_state = dataSnapshot.child("online").getValue().toString();
                            viewHolder.setUserOnline(online_state);
                        }
                        viewHolder.setName(name);
                        viewHolder.setStatus(status);
                        viewHolder.setImage(thumb_image_url,getContext());
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                viewHolder.mVIew.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        //alert dialog start

                        CharSequence options[] = new CharSequence[]{"Open profile" , "Send message"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Select Options ");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                //click event for each item
                                if(i==0){
                                    Intent profileIntent  = new Intent(getContext() , ProfileActivity.class);
                                    profileIntent.putExtra("userID" , list_user_id);
                                    startActivity(profileIntent);
                                }else{
                                    //navigate to other chat intent
                                    Intent chatIntent  = new Intent(getContext() , ChatActivity.class);
                                    chatIntent.putExtra("userID" , list_user_id);
                                    startActivity(chatIntent);
                                }
                            }
                        });
                        builder.show();
                        //alert dialog end
                    }
                });
            }
        };
        mFriendsFragmentList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder{

        View mVIew;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            mVIew =itemView;
        }

        public void setDate(String name){
            TextView mUserNameView = mVIew.findViewById(R.id.user_single_status);
            mUserNameView.setText(name);
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
            }else{
                single_image.setVisibility(View.INVISIBLE);
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
