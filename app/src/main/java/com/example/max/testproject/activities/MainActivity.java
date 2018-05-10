package com.example.max.testproject.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.max.testproject.R;
import com.example.max.testproject.activities.filters.BeforeAfterActivity;
import com.example.max.testproject.activities.filters.OneVsOneActivity;
import com.example.max.testproject.activities.filters.OtherActivity;
import com.example.max.testproject.adapter.PostListAdapter;
import com.example.max.testproject.model.Doubt;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";
    public static final String MESSAGES_CHILD = "choose";
    public static final String ANONYMOUS = "anonymous";

    public String mUsername;
    public String nameUser;
    public String mPhotoUrl;
    public SharedPreferences mSharedPreferences;
    public GoogleApiClient mGoogleApiClient;

    private List<Doubt> postUsers;
    public PostListAdapter postListAdapter;

    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;


    public FirebaseAuth mFirebaseAuth;
    public FirebaseUser mFirebaseUser;


    private Button mOneVsOneMain;
    private Button mBeforeAfterMain;
    private Button mOtherMain;

    public FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_add_choose_page:
                        Intent addChoose = new Intent(MainActivity.this, AddImage.class);
                        startActivity(addChoose);
                        break;

                    case R.id.navigation_tape_page:
                        Intent tapePage = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(tapePage);
                        break;

                    case R.id.navigation_home_page:
                        Intent homePage = new Intent(MainActivity.this, UserActivity.class);
                        startActivity(homePage);
                        break;
                }
                return false;
            }
        });

        mUsername = ANONYMOUS;

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            startActivity(new Intent(this, SingInActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();


        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mFirestore = FirebaseFirestore.getInstance();
        mOneVsOneMain = (Button) findViewById(R.id.oneVsOneMain);
        mBeforeAfterMain = (Button) findViewById(R.id.beforeAfterMain);
        mOtherMain = (Button) findViewById(R.id.otherMain);
        postUsers = new ArrayList<>();
        postListAdapter = new PostListAdapter(getApplicationContext(), postUsers);
        mMessageRecyclerView.setAdapter(postListAdapter);


        mOneVsOneMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent oneVsOne = new Intent(MainActivity.this, OneVsOneActivity.class);
                startActivity(oneVsOne);
            }
        });

        mBeforeAfterMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent beforeAfter = new Intent(MainActivity.this, BeforeAfterActivity.class);
                startActivity(beforeAfter);
            }
        });


        mOtherMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent other = new Intent(MainActivity.this, OtherActivity.class);
                startActivity(other);
            }
        });


    }

    @Override
    public void onStart(){

        super.onStart();


        mFirestore.collection(MESSAGES_CHILD).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {

                if (e != null){

                    Log.d(TAG,"Error: " + e.getMessage());

                }

                for (DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){

                    if (doc.getType()== DocumentChange.Type.ADDED){

                        String postId = doc.getDocument().getId();

                        Doubt post = doc.getDocument().toObject(Doubt.class).withId(postId);

                        postUsers.add(post);


                    }
                }

            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}

