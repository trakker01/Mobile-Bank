package com.phone.mobilebank.ui.cash_withdraw;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class Cash_withdrawViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public Cash_withdrawViewModel(){
        mText = new MutableLiveData<>();
        mText.setValue("This is cash withdraw fragment");
    }

    public LiveData<String> getText(){return mText;}
}
