package com.example.momentum.following;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.momentum.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class FollowingListAdapter extends ArrayAdapter<String> {

    private Context context;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private String uid;
    private CollectionReference eventsReference;

    private ArrayList<String> follow_requests = new ArrayList<String>();

    public FollowingListAdapter(Context context, ArrayList<String> follow_requests) {
        super(context, 0, follow_requests);
        this.context = context;
        this.follow_requests = follow_requests;
    }
    @Override
    public int getCount() {
        return follow_requests.size();
    }

    @Override
    public String getItem(int pos) {
        return follow_requests.get(pos);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.follower_request_list_layout, null);
        }

        //Handle TextView and display string from your list
        TextView followername= (TextView)view.findViewById(R.id.follower_name);
        followername.setText(follow_requests.get(position));

        Button allow = (Button)view.findViewById(R.id.allow_follow);
        Button deny = (Button)view.findViewById(R.id.deny_follow);

        allow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // sets a listener for the edit button
        deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }
}

