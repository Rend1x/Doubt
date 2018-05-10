package com.example.max.testproject.activities.filters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.example.max.testproject.R;
import com.example.max.testproject.activities.AddImage;
import com.example.max.testproject.activities.MainActivity;
import com.example.max.testproject.activities.SingInActivity;
import com.example.max.testproject.activities.UserActivity;
import com.example.max.testproject.adapter.PostListAdapter;
import com.example.max.testproject.model.Doubt;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class BeforeAfterActivity extends MainActivity {

    private Button mOneVsOneBeforeAfter;
    private Button mBeforeAfterBeforeAfter;
    private Button mOtherBeforeAfter;

    private RecyclerView mMessageRecyclerViewBeforeAfter;
    private LinearLayoutManager mLinearLayoutManagerBeforeAfter;

    private List<Doubt> postUsersBeforeAfter;

    private static final String TAG = "BeforeAfterActivity";

    private static final Integer BeforeAfter = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_before_after);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_add_choose_page:
                        Intent addChoose = new Intent(BeforeAfterActivity.this, AddImage.class);
                        startActivity(addChoose);
                        break;

                    case R.id.navigation_tape_page:
                        Intent tapePage = new Intent(BeforeAfterActivity.this, MainActivity.class);
                        startActivity(tapePage);
                        break;

                    case R.id.navigation_home_page:
                        Intent homePage = new Intent(BeforeAfterActivity.this, UserActivity.class);
                        startActivity(homePage);
                        break;
                }
                return false;
            }
        });

        mMessageRecyclerViewBeforeAfter = (RecyclerView) findViewById(R.id.messageRecyclerViewBefore);
        mLinearLayoutManagerBeforeAfter = new LinearLayoutManager(this);
        mLinearLayoutManagerBeforeAfter.setStackFromEnd(true);
        mMessageRecyclerViewBeforeAfter.setLayoutManager(mLinearLayoutManagerBeforeAfter);
        mFirestore = FirebaseFirestore.getInstance();
        postUsersBeforeAfter = new ArrayList<>();
        postListAdapter = new PostListAdapter(getApplicationContext(), postUsersBeforeAfter);
        mMessageRecyclerViewBeforeAfter.setAdapter(postListAdapter);
        mOneVsOneBeforeAfter = (Button) findViewById(R.id.oneVsOneBefore);
        mBeforeAfterBeforeAfter = (Button) findViewById(R.id.beforeAfterBefore);
        mOtherBeforeAfter = (Button) findViewById(R.id.otherBefore);


        mOneVsOneBeforeAfter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent oneVsOne = new Intent(BeforeAfterActivity.this, OneVsOneActivity.class);
                startActivity(oneVsOne);
            }
        });

        mBeforeAfterBeforeAfter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent beforeAfter = new Intent(BeforeAfterActivity.this, BeforeAfterActivity.class);
                startActivity(beforeAfter);
            }
        });


        mOtherBeforeAfter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent other = new Intent(BeforeAfterActivity.this, OtherActivity.class);
                startActivity(other);
            }
        });



    }

    @Override
    public void onStart(){

        super.onStart();


        mFirestore.collection(MESSAGES_CHILD).whereEqualTo("category",BeforeAfter).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {

                if (e != null){

                    Log.d(TAG,"Error: " + e.getMessage());

                }

                for (DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){

                    if (doc.getType()== DocumentChange.Type.ADDED){

                        String postId = doc.getDocument().getId();

                        Doubt post = doc.getDocument().toObject(Doubt.class).withId(postId);

                        postUsersBeforeAfter.add(post);


                    }
                }

            }
        });

    }
}
