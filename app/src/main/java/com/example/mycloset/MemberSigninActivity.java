package com.example.mycloset;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class MemberSigninActivity extends AppCompatActivity {
    private ImageButton returnButton;
    private Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_login);
        findViews();
        return_button();
    }

    private void findViews(){
        returnButton = findViewById(R.id.return_button);
        signupButton = findViewById(R.id.member_login_button);
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


