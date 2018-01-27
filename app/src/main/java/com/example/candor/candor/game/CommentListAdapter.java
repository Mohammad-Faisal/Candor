package com.example.candor.candor.game;

/**
 * Created by abrar on 12/17/17.
 */

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.candor.candor.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class CommentListAdapter extends ArrayAdapter<Score> {

    private static final String TAG = "CommentListAdapter";

    private LayoutInflater mInflater;
    private int layoutResource;
    ImageLoader mImageLoader;
    private Context mContext;

    public CommentListAdapter(@NonNull Context context, @LayoutRes int resource,
                              @NonNull List<Score> objects) {
        super(context, resource, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        layoutResource = resource;
    }

    private static class ViewHolder{
        TextView scoretext, username;
        CircleImageView profileImage;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;

        if(convertView == null){
            convertView = mInflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder();

            holder.scoretext = (TextView) convertView.findViewById(R.id.scoreonline);
            holder.username = (TextView) convertView.findViewById(R.id.display_name);
            holder.profileImage = (CircleImageView) convertView.findViewById(R.id.pro_image);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        //set the comment
       // holder.scoretext.setText(getItem(position).getScore());


        //set the username and profile image
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child("scores")
                .orderByChild("user_id")
                .equalTo(getItem(position).getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    holder.username.setText(
                            singleSnapshot.getValue(Score.class).getUsername());
                    int ss=singleSnapshot.getValue(Score.class).getScore();
                    String ss1=Integer.toString(ss);

                    holder.scoretext.setText("Score: " +ss1);



                    ImageLoader imageLoader = ImageLoader.getInstance();
                            imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));

                    imageLoader.displayImage(
                            singleSnapshot.getValue(Score.class).getProfile_photo(),
                            holder.profileImage);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });



        return convertView;
    }

    public ImageLoader getImageLoader() {

        if (mImageLoader == null) {
            mImageLoader = ImageLoader.getInstance();
            mImageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
        }
        return this.mImageLoader;
    }


}