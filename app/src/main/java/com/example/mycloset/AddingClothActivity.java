package com.example.mycloset;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mycloset.dataClasses.ClothUploadDto;
import com.example.mycloset.dataClasses.RegisterResponseDto;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
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
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddingClothActivity extends AppCompatActivity {
    ActivityResultLauncher<Intent> imagePickLauncher;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    Uri selectedClothUri;
    private int intUserId;
    private ImageView clothImage;
    private Button uploadButton;

    Spinner seasonSpinner;
    Spinner sectionSpinner;
    Spinner categorySpinner;

    int cloth_id;
    String cloth_id_string;
    private Button okayButton;
    private String image_url;

    private String TASK_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_cloth);

        Log.d("AppCheckInit", "Firebase App Check initialized with DebugProvider using provided secret");

        String user_id = getSharedPreferences("myClosetPrefs", MODE_PRIVATE)
                .getString("userId", null);
        intUserId = Integer.parseInt(user_id);

        findViews();

        ArrayAdapter<String> seasonAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Summer", "Winter"});
        seasonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        seasonSpinner.setAdapter(seasonAdapter);

        ArrayAdapter<String> sectionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Top", "Bottom"});
        sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sectionSpinner.setAdapter(sectionAdapter);
        sectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateCategorySpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        seasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateCategorySpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        uploadButton.setOnClickListener(v -> {
            if (selectedClothUri == null) {
                Toast.makeText(this, "Lütfen görsel seçiniz", Toast.LENGTH_SHORT).show();
                return;
            }

        });

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


        clothImage.setOnClickListener((v)->{
            ImagePicker.with(this).crop().compress(1080).maxResultSize(1080,1080)
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
                .baseUrl("http://192.168.135.3:8080/")
                //.baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WardrobeService wardrobeService = retrofit.create(WardrobeService.class);
        uploadButton.setOnClickListener(v-> uploadImageGetObj(wardrobeService));
        okayButton.setOnClickListener(v-> toTheMainMenu());
    }

    private void toTheMainMenu(){
        Intent i = new Intent(getApplicationContext(),ClothImageActivity2.class);
        startActivity(i);
    }

    private void updateCategorySpinner() {
        String season = seasonSpinner.getSelectedItem().toString().toLowerCase();
        String section = sectionSpinner.getSelectedItem().toString().toLowerCase();

        String[] categories;

        if (season.equals("summer") && section.equals("top")) {
            categories = new String[]{"BLOUSE", "DRESS", "JACKET", "SUIT", "TSHIRT"};
        } else if (season.equals("summer") && section.equals("bottom")) {
            categories = new String[]{"SKIRT", "PANTS", "SHORT"};
        } else if (season.equals("winter") && section.equals("top")) {
            categories = new String[]{"COAT", "SWEATSHIRT", "WINTERJACKET"};
        } else if (season.equals("winter") && section.equals("bottom")){
            categories = new String[]{"PANTS", "LEGGINGS", "SKIRT"};
        } else{
            categories = new String[]{"AAA"};
        }

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
    }


    private void findViews(){
        clothImage = findViewById(R.id.cloth_img);
        uploadButton = findViewById(R.id.upload_cloth_button);
        okayButton = findViewById(R.id.okay_cloth_button);
        seasonSpinner = findViewById(R.id.season_spinner);
        sectionSpinner = findViewById(R.id.section_spinner);
        categorySpinner = findViewById(R.id.category_spinner);

    }

    private void uploadImageGetObj(WardrobeService wardrobeService){

        String user_id = getSharedPreferences("myClosetPrefs", MODE_PRIVATE)
                .getString("userId", null);


        if(user_id!=null){
            intUserId = Integer.parseInt(user_id);
        }

        if(selectedClothUri != null) {

            // Spinner seçimlerini al
            String season = seasonSpinner.getSelectedItem().toString();
            String section = sectionSpinner.getSelectedItem().toString();
            String category = categorySpinner.getSelectedItem().toString();

            File file = null;
            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedClothUri);
                file = new File(getCacheDir(), "upload.jpg");
                FileOutputStream outputStream = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, len);
                }
                outputStream.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Dosya okunamadı", Toast.LENGTH_SHORT).show();
                return;
            }

            RequestBody requestFile = RequestBody.create(file, okhttp3.MediaType.parse("image/*"));
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
            RequestBody seasonPart = RequestBody.create(season, MultipartBody.FORM);
            RequestBody sectionPart = RequestBody.create(section, MultipartBody.FORM);
            RequestBody categoryPart = RequestBody.create(category, MultipartBody.FORM);

            RequestBody userIdPart = RequestBody.create(
                    String.valueOf(intUserId), MultipartBody.FORM);


            Call<Integer> id = wardrobeService.getNewClothId();
            id.enqueue(new Callback<Integer>() {

                @Override
                public void onResponse(Call<Integer> call_id, Response<Integer> response) {
                    if(response.body() != null && response.isSuccessful())
                        cloth_id = response.body();
                        cloth_id_string = String.valueOf(cloth_id);
                    if(cloth_id<10){
                        cloth_id_string = "0"+ String.valueOf(cloth_id);
                    }

                    Log.d("CLOTH_ID:", String.valueOf(cloth_id_string));

                    FirebaseUtil.getWardrobeItemStorageRef(user_id, season, section, category, cloth_id_string).putFile(selectedClothUri)
                            .addOnCompleteListener(task ->{
                                Log.d("CLOTH_ID:", String.valueOf(cloth_id));
                                StorageReference clothPicRef = task.getResult().getMetadata().getReference();

                                clothPicRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {

                                    image_url = downloadUri.toString();

                                    RequestBody imageUriPart = RequestBody.create(
                                            image_url, MultipartBody.FORM);

                                    Call<RegisterResponseDto> call = wardrobeService.uploadCloth(filePart, seasonPart, sectionPart, categoryPart, imageUriPart ,userIdPart);
                                    call.enqueue(new Callback<RegisterResponseDto>() {

                                        @Override
                                        public void onResponse(Call<RegisterResponseDto> call, Response<RegisterResponseDto> response) {
                                            if (response.isSuccessful()) {
                                                cloth_id = response.body().getId();
                                                Toast.makeText(AddingClothActivity.this, "Yükleme başarılı 🎉", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(AddingClothActivity.this, "Yükleme başarısız 😢", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<RegisterResponseDto> call, Throwable t) {
                                            Toast.makeText(AddingClothActivity.this, "Hata: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                        }

                                    });

                                    Log.d("AAAAAAAAAAAAAAAA", user_id);


                                    ClothUploadDto clothUploadDto = new ClothUploadDto(intUserId, image_url);

                                    Log.d("AAAAAAAAAAAAAAAA", clothUploadDto.getImage_url());

                                    Call<RegisterResponseDto> call1 = wardrobeService.imageTo3d(clothUploadDto);

                                    call1.enqueue(new Callback<RegisterResponseDto>() {
                                        @Override
                                        public void onResponse(Call<RegisterResponseDto> call, Response<RegisterResponseDto> response) {

                                            if (response.body() != null && response.isSuccessful()) {

                                                RegisterResponseDto res = response.body();
                                                Toast.makeText(AddingClothActivity.this, "Image Uploaded successfully", Toast.LENGTH_LONG).show();

                                                System.out.println(res.getStatus());
                                                Log.d("Update Activity", res.getBody());

                                                TASK_ID = res.getBody().trim();

                                                getImageObj(wardrobeService,season, section, category, cloth_id);

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
                                Log.e("Failed to upload image ", e.toString());
                            });

                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {

                }
            });
            // Seçilen görseli dosyaya çevir



        }else{
            Toast.makeText(AddingClothActivity.this, "Error: Image Couldn't uploaded" , Toast.LENGTH_LONG).show();
        }

    }

    private void getImageObj(WardrobeService wardrobeService, String season, String section, String category, int cloth_id){
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
                                                FirebaseUtil.getWardrobeGlbStorageRef(user_id, season, section, category, cloth_id_string).putFile(localUri)
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
        if (scheduler == null || scheduler.isShutdown() || scheduler.isTerminated()) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
        }
        scheduler.scheduleAtFixedRate(pollTask, 10, 10, TimeUnit.SECONDS);
        //scheduler.scheduleWithFixedDelay(pollTask, 10, 10, TimeUnit.SECONDS);
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
