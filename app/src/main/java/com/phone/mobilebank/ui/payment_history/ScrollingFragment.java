package com.phone.mobilebank.ui.payment_history;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.phone.mobilebank.MainActivity;
import com.phone.mobilebank.R;
import com.phone.mobilebank.databinding.FragmentPaymentHistoryBinding;
import com.phone.mobilebank.ui.home.HomeFragment;
import com.phone.mobilebank.ui.payment_history.ScrollingFragment;

import org.checkerframework.checker.units.qual.A;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ScrollingFragment extends Fragment implements AdapterView.OnItemClickListener{

    HomeFragment home;
    private FragmentPaymentHistoryBinding binding;
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    ArrayList<String> data = new ArrayList<>();
    ArrayList<String> data1 = new ArrayList<>();


    public ScrollingFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //PaymentHistoryAdapter viewModel = new ViewModelProvider(this).get(PaymentHistoryAdapter.class);
        binding = FragmentPaymentHistoryBinding.inflate(inflater,container,false);
        View root = binding.getRoot();

//        data1= home.getStores();
//        database.collection("Card-Payments").document("1").collection("Stores")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                data1.add(document.getId());
//                                Log.d("Mesaj : ",document.getId() + " Number " + data1.size());
//
//                            }
//                        }
//                    }
//                });
        try {
            FileInputStream fis = getActivity().openFileInput("DAT.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                data.add(line);

            }
            br.close();
            isr.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return root;
    }
    int i=0;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);


        database.collection("Card-Payments").document("1").collection("Stores")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                data1.add(document.getId());
                                Log.d("Mesaj : ", document.getId() + " Number " + data1.size());
                                i++;
                            }
                        }
                        ListView listView=(ListView) view.findViewById(R.id.list_view);
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,data1);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(ScrollingFragment.this);
                    }
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(i == 0){
            Toast.makeText(getActivity(),"Data",Toast.LENGTH_SHORT).show();
        }
    }
}