package com.phone.mobilebank.ui.payment_method;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.phone.mobilebank.R;
import com.phone.mobilebank.databinding.FragmentPaymentMethodBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Payment_methodFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Payment_methodFragment extends Fragment {

    private FragmentPaymentMethodBinding binding;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Payment_methodFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Payment_methodFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Payment_methodFragment newInstance(String param1, String param2) {
        Payment_methodFragment fragment = new Payment_methodFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        PaymentMethodViewModel paymentMethodViewModel = new ViewModelProvider(this).get(PaymentMethodViewModel.class);
        binding = FragmentPaymentMethodBinding.inflate(inflater,container,false);
        View root = binding.getRoot();
        final TextView textView = binding.textPayment;
        paymentMethodViewModel.getText().observe(getViewLifecycleOwner(),textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}