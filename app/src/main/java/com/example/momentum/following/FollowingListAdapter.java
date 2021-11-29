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
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.momentum.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Adapter to keep the list of people trying to follow up-to-date
 * @ author rittwage, misay
 */
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
                Toast.makeText(context, "Accepted Follow",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // sets a listener for the edit button
        deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Denied " + follower.getId());
                //deny_follow(follower);
                Toast.makeText(context, "Denied Follow",
                        Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    /**
     * Accept the follow of a user
     * @param f
     * follower
     */
    private void accept_follow(Follower f){
        follow_reqs
                .document(f.getId())
                .update("allow_follow", true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Follower Allowed");
                        Add_follow(f.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error", e);
                    }
                });

    }

    /**
     * Add the current user to following of the follower
     * @param fid
     * follower id
     */
    private void Add_follow(String fid){
        HashMap<String, Object> data = new HashMap<>();
        data.put("username", user.getDisplayName());
        db.collection("Users").document(fid).collection("Following")
                .document(uid)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}

