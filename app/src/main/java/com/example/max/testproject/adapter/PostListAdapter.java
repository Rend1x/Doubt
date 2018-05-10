package com.example.max.testproject.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.max.testproject.R;
import com.example.max.testproject.activities.MainActivity;
import com.example.max.testproject.model.Doubt;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.max.testproject.activities.MainActivity.MESSAGES_CHILD;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.ViewHolder>{


    private static final String TAG = "PostListAdapter";
    public List<Doubt> postUsers;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String currentUser;
    private String nameUser;
    private static Integer viewLikeOne,viewLikeTwo;
    public FirebaseFirestore mFirestore;
    private Context context;


    public PostListAdapter(Context context,List<Doubt> postUsers){
        this.postUsers = postUsers;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new PostListAdapter.ViewHolder(inflater.inflate(R.layout.item_message, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        currentUser = mFirebaseUser.getDisplayName();
        mFirestore = FirebaseFirestore.getInstance();

        final String post_Id = postUsers.get(position).postId;

        viewHolder.messageTextView.setText(postUsers.get(position).getYourChoose());

        String imageUrlOne = postUsers.get(position).getImageUrlOne();
        String imageUrlTwo = postUsers.get(position).getImageUrlTwo();

        StorageReference storageReferenceOne = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrlOne);
        storageReferenceOne.getDownloadUrl().addOnCompleteListener(
                new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            String downloadUrlOne = task.getResult().toString();
                            Picasso.with(viewHolder.messageImageViewOne.getContext())
                                    .load(downloadUrlOne)
                                    .fit()
                                    .placeholder(R.mipmap.ic_launcher)
                                    .into(viewHolder.messageImageViewOne);

                        } else {
                            Log.w(TAG, "Getting download url was not successful.",
                                    task.getException());
                        }
                    }
                });



        StorageReference storageReferenceTwo = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrlTwo);
        storageReferenceTwo.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    String downloadUrlTwo = task.getResult().toString();
                    Picasso.with(viewHolder.messageImageViewTwo.getContext())
                            .load(downloadUrlTwo)
                            .fit()
                            .placeholder(R.mipmap.ic_launcher)
                            .into(viewHolder.messageImageViewTwo);
                } else {
                    Log.w(TAG, "Getting download url was not successful.",
                            task.getException());
                }
            }
        });

        Log.d(TAG,"id usr 1 " + mFirebaseUser.getUid());

        Log.d(TAG,"id usr 2 " + postUsers.get(position).getId());

        if (mFirebaseUser.getUid().equals(postUsers.get(position).getId())){

            viewLikeOne = postUsers.get(position).getChooseOne();
            viewLikeTwo = postUsers.get(position).getChooseTwo();

            viewHolder.countViewOne.setText("За первое фото проголосавали: " + viewLikeOne);
            viewHolder.countViewTwo.setText("За второе фото проголосавали: " + viewLikeTwo);

        }else {

            final DocumentReference documentReference = mFirestore.collection(MESSAGES_CHILD)
                    .document(post_Id).collection("VotesUsers_"+post_Id).document(currentUser);

            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                        nameUser = documentSnapshot.getString(currentUser);

                        Log.d(TAG, "Name USer " + nameUser);


                        if (mFirebaseUser.getUid().equals(nameUser)){

                            Log.d(TAG,"Name USer 1 " + nameUser);

                            viewHolder.messageImageViewOne.setEnabled(false);
                            viewHolder.messageImageViewTwo.setEnabled(false);

                            viewLikeOne = postUsers.get(position).getChooseOne();
                            viewLikeTwo = postUsers.get(position).getChooseTwo();

                            viewHolder.countViewOne.setText("За первое фото проголосавали: " + viewLikeOne);
                            viewHolder.countViewTwo.setText("За второе фото проголосавали: " + viewLikeTwo);

                        }else {

                            viewHolder.messageImageViewOne.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(final View view) {


                                    final DocumentReference documentReference = mFirestore.collection(MESSAGES_CHILD).document(post_Id);

                                    mFirestore.runTransaction(new Transaction.Function<Double>() {
                                        @Override
                                        public Double apply (Transaction transaction)  throws FirebaseFirestoreException {

                                            DocumentSnapshot snapshot = transaction.get(documentReference);

                                            Double newCountLike = snapshot.getDouble("chooseOne") + 1;

                                            transaction.update(documentReference,"chooseOne",newCountLike);

                                            return newCountLike;

                                        }

                                    });
                                    Map<String,Object> likeMap = new HashMap<>();
                                    likeMap.put(currentUser,mFirebaseUser.getUid());

                                    mFirestore.collection(MESSAGES_CHILD).document(post_Id).collection("VotesUsers_"+post_Id)
                                            .document(currentUser).set(likeMap);
                                }
                            });

//                            viewHolder.messageImageViewTwo.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(final View view) {
//
//
//                                    final DocumentReference documentReference = mFirestore.collection(MESSAGES_CHILD).document(post_Id);
//
//                                    mFirestore.runTransaction(new Transaction.Function<Double>() {
//                                        @Override
//                                        public Double apply (Transaction transaction)  throws FirebaseFirestoreException {
//
//                                            DocumentSnapshot snapshot = transaction.get(documentReference);
//
//                                            Double newCountLike = snapshot.getDouble("chooseTwo") + 1;
//
//                                            transaction.update(documentReference,"chooseTwo",newCountLike);
//
//                                            return newCountLike;
//
//                                        }
//
//                                    });
//                                    Map<String,Object> likeMap = new HashMap<>();
//                                    likeMap.put(currentUser,mFirebaseUser.getUid());
//
//                                    mFirestore.collection(MESSAGES_CHILD).document(post_Id).collection("VotesUsers_"+post_Id)
//                                            .document(currentUser).set(likeMap);
//                                }
//                            });
                        }
                    }
            });
        }

        viewHolder.messengerTextView.setText(postUsers.get(position).getNameUser());
        if (postUsers.get(position).getPhotoUrl() == null) {
            viewHolder.messengerImageView.setImageDrawable(ContextCompat.getDrawable(context,
                    R.drawable.ic_account_circle_black_36dp));
        } else {
            Picasso.with(viewHolder.messengerImageView.getContext())
                    .load(postUsers.get(position).getPhotoUrl())
                    .into(viewHolder.messengerImageView);
        }
    }

    @Override
    public int getItemCount() {
        return postUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView messageTextView;
        public ImageView messageImageViewOne;
        public ImageView messageImageViewTwo;
        public TextView messengerTextView;
        public TextView countViewOne;
        public TextView countViewTwo;
        public CircleImageView messengerImageView;

        public ViewHolder(View itemView) {
            super(itemView);

            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            messageImageViewOne = (ImageView) itemView.findViewById(R.id.messageImageViewOne);
            messageImageViewTwo = (ImageView) itemView.findViewById(R.id.messageImageViewTwo);
            messengerTextView = (TextView) itemView.findViewById(R.id.userNameTextView);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
            countViewOne = (TextView) itemView.findViewById(R.id.choose_count_one);
            countViewTwo = (TextView) itemView.findViewById(R.id.choose_count_two);
        }
    }
}