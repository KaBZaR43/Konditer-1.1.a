package com.example.konditer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.konditer.R;
import com.example.konditer.adapters.HistoryAdapter;
import com.example.konditer.db.JsonStorageHelper;
import com.example.konditer.models.OrderInfo;
import com.example.konditer.models.OrderStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private HistoryAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerViewHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Загружаем все заказы и фильтруем только выполненные и отменённые
        List<OrderInfo> orders = JsonStorageHelper.loadOrders(getContext());
        List<OrderInfo> filteredOrders = filterCompletedAndCanceledOrders(orders);

        // Создаем адаптер и передаем контекст
        adapter = new HistoryAdapter(filteredOrders, getContext());
        adapter.setOnItemClickListener((position) -> {
            OrderInfo selectedOrder = filteredOrders.get(position);
            openOrderDetails(selectedOrder); // Открываем подробный фрагмент
        });
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    private void openOrderDetails(OrderInfo order) {
        // Открытием фрагмента с подробностями заказа
        OrderDetailsFragment detailsFragment = OrderDetailsFragment.newInstance(order);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, detailsFragment)
                .addToBackStack(null)
                .commit();
    }

    // Фильтруем заказы, оставляя только завершенные и отмененные
    private List<OrderInfo> filterCompletedAndCanceledOrders(List<OrderInfo> orders) {
        return orders.stream()
                .filter(order -> order.getStatus() == OrderStatus.Выполнен || order.getStatus() == OrderStatus.Отменен)
                .collect(Collectors.toList());
    }
}