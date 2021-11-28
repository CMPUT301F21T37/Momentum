package com.example.momentum.following;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

/**
 * Custom ViewModel for FollowingFragment
 */
public class FollowingViewModel extends ViewModel {

    private MutableLiveData<ArrayList<String>> requestList;

    public FollowingViewModel() {
        requestList = new MutableLiveData<>();
    }

    public LiveData<ArrayList<String>> getRequestList() {
        return requestList;
    }

    public void addRequest(String user){
        ArrayList<String> listHelper;
        if (requestList.getValue() != null){
            listHelper = new ArrayList<>(requestList.getValue());
        }
        else {
        listHelper = new ArrayList<>();
        }
        listHelper.add(user);
        requestList.setValue(listHelper);
    }

    public void clearRequestList(){
        ArrayList<String> listHelper = new ArrayList<>();
        requestList.setValue(listHelper);
    }
}
