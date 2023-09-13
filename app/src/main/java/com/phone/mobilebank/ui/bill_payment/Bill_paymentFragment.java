package com.phone.mobilebank.ui.bill_payment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.phone.mobilebank.R;
import com.phone.mobilebank.databinding.FragmentBillPaymentBinding;

import org.checkerframework.checker.units.qual.C;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Bill_paymentFragment extends Fragment{

    FirebaseFirestore database = FirebaseFirestore.getInstance();

    private FragmentBillPaymentBinding binding;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Bill_paymentFragment(){}

    public static Bill_paymentFragment newInstance(String param1, String param2) {
        Bill_paymentFragment fragment = new Bill_paymentFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public String onItemSelected(AdapterView<?> parent, View view, int pos, long id){
        return String.valueOf(parent.getSelectedItemId());
    }

    private String company;

    private String[] Companies = new String[5000];

    private String[] data = new String[8];
    private int i=0;
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bill_paymentViewModel bill_paymentViewModel = new ViewModelProvider(this).get(Bill_paymentViewModel.class);
        binding = FragmentBillPaymentBinding.inflate(inflater,container,false);
        View root = binding.getRoot();

        try {
            FileInputStream fis = getActivity().openFileInput("DAT.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String line;
            int j = 0;
            while ((line = br.readLine()) != null) {
                data[j] = line;
                j++;
            }
            br.close();
            isr.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        database.collection("Companies").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Companies[i] = document.getId().toString();
                        i++;
                    }
                }
            }
        });

        Spinner spinner = binding.spinner;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, Companies);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                company = String.valueOf(spinner.getItemAtPosition(i));
                adapterView.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

                company=null;

            }
        });

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!binding.editCodclient.getText().equals("") && company!=null) {

                    company = spinner.getSelectedItem().toString();

                     final String[] iban = new String[1];
                    final String[] id = new String[1];
                    boolean[] success = {false};
                    database.collection("Companies").document(company).collection("Facturi")
                            .whereEqualTo("Client_cod", binding.editCodclient.getText().toString()).whereEqualTo("Number", binding.editNumar.getText().toString())
                            .whereEqualTo("Series", binding.editSeria.getText().toString()).whereEqualTo("Value", Double.valueOf(binding.editSuma.getText().toString()))
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            iban[0] = document.getString("IBAN");
                                            id[0] = document.getId();
                                            success[0] = true;
                                        }
                                    }
                                }
                            });
                    if (success[0]) {
                        database.collection("Accounts").document(decrypt(data[4], mkey))
                                .update("Balance", Double.parseDouble(decrypt(data[5], mkey)) - Double.parseDouble(binding.editSuma.getText().toString()))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        database.collection("Companies").document(company).collection("Facturi").document(id[0])
                                                .update("Paid", "true").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Date currentTime = Calendar.getInstance().getTime();
                                                        Map<String, Object> Payment_bill = new HashMap<>();
                                                        Payment_bill.put("Value", Double.parseDouble(binding.editSuma.getText().toString()));
                                                        Payment_bill.put("IBAN", iban[0]);
                                                        Payment_bill.put("Company_name", company);
                                                        Payment_bill.put("Date", currentTime.toString());
                                                        database.collection("Card-Payments").document(decrypt(data[4], mkey)).collection("Payments")
                                                                .add(Payment_bill).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                    @Override
                                                                    public void onSuccess(DocumentReference documentReference) {
                                                                        Log.d("Mesaj: ", "DocumentSnapshot written with ID: " + documentReference.getId());
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Log.w("mesaj ", "Error adding document", e);
                                                                    }
                                                                });
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w("Mesaj", "Error updating document", e);
                                                    }

                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("Mesaj", "Error updating document", e);
                                    }

                                });

                    } else {
                        binding.eroare.setText("Nu ai selectat firma!");
                    }
                }
            }
        });





        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
