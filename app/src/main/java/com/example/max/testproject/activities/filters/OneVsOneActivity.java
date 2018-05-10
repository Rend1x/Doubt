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


public class OneVsOneActivity extends MainActivity {

    private Button mOneVsOneOne;
    private Button mBeforeAfterOne;
    private Button mOtherOne;

    private RecyclerView mMessageRecyclerViewOneVsOne;
    private LinearLayoutManager mLinearLayoutManagerOneVsOne;

    private List<Doubt> postUsersOneVsOne;

    private static final String TAG = "OneVsOneActivity";

    private static final Integer OneVsOne = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_one_vs_one);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_add_choose_page:
                        Intent addChoose = new Intent(OneVsOneActivity.this, AddImage.class);
                        startActivity(addChoose);
                        break;

                    case R.id.navigation_tape_page:
                        Intent tapePage = new Intent(OneVsOneActivity.this, MainActivity.class);
                        startActivity(tapePage);
                        break;

                    case R.id.navigation_home_page:
                        Intent homePage = new Intent(OneVsOneActivity.this, UserActivity.class);
                        startActivity(homePage);
                        break;
                }
                return false;
            }
        });

        mMessageRecyclerViewOneVsOne = (RecyclerView) findViewById(R.id.messageRecyclerViewOne);
        mLinearLayoutManagerOneVsOne = new LinearLayoutManager(this);
        mLinearLayoutManagerOneVsOne.setStackFromEnd(true);
        mMessageRecyclerViewOneVsOne.setLayoutManager(mLinearLayoutManagerOneVsOne);
        mFirestore = FirebaseFirestore.getInstance();
        postUsersOneVsOne = new ArrayList<>();
        postListAdapter = new PostListAdapter(getApplicationContext(), postUsersOneVsOne);
        mMessageRecyclerViewOneVsOne.setAdapter(postListAdapter);
        mOneVsOneOne = (Button) findViewById(R.id.oneVsOneOne);
        mBeforeAfterOne = (Button) findViewById(R.id.beforeAfterOne);
        mOtherOne = (Button) findViewById(R.id.otherOne);



        mOneVsOneOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent oneVsOne = new Intent(OneVsOneActivity.this, OneVsOneActivity.class);
                startActivity(oneVsOne);
            }
        });

        mBeforeAfterOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent beforeAfter = new Intent(OneVsOneActivity.this, BeforeAfterActivity.class);
                startActivity(beforeAfter);
            }
        });


        mOtherOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent other = new Intent(OneVsOneActivity.this, OtherActivity.class);
                startActivity(other);
            }
        });


    }

    @Override
    public void onStart(){

        super.onStart();


        mFirestore.collection(MESSAGES_CHILD).whereEqualTo("category",OneVsOne).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {

                if (e != null){

                    Log.d(TAG,"Error: " + e.getMessage());

                }

                for (DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){

                    if (doc.getType()== DocumentChange.Type.ADDED){

                        String postId = doc.getDocument().getId();

                        Doubt post = doc.getDocument().toObject(Doubt.class).withId(postId);

                        postUsersOneVsOne.add(post);


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
}
