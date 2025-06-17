package com.example.mycloset;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.mycloset.dataClasses.AuthenticationRequestDto;
import com.example.mycloset.dataClasses.RegisterResponseDto;
import com.example.mycloset.dataClasses.UpdateUserDto;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

import APIService.AuthService;
import APIService.UserService;
import Enums.HairType;
import Utils.AndroidUtil;
import Utils.FirebaseUtil;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class UserProfileRegisterActivity extends AppCompatActivity {

    private EditText height;
    private EditText weight;
    private EditText phoneNumber;
    private EditText age;
    private ImageView profile_picture;
    private ImageButton black, brown, blonde, red, green, pink, blue, purple, white, unspecified;

    private Button update_user_button;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;

    private float floatHeight;
    private float floatWeight;
    private long longPhoneNumber;
    private int intAge;
    private HairType enumHairColor;
    private int intUserId;

    private String imageUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_register);

        black=(ImageButton) findViewById(R.id.hair_color_black); brown= (ImageButton) findViewById(R.id.hair_color_brown);
        red= (ImageButton) findViewById(R.id.hair_color_red); blonde= (ImageButton)findViewById(R.id.hair_color_blonde);
        pink= (ImageButton) findViewById(R.id.hair_color_pink); blue=(ImageButton) findViewById(R.id.hair_color_blue);
        purple=(ImageButton) findViewById(R.id.hair_color_purple); green= (ImageButton) findViewById(R.id.hair_color_green);
        white= (ImageButton) findViewById(R.id.hair_color_white); unspecified= (ImageButton) findViewById(R.id.hair_color_unspecified);

        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if(data != null && data.getData() != null){
                            selectedImageUri = data.getData();
                            AndroidUtil.setProfilePic(this, selectedImageUri, profile_picture);
                        }
                    }
                });

        findViews();
        selectHairColor();


        profile_picture.setOnClickListener((v)->{
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512,512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickLauncher.launch(intent);
                            return null;
                        }
                    });
        });



        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.170.3:8080/")
                //.baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        UserService userService = retrofit.create(UserService.class);
        update_user_button.setOnClickListener(v -> updateUser(userService));

    }

    private void selectHairColor(){
        black.setOnClickListener(v -> {
            clearSelection();
            black.setBackgroundResource(R.drawable.selected_hair_color);
            enumHairColor = HairType.BLACK;
        });
        brown.setOnClickListener(v -> {
            clearSelection();
            brown.setBackgroundResource(R.drawable.selected_hair_color);
            enumHairColor = HairType.BROWN;
        });
        blonde.setOnClickListener(v -> {
            clearSelection();
            blonde.setBackgroundResource(R.drawable.selected_hair_color);
            enumHairColor = HairType.BLONDE;
        });
        red.setOnClickListener(v -> {
            clearSelection();
            red.setBackgroundResource(R.drawable.selected_hair_color);
            enumHairColor = HairType.RED;
        });
        green.setOnClickListener(v -> {
            clearSelection();
            green.setBackgroundResource(R.drawable.selected_hair_color);
            enumHairColor = HairType.GREEN;
        });
        pink.setOnClickListener(v -> {
            clearSelection();
            pink.setBackgroundResource(R.drawable.selected_hair_color);
            enumHairColor = HairType.PINK;
        });
        blue.setOnClickListener(v -> {
            clearSelection();
            blue.setBackgroundResource(R.drawable.selected_hair_color);
            enumHairColor = HairType.BLUE;
        });
        purple.setOnClickListener(v -> {
            clearSelection();
            purple.setBackgroundResource(R.drawable.selected_hair_color);
            enumHairColor = HairType.PURPLE;
        });
        white.setOnClickListener(v -> {
            clearSelection();
            white.setBackgroundResource(R.drawable.selected_hair_color);
            enumHairColor = HairType.WHITE;
        });
        unspecified.setOnClickListener(v -> {
            clearSelection();
            unspecified.setBackgroundResource(R.drawable.selected_hair_color);
            enumHairColor = HairType.UNSPECIFIED;
        });
    }
    private void clearSelection() {
        black.setBackgroundColor(Color.WHITE);
        black.setBackgroundResource(R.drawable.circle);
        brown.setBackgroundColor(Color.WHITE);
        brown.setBackgroundResource(R.drawable.circle);
        blonde.setBackgroundColor(Color.WHITE);
        blonde.setBackgroundResource(R.drawable.circle);
        blue.setBackgroundColor(Color.WHITE);
        blue.setBackgroundResource(R.drawable.circle);
        red.setBackgroundColor(Color.WHITE);
        red.setBackgroundResource(R.drawable.circle);
        pink.setBackgroundColor(Color.WHITE);
        pink.setBackgroundResource(R.drawable.circle);
        purple.setBackgroundColor(Color.WHITE);
        purple.setBackgroundResource(R.drawable.circle);
        green.setBackgroundColor(Color.WHITE);
        green.setBackgroundResource(R.drawable.circle);
        white.setBackgroundColor(Color.WHITE);
        white.setBackgroundResource(R.drawable.circle);
        unspecified.setBackgroundColor(Color.WHITE);
        unspecified.setBackgroundResource(R.drawable.circle);
    }
    private void updateUser(UserService userService) {

        String user_id = getSharedPreferences("myClosetPrefs", MODE_PRIVATE)
                .getString("userId", null);


        if(user_id!=null){
            intUserId = Integer.parseInt(user_id);
        }

        if(enumHairColor==null){
            Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            floatHeight = Float.parseFloat(height.getText().toString().trim());
            floatWeight = Float.parseFloat(weight.getText().toString().trim());
            longPhoneNumber = Long.parseLong(phoneNumber.getText().toString().trim());
            intAge = Integer.parseInt(age.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show();
            return;
        }

        if(selectedImageUri != null){//add a profile pic
            FirebaseUtil.getCurrentProfilePicStorageRef(user_id).putFile(selectedImageUri)
                    .addOnSuccessListener(task ->{
                        StorageReference profilePicRef = FirebaseUtil.getCurrentProfilePicStorageRef(user_id);

                        profilePicRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                            imageUrl = downloadUri.toString();
                            Log.d("Firebase", "Haircolor: " + enumHairColor);

                            UpdateUserDto request = new UpdateUserDto(intUserId,imageUrl,floatHeight, floatWeight,longPhoneNumber, intAge, enumHairColor);
                            Call<RegisterResponseDto> call = userService.updateUser(request);
                            call.enqueue(new Callback<RegisterResponseDto>() {
                                @Override
                                public void onResponse(Call<RegisterResponseDto> call, Response<RegisterResponseDto> response) {

                                    if(response.body()!=null && response.isSuccessful()){

                                        RegisterResponseDto res = response.body();
                                        Toast.makeText(UserProfileRegisterActivity.this, "Updated successfully", Toast.LENGTH_LONG).show();

                                        System.out.println(res.getStatus());
                                        Log.d("Update Activity", res.getBody());

                                        //burada ana sayfaya yönlendirme
                                        Intent i = new Intent(getApplicationContext(),ProfilePageActivity.class);
                                        startActivity(i);


                                    } else{
                                        try {
                                            if (response.errorBody() != null) {
                                                Toast.makeText(UserProfileRegisterActivity.this, "Please try again", Toast.LENGTH_LONG).show();
                                            }else {
                                                Toast.makeText(UserProfileRegisterActivity.this, "Please try again", Toast.LENGTH_LONG).show();
                                            }

                                        }catch (Exception e) {
                                            e.printStackTrace();
                                            Toast.makeText(UserProfileRegisterActivity.this, "Unknown Error", Toast.LENGTH_LONG).show();

                                        }

                                    }
                                }
                                @Override
                                public void onFailure(Call<RegisterResponseDto> call, Throwable t) {
                                    Toast.makeText(UserProfileRegisterActivity.this, "Error: "+ t.getMessage(), Toast.LENGTH_LONG).show();
                                    Log.d("ImageUpdate ", t.getMessage());
                                }

                            });
                        });

                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to upload image "+ null, Toast.LENGTH_SHORT).show();
                    });
        }else{//not add a profile pic
            Uri uri = Uri.parse("android.resource://com.example.mycloset/drawable/user");

            FirebaseUtil.getCurrentProfilePicStorageRef(user_id).putFile(uri)
                    .addOnSuccessListener(task ->{
                        StorageReference profilePicRef = FirebaseUtil.getCurrentProfilePicStorageRef(user_id);

                        profilePicRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                            imageUrl = downloadUri.toString();
                            Log.d("Firebase", "Haircolor: " + enumHairColor);

                            UpdateUserDto request = new UpdateUserDto(intUserId,imageUrl,floatHeight, floatWeight,longPhoneNumber, intAge, enumHairColor);
                            Call<RegisterResponseDto> call = userService.updateUser(request);
                            call.enqueue(new Callback<RegisterResponseDto>() {
                                @Override
                                public void onResponse(Call<RegisterResponseDto> call, Response<RegisterResponseDto> response) {

                                    if(response.body()!=null && response.isSuccessful()){

                                        RegisterResponseDto res = response.body();
                                        Toast.makeText(UserProfileRegisterActivity.this, "Updated successfully", Toast.LENGTH_LONG).show();

                                        System.out.println(res.getStatus());
                                        Log.d("Update Activity", res.getBody());

                                        //burada ana sayfaya yönlendirme
                                        Intent i = new Intent(getApplicationContext(),ProfilePageActivity.class);
                                        startActivity(i);


                                    } else{
                                        try {
                                            if (response.errorBody() != null) {
                                                Toast.makeText(UserProfileRegisterActivity.this, "Please try again", Toast.LENGTH_LONG).show();
                                            }else {
                                                Toast.makeText(UserProfileRegisterActivity.this, "Please try again", Toast.LENGTH_LONG).show();
                                            }

                                        }catch (Exception e) {
                                            e.printStackTrace();
                                            Toast.makeText(UserProfileRegisterActivity.this, "Unknown Error", Toast.LENGTH_LONG).show();

                                        }

                                    }
                                }
                                @Override
                                public void onFailure(Call<RegisterResponseDto> call, Throwable t) {
                                    Toast.makeText(UserProfileRegisterActivity.this, "Error: "+ t.getMessage(), Toast.LENGTH_LONG).show();
                                    Log.d("ImageUpdate ", t.getMessage());
                                }

                            });
                        });

                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to upload image "+ null, Toast.LENGTH_SHORT).show();
                    });
        }

    }

    private void findViews(){
        height = findViewById(R.id.height);
        weight = findViewById(R.id.weight);
        phoneNumber = findViewById(R.id.phone_number);
        age = findViewById(R.id.age);
        profile_picture = findViewById(R.id.profile_picture);
        update_user_button = findViewById(R.id.update_user_button);

    }

}
