package com.ynr.keypsd.mobileprogramminghw2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    public static String loginSuccess = "loginSuccess";

    TextInputEditText login_username_et;
    TextInputEditText login_password_et;
    Button login_complete_button;
    String username;
    String password;
    int failCount;
    Button signup_button;
    Button list_songs_button;
    SharedPreferences mPrefs;
    SharedPreferences.Editor prefsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        define();
        setButtonClickListeners();
        // If user loginned once successfully, pass to login screen
        if(mPrefs.getBoolean(loginSuccess, false)){
            startActivity(new Intent(MainActivity.this, ListSongsActivity.class));
        }
    }


    private void define(){
        login_username_et = findViewById(R.id.login_username_et);
        login_password_et = findViewById(R.id.login_password_et);
        login_complete_button = findViewById(R.id.login_complete_button);
        signup_button = findViewById(R.id.signup_button);
        list_songs_button = findViewById(R.id.list_songs_button);
        mPrefs = getSharedPreferences("MusicPlayerSP", Context.MODE_PRIVATE);
        prefsEditor = mPrefs.edit();

        username = mPrefs.getString("username", "");
        password = mPrefs.getString("password", "");
    }

    private void setButtonClickListeners(){
        login_complete_button.setOnClickListener(view -> {
            String usernameInput = login_username_et.getText().toString();
            String passwordInput = login_password_et.getText().toString();
            if(!usernameInput.trim().isEmpty()
                    && !passwordInput.trim().isEmpty()
                    && usernameInput.equals(username)
                    && passwordInput.equals(password))
                onLoginSuccessful();
            else
                onLoginFailed(usernameInput, passwordInput);

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

    private void onLoginSuccessful() {
        Toast.makeText(MainActivity.this,
                "Login successful!",
                Toast.LENGTH_LONG).show();
        prefsEditor.putBoolean(loginSuccess, true);
        prefsEditor.commit();

        Intent intent = new Intent(MainActivity.this, ListSongsActivity.class);
        startActivity(intent);
    }

    private void onLoginFailed(String usernameInput, String passwordInput){
        failCount++;

        if(failCount == 3){
            Toast.makeText(MainActivity.this,
                    "You've failed to login 3 times, please register",
                    Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(intent);
            return;
        }

        Toast.makeText(MainActivity.this,
                "Username or password is wrong!",
                Toast.LENGTH_LONG).show();

    }

}