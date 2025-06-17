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
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import APIService.WardrobeService;
import Utils.AndroidUtil;
import Utils.FirebaseUtil;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddingClothActivity extends AppCompatActivity {
    ActivityResultLauncher<Intent> imagePickLauncher;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
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
                            AndroidUtil.setClothObjPic(AddingClothActivity.this, selectedClothUri, clothImage);
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

        new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.170.3:8080/")
                //.baseUrl("http://10.0.2.2:8080/")
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

                                        getImageObj(wardrobeService);

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
        String user_id = getSharedPreferences("myClosetPrefs", MODE_PRIVATE).getString("userId", null);

        if (user_id != null) {
            intUserId = Integer.parseInt(user_id);
        }

        Runnable pollTask = new Runnable() {
            @Override
            public void run() {
                if (TASK_ID != null) {
                    Call<RegisterResponseDto> getObjCall = wardrobeService.GetObjFromMeshy(TASK_ID, intUserId);
                    getObjCall.enqueue(new Callback<RegisterResponseDto>() {
                        @Override
                        public void onResponse(Call<RegisterResponseDto> call, Response<RegisterResponseDto> response) {
                            if (response.body() != null && response.isSuccessful()) {
                                RegisterResponseDto objResponse = response.body();
                                String objUrl = objResponse.getBody();

                                if (objUrl != null && !objUrl.isEmpty()) {
                                    // Stop polling
                                    scheduler.shutdown();

                                    // Download and upload .obj
                                    ExecutorService executor = Executors.newSingleThreadExecutor();
                                    executor.execute(() -> {
                                        try {
                                            File localFile = downloadFile(objUrl, "model.glb");
                                            Uri localUri = Uri.fromFile(localFile);

                                            runOnUiThread(() -> {
                                                FirebaseUtil.getWardrobeGlbStorageRef(user_id).putFile(localUri)
                                                        .addOnCompleteListener(task -> {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(AddingClothActivity.this, "Obj added successfully", Toast.LENGTH_LONG).show();
                                                                Log.d("MeshyOBJ", "Obj. added");
                                                            } else {
                                                                Toast.makeText(AddingClothActivity.this, "Obj couldn't be added", Toast.LENGTH_LONG).show();
                                                                Log.e("MeshyOBJ", "Obj. couldn't be added");
                                                            }
                                                        });
                                            });

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    });
                                } else {
                                    Log.d("MeshyOBJ", "OBJ not ready yet, polling again...");
                                }
                            } else {
                                Log.e("MeshyOBJ", "Response failed: " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<RegisterResponseDto> call, Throwable t) {
                            Log.e("MeshyOBJ", "Polling failed: " + t.getMessage());
                        }
                    });
                }
            }
        };

        scheduler.scheduleAtFixedRate(pollTask, 3, 10, TimeUnit.SECONDS);
    }

    private File downloadFile(String urlStr, String filename) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();

        File file = new File(getExternalFilesDir(null), filename);
        try (InputStream input = connection.getInputStream();
             FileOutputStream output = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        }
        return file;
    }
}
