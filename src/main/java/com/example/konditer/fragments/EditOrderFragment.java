package com.example.konditer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.konditer.R;
import com.example.konditer.db.JsonStorageHelper;
import com.example.konditer.models.OrderInfo;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class EditOrderFragment extends Fragment {

    private OrderInfo editedOrder;

    public static EditOrderFragment newInstance(OrderInfo order) {
        EditOrderFragment fragment = new EditOrderFragment();
        Bundle args = new Bundle();
        args.putParcelable("editedOrder", order);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_order, container, false);

        editedOrder = getArguments().getParcelable("editedOrder");

        // Устанавливаем текущие значения полей
        ((TextInputEditText)rootView.findViewById(R.id.editClientFullName)).setText(editedOrder.clientFullName);
        ((TextInputEditText)rootView.findViewById(R.id.editOrderName)).setText(editedOrder.orderName);
        ((TextInputEditText)rootView.findViewById(R.id.editDeliveryDate)).setText(editedOrder.deliveryDate);
        ((TextInputEditText)rootView.findViewById(R.id.editPrice)).setText(String.valueOf(editedOrder.price));
        ((TextInputEditText)rootView.findViewById(R.id.editDiscountPercentage)).setText(String.valueOf(editedOrder.discountPercentage));
        ((TextInputEditText)rootView.findViewById(R.id.editBiscuits)).setText(convertListToString(editedOrder.biscuits)); // Преобразование списка в строку
        ((TextInputEditText)rootView.findViewById(R.id.editFillings)).setText(convertListToString(editedOrder.fillings));
        ((TextInputEditText)rootView.findViewById(R.id.editCreams)).setText(convertListToString(editedOrder.creams));
        ((TextInputEditText)rootView.findViewById(R.id.editSocialNetwork)).setText(editedOrder.socialNetwork);
        ((TextInputEditText)rootView.findViewById(R.id.editNotes)).setText(editedOrder.notes);
        ((TextInputEditText)rootView.findViewById(R.id.editPhoneNumber)).setText(editedOrder.phoneNumber);
        ((TextInputEditText)rootView.findViewById(R.id.editDecorationDescription)).setText(editedOrder.decorationDescription);

        Button saveChangesBtn = rootView.findViewById(R.id.btn_save_order);
        saveChangesBtn.setOnClickListener(v -> {
            updateOrderData();
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return rootView;
    }

    // Вспомогательная функция для преобразования списка строк в одну строку
    private String convertListToString(List<String> list) {
        if (list != null && !list.isEmpty()) {
            return String.join(", ", list);
        }
        return "";
    }

    private void updateOrderData() {
        // Читаем новые значения из форм
        editedOrder.clientFullName = ((TextInputEditText)getView().findViewById(R.id.editClientFullName)).getText().toString();
        editedOrder.orderName = ((TextInputEditText)getView().findViewById(R.id.editOrderName)).getText().toString();
        editedOrder.deliveryDate = ((TextInputEditText)getView().findViewById(R.id.editDeliveryDate)).getText().toString();
        try {
            editedOrder.price = Double.parseDouble(((TextInputEditText)getView().findViewById(R.id.editPrice)).getText().toString());
        } catch(NumberFormatException e){
            // Логика обработки ошибок
        }
        try {
            editedOrder.discountPercentage = Integer.parseInt(((TextInputEditText)getView().findViewById(R.id.editDiscountPercentage)).getText().toString());
        } catch(NumberFormatException e){
            // Логика обработки ошибок
        }
        editedOrder.biscuits = convertStringToList(((TextInputEditText)getView().findViewById(R.id.editBiscuits)).getText().toString());
        editedOrder.fillings = convertStringToList(((TextInputEditText)getView().findViewById(R.id.editFillings)).getText().toString());
        editedOrder.creams = convertStringToList(((TextInputEditText)getView().findViewById(R.id.editCreams)).getText().toString());
        editedOrder.socialNetwork = ((TextInputEditText)getView().findViewById(R.id.editSocialNetwork)).getText().toString();
        editedOrder.notes = ((TextInputEditText)getView().findViewById(R.id.editNotes)).getText().toString();
        editedOrder.phoneNumber = ((TextInputEditText)getView().findViewById(R.id.editPhoneNumber)).getText().toString();
        editedOrder.decorationDescription = ((TextInputEditText)getView().findViewById(R.id.editDecorationDescription)).getText().toString();

        // Сохраняем изменения в файле
        List<OrderInfo> allOrders = JsonStorageHelper.loadOrders(requireContext());

        // Нахождение заказа по уникальному идентификатору
        for (OrderInfo existingOrder : allOrders) {
            if (existingOrder.getId() == editedOrder.getId()) {
                existingOrder.updateFrom(editedOrder); // Обновляем данные
                break;
            }
        }

        // Сохраняем обновленный список
        JsonStorageHelper.saveOrders(requireContext(), allOrders);
    }

    // Вспомогательная функция для конвертации строки в список
    private List<String> convertStringToList(String input) {
        if(input != null && !input.trim().isEmpty()){
            return List.of(input.split(","));
        }
        return List.of();
    }
}