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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.phone.mobilebank.databinding.FragmentSendCashBinding;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class SendCashFragment extends Fragment {

    private FragmentSendCashBinding binding;
    private String[] data = new String[9];

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSendCashBinding.inflate(inflater,container,false);
        View root = binding.getRoot();



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

                Double d1i = Double.parseDouble(d1);
                final Double[] balance = new Double[1];
                final Double[] finalBalance = new Double[1];



                if(d1i!=0 && d1i >= 15 && !d2.equals("") && !d3.equals("") && !d4.equals("") && data[4]!=null) {


                    final String[] id1 = new String[1];
                    final Double[] bal = new Double[1];

                    FirebaseFirestore database = FirebaseFirestore.getInstance();
                    database.collection("Accounts")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            {
                                                if (Objects.requireNonNull(document.getString("IBAN")).equals(d3)) {
                                                    id1[0] = document.getId();
                                                    bal[0] = document.getDouble("Balance");
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            );

                    Map<String, Object> transaction = new HashMap<>();
                    transaction.put("Recipient",d2);
                    transaction.put("Amount",d1i);
                    transaction.put("IBAN",d3);
                    transaction.put("IBANSender",data[6]);
                    transaction.put("Details",d4);

                    database.collection("Card-Transfer").document(data[4]).collection("Sent")
                            .add(transaction).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {

                                    Map<String, Object> Bill = new HashMap<>();
                                    Bill.put("Sender", data[0]);
                                    Bill.put("Amount", d1i);
                                    Bill.put("IBAN", data[6]);
                                    Bill.put("IBANReceiver", d3);
                                    Bill.put("Details", d4);

                                    database.collection("Card-Transfer").document(id1[0]).collection("Received")
                                            .add(Bill).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    DocumentReference docRef = database.collection("Accounts").document(data[4]);
                                                    docRef.get()
                                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        DocumentSnapshot document = task.getResult();
                                                                        if (document.exists()) {
                                                                            String val = document.getDouble("Balance").toString();
                                                                            balance[0] = Double.parseDouble(val);
                                                                            finalBalance[0] = balance[0] - d1i;

                                                                            database.collection("Accounts").document(data[4]).update("Balance", finalBalance[0])
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void unused) {
                                                                                            database.collection("Accounts").document(id1[0])
                                                                                                    .update("Balance", bal[0]+d1i)
                                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void unused) {}
                                                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                                                @Override
                                                                                                public void onFailure(@NonNull Exception e) {}
                                                                                            });
                                                                                        }
                                                                                    })
                                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                                        @Override
                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                            Log.w("Mesaj update", "Error updating document", e);
                                                                                        }
                                                                                    });

                                                                        } else {
                                                                            Log.d("Mesaj : ", "No such document");
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
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("mesaj ", "Error adding document", e);
                                }
                            });
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
