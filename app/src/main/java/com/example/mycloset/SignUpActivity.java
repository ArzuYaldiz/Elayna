package com.example.mycloset;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.mycloset.dataClasses.RegisterRequestDto;
import com.example.mycloset.dataClasses.RegisterResponseDto;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import APIService.AuthService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUpActivity extends AppCompatActivity {
    private ImageButton returnButton;
    private Button signupButton;

    private EditText username;
    private EditText email;
    private EditText password;
    private String StrUsername;
    private String StrEmail;
    private String StrPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        findViews();
        return_button();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.170.3:8080/")
                //.baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        AuthService authService = retrofit.create(AuthService.class);

        signupButton.setOnClickListener(v-> signup(authService));

    }

    private void signup(AuthService authService){
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        StrUsername = username.getText().toString().trim();
        StrEmail = email.getText().toString().trim();
        StrPassword = password.getText().toString().trim();


        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);

        if (StrEmail.isEmpty() || StrUsername.isEmpty() || StrPassword.isEmpty()) {
            builder.setTitle("Empty Field")
                    .setMessage("Please fill in all fields")
                    .setCancelable(true)
                    .setPositiveButton("OK", null)
                    .create()
                    .show();
            return;
        }

        RegisterRequestDto request = new RegisterRequestDto(StrUsername,StrPassword,StrEmail);

        Call<RegisterResponseDto> call = authService.register(request);

        call.enqueue(new Callback<RegisterResponseDto>() {
            @Override
            public void onResponse(Call<RegisterResponseDto> call, Response<RegisterResponseDto> response) {
                RegisterResponseDto res = response.body();
                if(response.body()!= null){
                    if (response.isSuccessful()) {

                        builder.setTitle("Success!")
                                .setMessage(res.getBody())
                                .setCancelable(true)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent i = new Intent(getApplicationContext(),LoginActivity.class);//login sayfasına geri dönme
                                        startActivity(i);
                                        finish();
                                    }
                                })
                                .create()
                                .show();

                    } else {
                        builder.setTitle("Oh No!")
                                .setMessage("Registration failed")
                                .setCancelable(true)
                                .setPositiveButton("OK", null)
                                .create()
                                .show();
                    }
                }
            }
            @Override
            public void onFailure(Call<RegisterResponseDto> call, Throwable t) {
                builder.setTitle("Something Went Wrong")
                        .setMessage("Error: "+ t.getMessage())
                        .setCancelable(true)
                        .setPositiveButton("OK", null)
                        .create()
                        .show();
            }

        });
    }

    private void findViews(){
        returnButton = findViewById(R.id.return_button);
        signupButton = findViewById(R.id.register_button);
    }

    private void return_button(){
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);

            }
        });
    }


}

