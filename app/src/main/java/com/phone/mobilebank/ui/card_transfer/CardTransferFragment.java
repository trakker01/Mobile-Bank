package com.phone.mobilebank.ui.card_transfer;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.arch.core.*;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.Gravity;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.phone.mobilebank.R;
import com.phone.mobilebank.databinding.FragmentCardTransferBinding;
import com.phone.mobilebank.ui.payment_history.ScrollingFragment;

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

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class CardTransferFragment extends Fragment implements AdapterView.OnItemClickListener{

    private FragmentCardTransferBinding binding;
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    public CardTransferFragment() {
        // Required empty public constructor
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

    private String[] data = new String[9];

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

    ArrayList<String> data1 = new ArrayList<>();
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

         database.collection("Card-Transfer").document(data[4]).collection("Sent")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                data1.add(document.getString("Recipient"));
                                data2.add(document.getId());
                                Log.d("Mesaj : ", document.getId() + " Number " + data1.size());
                                i++;
                            }
                        }
                        ListView listView=(ListView) view.findViewById(R.id.list_card_transfer_history);
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,data1);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(CardTransferFragment.this);
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
        dialog3.setContentView(R.layout.card_transfer_dialog_menu);

        LinearLayout name = dialog3.findViewById(R.id.Recipient);
        LinearLayout iban = dialog3.findViewById(R.id.IBAN);
        LinearLayout value = dialog3.findViewById(R.id.value_sent);
        LinearLayout detail = dialog3.findViewById(R.id.Detail);

        database.collection("Card-Transfer").document(data[4]).collection("Sent").document(data2.get(i))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()) {
                                TextView t1 = name.findViewById(R.id.PRecipient);
                                TextView t2 = iban.findViewById(R.id.PIBAN);
                                TextView t3 = value.findViewById(R.id.Pvalue);
                                TextView t4 = detail.findViewById(R.id.PDetail);

                                t1.setText(Cname);
                                t4.setText(document.getString("Details"));
                                t2.setText(document.getString("IBAN"));
                                t3.setText(String.valueOf(document.getDouble("Amount")));
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