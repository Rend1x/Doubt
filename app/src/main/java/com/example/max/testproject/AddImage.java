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
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class AddImage extends MainActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "AddImage";
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";
    private ImageView mAddImageOne;
    private ImageView mAddImageTwo;
    private static final int DEFAULT_CHS_LENGTH_LIMIT = 10;
    private static final int Image_Request_Code_One = 1;
    private static final int Image_Request_Code_Two = 2;
    private EditText mEditText;
    private Button mSendButton;
    private static final String FRIENDLY_CHS_LENGTH = "friendly_chs_length";
    private ImageView mImageViewOne;
    private ImageView mImageViewTwo;
    private StorageReference storageReference;
    private String key;
    private Uri mFirebaseUriOne;
    private Uri mFirebaseUriTwo;

    public String imageNameOne;
    public String imageNameTwo;

    private String downloadUrlOne;
    private String downloadUrlTwo;


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
    }
    public void handleChooseImageOne(View view) {
        Intent pickerPhotoIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickerPhotoIntent, 0);
    }

    public void handleChooseImageTwo(View view) {
        Intent pickerPhotoIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickerPhotoIntent, 1);
    }

    public void handleUploadChoose(View view){

        if (TextUtils.isEmpty(mEditText.getText().toString())){
            Toast.makeText(this,"Your choose null",Toast.LENGTH_SHORT);
            return;
        }
        if (mImageViewOne.getDrawable()==null){
            Toast.makeText(this,"You must select image",Toast.LENGTH_SHORT);
            return;
        }
        if (mImageViewTwo.getDrawable()==null){
            Toast.makeText(this,"You must select image",Toast.LENGTH_SHORT);
            return;
        }

        TestProject upload = new TestProject(mEditText.getText().toString()
                ,mUsername
                ,mPhotoUrl
                ,downloadUrlOne
                ,downloadUrlTwo);
        mFirebaseDatabaseReference.child(MESSAGES_CHILD).push()
                .setValue(upload, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError,
                                           DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            String key = databaseReference.getKey();
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
                    }
                });
        Toast.makeText(AddImage.this,"Upload your choose",Toast.LENGTH_SHORT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK && data != null  ) {

            mFirebaseUriOne = data.getData();
            Log.i(TAG, "selected Image = " + mFirebaseUriOne);
            mImageViewOne.setImageURI(mFirebaseUriOne);
            uploadImageToFirebaseOne(storageReference,key,mFirebaseUriOne);

        }else if (requestCode == 1 && resultCode == RESULT_OK && data != null){
            mFirebaseUriTwo = data.getData();
            Log.i(TAG, "selected Image = "+ mFirebaseUriTwo);
            mImageViewTwo.setImageURI(mFirebaseUriTwo);
            uploadImageToFirebaseTwo(storageReference,key,mFirebaseUriTwo);
        }
    }

    private void uploadImageToFirebaseOne(StorageReference storageReference, String key, Uri mFirebaseUriOne) {

        imageNameOne = StringUtils.getRandomString(20 ) + ".jpg";
        StorageReference mountainsRef = storageReference.child(mFirebaseUser.getUid()).child(imageNameOne);
        UploadTask uploadTask = mountainsRef.putFile(mFirebaseUriOne);

        uploadTask.addOnFailureListener(new OnFailureListener() {

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

        imageNameTwo = StringUtils.getRandomString(20)  + ".jpg";
        StorageReference mountainsRef = storageReference.child(mFirebaseUser.getUid()).child(imageNameTwo);
        UploadTask uploadTask = mountainsRef.putFile(mFirebaseUriTwo);

        uploadTask.addOnFailureListener(new OnFailureListener() {

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

