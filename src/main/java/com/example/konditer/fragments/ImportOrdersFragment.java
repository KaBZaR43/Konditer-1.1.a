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
import java.util.List;

public class ImportOrdersFragment extends Fragment {

    private OnImportCallback callback;

    public interface OnImportCallback {
        void onImportSuccess(List<OrderInfo> importedOrders);
    }

    public static ImportOrdersFragment newInstance(OnImportCallback callback) {
        ImportOrdersFragment fragment = new ImportOrdersFragment();
        fragment.callback = callback;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_main, container, false);

        Button buttonImport = rootView.findViewById(R.id.btn_import_json);
        buttonImport.setOnClickListener(v -> performImport());

        return rootView;
    }

    private void performImport() {
        StorageService storageService = new StorageService();
        String jsonData = storageService.loadFromFile(requireContext());

        if (!jsonData.isEmpty()) {
            JsonService jsonService = new JsonService();
            List<OrderInfo> importedOrders = jsonService.deserializeOrders(requireContext(), jsonData);

            if (callback != null) {
                callback.onImportSuccess(importedOrders);
            }
            Toast.makeText(requireContext(), "Импорт успешно завершён.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(requireContext(), "Ничего не найдено для импорта.", Toast.LENGTH_LONG).show();
        }
    }
}