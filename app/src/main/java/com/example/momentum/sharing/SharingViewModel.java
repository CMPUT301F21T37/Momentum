package com.example.momentum.sharing;

import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom ViewModel for SharingFragment and get following user info
 * @author boxiao
 */
public class SharingViewModel extends ViewModel {
    private static final String TAG = SharingViewModel.class.getSimpleName();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public MutableLiveData<List<FollowingEntity>> following = new MutableLiveData<>();

    //get following user info
    public void getFollowing(String followingName) {
        if (user != null && !TextUtils.isEmpty(user.getUid())) {
            db.collection("Users")
                    .document(user.getUid())
                    .collection("Following")
                    .addSnapshotListener((queryDocumentSnapshots, error) -> {
                        if (queryDocumentSnapshots != null) {
                            List<FollowingEntity> followingIdList = new ArrayList<>();
                            for(QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                                if (doc != null) {
                                    String name = (String) doc.getData().get("username");
                                    FollowingEntity following = new FollowingEntity(name, doc.getId());
                                    //If 'followingName' is null or empty, not filter
                                    if (!TextUtils.isEmpty(followingName)) {
                                        //Fuzzy search, name is case insensitive.
                                        if ((name != null) && name.toLowerCase().contains(followingName.toLowerCase())) {
                                            followingIdList.add(following);
                                        }
                                    } else {
                                        followingIdList.add(following);
                                    }
                                }
                            }
                            following.setValue(followingIdList);
                        } else {
                            Log.w(TAG, "getFollowers queryDocumentSnapshots is null");
                        }
                    });
        }
    }
}
