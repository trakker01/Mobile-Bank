package com.phone.mobilebank.ui.add_card;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;



public class Add_cardViewModel extends ViewModel{

    protected MutableLiveData<String > mText;

    public Add_cardViewModel(){
        mText = new MutableLiveData<>();
        mText.setValue("this is Add card fragment");
    }

    public void ChangeText(String text){
        mText.setValue(text);
    }

    public LiveData<String> getText(){return mText;}
}
