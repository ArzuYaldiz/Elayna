package com.example.mycloset;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mycloset.dataClasses.AuthenticationRequestDto;
import com.example.mycloset.dataClasses.RegisterRequestDto;
import com.example.mycloset.dataClasses.RegisterResponseDto;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import APIService.AuthService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button adminButton;
    private Button memberButton;
    private Button signUpButton;
    private Button loginButton;
    private String strEmail;
    private String strPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            mAuth.signInAnonymously()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("Auth", "Signed in anonymously");
                            FirebaseUser user = mAuth.getCurrentUser();

                            // Now the user has a valid auth token
                            // You can now safely access Firebase Storage
                        } else {
                            Log.e("Auth", "Sign-in failed", task.getException());
                        }
                    });
        } else {
            Log.d("Auth", "Already signed in");
        }



        findViews();
        signup();
        adminSignIn();
        memberSignIn();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        AuthService authService = retrofit.create(AuthService.class);
        loginButton.setOnClickListener(v-> login(authService));

    }

    private void findViews(){
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        loginButton = findViewById(R.id.login_button);
        adminButton = findViewById(R.id.admin_signin_button);
        memberButton = findViewById(R.id.member_signin_button);
        signUpButton = findViewById(R.id.sign_up_button);
    }

    private void login( AuthService authService){

        strEmail = email.getText().toString().trim();
        strPassword = password.getText().toString().trim();

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

        AuthenticationRequestDto request = new AuthenticationRequestDto(strEmail, strPassword);

        Call<RegisterResponseDto> call = authService.login(request);

        call.enqueue(new Callback<RegisterResponseDto>() {
            @Override
            public void onResponse(Call<RegisterResponseDto> call, Response<RegisterResponseDto> response) {
                if(response.body()!=null && response.isSuccessful()){
                    RegisterResponseDto res = response.body();
                    String user_id = String.valueOf(response.body().getId());

                    builder.setTitle("Successfully logged in")//delete later
                            .setMessage("Welcome back to Elayna")
                            .setCancelable(true)
                            .setPositiveButton("OK", null)
                            .create()
                            .show();

                    System.out.println(res.getStatus());
                    Log.d("Login Activity", res.getBody());

                    getSharedPreferences("myClosetPrefs", MODE_PRIVATE)
                            .edit()
                            .putString("userId", user_id)
                            .apply();

                    //kullanıcı uygulamayı kapatınca bunu silll
                    /*
                    * getSharedPreferences("myClosetPrefs", MODE_PRIVATE).edit().clear().apply();
                    */

                    if(Objects.equals(res.getBody(), "First time user")){
                        Intent i = new Intent(getApplicationContext(),UserProfileRegisterActivity.class);
                        startActivity(i);
                    }
                    else{
                        getSharedPreferences("myClosetPrefs", MODE_PRIVATE)
                                .getString("userId", null);
                        //burada ana sayfaya yönlendirme
                        Intent i = new Intent(getApplicationContext(),ProfilePageActivity.class);
                        startActivity(i);
                        /*Intent i = new Intent(getApplicationContext(),ClothImageActivity.class);
                        startActivity(i);*/
                    }


                } else{
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            Gson gson = new Gson();
                            RegisterResponseDto errorResponse = gson.fromJson(errorJson, RegisterResponseDto.class);

                            String message = errorResponse.getBody();
                            if ("Please verify your account".equals(message)) {
                                builder.setTitle("Please verify your account")
                                        .setMessage(message)
                                        .setCancelable(true)
                                        .setPositiveButton("OK", null)
                                        .create()
                                        .show();
                            }
                            else {
                                builder.setTitle("Login failed")
                                        .setMessage(message)
                                        .setCancelable(true)
                                        .setPositiveButton("OK", null)
                                        .create()
                                        .show();
                            }
                        }else {
                            builder.setTitle("Login failed")
                                    .setMessage("Unknown error")
                                    .setCancelable(true)
                                    .setPositiveButton("OK", null)
                                    .create()
                                    .show();
                        }

                    }catch (Exception e) {
                        e.printStackTrace();
                        builder.setTitle("Error")
                                .setMessage("Something went wrong")
                                .setCancelable(true)
                                .setPositiveButton("OK", null)
                                .create()
                                .show();
                    }

                }
            }
            @Override
            public void onFailure(Call<RegisterResponseDto> call, Throwable t) {
                dialogMessage(builder, "Something Went Wrong","Error: "+ t.getMessage());
            }

        });
    }

    private void dialogMessage( AlertDialog.Builder builder, String title, String message){
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("OK", null)
                .create()
                .show();
    }
    private void signup(){
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(getApplicationContext(),SignUpActivity.class);
                startActivity(i);

            }
        });
    }
    private void adminSignIn(){
        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(getApplicationContext(),AdminSigninActivity.class);
                startActivity(i);

            }
        });
    }

    private void memberSignIn(){
        memberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(getApplicationContext(),MemberSigninActivity.class);
                startActivity(i);

            }
        });
    }
}
