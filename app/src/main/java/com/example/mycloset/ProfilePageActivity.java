package com.example.mycloset;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mycloset.dataClasses.ProfileInformation;
import com.example.mycloset.dataClasses.RegisterResponseDto;

import APIService.UserService;
import Utils.FirebaseUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfilePageActivity extends AppCompatActivity {

    private ImageView profile_pic;
    private TextView usernameTextview;
    private int intUserId;

    private Button addClothButton;
    private Button menuButton;
    private Button favoritesButton;

    private Button mainMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        String user_id = getSharedPreferences("myClosetPrefs", MODE_PRIVATE)
                .getString("userId", null);
        intUserId = Integer.parseInt(user_id);

        findViews();

        FirebaseUtil.getCurrentProfilePicStorageRef(user_id).getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Uri uri = task.getResult();
                        FirebaseUtil.setProfilePic(this, uri,profile_pic);
                    }
                });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UserService userService = retrofit.create(UserService.class);

        Call<ProfileInformation> call = userService.getProfile(intUserId);
        call.enqueue(new Callback<ProfileInformation>() {
            @Override
            public void onResponse(Call<ProfileInformation> call, Response<ProfileInformation> response) {

                if(response.body()!=null && response.isSuccessful()){

                    ProfileInformation res = response.body();
                    String username = res.getUsername();
                    usernameTextview.setText(username);

                } else{
                    try {
                        if (response.errorBody() != null) {
                            Toast.makeText(ProfilePageActivity.this, "Please try again", Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(ProfilePageActivity.this, "Please try again", Toast.LENGTH_LONG).show();
                        }

                    }catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(ProfilePageActivity.this, "Unknown Error", Toast.LENGTH_LONG).show();

                    }

                }
            }
            @Override
            public void onFailure(Call<ProfileInformation> call, Throwable t) {
                Toast.makeText(ProfilePageActivity.this, "Error: "+ t.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("ImageUpdate ", t.getMessage());
            }

        });

        addClothButton.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(),AddingClothActivity.class);
            startActivity(i);
        });

        menuButton.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(),ClothImageActivity2.class);
            startActivity(i);
        });

        mainMenu.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(),ClothImageActivity2.class);
            startActivity(i);
        });

        favoritesButton.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(),FavouritesActivity.class);
            startActivity(i);
        });

    }

    private void findViews(){
        profile_pic = findViewById(R.id.profile_picture_profile);
        usernameTextview = findViewById(R.id.username_pp);
        addClothButton =findViewById(R.id.wardrobe_button);
        menuButton = findViewById(R.id.menu_button);
        favoritesButton = findViewById(R.id.Favourites_button);
        mainMenu = findViewById(R.id.main_button);
    }
}
