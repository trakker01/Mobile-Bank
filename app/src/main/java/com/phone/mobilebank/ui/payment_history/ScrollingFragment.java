package com.phone.mobilebank.ui.payment_history;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.LabelDescriptor;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
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
import java.util.Objects;

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
    ArrayList<String> data2 = new ArrayList<>();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);


        database.collection("Card-Payments").document(data.get(4)).collection("Stores")
                .orderBy("Date",Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                data1.add(document.getString("Name"));
                                data2.add(document.getId());
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
            String t = adapterView.getAdapter().getItem(i).toString();
            ShowDialog(t,i);
    }

    private void ShowDialog(String Cname,int i){
        final Dialog dialog3 = new Dialog(getContext());
        dialog3.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog3.setContentView(R.layout.payment_history_dialog_menu);

        LinearLayout name = dialog3.findViewById(R.id.name);
        LinearLayout date = dialog3.findViewById(R.id.date);
        LinearLayout value = dialog3.findViewById(R.id.value);
        LinearLayout type = dialog3.findViewById(R.id.type_transaction);

        database.collection("Card-Payments").document(data.get(4)).collection("Stores").document(data2.get(i))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()) {
                                TextView t1 = name.findViewById(R.id.personal_name);
                                TextView t2 = date.findViewById(R.id.personal_date);
                                TextView t3 = type.findViewById(R.id.personal_type_transaction);
                                TextView t4 = value.findViewById(R.id.personal_value);

                                t1.setText(Cname);
                                String Hr = document.getString("Hour");
                                String Me = document.getString("Minute");
                                String S = document.getString("Second");
                                String Y = document.getString("Year");
                                String Mh = document.getString("Month");
                                String D = document.getString("Day");
                                String date = Hr+":"+Me+":"+S+"          "+D+"/"+Mh+"/"+Y;
                                t2.setText(date);
                                Double t = document.getDouble("Value");
                                String va = Objects.requireNonNull(t).toString();
                                t4.setText(va);
                                t3.setText(document.getString("Type"));
                            }
                        }
                    }
                });
        dialog3.show();
        dialog3.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog3.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog3.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog3.getWindow().setGravity(Gravity.BOTTOM);
    }
}