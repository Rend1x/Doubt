package com.example.max.testproject;


public class TestProject {

    private String id;
    private String text;
    private String name;
    private String photoUrl;
    private String imageUrlOne;
    private String imageUrlTwo;

    public TestProject(){

    }

    public TestProject(String text, String name, String photoUrl, String imageUrlOne, String imageUrlTwo) {
        this.text = text;
        this.name = name;
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

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getText() {
        return text;
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
