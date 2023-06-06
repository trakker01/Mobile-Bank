package com.phone.mobilebank.ui.card_transfer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.arch.core.*;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.phone.mobilebank.R;
import com.phone.mobilebank.databinding.FragmentCardTransferBinding;
import com.phone.mobilebank.ui.payment_history.ScrollingFragment;

import java.util.ArrayList;

public class CardTransferFragment extends Fragment implements AdapterView.OnItemClickListener{

    private FragmentCardTransferBinding binding;
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    public CardTransferFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        CardTransferViewModel cardTransferViewModel =
                new ViewModelProvider(this).get(CardTransferViewModel.class);

        binding = FragmentCardTransferBinding.inflate(inflater,container,false);
        View root = binding.getRoot();

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(CardTransferFragment.this)
                        .navigate(R.id.nav_send_cash);
            }
        });


        return root;
    }

    int i =0;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);

        ArrayList<String> data1 = new ArrayList<>();

//        database.collection("Card-Payments").document("1").collection("Stores")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                data1.add(document.getId());
//                                Log.d("Mesaj : ", document.getId() + " Number " + data1.size());
//                                i++;
//                            }
//                        }
//                        ListView listView=(ListView) view.findViewById(R.id.list_card_transfer_history);
//                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,data1);
//                        listView.setAdapter(adapter);
//                        listView.setOnItemClickListener(CardTransferFragment.this);
//                    }
//                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String t = String.valueOf(i);
        String t1 = adapterView.getAdapter().getItem(i).toString();
        Toast.makeText(getActivity(),t1,Toast.LENGTH_SHORT).show();
    }
}