package com.phone.mobilebank.ui.data_base;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.phone.mobilebank.MainActivity;
import com.google.firebase.firestore.FirebaseFirestore;

public class Communication {
   MainActivity m ;
   FirebaseFirestore database;
   public static final String TAG = "YOUR-TAG-NAME";

   public void StartDatabase(){
       database = FirebaseFirestore.getInstance();
   }

   public FirebaseFirestore GetDB(){
       return database;
   }

   public void GetData(FirebaseFirestore db){
      db.collection("Accounts")
              .get()
              .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                 @Override
                 public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                       for (QueryDocumentSnapshot document : task.getResult()) {
                          Log.d(TAG, document.getId() + " => " + document.getData());
                       }
                    } else {
                       Log.w(TAG, "Error getting documents.", task.getException());
                    }
                 }
              });
   }
}