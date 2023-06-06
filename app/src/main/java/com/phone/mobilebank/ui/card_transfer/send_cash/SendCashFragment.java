package com.phone.mobilebank.ui.card_transfer.send_cash;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.phone.mobilebank.databinding.FragmentSendCashBinding;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class SendCashFragment extends Fragment {

    private FragmentSendCashBinding binding;
    private FirebaseFirestore database;

    private String[] data = new String[6];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSendCashBinding.inflate(inflater,container,false);
        View root = binding.getRoot();

        database = FirebaseFirestore.getInstance();

        binding.etxtDetails.setText(null);
        binding.etxtIBAN.setText(null);
        binding.etxtRecipient.setText(null);
        binding.etxtSumToSend.setText(null);

        binding.btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String d1,d2,d3,d4;
                d1 = binding.etxtSumToSend.getText().toString();
                d2 = binding.etxtRecipient.getText().toString();
                d3 = binding.etxtIBAN.getText().toString();
                d4 = binding.etxtDetails.getText().toString();

                try {
                    FileInputStream fis = getActivity().openFileInput("DAT.txt");
                    InputStreamReader isr = new InputStreamReader(fis);
                    BufferedReader br = new BufferedReader(isr);
                    String line;
                    int i = 0;
                    while ((line = br.readLine()) != null) {
                        data[i] = line;
                        i++;
                    }
                    br.close();
                    isr.close();
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Double d1i = Double.parseDouble(d1);
                final Double[] balance = new Double[1];
                final Double[] finalBalance = new Double[1];

                Map<String, Object> transaction = new HashMap<>();
                transaction.put("Recipient",d2);
                transaction.put("Amount",d1i);
                transaction.put("IBAN",d3);
                transaction.put("IBANSender",data[4]);
                transaction.put("Details",d4);
                database.collection("Card-Transfer").document(data[4]).collection("Sent")
                        .add(transaction).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {

                                Map<String, Object> Bill = new HashMap<>();
                                Bill.put("Sender",data[0]);
                                Bill.put("Amount",d1i);
                                Bill.put("IBAN",data[4]);
                                Bill.put("IBANReceiver",d3);
                                Bill.put("Details",d4);
                                database.collection("Card-Transfer").document(d3).collection("Received")
                                        .add(Bill).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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

                                DocumentReference docRef = database.collection("Accounts").document(data[4]);
                                docRef.get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists())
                                        {
                                            String val = document.getDouble("Balance").toString();
                                            balance[0] = Double.parseDouble(val);
                                            finalBalance[0] = balance[0] -d1i;

                                            database.collection("Accounts").document(data[4]).update("Balance", finalBalance[0])
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Log.d("Mesaj update", "DocumentSnapshot successfully updated!");

                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w("Mesaj update", "Error updating document", e);
                                                        }
                                                    });

                                    }else{
                                         Log.d("Mesaj : ","No such document");
                                    }
                                } else {
                                    Log.w("Mesaj", "Error getting documents.", task.getException());
                                    }
                                }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("mesaj ", "Error adding document", e);
                            }
                        });
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
