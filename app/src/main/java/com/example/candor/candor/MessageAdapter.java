package com.example.candor.candor;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mohammad Faisal on 12/3/2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{


    private List <Messages> mMessageList;
    private FirebaseAuth mAuth;
    private DatabaseReference mRootRef;

    public MessageAdapter(List<Messages> mMessageList){
        this.mMessageList = mMessageList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single , parent , false);
        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {

        Messages c = mMessageList.get(position);
        mAuth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        String current_user_id = mAuth.getCurrentUser().getUid();  //ke chat kortese
        String from_user = c.getFrom();    //kar message amra show korbo ?  jodi duita same hoy tar mane eita amr e message ar na hole eita onnojoner
        String messageTime = String.valueOf(c.getTime());
        holder.time.setText(messageTime);
        if(from_user!=null){
            if(from_user.equals(current_user_id)){
                holder.messageText.setBackgroundColor(Color.WHITE);
                holder.messageText.setTextColor(Color.BLACK);
                holder.messageText.setText(c.getMessage());
                holder.messageText.setGravity(Gravity.RIGHT);

                mRootRef.child("users").child(current_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot!=null){
                            String from_user_name=  dataSnapshot.child("name").getValue().toString();
                            holder.userName.setText(from_user_name);
                            String from_user_thumb_image  = dataSnapshot.child("thumb_image").getValue().toString();
                            Picasso.with(holder.profileImage.getContext()).load(from_user_thumb_image).placeholder(R.drawable.blank_profile).into(holder.profileImage);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                holder.messageText.setGravity(Gravity.RIGHT);
                holder.userName.setGravity(Gravity.RIGHT);

            }
            else{
                holder.messageText.setBackgroundColor(Color.BLACK);
                holder.messageText.setTextColor(Color.WHITE);
                holder.messageText.setText(c.getMessage());

                mRootRef.child("users").child(from_user).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot!=null){

                            String from_user_name=  dataSnapshot.child("name").getValue().toString();
                            holder.userName.setText(from_user_name);
                            String from_user_thumb_image  = dataSnapshot.child("thumb_image").getValue().toString();
                            Picasso.with(holder.profileImage.getContext()).load(from_user_thumb_image).placeholder(R.drawable.blank_profile).into(holder.profileImage);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public class MessageViewHolder  extends RecyclerView.ViewHolder{

        public Layout messageItem;
        public TextView messageText;
        public CircleImageView profileImage;
        public TextView userName;
        public TextView time;


        public MessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_item_text);
            //profileImage = itemView.findViewById(R.id.message_item_profile_image);
            //userName = itemView.findViewById(R.id.message_item_name);
            //time = itemView.findViewById(R.id.message_item_time);
        }
    }


}
