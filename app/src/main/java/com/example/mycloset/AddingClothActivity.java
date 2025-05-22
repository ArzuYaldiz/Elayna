package com.example.mycloset;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mycloset.dataClasses.ClothUploadDto;
import com.example.mycloset.dataClasses.RegisterResponseDto;
import com.example.mycloset.dataClasses.UpdateUserDto;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.storage.StorageReference;

import APIService.WardrobeService;
import Utils.AndroidUtil;
import Utils.FirebaseUtil;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddingClothActivity extends AppCompatActivity {
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedClothUri;
    private int intUserId;
    private ImageView clothImage;
    private Button uploadButton;

    private Button okayButton;
    private String image_url;

    private String TASK_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_cloth);

        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if(data != null && data.getData() != null){
                            selectedClothUri = data.getData();
                        }
                    }
                });

        findViews();

        clothImage.setOnClickListener((v)->{
            ImagePicker.with(this).cropSquare().compress(1080).maxResultSize(1080,1080)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickLauncher.launch(intent);
                            AndroidUtil.setClothObjPic(AddingClothActivity.this, selectedClothUri, clothImage);
                            return null;
                        }
                    });
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WardrobeService wardrobeService = retrofit.create(WardrobeService.class);
        uploadButton.setOnClickListener(v-> uploadImageGetObj(wardrobeService));
        okayButton.setOnClickListener(v-> getImageObj(wardrobeService));
    }

    private void findViews(){
        clothImage = findViewById(R.id.cloth_img);
        uploadButton = findViewById(R.id.upload_cloth_button);
        okayButton = findViewById(R.id.okay_cloth_button);
    }

    private void uploadImageGetObj(WardrobeService wardrobeService){

        String user_id = getSharedPreferences("myClosetPrefs", MODE_PRIVATE)
                .getString("userId", null);


        if(user_id!=null){
            intUserId = Integer.parseInt(user_id);
        }

        if(selectedClothUri != null) {
            FirebaseUtil.getWardrobeItemStorageRef(user_id).putFile(selectedClothUri)
                    .addOnSuccessListener(task ->{
                        StorageReference clothPicRef = FirebaseUtil.getWardrobeItemStorageRef(user_id);

                        clothPicRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {

                            image_url = downloadUri.toString();

                            Log.d("AAAAAAAAAAAAAAAA", user_id);


                            ClothUploadDto clothUploadDto = new ClothUploadDto(intUserId, image_url);

                            Log.d("AAAAAAAAAAAAAAAA", clothUploadDto.getImage_url());

                            Call<RegisterResponseDto> call = wardrobeService.imageTo3d(clothUploadDto);

                            call.enqueue(new Callback<RegisterResponseDto>() {
                                @Override
                                public void onResponse(Call<RegisterResponseDto> call, Response<RegisterResponseDto> response) {

                                    if (response.body() != null && response.isSuccessful()) {

                                        RegisterResponseDto res = response.body();
                                        Toast.makeText(AddingClothActivity.this, "Image Uploaded successfully", Toast.LENGTH_LONG).show();

                                        System.out.println(res.getStatus());
                                        Log.d("Update Activity", res.getBody());

                                        TASK_ID = res.getBody().trim();

                                    } else {
                                        try {
                                            if (response.errorBody() != null) {
                                                Toast.makeText(AddingClothActivity.this, "Please try again", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(AddingClothActivity.this, "Please try again", Toast.LENGTH_LONG).show();
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Toast.makeText(AddingClothActivity.this, "Unknown Error", Toast.LENGTH_LONG).show();

                                        }

                                    }
                                }

                                @Override
                                public void onFailure(Call<RegisterResponseDto> call, Throwable t) {
                                    Toast.makeText(AddingClothActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                                    Log.d("ImageUpdate ", t.getMessage());
                                }

                            });


                        });

                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to upload image "+ null, Toast.LENGTH_SHORT).show();
                    });


        }else{
            Toast.makeText(AddingClothActivity.this, "Error: Image Couldn't uploaded" , Toast.LENGTH_LONG).show();
        }

    }

    private void getImageObj(WardrobeService wardrobeService){


        String user_id = getSharedPreferences("myClosetPrefs", MODE_PRIVATE)
                .getString("userId", null);


        if(user_id!=null){
            intUserId = Integer.parseInt(user_id);
        }

        Call<RegisterResponseDto> getObjCall = wardrobeService.GetObjFromMeshy(TASK_ID, intUserId);
        getObjCall.enqueue(new Callback<RegisterResponseDto>() {
            @Override
            public void onResponse(Call<RegisterResponseDto> call, Response<RegisterResponseDto> response) {
                if (response.body() != null && response.isSuccessful()) {
                    RegisterResponseDto objResponse = response.body();
                    Log.d("MeshyOBJ", "Model .obj URL or response body: " + objResponse.getBody());

                    FirebaseUtil.getWardrobeObjStorageRef(user_id).putFile(selectedClothUri)
                            .addOnSuccessListener(task ->{});

                } else {
                    Log.e("MeshyOBJ", "Failed to fetch 3D model details. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<RegisterResponseDto> call, Throwable t) {
                Log.e("MeshyOBJ", "Error while getting 3D model: " + t.getMessage(), t);
            }
        });
    }
}
