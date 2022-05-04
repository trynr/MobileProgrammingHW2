package com.ynr.keypsd.mobileprogramminghw2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button login_button;
    Button signup_button;
    Button list_songs_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        define();
        setButtonClickListeners();

    }


    private void define(){
        login_button = findViewById(R.id.login_button);
        signup_button = findViewById(R.id.signup_button);
        list_songs_button = findViewById(R.id.list_songs_button);
    }

    private void setButtonClickListeners(){
        login_button.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        signup_button.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        list_songs_button.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ListSongsActivity.class);
            startActivity(intent);
        });
    }

}