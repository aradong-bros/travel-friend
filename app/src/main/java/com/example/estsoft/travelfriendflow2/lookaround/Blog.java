package com.example.estsoft.travelfriendflow2.lookaround;


import io.realm.RealmObject;

public class Blog extends RealmObject{

    private String title;
    private String url;
    private String content;
    private String date;
    private String plan_time;
    private String plan_season;

    public Blog() {
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPlan_season(String plan_season) {
        this.plan_season = plan_season;
    }

    public void setPlan_time(String plan_time) {
        this.plan_time = plan_time;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    public String getPlan_season() {
        return plan_season;
    }

    public String getPlan_time() {
        return plan_time;
    }
}
