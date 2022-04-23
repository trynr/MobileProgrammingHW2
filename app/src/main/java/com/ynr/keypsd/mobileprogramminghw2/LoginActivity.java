package com.ynr.keypsd.mobileprogramminghw2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText login_username_et;
    TextInputEditText login_password_et;
    Button login_complete_button;
    String username = "user";
    String password = "123";
    int failCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        define();
        setButtonClickListeners();

    }

    private void define(){
        login_username_et = findViewById(R.id.login_username_et);
        login_password_et = findViewById(R.id.login_password_et);
        login_complete_button = findViewById(R.id.login_complete_button);
    }

    private void setButtonClickListeners(){
        login_complete_button.setOnClickListener(view -> {
            String usernameInput = login_username_et.getText().toString();
            String passwordInput = login_password_et.getText().toString();
            if(usernameInput.equals(username)
                && passwordInput.equals(password))
                    onLoginSuccessful();
            else
                onLoginFailed(usernameInput, passwordInput);


        });
    }

    private void onLoginSuccessful() {
        Toast.makeText(LoginActivity.this,
                "Giriş başarılı!",
                Toast.LENGTH_LONG).show();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void onLoginFailed(String usernameInput, String passwordInput){
        failCount++;

        if(failCount == 3){
            Toast.makeText(LoginActivity.this,
                    "3 kez başarısız giriş yaptınız, lütfen kayıt olunuz.",
                    Toast.LENGTH_LONG).show();
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
            return;
        }

        if(!username.equals(usernameInput) && password.equals(passwordInput)){
            Toast.makeText(LoginActivity.this,
                    "Kullanıcı adı hatalı!",
                    Toast.LENGTH_LONG).show();
        }
        else if(username.equals(usernameInput) && !password.equals(passwordInput)){
            Toast.makeText(LoginActivity.this,
                    "Parola hatalı!",
                    Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(LoginActivity.this,
                    "Kullanıcı adı ve parola hatalı!",
                    Toast.LENGTH_LONG).show();
        }

    }



}