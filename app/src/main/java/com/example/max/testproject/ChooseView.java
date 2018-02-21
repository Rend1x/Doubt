package com.example.max.testproject;


import java.util.Map;

public class ChooseView {

    int chooseOne;
    int chooseTwo;

    public ChooseView(){}

    public ChooseView(int chooseOne, int chooseTwo){
        this.chooseOne = chooseOne;
        this.chooseTwo  = chooseTwo;
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

    Map<String, Object> ChooseView(){
        return null;
    }
}
