package com.example.konditer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.konditer.MainActivity;
import com.example.konditer.R;
import com.example.konditer.models.OrderInfo;
import com.example.konditer.Service.JsonService;
import com.example.konditer.Service.StorageService;

import java.util.ArrayList;
import java.util.List;

public class ExportOrdersFragment extends Fragment {

    private List<OrderInfo> orders;

    public static ExportOrdersFragment newInstance(List<OrderInfo> orders) {
        ExportOrdersFragment fragment = new ExportOrdersFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("ORDERS_LIST", new ArrayList<>(orders));
        fragment.setArguments(args);
        return fragment;
    }

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

        Button buttonExport = rootView.findViewById(R.id.btn_export_json);
        buttonExport.setOnClickListener(v -> performExport());

        return rootView;
    }

    private void performExport() {
        if (orders != null && !orders.isEmpty()) {
            JsonService jsonService = new JsonService();
            String serializedData = jsonService.serializeOrders(requireContext(), orders);

            StorageService storageService = new StorageService();
            storageService.saveToFile(requireContext(), serializedData);

            Toast.makeText(requireContext(), "Экспорт успешно завершён.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(requireContext(), "Нет заказов для экспорта.", Toast.LENGTH_LONG).show();
        }
    }
}