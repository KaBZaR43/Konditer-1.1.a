package com.example.konditer.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.konditer.R;
import com.example.konditer.adapters.OrderAdapter;
import com.example.konditer.db.JsonStorageHelper;
import com.example.konditer.models.OrderInfo;
import com.example.konditer.models.OrderStatus;
import com.example.konditer.utils.Utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OrdersListFragment extends Fragment {

    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private List<OrderInfo> mOrders;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_order_list, container, false);

        recyclerView = rootView.findViewById(R.id.orders_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Инициализация адаптера
        initAdapter();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Обновление данных при возвращении на этот экран
        reloadOrders();
    }

    /**
     * Инициализирует адаптер и устанавливает его в RecyclerView
     */
    private void initAdapter() {
        List<OrderInfo> emptyList = new ArrayList<>();
        adapter = new OrderAdapter(getContext(), emptyList);
        recyclerView.setAdapter(adapter);

        // Устанавливаем слушатель кликов по элементам списка
        adapter.setOnItemClickListener((position) -> {
            OrderInfo selectedOrder = adapter.getCurrentList().get(position);
            // Открываем детализацию выбранного заказа в новом фрагменте
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, OrderDetailsFragment.newInstance(selectedOrder))
                    .addToBackStack(null)
                    .commit();
        });
    }

    /**
     * Загружает новые данные из хранилища и обновляет список
     */
    // Фильтрация активных заказов
    private void reloadOrders() {
        List<OrderInfo> orders = JsonStorageHelper.loadOrders(getContext());
        List<OrderInfo> activeOrders = new ArrayList<>();
        for (OrderInfo order : orders) {
            if (order.getStatus() == OrderStatus.Активный) {
                activeOrders.add(order);
            }
        }
        mOrders = activeOrders; // Сохраняем активный список заказов
        adapter.updateOrders(activeOrders);
    }

    // Сортируем по дате сдачи заказа
    public void sortByDate() {
        reloadOrders();
        if (mOrders != null && !mOrders.isEmpty()) {
            Log.d("Sorting", "Sorting by date with " + mOrders.size() + " orders");
            // Преобразуем строки дат в объекты LocalDate и сортируем
            Collections.sort(mOrders, Comparator.nullsLast(Comparator.comparing(o -> {
                try {
                    return Utils.parseDate(o.getDeliveryDate());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            })));
            adapter.updateOrders(mOrders);
            Log.d("Sorting", "After sorting by date: " + mOrders.size() + " orders");
        } else {
            Log.d("Sorting", "No orders available for sorting");
            Toast.makeText(requireContext(), "Нет заказов для сортировки", Toast.LENGTH_SHORT).show();
        }
    }

    // Сортируем по стоимости заказа
    public void sortByCost() {
        reloadOrders();
        if (mOrders != null && !mOrders.isEmpty()) {
            Log.d("Sorting", "Sorting by cost with " + mOrders.size() + " orders");
            Collections.sort(mOrders, Comparator.comparingDouble(OrderInfo::getPrice));
            adapter.updateOrders(mOrders);
        } else {
            Log.d("Sorting", "No orders available for sorting");
            Toast.makeText(requireContext(), "Нет заказов для сортировки", Toast.LENGTH_SHORT).show();
        }
    }

    // Сортируем по платформе связи
    public void sortBySocialNetwork() {
        reloadOrders();
        if (mOrders != null && !mOrders.isEmpty()) {
            Log.d("Sorting", "Sorting by social network with " + mOrders.size() + " orders");
            Collections.sort(mOrders, Comparator.comparing(OrderInfo::getSocialNetwork));
            adapter.updateOrders(mOrders);
        } else {
            Log.d("Sorting", "No orders available for sorting");
            Toast.makeText(requireContext(), "Нет заказов для сортировки", Toast.LENGTH_SHORT).show();
        }
    }
}