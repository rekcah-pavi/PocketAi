package com.hci.pocketai.ui.transform;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class TransformViewModel extends ViewModel {

    private final MutableLiveData<List<String>> mTexts;

    public TransformViewModel() {
        mTexts = new MutableLiveData<>();
        List<String> texts = new ArrayList<>();
        texts.add("Text to Image");
        texts.add("Voice to Text");
        texts.add("Image to Text");
        texts.add("Text to Voice");
        texts.add("Bg Remover");


        mTexts.setValue(texts);
    }



    public LiveData<List<String>> getTexts() {
        return mTexts;
    }
}