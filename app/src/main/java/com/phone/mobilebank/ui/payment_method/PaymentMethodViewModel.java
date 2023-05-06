package com.phone.mobilebank.ui.payment_method;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PaymentMethodViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public PaymentMethodViewModel(){
        mText = new MutableLiveData<>();
        mText.setValue("this is Payment method fragment");
    }

    public LiveData<String> getText(){return mText;}

}
