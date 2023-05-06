package com.phone.mobilebank.ui.card_transfer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CardTransferViewModel extends ViewModel{

    private final MutableLiveData<String> mText;

    public CardTransferViewModel(){
        mText = new MutableLiveData<>();
        mText.setValue("This is card transfer fragment");
    }

    public LiveData<String> getText(){return mText;}

}
