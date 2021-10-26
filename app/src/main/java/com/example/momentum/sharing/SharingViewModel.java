package com.example.momentum.sharing;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharingViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SharingViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is sharing fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
