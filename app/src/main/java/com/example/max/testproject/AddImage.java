package com.example.max.testproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;


public class AddImage extends MainActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "AddImage";

    private static final int Image_Request_Code_One = 1;
    private static final int Image_Request_Code_Two = 2;
    private EditText mEditText;
    private Button mSendButton;
    private ImageView mImageViewOne;
    private ImageView mImageViewTwo;
    private StorageReference storageReference;
    private Uri mFirebaseUriOne;
    private Uri mFirebaseUriTwo;

    public String imageNameOne;
    public String imageNameTwo;

    private String downloadUrlOne;
    private String downloadUrlTwo;

    private StorageTask uploadTaskOne;
    private StorageTask uploadTaskTwo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_image);

        mImageViewOne = (ImageView) findViewById(R.id.image_one);
        mImageViewTwo = (ImageView) findViewById(R.id.image_two);
        mSendButton = (Button) findViewById(R.id.sendButton);
        mEditText =(EditText) findViewById(R.id.messageEditText);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference(mFirebaseUser.getUid());
        mFirebaseDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null){
                    return;
                }
                Map<String, Object> updatedFlower = (Map<String, Object>) dataSnapshot.getValue();
                Log.i(TAG, "updatedFlower = " + updatedFlower.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "onCancelled");
            }
        });
    mImageViewOne.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent pickerPhotoIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickerPhotoIntent, Image_Request_Code_One);
        }
    });

    mImageViewTwo.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent pickerPhotoIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickerPhotoIntent, Image_Request_Code_Two);
        }
    });

    mSendButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (uploadTaskOne != null && uploadTaskOne.isInProgress() && uploadTaskTwo != null && uploadTaskTwo.isInProgress()){
                Toast.makeText(AddImage.this,"Uploading",Toast.LENGTH_SHORT);

            }else{
            if (TextUtils.isEmpty(mEditText.getText().toString())){
                Toast.makeText(AddImage.this,"Your choose null",Toast.LENGTH_SHORT);
                return;
            }
            if (mImageViewOne.getDrawable()==null){
                Toast.makeText(AddImage.this,"You must select image",Toast.LENGTH_SHORT);
                return;
            }
            if (mImageViewTwo.getDrawable()==null){
                Toast.makeText(AddImage.this,"You must select image",Toast.LENGTH_SHORT);
                return;
            }

            final TestProject upload = new TestProject(mEditText.getText().toString()
                    ,mUsername
                    ,mPhotoUrl
                    ,downloadUrlOne
                    ,downloadUrlTwo,0,0);
            mFirebaseDatabaseReference.child(MESSAGES_CHILD).push()
                    .setValue(upload, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError,
                                               DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                String key = databaseReference.child(MESSAGES_CHILD).getParent().getKey();
                                StorageReference storageReference =
                                        FirebaseStorage.getInstance()
                                                .getReference(mFirebaseUser.getUid())
                                                .child(key)
                                                .child(mFirebaseUriOne.getLastPathSegment())
                                                .child(mFirebaseUriTwo.getLastPathSegment());
                                uploadImageToFirebaseOne(storageReference,key,mFirebaseUriOne);
                                uploadImageToFirebaseTwo(storageReference,key,mFirebaseUriTwo);
                            } else {
                                Log.w(TAG, "Unable to write message to database.",
                                        databaseError.toException());
                            }
                            if (upload != null){
                                Intent backToActivity = new Intent(AddImage.this, MainActivity.class);
                                backToActivity.putExtra("key",key);
                                startActivity(backToActivity);
                                finish();
                            }
                        }
                    });
            Toast.makeText(AddImage.this,"Upload your choose",Toast.LENGTH_SHORT);
            }
        }
    });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Request_Code_One && resultCode == RESULT_OK && data != null  ) {

            mFirebaseUriOne = data.getData();
            Log.i(TAG, "selected Image = " + mFirebaseUriOne);
            mImageViewOne.setImageURI(mFirebaseUriOne);
            uploadImageToFirebaseOne(storageReference,key,mFirebaseUriOne);

        }else if (requestCode == Image_Request_Code_Two && resultCode == RESULT_OK && data != null){
            mFirebaseUriTwo = data.getData();
            Log.i(TAG, "selected Image = "+ mFirebaseUriTwo);
            mImageViewTwo.setImageURI(mFirebaseUriTwo);
            uploadImageToFirebaseTwo(storageReference,key,mFirebaseUriTwo);
        }
    }

    private void uploadImageToFirebaseOne(StorageReference storageReference, String key, Uri mFirebaseUriOne) {

        imageNameOne = StringUtils.getRandomString(20 ) + ".png";
        StorageReference mountainsRef = storageReference.child(mFirebaseUser.getUid()).child(imageNameOne);
        uploadTaskOne = mountainsRef.putFile(mFirebaseUriOne);

        uploadTaskOne.addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.i(TAG, "Upload failed");
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                downloadUrlOne = task.getResult().getDownloadUrl().toString();
                Log.i(TAG, "Upload successful, downloadUrl = "+ downloadUrlOne);
            }
        });
    }
    private void uploadImageToFirebaseTwo(StorageReference storageReference, String key, Uri mFirebaseUriTwo) {

        imageNameTwo = StringUtils.getRandomString(20)  + ".png";
        StorageReference mountainsRef = storageReference.child(mFirebaseUser.getUid()).child(imageNameTwo);
        uploadTaskTwo = mountainsRef.putFile(mFirebaseUriTwo);

        uploadTaskTwo.addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.i(TAG, "Upload failed");
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                downloadUrlTwo = task.getResult().getDownloadUrl().toString();
                Log.i(TAG, "Upload successful, downloadUrl = "+ downloadUrlTwo);
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
