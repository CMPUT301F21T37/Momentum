package com.example.momentum.following;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

/**
 * Custom ViewModel for FollowingFragment
 */
public class FollowingViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Follower>> requestList;

    public FollowingViewModel() {
        requestList = new MutableLiveData<>();
    }

    public LiveData<ArrayList<Follower>> getRequestList() {
        return requestList;
    }

    public void addRequest(Follower follower){
        ArrayList<Follower> listHelper;
        if (requestList.getValue() != null){
            listHelper = new ArrayList<>(requestList.getValue());
        }
        else {
            listHelper = new ArrayList<>();
        }
        listHelper.add(follower);
        requestList.setValue(listHelper);
    }

    public void clearRequestList(){
        ArrayList<Follower> listHelper = new ArrayList<>();
        requestList.setValue(listHelper);
    }
}
