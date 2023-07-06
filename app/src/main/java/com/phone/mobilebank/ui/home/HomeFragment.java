package com.phone.mobilebank.ui.home;

import static android.content.Context.MODE_PRIVATE;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.phone.mobilebank.ui.data_base.Communication;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.phone.mobilebank.R;
import com.phone.mobilebank.databinding.FragmentHomeBinding;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Communication com;
    private String[] data = new String[9];
    public String[] data1= {"A"};
    public ArrayList<String> store= new ArrayList<>();
    int i=1;
    FirebaseFirestore database = FirebaseFirestore.getInstance();

    public static final String TAG = "YOUR-TAG-NAME";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        final String[] name1 = new String[1];

        binding.IMBTNCardTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.nav_card_transfer);
            }
        });

        binding.IMBTNBillPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.nav_bill_payment);

                }
        });

        binding.IMBTNMoreOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowDialog();
            }
        });

        return root;
    }

    public void Payment(){

    }

    public ArrayList<String> getStores(){
        return store;
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

    private String encrypt(String text, String key){
        try{
            createSecreteKey(key);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(text.getBytes("UTF-8")));
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
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

    private void ShowDialog() {

        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_dialog_menu);



        LinearLayout card_details = dialog.findViewById(R.id.card_detail);
        LinearLayout get_iban = dialog.findViewById(R.id.get_IBAN);
        LinearLayout block_card = dialog.findViewById(R.id.block_card);
        LinearLayout transaction_history = dialog.findViewById(R.id.payment_history);

        block_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

               if(data[7].equals("false")){
                    Snackbar mySnackbar = Snackbar.make(view,"Cardul este activ.", BaseTransientBottomBar.LENGTH_SHORT);
                    mySnackbar.show();
                    database.collection("Accounts").document(data[4]).update("Active",true).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                        }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error updating document", e);
                                }

                            });
                   try {
                       FileOutputStream fos = getActivity().openFileOutput("DAT.txt", MODE_PRIVATE);
                       String t1 = encrypt(data[0],mkey);
                       fos.write(t1.getBytes(StandardCharsets.UTF_8));
                       fos.write('\n');
                       String t2 = encrypt(data[1],mkey);
                       fos.write(t2.getBytes(StandardCharsets.UTF_8));
                       fos.write('\n');
                       String t3 = encrypt(data[2],mkey);
                       fos.write(t3.getBytes(StandardCharsets.UTF_8));
                       fos.write('\n');
                       String t4 = encrypt(data[3],mkey);
                       fos.write(t4.getBytes(StandardCharsets.UTF_8));
                       fos.write('\n');
                       String t5 = encrypt(data[4],mkey);
                       fos.write(t5.getBytes(StandardCharsets.UTF_8));
                       fos.write('\n');
                       String t6 = encrypt(data[5],mkey);
                       fos.write(t6.getBytes(StandardCharsets.UTF_8));
                       fos.write('\n');
                       String t7 = encrypt(data[6],mkey);
                       fos.write(t7.getBytes(StandardCharsets.UTF_8));
                       fos.write('\n');
                       String t8 = encrypt("true",mkey);
                       fos.write(t8.getBytes(StandardCharsets.UTF_8));
                       fos.write('\n');
                   } catch (IOException ez) {
                       ez.printStackTrace();
                   }
               }
               else{
                   Snackbar mySnackbar = Snackbar.make(view,"Cardul este blocat.", BaseTransientBottomBar.LENGTH_SHORT);
                   mySnackbar.show();
                   database.collection("Accounts").document(data[4]).update("Active",false).addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                                   Log.d(TAG, "DocumentSnapshot successfully updated!");
                               }
                           })
                           .addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception e) {
                                   Log.w(TAG, "Error updating document", e);
                               }

                           });

                   try {
                       FileOutputStream fos = getActivity().openFileOutput("DAT.txt", MODE_PRIVATE);
                       String t1 = encrypt(data[0],mkey);
                       fos.write(t1.getBytes(StandardCharsets.UTF_8));
                       fos.write('\n');
                       String t2 = encrypt(data[1],mkey);
                       fos.write(t2.getBytes(StandardCharsets.UTF_8));
                       fos.write('\n');
                       String t3 = encrypt(data[2],mkey);
                       fos.write(t3.getBytes(StandardCharsets.UTF_8));
                       fos.write('\n');
                       String t4 = encrypt(data[3],mkey);
                       fos.write(t4.getBytes(StandardCharsets.UTF_8));
                       fos.write('\n');
                       String t5 = encrypt(data[4],mkey);
                       fos.write(t5.getBytes(StandardCharsets.UTF_8));
                       fos.write('\n');
                       String t6 = encrypt(data[5],mkey);
                       fos.write(t6.getBytes(StandardCharsets.UTF_8));
                       fos.write('\n');
                       String t7 = encrypt(data[6],mkey);
                       fos.write(t7.getBytes(StandardCharsets.UTF_8));
                       fos.write('\n');
                       String t8 = encrypt("false",mkey);
                       fos.write(t8.getBytes(StandardCharsets.UTF_8));
                       fos.write('\n');
                   } catch (IOException ez) {
                       ez.printStackTrace();
                   }
               }
            }
        });

        get_iban.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    FileInputStream fis = getActivity().openFileInput("DAT.txt");
                    InputStreamReader isr = new InputStreamReader(fis);
                    BufferedReader br = new BufferedReader(isr);
                    StringBuilder sb = new StringBuilder();
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
                    Snackbar mySnackbar = Snackbar.make(view,decrypt(data[6],mkey), BaseTransientBottomBar.LENGTH_LONG);
                    mySnackbar.show();
            }
        });

        transaction_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.nav_payment_history);
                dialog.cancel();
            }
        });

        card_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog2 = new Dialog(getContext());
                dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog2.setContentView(R.layout.card_details_dialog_menu);
                LinearLayout p_card_number = dialog2.findViewById(R.id.card_number);
                LinearLayout p_name = dialog2.findViewById(R.id.card_name);
                LinearLayout p_expiry_Day = dialog2.findViewById(R.id.card_expiry_date);
                LinearLayout p_cw = dialog2.findViewById(R.id.card_cw);

                try {
                    FileInputStream fis = getActivity().openFileInput("DAT.txt");
                    InputStreamReader isr = new InputStreamReader(fis);
                    BufferedReader br = new BufferedReader(isr);
                    StringBuilder sb = new StringBuilder();
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

                TextView t1 = p_card_number.findViewById(R.id.personal_card_number);
                String ent1 = decrypt(data[1],mkey);
                t1.setText(ent1);
                TextView t2 = p_name.findViewById(R.id.personal_card_name);
                String ent2 = decrypt(data[0],mkey);
                t2.setText(ent2);
                TextView t3 = p_expiry_Day.findViewById(R.id.personal_card_expiry_date);
                String ent3 = decrypt(data[2],mkey);
                t3.setText(ent3);
                TextView t4 = p_cw.findViewById(R.id.personal_card_cw);
                String ent4 = decrypt(data[3],mkey);
                t4.setText(ent4);

                dialog2.show();
                dialog2.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog2.getWindow().getAttributes().windowAnimations =R.style.DialogAnimation;
                dialog2.getWindow().setGravity(Gravity.BOTTOM);

                dialog.cancel();

            }
        });

        dialog.show();;
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations =R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void ShowDialog2(){
        final Dialog dialog2 = new Dialog(getContext());
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}