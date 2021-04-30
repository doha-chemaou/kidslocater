package com.example.kidlocater;

import com.google.firebase.database.DatabaseReference;

public class UserHelper {
    String prenom,nom;
    String date_naissance,genre,email,phone,password;

    public UserHelper() {
    }

    public UserHelper(String prenom, String nom, String date_naissance, String genre, String email, String phone, String password) {
        this.prenom = prenom;
        this.nom = nom;
        this.date_naissance = date_naissance;
        this.genre = genre;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    public UserHelper(String prenom, String nom) {
        this.prenom = prenom;
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDate_naissance() {
        return date_naissance;
    }

    public void setDate_naissance(String date_naissance) {
        this.date_naissance = date_naissance;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
