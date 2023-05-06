package com.phone.mobilebank.ui.home;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Communication com;

    String T1 ,T2;

    public static final String TAG = "YOUR-TAG-NAME";

    FirebaseFirestore database = FirebaseFirestore.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        binding.IMBTNCardTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.nav_card_transfer);
                database.collection("Accounts")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        T1 = document.getId().toString();
                                        T2 = document.getData().toString();
                                    }
                                } else {
                                    Log.w(TAG, "Error getting documents.", task.getException());
                                }
                            }
                        });
            }
        });

        binding.IMBTNBillPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.nav_bill_payment);

                }
        });

        binding.IMBTNRetrieveCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.nav_cash_withdraw);
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

    private void ShowDialog() {

        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_dialog_menu);

        LinearLayout card_details = dialog.findViewById(R.id.card_detail);
        LinearLayout get_iban = dialog.findViewById(R.id.get_IBAN);
        LinearLayout block_card = dialog.findViewById(R.id.block_card);
        LinearLayout transaction_history = dialog.findViewById(R.id.payment_history);

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

                TextView t1 = p_card_number.findViewById(R.id.personal_card_number);
                t1.setText(T1);
                TextView t2 = p_name.findViewById(R.id.personal_card_name);
                t2.setText(T2);
                TextView t3 = p_expiry_Day.findViewById(R.id.personal_card_expiry_date);
                t3.setText("08 / 27");
                TextView t4 = p_cw.findViewById(R.id.personal_card_cw);
                t4.setText("537");

                dialog2.show();;
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