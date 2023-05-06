package com.phone.mobilebank.ui.user_guide;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class User_GuideViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public User_GuideViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This isuser_guide user guide fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}