package com.example.momentum.following;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.momentum.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class FollowingListAdapter extends ArrayAdapter<Follower> {

    private ArrayList<Follower> follow_requests;
    private Context context;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private String uid;
    private CollectionReference follow_reqs;



    public FollowingListAdapter(Context context, ArrayList<Follower> follow_requests) {
        super(context, 0, follow_requests);
        this.context = context;
        this.follow_requests = follow_requests;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        Follower follower = getItem(position);
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.follower_request_list_layout, parent,false);
        }

        //Handle TextView and display string from your list
        TextView followername= (TextView)view.findViewById(R.id.follower_name);
        followername.setText(follower.getName());

        //initializes buttons
        Button allow = (Button)view.findViewById(R.id.allow_follow);
        Button deny = (Button)view.findViewById(R.id.deny_follow);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        follow_reqs = db.collection("Users").document(uid).collection("Followers");

        allow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Allowed " + follower.getId());
                accept_follow(follower);
            }
        });

        // sets a listener for the edit button
        deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Denied " + follower.getId());
                deny_follow(follower);
            }
        });

        return view;
    }

    private void accept_follow(Follower f){
        follow_reqs
                .document(f.getId())
                .update("allow_follow", true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Follower Allowed");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error", e);
                    }
                });

    }
    private void deny_follow(Follower f){
        db.collection("Users").document(f.getId()).collection("Following")
                .document(uid)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Removed you from their following list");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error", e);
                    }
                });
        follow_reqs
                .document(f.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Follower Denied");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error", e);
                    }
                });


    }
}

