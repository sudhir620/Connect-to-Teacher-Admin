package com.skcoder.ctzoneadmin.ui.addteachers;

public class model {
    String name, email, post, purl, key;

    public model() {
    }

    public model(String name, String email, String post, String purl, String key) {
        this.name = name;
        this.email = email;
        this.post = post;
        this.purl = purl;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getPurl() {
        return purl;
    }

    public void setPurl(String purl) {
        this.purl = purl;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
