package com.example.max.testproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


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
    private Uri mFirebaseUriOne;

    private Uri mFirebaseUriTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_image);

        mSendButton = (Button) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TestProject tempMessage = new TestProject(mEditText.getText().toString()
                        ,mUsername
                        ,mPhotoUrl
                        ,null
                        ,null);
                mFirebaseDatabaseReference.child(MESSAGES_CHILD).push()
                        .setValue(tempMessage, new DatabaseReference.CompletionListener() {
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
                                    putImageInStorage(storageReference, mFirebaseUriOne,mFirebaseUriTwo, key);
                                    mEditText.setText("");
                                } else {
                                    Log.w(TAG, "Unable to write message to database.",
                                            databaseError.toException());
                                }
                            }
                        });
            }
        });


        mEditText = (EditText) findViewById(R.id.messageEditText);
        mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mSharedPreferences
                .getInt(FRIENDLY_CHS_LENGTH, DEFAULT_CHS_LENGTH_LIMIT))});
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mAddImageOne = (ImageView) findViewById(R.id.image_button_one);
        mAddImageOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentOne = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intentOne.addCategory(Intent.CATEGORY_OPENABLE);
                intentOne.setType("image/*");
                startActivityForResult(intentOne,Image_Request_Code_One);


            }
        });

        mAddImageTwo = (ImageView) findViewById(R.id.image_button_two);
        mAddImageTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentTwo = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intentTwo.addCategory(Intent.CATEGORY_OPENABLE);
                intentTwo.setType("image/*");
                startActivityForResult(intentTwo,Image_Request_Code_Two);

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       super.onActivityResult(requestCode,resultCode,data);

        if (requestCode == Image_Request_Code_One && resultCode == RESULT_OK && data != null  ) {

            mFirebaseUriOne = data.getData();


            try {
                Bitmap bitmapOne = MediaStore.Images.Media.getBitmap (getContentResolver (), mFirebaseUriOne);
                mAddImageOne.setImageBitmap(bitmapOne);
                mImageViewOne = (ImageView) findViewById(R.id.image_one);
                mImageViewOne.setImageBitmap(bitmapOne);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if (requestCode == Image_Request_Code_Two && resultCode == RESULT_OK && data != null){

            mFirebaseUriTwo = data.getData();

            try {

                Bitmap bitmapTwo = MediaStore.Images.Media.getBitmap (getContentResolver (), mFirebaseUriTwo);
                mAddImageTwo.setImageBitmap(bitmapTwo);
                mImageViewTwo = (ImageView) findViewById(R.id.image_two);
                mImageViewTwo.setImageBitmap(bitmapTwo);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void putImageInStorage(StorageReference storageReference, Uri mFirebaseUriOne,Uri mFirebaseUriTwo, final String key) {

            storageReference.putFile(mFirebaseUriOne).addOnCompleteListener(AddImage.this,
                new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            TestProject friendlyMessage =
                                    new TestProject(
                                            mEditText.getText().toString()
                                            ,mUsername
                                            ,mPhotoUrl
                                            ,task.getResult().getDownloadUrl().toString()
                                            ,task.getResult().getDownloadUrl().toString());
                            mEditText.setText("");
                            mFirebaseDatabaseReference.child(MESSAGES_CHILD).child(key)
                                    .setValue(friendlyMessage);
                        } else {
                            Log.w(TAG, "Image upload task was not successful.",
                                    task.getException());
                        }
                    }
        });
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}

