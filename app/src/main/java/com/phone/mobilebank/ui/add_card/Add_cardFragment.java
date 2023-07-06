package com.phone.mobilebank.ui.add_card;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.phone.mobilebank.databinding.FragmentAddCardBinding;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Add_cardFragment extends Fragment {

    public static final String TAG = "YOUR-TAG-NAME";
    private static final String File_Name = "DAT.txt";

    //AESEncryptionDecryption aesEncryptionDecryption = new AESEncryptionDecryption();
    FirebaseFirestore database;
    private FragmentAddCardBinding binding;

    public Add_cardFragment() {
        // Required empty public constructor
    }

    public static Add_cardFragment newInstance() {
        Add_cardFragment fragment = new Add_cardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private static SecretKeySpec secretKey;
    private static byte[] key;
    private final String mKey = "UMfDlSAKArrhmvCJEBZvcE3jBZkMavmw";
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

    private String success = "No";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Add_cardViewModel add_cardViewModel = new ViewModelProvider(this).get(Add_cardViewModel.class);
        binding = FragmentAddCardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        final TextView textView = binding.textAddCard;

        binding.textName.setText("");
        binding.textNumberCard.setText("");
        binding.textExpirationCard.setText("");


        binding.submitData.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {

                //binding.erorareCard.setText("");
                if (!binding.textName.getText().toString().equals("")) {
                    TextView ET;
                    if (!binding.textNumberCard.getText().toString().equals("")) {
                    if (!binding.textExpirationCard.getText().toString().equals("")) {


                            String Name = binding.textName.getText().toString();
                            String Expiration = binding.textExpirationCard.getText().toString();
                            String Number = binding.textNumberCard.getText().toString();



                            database = FirebaseFirestore.getInstance();
                            database.collection("Accounts").whereEqualTo("Name", Name)
                                    .whereEqualTo("ExpirationDate", Expiration).whereEqualTo("CardNumber",Number)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {

                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                                try {
                                                                    success = "yes";
                                                                    FileOutputStream fos = getActivity().openFileOutput(File_Name, MODE_PRIVATE);
                                                                    String t1 = encrypt(document.getString("Name"),mKey);
                                                                    fos.write(t1.getBytes(StandardCharsets.UTF_8));
                                                                    fos.write('\n');
                                                                    String t2 = encrypt(document.getString("CardNumber"),mKey);
                                                                    fos.write(t2.getBytes(StandardCharsets.UTF_8));
                                                                    fos.write('\n');
                                                                    String t3 = encrypt(document.getString("ExpirationDate"),mKey);
                                                                    fos.write(t3.getBytes(StandardCharsets.UTF_8));
                                                                    fos.write('\n');
                                                                    String t4 = encrypt(document.getString("CW"),mKey);
                                                                    fos.write(t4.getBytes(StandardCharsets.UTF_8));
                                                                    fos.write('\n');
                                                                    String t5 = encrypt(document.getId(),mKey);
                                                                    fos.write(t5.getBytes(StandardCharsets.UTF_8));
                                                                    fos.write('\n');
                                                                    String balance = document.getDouble("Balance").toString();
                                                                    String t6 = encrypt(balance,mKey);
                                                                    fos.write(t6.getBytes(StandardCharsets.UTF_8));
                                                                    fos.write('\n');
                                                                    String t7 = encrypt(document.getString("IBAN"),mKey);
                                                                    fos.write(t7.getBytes(StandardCharsets.UTF_8));
                                                                    fos.write('\n');
                                                                    String bool = document.getBoolean("Active").toString();
                                                                    String t8 = encrypt(bool,mKey);
                                                                    fos.write(t8.getBytes(StandardCharsets.UTF_8));
                                                                    fos.write('\n');
                                                                } catch (IOException ez) {
                                                                    ez.printStackTrace();

                                                                }
                                                }
                                            } else {
                                                binding.erorareCard.setText("Datele introduse nu sunt asociate cu un cont existent!");
                                            }
                                        }
                                    });
                            if(success.equals("yes")){
                                binding.erorareCard.setText("Datele introduse nu sunt asociate cu un cont existent!");
                            }else{
                                Snackbar mySnackbar = Snackbar.make(view,"Cardul a fost adaugat!", BaseTransientBottomBar.LENGTH_LONG);
                                mySnackbar.show();
                            }
                        } else {
                        binding.erorareCard.setText("Nu ai introdus data de expirare a  cardului!");
                        }
                    } else {
                        binding.erorareCard.setText("Nu ai introdus numarul cardului!");
                    }
                } else {
                    binding.erorareCard.setText("Nu ai introdus numele detinatorul cardului!");
                }
            }
        });

        // add_cardViewModel.ChangeText(data);

        return root;
    }

    @Override
    public void onDestroyView() {
        binding.textName.setText("");
        binding.textNumberCard.setText("");
        binding.textExpirationCard.setText("");
        super.onDestroyView();
        binding = null;
    }
}