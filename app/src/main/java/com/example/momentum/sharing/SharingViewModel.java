package com.example.momentum.sharing;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Custom ViewModel for SharingFragment
 */
public class SharingViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SharingViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is sharing fragment");
    }

    /**
     * When view is destroyed, set binding to null
     */
    public LiveData<String> getText() {
        return mText;
    }
}
