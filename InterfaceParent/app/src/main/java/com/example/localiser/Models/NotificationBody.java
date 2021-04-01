package com.example.localiser.Models;

public class NotificationBody {
    private String titre;
    private String contenu;

    public NotificationBody(String titre, String contenu) {
        this.titre = titre;
        this.contenu = contenu;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }
}
