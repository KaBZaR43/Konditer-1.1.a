// SearchPresenter.java
package com.example.konditer.presenters;

import android.content.Context;

import com.example.konditer.models.OrderInfo;
import com.example.konditer.repositories.Repository;
import com.example.konditer.fragments.SearchFragment;

import java.util.List;

public class SearchPresenter {

    private Repository repository;
    private SearchFragment view;

    public SearchPresenter(SearchFragment view, Context context) {
        this.repository = new Repository(context); // Используется контекст для работы с базой данных
        this.view = view;
    }

    // Метод фильтрации заказов по клиенту
    public void performSearch(String query) {
        List<OrderInfo> results = repository.getOrdersByCustomer(query);
        view.updateOrders(results);
    }
}