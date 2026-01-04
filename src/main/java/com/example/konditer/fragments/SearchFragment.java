// SearchFragment.java
package com.example.konditer.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.konditer.R;
import com.example.konditer.adapters.OrderAdapter;
import com.example.konditer.models.OrderInfo;
import com.example.konditer.presenters.SearchPresenter;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private SearchPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment, container, false);

        searchView = view.findViewById(R.id.searchView);
        recyclerView = view.findViewById(R.id.recyclerView);

        // Инициализация адаптера
        adapter = new OrderAdapter(getContext(), null);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        // Presenter берёт на себя всю логику поиска
        presenter = new SearchPresenter(this, requireContext()); // Передаём контекст

        // Обработка события поиска
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                presenter.performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                presenter.performSearch(newText);
                return true;
            }
        });

        return view;
    }

    // Метод для обновления списка заказов в адаптере
    public void updateOrders(List<OrderInfo> orders) {
        if (adapter != null) {
            if (orders == null) {
                orders = new ArrayList<>(); // Создаем пустой список, если пришел null
                Log.d("SearchFragment", "Received null orders list, using empty list instead.");
            }
            adapter.updateOrders(orders);
        } else {
            Log.w("SearchFragment", "Adapter is null when trying to update orders!");
        }
    }
}