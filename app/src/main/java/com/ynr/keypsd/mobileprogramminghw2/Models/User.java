package com.ynr.keypsd.mobileprogramminghw2.Models;

import android.graphics.Bitmap;

public class User {

    private Bitmap profilePicture;
    private String userName;
    private String userNameAndSurname;
    private String phone;
    private String email;
    private String password;

    public User(Bitmap profilePicture, String userName, String userNameAndSurname, String phone, String email, String password) {
        this.profilePicture = profilePicture;
        this.userName = userName;
        this.userNameAndSurname = userNameAndSurname;
        this.phone = phone;
        this.email = email;
        this.password = password;
    }

    public Bitmap getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(Bitmap profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserNameAndSurname() {
        return userNameAndSurname;
    }

    public void setUserNameAndSurname(String userNameAndSurname) {
        this.userNameAndSurname = userNameAndSurname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return  "Kullanıcı Adı: " + userName + '\n' +
                "Ad Soyad: " + userNameAndSurname + '\n' +
                "Telefon: " + phone + '\n' +
                "Email: " + email + '\n' +
                "Şifre: '" + password + '\n';
    }
}
