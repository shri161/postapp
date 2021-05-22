package com.example.postapp;

public class Blog {
    private String title;
    private String desc;
    private String image;
    private String username;
    public Blog(){

    }
    public Blog(String title,String desc,String image,String username)
    {
        this.desc=desc;
        this.image=image;
        this.title=title;
        this.username=username;
    }
    public String getUsername(){
        return username;
    }
    public void setUsername()
    {this.username=username;

    }
    public String getDesc() {
        return desc;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
