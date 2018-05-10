package com.example.max.testproject.activities;


import android.app.DownloadManager;
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
import com.example.max.testproject.adapter.PostListAdapter;
import com.example.max.testproject.adapter.PostUserAdapter;
import com.example.max.testproject.model.Doubt;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class UserActivity extends MainActivity implements GoogleApiClient.OnConnectionFailedListener {

    private RecyclerView mUserRecyclerView;
    private static final String TAG = "UserActivity";
    private TextView nameUser;

    private List<Doubt> postUserActivity;
    public PostUserAdapter postUserAdapter;
    private ImageView imageUser, settingsButton;

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

        postUserActivity = new ArrayList<>();
        postUserAdapter = new PostUserAdapter(getApplicationContext(), postUserActivity,this);
        mUserRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mUserRecyclerView.setAdapter(postUserAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();

        mFirestore.collection(MESSAGES_CHILD)
                .whereEqualTo("id",mFirebaseAuth.getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {

                if (e != null){

                    Log.d(TAG,"Error: " + e.getMessage());

                }

                for (DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){

                    if (doc.getType()== DocumentChange.Type.ADDED){

                        String postId = doc.getDocument().getId();

                        Doubt post = doc.getDocument().toObject(Doubt.class).withId(postId);

                        postUserActivity.add(post);


                    }
                }

            }
        });

    }

    public void onPostSelected(Doubt choose){
        Intent intent = new Intent(this, FullImageActivity.class);
        // passing data to the book activity
        intent.putExtra("Choose", choose.getYourChoose());
        intent.putExtra("Image One", choose.getImageUrlOne());
        intent.putExtra("Image Two", choose.getImageUrlTwo());
        intent.putExtra("User Name", choose.getNameUser());
        intent.putExtra("Count Like One",choose.getChooseOne());
        intent.putExtra("Count Like Two", choose.getChooseTwo());
        // start the activity
        this.startActivity(intent);
    }
}