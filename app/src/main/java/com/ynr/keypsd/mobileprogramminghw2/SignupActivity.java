package com.ynr.keypsd.mobileprogramminghw2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.ynr.keypsd.mobileprogramminghw2.Email.MailSender;
import com.ynr.keypsd.mobileprogramminghw2.Models.User;

import java.util.ArrayList;
import java.util.List;

public class SignupActivity extends AppCompatActivity {

    public static final int REQUEST_ID_READ_EXTERNAL_STORAGE = 101;
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

        registeredUsers = new ArrayList<>();
        registeredUsers.add(new User(null, "user1", "Kerem Yener", "02122213123",
                "kerem@gmail.com", "123"));
        registeredUsers.add(new User(null, "user2", "Mehmet Yener", "02123212321",
                "mehmet@gmail.com", "1234"));
        registeredUsers.add(new User(null, "user3", "Ahmet Yener", "02121234123",
                "ahmet@gmail.com", "132"));
    }

    private void setClickListeners(){
        add_profile_picture_button.setOnClickListener(v -> {
            if(checkAndRequestPermissions(SignupActivity.this)){
                // choose from  external storage
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , REQUEST_TAKE_FROM_GALLERY);
            }
        });
        update_profile_picture_button.setOnClickListener(view -> {
            if(checkAndRequestPermissions(SignupActivity.this)){
                // choose from  external storage
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
                if(!userNameInput.trim().equals(""))
                    throw new Exception("Kullanıcı adı alanı boş bırakılamaz!");
                if(checkIfUserNameAlreadyExists(userNameInput))
                    throw new Exception("Kullanıcı adı daha önce alınmış!");
                if(!nameAndSurnameInput.trim().equals(""))
                    throw new Exception("Ad soyad alanı boş bırakılamaz!");
                if(!phoneInput.trim().equals(""))
                    throw new Exception("Telefon alanı boş bırakılamaz!");
                if(!emailInput.trim().equals(""))
                    throw new Exception("Email alanı boş bırakılamaz!");
                if(!passwordInput.trim().equals(""))
                    throw new Exception("Parola alanı boş bırakılamaz!");
                if(!confirmPasswordInput.trim().equals(""))
                    throw new Exception("Parola doğrulama alanı boş bırakılamaz!");
                if(!passwordInput.equals(confirmPasswordInput))
                    throw new Exception("Girdiğiniz parolalar uyuşmamaktadır!");

                User user = new User(null, userNameInput, nameAndSurnameInput, phoneInput, emailInput, passwordInput);
                onRegisterSuccesful(user);
            }
            catch(Exception e){
                Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }

        });


    }

    private void onRegisterSuccesful(User user) {
        Toast.makeText(SignupActivity.this, "Başarıyla kayıt oldunuz.", Toast.LENGTH_LONG).show();

        sendEmailToUser(user);

        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void sendEmailToUser(User user) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    MailSender sender = new MailSender("Your_Gmail_UserName@gmail.com",
                            "Your_Gmail_password");
                    sender.sendMail("This is a test subject", "This is the test body content",
                            "mkeremyenerce@gmail.com", "keypsdev@gmail.com");
                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
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

    public static boolean checkAndRequestPermissions(final Activity context) {
        int WExtstorePermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (WExtstorePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded
                    .add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(context,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_READ_EXTERNAL_STORAGE);
            return false;
        }
        return true;
    }

    // Handled permission Result
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ID_READ_EXTERNAL_STORAGE:
                if (ContextCompat.checkSelfPermission(SignupActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                            "Storage erişimi gereklidir.",
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