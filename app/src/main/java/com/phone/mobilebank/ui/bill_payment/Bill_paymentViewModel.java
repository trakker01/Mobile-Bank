package com.phone.mobilebank.ui.bill_payment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class Bill_paymentViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public Bill_paymentViewModel(){
        mText = new MutableLiveData<>();
        mText.setValue("this is bill payment fragment");
    }

    public LiveData<String> getText(){return mText;}

}