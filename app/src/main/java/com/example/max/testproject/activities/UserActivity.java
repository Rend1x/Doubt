package com.example.max.testproject.activities;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.max.testproject.R;
import com.example.max.testproject.model.Doubt;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserActivity extends MainActivity implements GoogleApiClient.OnConnectionFailedListener {

    private RecyclerView mUserRecyclerView;
    private static final String TAG = "UserActivity";
    private TextView nameUser;
    private ImageView imageUser, settingsButton;


    //public FirebaseRecyclerAdapter<Doubt, UserViewHolder> mFirebaseAdapterUser;


    public static class UserViewHolder extends RecyclerView.ViewHolder {

        public ImageView userImageOne;
        public ImageView userImageTwo;

        public UserViewHolder(View v) {
            super(v);

            userImageOne = (ImageView) itemView.findViewById(R.id.image_user_one);
            userImageTwo = (ImageView) itemView.findViewById(R.id.image_user_two);

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_add_choose_page:
                        Intent addChoose = new Intent(UserActivity.this, AddImage.class);
                        startActivity(addChoose);
                        break;
                    case R.id.navigation_tape_page:
                        Intent tapePage = new Intent(UserActivity.this, MainActivity.class);
                        startActivity(tapePage);
                        break;
                    case R.id.navigation_home_page:
                        Intent homePage = new Intent(UserActivity.this, UserActivity.class);
                        startActivity(homePage);
                        break;
                }
                return false;
            }
        });

        settingsButton = (ImageView) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent setting = new Intent(UserActivity.this, SettingsActivity.class);
                startActivity(setting);
            }
        });
        imageUser = (ImageView) findViewById(R.id.userImageView);
        if (mFirebaseUser.getPhotoUrl() == null) {
            imageUser.setImageDrawable(ContextCompat.getDrawable(UserActivity.this,
                    R.drawable.ic_account_circle_black_36dp));
        } else {
            Picasso.with(UserActivity.this)
                    .load(mFirebaseUser.getPhotoUrl())
                    .into(imageUser);
        }

        nameUser = (TextView) findViewById(R.id.nameUser);
        nameUser.setText(mFirebaseUser.getDisplayName());


        mUserRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_id);



//        mFirebaseAdapterUser = new FirebaseRecyclerAdapter<Doubt, UserViewHolder>(options) {
//            public UserActivity.UserViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
//                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
//                return new UserActivity.UserViewHolder(inflater.inflate(R.layout.user_item, viewGroup, false));
//            }
//
//            protected void onBindViewHolder(final UserActivity.UserViewHolder viewHolder,
//                                            int position,
//                                            final Doubt choose) {
//
//                    Log.i(TAG, "help" + choose.getChooseOne());
//
//                    String imageUrlOne = choose.getImageUrlOne();
//                    String imageUrlTwo = choose.getImageUrlTwo();
//
//                    StorageReference storageReferenceOne = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrlOne);
//                    storageReferenceOne.getDownloadUrl().addOnCompleteListener(
//                            new OnCompleteListener<Uri>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Uri> task) {
//                                    if (task.isSuccessful()) {
//                                        String downloadUrlOne = task.getResult().toString();
//                                        Picasso.with(viewHolder.userImageOne.getContext())
//                                                .load(downloadUrlOne)
//                                                .fit()
//                                                .placeholder(R.mipmap.ic_launcher)
//                                                .into(viewHolder.userImageOne);
//
//                                    } else {
//                                        Log.w(TAG, "Getting download url was not successful.",
//                                                task.getException());
//                                    }
//                                }
//                            });
//
//                    StorageReference storageReferenceTwo = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrlTwo);
//                    storageReferenceTwo.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Uri> task) {
//                            if (task.isSuccessful()) {
//                                String downloadUrlTwo = task.getResult().toString();
//                                Picasso.with(viewHolder.userImageTwo.getContext())
//                                        .load(downloadUrlTwo)
//                                        .fit()
//                                        .placeholder(R.mipmap.ic_launcher)
//                                        .into(viewHolder.userImageTwo);
//                            } else {
//                                Log.w(TAG, "Getting download url was not successful.",
//                                        task.getException());
//                            }
//                        }
//                    });
//
//                    viewHolder.userImageOne.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//
//                            Intent intent = new Intent(UserActivity.this, FullImageActivity.class);
//
//                            // passing data to the book activity
//                            intent.putExtra("Choose", choose.getYourChoose());
//                            intent.putExtra("Image One", choose.getImageUrlOne());
//                            intent.putExtra("Image Two", choose.getImageUrlTwo());
//                            intent.putExtra("User Name", choose.getNameUser());
//                            intent.putExtra("Count Like One", choose.getChooseOne());
//                            intent.putExtra("Count Like Two", choose.getChooseTwo());
//                            // start the activity
//                            UserActivity.this.startActivity(intent);
//                        }
//                    });
//
//                    viewHolder.userImageTwo.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//
//                            Intent intent = new Intent(UserActivity.this, FullImageActivity.class);
//
//                            // passing data to the book activity
//                            intent.putExtra("Choose", choose.getYourChoose());
//                            intent.putExtra("Image One", choose.getImageUrlOne());
//                            intent.putExtra("Image Two", choose.getImageUrlTwo());
//                            intent.putExtra("User Name", choose.getNameUser());
//                            intent.putExtra("Count Like One", choose.getChooseOne());
//                            intent.putExtra("Count Like Two", choose.getChooseTwo());
//                            // start the activity
//                            UserActivity.this.startActivity(intent);
//                        }
//                    });
//            }
//        };




        mUserRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
       // mUserRecyclerView.setAdapter(mFirebaseAdapterUser);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
      //  mFirebaseAdapterUser.stopListening();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
       // mFirebaseAdapterUser.startListening();
    }


}