package com.example.max.testproject;


import com.google.firebase.storage.StorageTask;

public class TestProject {

    private String id;
    private String yourChoose;
    private String nameUser;
    private String photoUrl;
    private String imageUrlOne;
    private String imageUrlTwo;

    public TestProject(){}

    public TestProject(String yourChoose, String nameUser, String photoUrl, String imageUrlOne, String imageUrlTwo) {
        this.yourChoose = yourChoose;
        this.nameUser = nameUser;
        this.photoUrl = photoUrl;
        this.imageUrlOne = imageUrlOne;
        this.imageUrlTwo = imageUrlTwo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setYourChoose(String yourChoose) {
        this.yourChoose = yourChoose;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getYourChoose() {
        return yourChoose;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getImageUrlOne() {
        return imageUrlOne;
    }

    public  String getImageUrlTwo(){
        return imageUrlTwo;
    }

    public void setImageUrlOne(String imageUrlOne) {
        this.imageUrlOne = imageUrlOne;
    }

    public void setImageUrlTwo(String imageUrlTwo){
        this.imageUrlTwo = imageUrlTwo;
    }


}
