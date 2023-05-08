package com.phone.mobilebank.ui.add_card;

//import com.phone.mobilebank.ui.BazaDeDate.Communication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.phone.mobilebank.ui.add_card.Add_cardViewModel;

import com.phone.mobilebank.R;
import com.phone.mobilebank.databinding.FragmentAddCardBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Add_cardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Add_cardFragment extends Fragment {

    private FragmentAddCardBinding binding;

    FirebaseFirestore database;
    public static final String TAG = "YOUR-TAG-NAME";
    //Communication com = new Communication();

    //String data = com.GetData();
    //Add_cardViewModel card = new Add_cardViewModel();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String DBName;
    private String DBExpiration_Date;
    private String DBNumber_card;

    public Add_cardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Add_cardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Add_cardFragment newInstance(String param1, String param2) {
        Add_cardFragment fragment = new Add_cardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //com.GetData();
        Add_cardViewModel add_cardViewModel = new ViewModelProvider(this).get(Add_cardViewModel.class);
        binding = FragmentAddCardBinding.inflate(inflater,container,false);
        View root = binding.getRoot();
        final TextView textView = binding.textAddCard;
        add_cardViewModel.getText().observe(getViewLifecycleOwner(),textView::setText);



        binding.submitData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  if(!binding.textName.getText().toString().equals(""))
               // { TextView ET;
                 //   if(!binding.textExpirationCard.getText().toString().equals(""))
                 //   {
                     //   if(!binding.textNumberCard.getText().toString().equals(""))
                     //   {

                            String Name = binding.textName.getText().toString();
                            String Expiration = binding.textExpirationCard.getText().toString();
                            String Number = binding.textNumberCard.getText().toString();

                            database = FirebaseFirestore.getInstance();
                            //QueryDocumentSnapshot docRef = database.collection("Accounts");
                            database.collection("Accounts")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for(QueryDocumentSnapshot document : task.getResult())
                                                {
                                                        if(Name.equals(document.getString("Name")) &&
                                                                Expiration.equals(document.getString("Expiration_date"))
                                                                        && Number.equals(document.getString("Card_Number"))){

                                                            Log.d(TAG,document.getData() + " " + document.getString("Name") + " " +
                                                                    document.getString("Expiration_date") + " " + document.getString("Card_Number"));

                                                            binding.textAddCard.setText(document.getString("Name"));
                                                            binding.textAddCard2.setText(document.getString("Expiration_date"));
                                                            binding.textAddCard3.setText(document.getString("Card_Number"));

                                                        }
                                                }
                                            } else {
                                                Log.w(TAG, "Error getting documents.", task.getException());
                                            }
                                        }
                                    });

                      //  }else {

                     //   }
                   // }
                   // else{

                 //   }
              //  }
               // else {

             //   }
            }
        });


       // add_cardViewModel.ChangeText(data);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}