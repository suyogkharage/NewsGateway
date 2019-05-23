package com.assignment5.Suyog.NewsGateway;

import java.io.Serializable;

public class ArticleList implements Serializable {

    private String Id;
    private String Author;
    private String Title;
    private String Description;
    private String ImageUrl;
    private String Url;
    private String PublishedAt;

    public ArticleList(String ident, String au, String tit, String des, String img, String url, String time){
        this.Id = ident;
        this.Author = au;
        this.Title = tit;
        this.Description = des;
        this.ImageUrl = img;
        this.Url = url;
        this.PublishedAt = time;
    }

    public String getId() {
        return Id;
    }

    public  void setId(String id) {
        this.Id = id;
    }

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String auth) {
        this.Author = auth;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String tit) {
        this.Title = tit;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String des) {
        this.Description = des;
    }

    public String getimageUrl() {
        return ImageUrl;
    }

    public void setimageUrl(String img) {
        this.ImageUrl = img;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String ur) {
        this.Url = ur;
    }

    public String getPublishedAt() {
        return PublishedAt;
    }

    public void setPublishedAt(String time) {
        this.PublishedAt = time;
    }


}
