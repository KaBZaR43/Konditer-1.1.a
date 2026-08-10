package com.example.konditer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.example.konditer.R;
import com.example.konditer.models.OrderInfo;

import java.util.List;

public class ExportOrdersFragment extends Fragment {

    private List<OrderInfo> orders;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orders = getArguments().getParcelableArrayList("ORDERS_LIST");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_main, container, false);
        return rootView;
    }

}