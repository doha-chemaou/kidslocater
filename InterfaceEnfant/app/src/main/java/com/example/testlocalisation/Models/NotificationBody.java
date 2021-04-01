package com.example.testlocalisation.Models;

public class NotificationBody {
    private String title;
    private String content;

    public NotificationBody(String title, String content) {
        this.title = title;
        this.content = content;
    }
    public  NotificationBody(){}
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
