package com.example.max.testproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class FullImageActivity extends MainActivity {

    private TextView chooseCountTwoUser,chooseCountOneUser,userTextView,userNameTextView;
    private ImageView userImageViewOne,userImageViewTwo,imageUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_full_image);

        userNameTextView = (TextView) findViewById(R.id.userNameTextView);
        userTextView = (TextView) findViewById(R.id.userTextView);
        chooseCountOneUser = (TextView) findViewById(R.id.choose_count_one_user);
        chooseCountTwoUser = (TextView) findViewById(R.id.choose_count_two_user);

        imageUser = (ImageView) findViewById(R.id.userCardImageView);
        userImageViewOne = (ImageView) findViewById(R.id.userImageViewOne);
        userImageViewTwo = (ImageView) findViewById(R.id.userImageViewTwo);

        Intent intent = getIntent();

        String UserName = intent.getExtras().getString("User Name");
        String Choose = intent.getExtras().getString("Choose");
        int countLikeOne = intent.getExtras().getInt("Count Like One");
        int countLikeTwo = intent.getExtras().getInt("Count Like Two");

        String imageURLOne = intent.getExtras().getString("Image One");
        String imageURLTwo = intent.getExtras().getString("Image Two");



        if (mFirebaseUser.getPhotoUrl() == null) {
            imageUser.setImageDrawable(ContextCompat.getDrawable(FullImageActivity.this,
                    R.drawable.ic_account_circle_black_36dp));
        } else {
            Picasso.with(FullImageActivity.this)
                    .load(mFirebaseUser.getPhotoUrl())
                    .into(imageUser);
        }
        userNameTextView.setText(UserName);
        userTextView.setText(Choose);
        chooseCountOneUser.setText("За первое фото проголосавали: " + countLikeOne);
        chooseCountTwoUser.setText("За первое фото проголосавали: " + countLikeTwo);

        Picasso.with(userImageViewOne.getContext()).load(imageURLOne).into(userImageViewOne);
        Picasso.with(userImageViewTwo.getContext()).load(imageURLTwo).into(userImageViewTwo);

    }

}
