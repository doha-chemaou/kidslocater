package com.example.localiser.Models;

import androidx.annotation.NonNull;

public class HistoryWeb {
    private String titre;
    private String url;

    public HistoryWeb(){

    }

    public HistoryWeb(String titre, String url) {
        this.titre = titre;
        this.url = url;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @NonNull
    @Override
    public String toString() {
        return  "Titre :"+titre+"\n" +"Url :"+url;
    }
}
