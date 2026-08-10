// Repository.java
package com.example.konditer.repositories;

import android.content.Context;
import com.example.konditer.db.JsonStorageHelper;
import com.example.konditer.models.OrderInfo;

import java.util.List;
import java.util.stream.Collectors;

public class Repository {

    private Context context;

    public Repository(Context context) {
        this.context = context;
    }

    // Загрузка всех заказов из JSON-файла
    public List<OrderInfo> getAllOrders() {
        return JsonStorageHelper.loadOrders(context);
    }

    // Сохранение всех заказов в JSON-файл
    public boolean saveOrders(List<OrderInfo> orders) {
        return JsonStorageHelper.saveOrders(context, orders);
    }

    // Получение конкретных заказов по имени клиента
    public List<OrderInfo> getOrdersByCustomer(String customerName) {
        List<OrderInfo> allOrders = getAllOrders();
        return allOrders.stream()
                .filter(order -> order.getClientFullName().contains(customerName))
                .collect(Collectors.toList());
    }
}