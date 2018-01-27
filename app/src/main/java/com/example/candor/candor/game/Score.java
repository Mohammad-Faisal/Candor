package com.example.candor.candor.game;

/**
 * Created by abrar on 12/20/17.
 */

public class Score {
    public String username;
    public int score;
    public String user_id;
    public String profile_photo;

    public Score() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Score(String username, int score,String user_id,String profile_photo) {
        this.username = username;
        this.score = score;
        this.user_id=user_id;
        this.profile_photo=profile_photo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }
}
