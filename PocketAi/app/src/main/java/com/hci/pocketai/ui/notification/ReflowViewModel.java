package com.hci.pocketai.ui.notification;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ReflowViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ReflowViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("You don't have any notifications!");
    }

    public LiveData<String> getText() {
        return mText;
    }
}