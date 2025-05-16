package com.example.mycloset;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import Utils.FirebaseUtil;

public class ProfilePageActivity extends AppCompatActivity {

    private ImageView profile_pic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        String user_id = getSharedPreferences("myClosetPrefs", MODE_PRIVATE)
                .getString("userId", null);

        ImageView profile_pic = findViewById(R.id.profile_picture_profile);
        FirebaseUtil.getCurrentProfilePicStorageRef(user_id).getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Uri uri = task.getResult();
                        FirebaseUtil.setProfilePic(this, uri,profile_pic);
                    }
                });
    }

}
