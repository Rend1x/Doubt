package com.example.max.testproject.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.max.testproject.R;
import com.example.max.testproject.model.Doubt;
import com.example.max.testproject.utils.StringUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import java.util.Map;


public class AddImage extends MainActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "AddImage";

    private static final int Image_Request_Code_One = 1;
    private static final int Image_Request_Code_Two = 2;
    private int category = 0;
    private EditText mEditText;
    private Button mSendButton;
    private Button mOneVsOne;
    private Button mBeforeAfter;
    private Button mOther;
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

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_add_choose_page:
                        Intent addChoose = new Intent(AddImage.this, AddImage.class);
                        startActivity(addChoose);
                        break;
                    case R.id.navigation_tape_page:
                        Intent tapePage = new Intent(AddImage.this, MainActivity.class);
                        startActivity(tapePage);
                        break;
                    case R.id.navigation_home_page:
                        Intent homePage = new Intent(AddImage.this, UserActivity.class);
                        startActivity(homePage);
                        break;
                }
                return false;
            }
        });

        mImageViewOne = (ImageView) findViewById(R.id.image_one);
        mImageViewTwo = (ImageView) findViewById(R.id.image_two);
        mSendButton = (Button) findViewById(R.id.sendButton);
        mEditText = (EditText) findViewById(R.id.messageEditText);
        mOneVsOne = (Button) findViewById(R.id.oneVsOne);
        mBeforeAfter = (Button) findViewById(R.id.beforeAfter);
        mOther = (Button) findViewById(R.id.other);


        mFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference(mFirebaseUser.getUid());

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

        /*
        * Присвоение переменной category значение 1 (Выбранна категория 1 на 1 или лицом к лицу)
        * */
        mOneVsOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = 1;

                Log.d(TAG,"category1: " + category);
            }
        });
        /*
         * Присвоение переменной category значение 2 (Выбранна категория до/после)
         * */
        mBeforeAfter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = 2;

                Log.d(TAG,"category2: " + category);
            }
        });
        /*
         * Присвоение переменной category значение 3 (Выбранна категория разное)
         * */
        mOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = 3;

                Log.d(TAG,"category3: " + category);
            }
        });

        /*
         * Отправка данных в базу данных
         * Проверка данных на корректность перед отправкой
         * Возврат на другую активность если данные заполнены корректно
         * Если данные введены не корректно сообщение об ошибки или не заполнености даных
         * */
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(mEditText.getText().toString())) {
                    Toast.makeText(AddImage.this, "Your choose null", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mImageViewOne.getDrawable() == null) {
                    Toast.makeText(AddImage.this, "You must select image", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mImageViewTwo.getDrawable() == null) {
                    Toast.makeText(AddImage.this, "You must select image", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (category == 0){

                    Toast.makeText(AddImage.this,"Выберите категорию",Toast.LENGTH_LONG).show();

                }else if(downloadUrlOne == null && downloadUrlTwo == null){

                    Toast.makeText(AddImage.this,"Картинки загружаються",Toast.LENGTH_LONG).show();

                    }else {

                    Doubt upload = new Doubt(mFirebaseUser.getUid(), mEditText.getText().toString()
                            , mUsername
                            , mPhotoUrl
                            , downloadUrlOne
                            , downloadUrlTwo, 0, 0, category);

                    mFirestore.collection(MESSAGES_CHILD).add(upload);

                    if (upload != null) {
                        Intent backToActivity = new Intent(AddImage.this, MainActivity.class);
                        startActivity(backToActivity);
                        finish();
                    }
                    Toast.makeText(AddImage.this, "Upload your choose", Toast.LENGTH_SHORT).show();
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
            uploadImageToFirebaseOne();

        }else if (requestCode == Image_Request_Code_Two && resultCode == RESULT_OK && data != null){
            mFirebaseUriTwo = data.getData();
            Log.i(TAG, "selected Image = "+ mFirebaseUriTwo);
            mImageViewTwo.setImageURI(mFirebaseUriTwo);
            uploadImageToFirebaseTwo();
        }
    }


    /*
     * Загрузка картинок в Firebase Storange
     * */
    private void uploadImageToFirebaseOne() {

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
    private void uploadImageToFirebaseTwo() {

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