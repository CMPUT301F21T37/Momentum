package com.example.momentum.following;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.momentum.databinding.FragmentFollowingBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

/**
 * @version 3.0
 * @author rittwage
 */

public class FollowingFragment extends Fragment{
    public static final String UPDATE_COUNT = "UPDATE_COUNT";
    private FollowingViewModel FollowingViewModel;
    private FragmentFollowingBinding binding;

    private FirebaseFirestore db;
    private FirebaseUser user;
    private String uid;
    private CollectionReference collectionReference;


    Button follow_user;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FollowingViewModel = new ViewModelProvider(this).get(FollowingViewModel.class);

        /**
         * Database
         */
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        binding = FragmentFollowingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        follow_user = binding.followUser;
        Activity activity = getActivity();
        follow_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                        if(user_edit == null || entered_username.length() < 1){
                            Toast.makeText(activity, "Proper UID Entry Required",
                                    Toast.LENGTH_SHORT).show();
                        }
                        //QF9w rkfi GWVt tVkY u002 ZDV1 l0A3
                        else{
                            db.collection("Users")
                                    .whereEqualTo("Username", entered_username)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    if (document == null){
                                                        Toast.makeText(activity, "user not found",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                    else{

                                                        Add_follow(document.getId(), document.getData().get("Username").toString());
                                                        Req_follow(document.getId());
                                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                                    }

                                                }
                                            } else {
                                                Log.d(TAG, "Error getting documents: ", task.getException());
                                            }
                                        }
                                    });
                        }

                    }});
                enter.show();

            }
        });
        return root;
    }

    private void Add_follow(String fid, String Fusername){
        HashMap<String, Object> data = new HashMap<>();
        data.put("username", Fusername);
        db.collection("Users").document(uid).collection("Followers")
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
    private void Req_follow(String fid){
        String Uusername = user.getDisplayName();
        HashMap<String, Object> data = new HashMap<>();
        data.put("username", Uusername);
        data.put("allow_follow", false);
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


    /**
     * When view is destroyed, set binding to null
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


