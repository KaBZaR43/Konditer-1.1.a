package com.example.konditer.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.konditer.R;
import com.example.konditer.db.JsonStorageHelper;
import com.example.konditer.models.OrderInfo;
import com.example.konditer.models.OrderStatus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


public class StatisticsFragment extends Fragment {

    private Spinner spinnerMonth;
    private Spinner spinnerYear;
    private TextView statisticsTotalOrders;
    private TextView statisticsTotalValue;
    private TextView statisticsCompletedOrders;
    private TextView statisticsCompletedValue;
    private TextView statisticsCanceledOrders;
    private TextView statisticsCanceledValue;
    private TextView statisticsRevenue;
    private TextView statisticsRevenueValue;

    private List<OrderInfo> orders;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_statistics, container, false);

        // Инициализируем элементы интерфейса
        spinnerMonth = rootView.findViewById(R.id.spinner_month);
        spinnerYear = rootView.findViewById(R.id.spinner_year);
        statisticsTotalOrders = rootView.findViewById(R.id.statistics_total_orders);
        statisticsTotalValue = rootView.findViewById(R.id.statistics_total_value);
        statisticsCompletedOrders = rootView.findViewById(R.id.statistics_completed_orders);
        statisticsCompletedValue = rootView.findViewById(R.id.statistics_completed_value);
        statisticsCanceledOrders = rootView.findViewById(R.id.statistics_canceled_orders);
        statisticsCanceledValue = rootView.findViewById(R.id.statistics_canceled_value);
        statisticsRevenue = rootView.findViewById(R.id.statistics_revenue);
        statisticsRevenueValue = rootView.findViewById(R.id.statistics_revenue_value);

        // Загружаем все заказы
        orders = JsonStorageHelper.loadOrders(getContext());

        // Конфигурация спиннеров
        configureSpinners();

        // Первоначальная фильтрация (для текущего месяца)
        applyFilters(spinnerMonth.getSelectedItemPosition(), (int) spinnerYear.getSelectedItem());

        return rootView;
    }

    private void configureSpinners() {
        // Настройки для выбора месяца
        String[] months = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
                "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);

        // Настройки для выбора года
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int minYear = currentYear - 5; // Показывать последние пять лет назад
        int maxYear = currentYear + 5; // Плюс пять лет вперёд
        List<Integer> years = new ArrayList<>();
        for (int y = minYear; y <= maxYear; y++) {
            years.add(y);
        }
        ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);

        // Обработчики выбора месяца и года
        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters(position, (int) spinnerYear.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters(spinnerMonth.getSelectedItemPosition(), (int) spinnerYear.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void applyFilters(int monthIndex, int year) {
        // Фильтруем заказы по выбранному месяцу и году
        List<OrderInfo> filteredOrders = filterOrdersByMonthAndYear(orders, monthIndex, year);

        // Обновляем статистику
        updateStatistics(filteredOrders);
    }

    private List<OrderInfo> filterOrdersByMonthAndYear(List<OrderInfo> orders, int monthIndex, int year) {
        return orders.stream()
                .filter(order -> isInSelectedMonthAndYear(order.getDeliveryDate(), monthIndex, year))
                .collect(Collectors.toList());
    }

    private boolean isInSelectedMonthAndYear(String deliveryDate, int monthIndex, int year) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            Date date = sdf.parse(deliveryDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal.get(Calendar.YEAR) == year && cal.get(Calendar.MONTH) == monthIndex;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void updateStatistics(List<OrderInfo> filteredOrders) {
        if (!filteredOrders.isEmpty()) {
            int totalOrders = filteredOrders.size();
            int completedOrders = countCompletedOrders(filteredOrders);
            int canceledOrders = countCanceledOrders(filteredOrders);
            double totalRevenue = calculateTotalRevenue(filteredOrders);

            statisticsTotalValue.setText("Всего заказов: " + totalOrders);
            statisticsCompletedValue.setText("Выполнено заказов: " + completedOrders);
            statisticsCanceledValue.setText("Отменено заказов: " + canceledOrders);
            statisticsRevenueValue.setText("Общая выручка (за выполненные заказы): " + totalRevenue + " руб.");
        } else {
            statisticsTotalValue.setText("Нет заказов за выбранный период");
            statisticsCompletedValue.setText("");
            statisticsCanceledValue.setText("");
            statisticsRevenueValue.setText("");
        }
    }

    private int countCompletedOrders(List<OrderInfo> orders) {
        return (int) orders.stream()
                .filter(order -> order.getStatus() == OrderStatus.Выполнен)
                .count();
    }

    private int countCanceledOrders(List<OrderInfo> orders) {
        return (int) orders.stream()
                .filter(order -> order.getStatus() == OrderStatus.Отменен)
                .count();
    }

    private double calculateTotalRevenue(List<OrderInfo> orders) {
        return orders.stream()
                .filter(order -> order.getStatus() == OrderStatus.Выполнен) // Только выполненные заказы
                .mapToDouble(OrderInfo::getPrice)
                .sum();
    }
}