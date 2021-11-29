package com.example.momentum.following;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.momentum.R;
import com.example.momentum.databinding.FragmentFollowingBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @version 4.0
 * @author rittwage
 */

public class FollowingFragment extends Fragment {
    public static final String UPDATE_COUNT = "UPDATE_COUNT";
    private FollowingViewModel FollowingViewModel;
    private FragmentFollowingBinding binding;

    private FirebaseFirestore db;
    private FirebaseUser user;
    private String uid;
    private CollectionReference collectionReference;

    private ArrayAdapter<Follower> followerAdapter;
    private ListView requestListView;


    Button follow_user;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FollowingViewModel = new ViewModelProvider(this).get(FollowingViewModel.class);

        binding = FragmentFollowingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        follow_user = binding.followUser;
        Activity activity = getActivity();

        //initialize database and relevant variables
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        final CollectionReference requestReference = db.collection("Users").document(uid).collection("Followers");


        // listener for the Firestore database to accept realtime updates
        //Listview implementaion was learned from HabitEvent listview creation by hyan3 and misay
        requestReference
                .whereEqualTo("allow_follow", false)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                        @Nullable FirebaseFirestoreException error) {
                        FollowingViewModel.clearRequestList();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            String name = (String) doc.getData().get("username");
                            String id = (String) doc.getId();
                            // store it to the the view model

                            FollowingViewModel.addRequest(new Follower(id, name));
                        }
                        // Notifying the adapter to render any new data fetched from the cloud
                        followerAdapter.notifyDataSetChanged();
                    }
                });
        // initiates the display
        showRequests();

        //sets a click listener on request follow button
        follow_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dialog is created on click of the follow button
                AlertDialog.Builder enter = new AlertDialog.Builder(activity);
                final EditText user_edit = new EditText(activity);
                user_edit.setHint("Enter Username");
                LinearLayout layout = new LinearLayout(activity);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.addView(user_edit);
                enter.setView(layout);
                enter.setPositiveButton("Follow", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String entered_username = user_edit.getText().toString();
                        if (user_edit == null || entered_username.length() < 1) {
                            Toast.makeText(activity, "Proper UID Entry Required",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            db.collection("Users")
                                    .whereEqualTo("Username", entered_username)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    if (document == null) {
                                                        Toast.makeText(activity, "user not found",
                                                                Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Add_follow(document.getId(), document.getData().get("username").toString());
                                                        Req_follow(document.getId());
                                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                                        Toast.makeText(activity, "Follow Requested",
                                                                Toast.LENGTH_LONG).show();

                                                    }

                                                }
                                            } else {
                                                Log.d(TAG, "Error getting documents: ", task.getException());
                                            }
                                        }
                                    });
                        }

                    }
                });
                AlertDialog alertDialog = enter.create();
                alertDialog.show();

                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.red_main));
            }
        });
        return root;

    }

    /**
     *
     */
    public void showRequests(){
        requestListView = binding.requestList;
        FollowingViewModel.getRequestList().observe(getViewLifecycleOwner(), new Observer<ArrayList<Follower>>() {
            @Override
            public void onChanged(ArrayList<Follower> requestList){
                followerAdapter = new FollowingListAdapter(getContext(), requestList);
                requestListView.setAdapter(followerAdapter);
            }
        });
    }

    /**
     *
     * @param fid
     * @param Fusername
     */
    private void Add_follow(String fid, String Fusername){
        HashMap<String, Object> data = new HashMap<>();
        data.put("username", Fusername);
        db.collection("Users").document(uid).collection("Following")
                .document(fid)
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
    /**
     *
     * @param fid
     */
    private void Req_follow(String fid){
        String Uusername = user.getDisplayName();
        HashMap<String, Object> data = new HashMap<>();
        data.put("username", Uusername);
        data.put("allow_follow",false);
        Log.d(TAG, "Data Created");
        db.collection("Users").document(fid).collection("Followers")
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


    /**
     * When view is destroyed, set binding to null
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


