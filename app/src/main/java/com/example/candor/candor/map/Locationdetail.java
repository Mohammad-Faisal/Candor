package com.example.candor.candor.map;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class Locationdetail {

    public String uid;
    public LatLng latlng;
    public String author;
    public String waterlevel;
    public String placename;

    public Locationdetail() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Locationdetail(String uid, String author, LatLng latlng
    ,String waterlevel, String placename) {
        this.uid = uid;
        this.author = author;
        this.latlng=latlng;
        this.waterlevel=waterlevel;
        this.placename=placename;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("latlng", latlng);
        result.put("waterlevel", waterlevel);
        result.put("placename", placename);
        result.put("timestamp", ServerValue.TIMESTAMP);


        return result;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public LatLng getLatlng() {
        return latlng;
    }

    public void setLatlng(LatLng latlng) {
        this.latlng = latlng;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getWaterlevel() {
        return waterlevel;
    }

    public void setWaterlevel(String waterlevel) {
        this.waterlevel = waterlevel;
    }

    public String getPlacename() {
        return placename;
    }

    public void setPlacename(String placename) {
        this.placename = placename;
    }

    // [END post_to_map]

}
// [END post_class]
