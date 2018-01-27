package com.example.candor.candor;

import java.util.jar.Attributes;

/**
 * Created by Mohammad Faisal on 11/21/2017.
 */

public class Users {


    public String name;
    public String status;

    //firebase e jei name variable thake ei nam e eikhane thakte hobe ... beshi paknami kora jabena
    public String image;
    public String thumb_image;

    public Users(String name, String status, String profile_image_url, String profile_thumb_image_url) {
        this.name = name;
        this.status = status;
        this.image = profile_image_url;
        this.thumb_image = profile_thumb_image_url;
    }

    public Users() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfile_image_url() {
        return image;
    }

    public void setProfile_image_url(String profile_image_url) {
        this.image = profile_image_url;
    }

    public String getProfile_thumb_image_url() {
        return thumb_image;
    }

    public void setProfile_thumb_image_url(String profile_thumb_image_url) {
        this.thumb_image = profile_thumb_image_url;
    }
}
