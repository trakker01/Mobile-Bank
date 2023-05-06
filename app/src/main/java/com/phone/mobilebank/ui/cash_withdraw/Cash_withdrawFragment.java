package com.phone.mobilebank.ui.cash_withdraw;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.phone.mobilebank.R;
import com.phone.mobilebank.databinding.FragmentCashWithdrawBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Cash_withdrawFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Cash_withdrawFragment extends Fragment {

    private FragmentCashWithdrawBinding binding;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Cash_withdrawFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Cash_withdrawFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Cash_withdrawFragment newInstance(String param1, String param2) {
        Cash_withdrawFragment fragment = new Cash_withdrawFragment();
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
        // Inflate the layout for this fragment
        Cash_withdrawViewModel cashWithdrawViewModel = new ViewModelProvider(this).get(Cash_withdrawViewModel.class);
        binding = FragmentCashWithdrawBinding.inflate(inflater,container,false);
        View root = binding.getRoot();
        final TextView textView = binding.textCashwithdraw;
        cashWithdrawViewModel.getText().observe(getViewLifecycleOwner(),textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}