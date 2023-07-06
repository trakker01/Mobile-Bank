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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.phone.mobilebank.R;
import com.phone.mobilebank.databinding.FragmentPaymentHistoryBinding;
import com.phone.mobilebank.ui.home.HomeFragment;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class ScrollingFragment extends Fragment implements AdapterView.OnItemClickListener{

    HomeFragment home;
    private FragmentPaymentHistoryBinding binding;
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    private String[] data = new String[8];
    ArrayList<String> data1 = new ArrayList<>();


    public ScrollingFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    private static SecretKeySpec secretKey;
    private static byte[] key;
    private static final String mkey="UMfDlSAKArrhmvCJEBZvcE3jBZkMavmw";
    public void createSecreteKey(String myKey) {
        MessageDigest sha = null;
        try {
            key = myKey.getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public String decrypt(String text, String key) {
        try {
            createSecreteKey(key);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(text)));
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //PaymentHistoryAdapter viewModel = new ViewModelProvider(this).get(PaymentHistoryAdapter.class);
        binding = FragmentPaymentHistoryBinding.inflate(inflater,container,false);
        View root = binding.getRoot();


        return root;
    }
    int i=0;
    ArrayList<String> data2 = new ArrayList<>();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);

        try {
            FileInputStream fis = getActivity().openFileInput("DAT.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            int i = 0;
            while ((line = br.readLine()) != null) {
                data[i] = decrypt(line,mkey);
                i++;


            }
            br.close();
            isr.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("ID", data[4]);

        database.collection("Card-Payments").document(data[4]).collection("Payments")
                .orderBy("Date",Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                data1.add(document.getString("Company_name"));
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

        database.collection("Card-Payments").document(data[4]).collection("Payments").document(data2.get(i))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()) {
                                TextView t1 = name.findViewById(R.id.personal_name);
                                TextView t2 = date.findViewById(R.id.personal_date);
                                TextView t3 = type.findViewById(R.id.company_IBAN);
                                TextView t4 = value.findViewById(R.id.personal_value);

                                t1.setText(Cname);
                                t2.setText(document.getString("Date"));
                                t3.setText(document.getString("IBAN"));
                                t4.setText(String.valueOf(document.getDouble("Value")));
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