package com.example.max.testproject.model;

public class Doubt extends PostID{

    private String id;
    private String mKey;
    private String yourChoose;
    private String nameUser;
    private String photoUrl;
    private String imageUrlOne;
    private String imageUrlTwo;
    private int chooseOne;
    private int chooseTwo;

    public Doubt() {
    }


    public Doubt(String id, String yourChoose, String nameUser, String photoUrl, String imageUrlOne, String imageUrlTwo, int chooseOne, int chooseTwo) {
        this.yourChoose = yourChoose;
        this.id = id;
        this.nameUser = nameUser;
        this.photoUrl = photoUrl;
        this.imageUrlOne = imageUrlOne;
        this.imageUrlTwo = imageUrlTwo;
        this.chooseOne = chooseOne;
        this.chooseTwo = chooseTwo;
    }

    public String getmKey() {
        return mKey;
    }

    public void setmKey(String mKey) {
        this.mKey = mKey;
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

    public String getImageUrlTwo() {
        return imageUrlTwo;
    }

    public void setImageUrlOne(String imageUrlOne) {
        this.imageUrlOne = imageUrlOne;
    }

    public void setImageUrlTwo(String imageUrlTwo) {
        this.imageUrlTwo = imageUrlTwo;
    }

    public int getChooseOne() {
        return chooseOne;
    }

    public void setChooseOne(int chooseOne) {
        this.chooseOne = chooseOne;
    }

    public int getChooseTwo() {
        return chooseTwo;
    }

    public void setChooseTwo(int chooseTwo) {
        this.chooseTwo = chooseTwo;
    }


}
