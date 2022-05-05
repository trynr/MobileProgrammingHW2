package com.ynr.keypsd.mobileprogramminghw2;

import static com.ynr.keypsd.mobileprogramminghw2.Utils.Keys.GMAIL_ADMIN_EMAIL;
import static com.ynr.keypsd.mobileprogramminghw2.Utils.Keys.GMAIL_PASSWORD;
import static com.ynr.keypsd.mobileprogramminghw2.Utils.RequestPermission.REQUEST_ID_READ_EXTERNAL_STORAGE;
import static com.ynr.keypsd.mobileprogramminghw2.Utils.RequestPermission.checkAndRequestReadExternalStoragePermission;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.ynr.keypsd.mobileprogramminghw2.Email.MailSender;
import com.ynr.keypsd.mobileprogramminghw2.Models.User;

import java.util.ArrayList;
import java.util.List;

public class SignupActivity extends AppCompatActivity {

    public static final int REQUEST_TAKE_FROM_GALLERY = 102;

    AppCompatImageView user_profile_photo_iv;
    TextInputEditText signup_username_et;
    TextInputEditText signup_name_surname_et;
    TextInputEditText signup_phone_et;
    TextInputEditText signup_email_et;
    TextInputEditText signup_password_et;
    TextInputEditText signup_confirm_password_et;
    Button signup_complete_button;
    Button add_profile_picture_button;
    Button update_profile_picture_button;
    Button delete_profile_picture_button;
    List<User> registeredUsers;
    Bitmap userProfilePicture;

    private SharedPreferences mPrefs;
    private SharedPreferences.Editor prefsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        define();
        setClickListeners();
    }

    private void define(){
        user_profile_photo_iv = findViewById(R.id.user_profile_photo_iv);
        signup_username_et = findViewById(R.id.signup_username_et);
        signup_name_surname_et = findViewById(R.id.signup_name_surname_et);
        signup_phone_et = findViewById(R.id.signup_phone_et);
        signup_email_et = findViewById(R.id.signup_email_et);
        signup_password_et = findViewById(R.id.signup_password_et);
        signup_confirm_password_et = findViewById(R.id.signup_confirm_password_et);
        signup_complete_button = findViewById(R.id.signup_complete_button);
        add_profile_picture_button = findViewById(R.id.add_profile_picture_button);
        update_profile_picture_button = findViewById(R.id.update_profile_picture_button);
        delete_profile_picture_button = findViewById(R.id.delete_profile_picture_button);

        mPrefs = getSharedPreferences("MusicPlayerSP", Context.MODE_PRIVATE);
        prefsEditor = mPrefs.edit();

        registeredUsers = new ArrayList<>();
        registeredUsers.add(new User(null, "user1", "Kerem Yener", "02122213123",
                "kerem@gmail.com", "123456"));
        registeredUsers.add(new User(null, "user2", "Mehmet Yener", "02123212321",
                "mehmet@gmail.com", "123456"));
        registeredUsers.add(new User(null, "user3", "Ahmet Yener", "02121234123",
                "ahmet@gmail.com", "123456"));
    }

    private void setClickListeners(){
        add_profile_picture_button.setOnClickListener(v -> {
            if(checkAndRequestReadExternalStoragePermission(SignupActivity.this)){
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , REQUEST_TAKE_FROM_GALLERY);
            }
        });
        update_profile_picture_button.setOnClickListener(view -> {
            if(checkAndRequestReadExternalStoragePermission(SignupActivity.this)){
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , REQUEST_TAKE_FROM_GALLERY);
            }
        });
        delete_profile_picture_button.setOnClickListener(view -> {
            user_profile_photo_iv.setImageResource(R.drawable.profile_picture);
            add_profile_picture_button.setVisibility(View.VISIBLE);
            update_profile_picture_button.setVisibility(View.GONE);
            delete_profile_picture_button.setVisibility(View.GONE);
        });
        signup_complete_button.setOnClickListener(view -> {
            String userNameInput = signup_username_et.getText().toString();
            String nameAndSurnameInput = signup_name_surname_et.getText().toString();
            String phoneInput = signup_phone_et.getText().toString();
            String emailInput = signup_email_et.getText().toString();
            String passwordInput = signup_password_et.getText().toString();
            String confirmPasswordInput = signup_confirm_password_et.getText().toString();

            try{
                if(userNameInput.trim().equals(""))
                    throw new Exception("Username field cannot be empty!");
                if(checkIfUserNameAlreadyExists(userNameInput))
                    throw new Exception("Username is already taken!");
                if(nameAndSurnameInput.trim().equals(""))
                    throw new Exception("Name Surname field cannot be empty!");
                if(phoneInput.trim().equals(""))
                    throw new Exception("Phone field cannot be empty!");
                if(phoneInput.trim().length() != 11)
                    throw new Exception("Please enter a valid phone number!");
                if(emailInput.trim().equals(""))
                    throw new Exception("Email field cannot be empty!");
                if(!isEmailValid(emailInput.trim()))
                    throw new Exception("Please enter a valid email!");
                if(passwordInput.trim().equals(""))
                    throw new Exception("Password field cannot be empty!");
                if(passwordInput.trim().length() < 5 || passwordInput.trim().length() > 15)
                    throw new Exception("Password should have at least 5, at most 15 characters!");
                if(confirmPasswordInput.trim().equals(""))
                    throw new Exception("Confirm Password field cannot be empty!");
                if(!passwordInput.equals(confirmPasswordInput))
                    throw new Exception("Passwords you entered do not match!");

                User user = new User(null, userNameInput, nameAndSurnameInput, phoneInput, emailInput, passwordInput);
                onRegisterSuccesful(user);
            }
            catch(Exception e){
                Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }

        });


    }

    private boolean isEmailValid(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void onRegisterSuccesful(User user) {
        Toast.makeText(SignupActivity.this, "Register successful!", Toast.LENGTH_LONG).show();

        sendEmailToUser(user);

        prefsEditor.putString("username", user.getUserName());
        prefsEditor.putString("password", user.getPassword());
        prefsEditor.commit();

        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void sendEmailToUser(User user) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    MailSender sender = new MailSender(GMAIL_ADMIN_EMAIL,
                            GMAIL_PASSWORD);
                    sender.sendMail("Music Player App User Informations", user.toString(),
                            GMAIL_ADMIN_EMAIL, user.getEmail());
                } catch (Exception e) {
                    Log.i("SendMail", e.getMessage(), e);
                }
            }

        }).start();
    }

    private boolean checkIfUserNameAlreadyExists(String userNameInput) {
        for(User registeredUser : registeredUsers){
            if(registeredUser.getUserName().equals(userNameInput)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ID_READ_EXTERNAL_STORAGE:
                if (ContextCompat.checkSelfPermission(SignupActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                            "Storage access is needed.",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case REQUEST_TAKE_FROM_GALLERY:  // Get from gallery result
                    if (data != null) {
                        try {
                            // Get the Image from data
                            if(data.getData()!=null){
                                Uri mImageUri = data.getData();
                                user_profile_photo_iv.setImageURI(mImageUri);
                                add_profile_picture_button.setVisibility(View.GONE);
                                update_profile_picture_button.setVisibility(View.VISIBLE);
                                delete_profile_picture_button.setVisibility(View.VISIBLE);
                            }

                        } catch (Exception e) {
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG)
                                    .show();
                        }

                    }
                    break;
            }
        }

    }
}