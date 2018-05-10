package com.example.max.testproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.max.testproject.R;
import com.example.max.testproject.activities.FullImageActivity;
import com.example.max.testproject.activities.UserActivity;
import com.example.max.testproject.model.Doubt;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostUserAdapter extends RecyclerView.Adapter<PostUserAdapter.ViewHolder> {

    private static final String TAG = "PostUserAdapter";
    private Context context;
    public List<Doubt> postUserActivity;



    private UserActivity mListener;


    public PostUserAdapter(Context context, List<Doubt> postUserActivity, UserActivity listener){
        this.context = context;
        this.postUserActivity = postUserActivity;
        this.mListener = listener;

    }

    @Override

    public PostUserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new PostUserAdapter.ViewHolder(inflater.inflate(R.layout.user_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final PostUserAdapter.ViewHolder viewHolder, final int position) {

        String imageUrlOne = postUserActivity.get(position).getImageUrlOne();
        String imageUrlTwo = postUserActivity.get(position).getImageUrlTwo();

        StorageReference storageReferenceOne = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrlOne);
        storageReferenceOne.getDownloadUrl().addOnCompleteListener(
                new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            String downloadUrlOne = task.getResult().toString();
                            Picasso.with(viewHolder.userImageOne.getContext())
                                    .load(downloadUrlOne)
                                    .fit()
                                    .placeholder(R.mipmap.ic_launcher)
                                    .into(viewHolder.userImageOne);

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
                    Picasso.with(viewHolder.userImageTwo.getContext())
                            .load(downloadUrlTwo)
                            .fit()
                            .placeholder(R.mipmap.ic_launcher)
                            .into(viewHolder.userImageTwo);
                } else {
                    Log.w(TAG, "Getting download url was not successful.",
                            task.getException());
                }
            }
        });

        viewHolder.userImageOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null){

                    mListener.onPostSelected(postUserActivity.get(position));

                }
            }
        });

        viewHolder.userImageTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mListener != null){

                    mListener.onPostSelected(postUserActivity.get(position));

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return postUserActivity.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView userImageOne;
        public ImageView userImageTwo;

        public ViewHolder(View itemView) {
            super(itemView);

            userImageOne = (ImageView) itemView.findViewById(R.id.image_user_one);
            userImageTwo = (ImageView) itemView.findViewById(R.id.image_user_two);

        }
    }
}
