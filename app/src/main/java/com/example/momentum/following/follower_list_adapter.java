package com.example.momentum.following;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class follower_list_adapter extends ArrayAdapter<String> {

    private Context context;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private String uid;
    private CollectionReference eventsReference;

    private ArrayList<String> follow_requests = new ArrayList<String>();

    public follower_list_adapter(Context context, ArrayList<String> follow_requests) {
        super(context, 0, follow_requests);
        this.context = context;
        this.follow_requests = follow_requests;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view = convertView;
        RecyclerView.ViewHolder viewHolder;
        String string = getItem(position);
        return view;
    }
}

