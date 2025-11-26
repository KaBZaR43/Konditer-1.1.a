package com.example.konditer.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.konditer.R;
import com.example.konditer.db.JsonStorageHelper;
import com.example.konditer.models.OrderInfo;

import java.util.List;

public class OrderDetailsFragment extends Fragment {

    private OrderInfo selectedOrder;

    public static OrderDetailsFragment newInstance(OrderInfo order) {
        OrderDetailsFragment fragment = new OrderDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("selectedOrder", order);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_order_details, container, false);

        // Проверяем наличие аргументов и самого объекта заказа
        if (getArguments() != null) {
            selectedOrder = getArguments().getParcelable("selectedOrder");
        }

        if (selectedOrder == null) {
            // Показываем ошибку и прекращаем выполнение
            Toast.makeText(requireContext(), "Ошибка: заказ не передан", Toast.LENGTH_LONG).show();
            return rootView; // Вернуть представление без дальнейшей инициализации
        }

        // Получаем компоненты разметки
        TextView clientName = rootView.findViewById(R.id.textViewClientName);
        TextView orderName = rootView.findViewById(R.id.textViewOrderName);
        TextView deliveryDate = rootView.findViewById(R.id.textViewDeliveryDate);
        TextView weight = rootView.findViewById(R.id.textViewWeight); // Добавляем вес
        TextView totalAmount = rootView.findViewById(R.id.textViewTotalAmount);
        TextView discount = rootView.findViewById(R.id.textViewDiscount);
        TextView biscuits = rootView.findViewById(R.id.textViewBiscuits);
        TextView fillings = rootView.findViewById(R.id.textViewFillings);
        TextView creams = rootView.findViewById(R.id.textViewCreams);
        TextView socialNetwork = rootView.findViewById(R.id.textViewSocialNetwork);
        TextView status = rootView.findViewById(R.id.textViewStatus);
        TextView decorationDescription = rootView.findViewById(R.id.textViewDecorationDescription);
        TextView note = rootView.findViewById(R.id.textViewNote);

        // Заполняем текстовыми данными
        clientName.setText("Клиент: " + selectedOrder.clientFullName);
        orderName.setText("Название заказа: " + selectedOrder.orderName);
        deliveryDate.setText("Срок сдачи: " + selectedOrder.deliveryDate);
        weight.setText("Вес: " + selectedOrder.weight + " кг"); // Добавляем вес
        totalAmount.setText("Цена: " + selectedOrder.price + " руб.");
        discount.setText(selectedOrder.discountPercentage > 0 ?
                "Скидка: " + selectedOrder.discountPercentage + "%" :
                "Нет скидок");

        // Формируем маркированные списки для бисквитов, начинок и кремов
        biscuits.setText(makeBulletList(selectedOrder.biscuits));
        fillings.setText(makeBulletList(selectedOrder.fillings));
        creams.setText(makeBulletList(selectedOrder.creams));

        socialNetwork.setText("Платформа связи: " + selectedOrder.socialNetwork);
        status.setText("Статус заказа: " + selectedOrder.status.name());
        decorationDescription.setText("Описание декора: " + selectedOrder.decorationDescription);
        note.setText("Примечание: " + selectedOrder.notes);

        // Настройка кнопок
        Button completeButton = rootView.findViewById(R.id.buttonCompleteOrder);
        Button cancelButton = rootView.findViewById(R.id.buttonCancelOrder);
        Button buttonEditOrder = rootView.findViewById(R.id.buttonEditOrder);
        Button buttonDeleteOrder = rootView.findViewById(R.id.buttonDeleteOrder); // кнопка удаления

        completeButton.setOnClickListener(v -> {
            Log.d("OrderDetailsFragment", "Complete button pressed");
            handleCompleteOrder();
        });

        cancelButton.setOnClickListener(v -> {
            Log.d("OrderDetailsFragment", "Cancel button pressed");
            handleCancelOrder();
        });
        buttonEditOrder.setOnClickListener(v -> navigateToEditOrder());
        buttonDeleteOrder.setOnClickListener(v -> confirmDeletion()); // Подтверждаем удаление

        return rootView;
    }

    // Создание маркированного списка с использованием символов-маркеров (•)
    private CharSequence makeBulletList(List<String> items) {
        if (items == null || items.isEmpty()) {
            return "-"; // знак "-" обозначает отсутствие элементов
        }
        StringBuilder sb = new StringBuilder();
        for (String item : items) {
            sb.append("• ").append(item).append("\n");
        }
        return sb.toString();
    }


    private void handleCompleteOrder() {
        // Начинаем логировать операцию
        Log.d("OrderDetailsFragment", "Начало процедуры завершения заказа...");

        // Меняем статус заказа
        selectedOrder.markAsCompleted();
        Log.d("OrderDetailsFragment", "Статус заказа изменён на \"Выполнен\".");

        // Загружаем список текущих заказов
        List<OrderInfo> updatedOrders = JsonStorageHelper.loadOrders(requireContext());
        Log.d("OrderDetailsFragment", "Загружено " + updatedOrders.size() + " заказов из хранилища.");

        // Ищем заказ и обновляем его статус
        boolean found = false;
        for (OrderInfo order : updatedOrders) {
            if (order.getId() == selectedOrder.getId()) {
                order.markAsCompleted();
                found = true;
                Log.d("OrderDetailsFragment", "Заказ с ID " + order.getId() + " отмечен как выполненный.");
            }
        }

        if (!found) {
            Log.e("OrderDetailsFragment", "Ошибка: заказ с ID " + selectedOrder.getId() + " не найден среди загруженных заказов.");
        }

        // Сохраняем изменения
        boolean success = JsonStorageHelper.saveOrders(requireContext(), updatedOrders);
        if (success) {
            Log.d("OrderDetailsFragment", "Изменения успешно сохранены в хранилище.");
        } else {
            Log.e("OrderDetailsFragment", "Ошибка: не удалось сохранить изменения в хранилище.");
        }

        // Возвращаемся на предыдущий экран
        goBackToPreviousFragment();
        Log.d("OrderDetailsFragment", "Возвращаемся на предыдущий экран.");
    }

    private void handleCancelOrder() {
        selectedOrder.markAsCancelled(); // Меняем статус заказа
        List<OrderInfo> updatedOrders = JsonStorageHelper.loadOrders(requireContext());
        for (OrderInfo order : updatedOrders) {
            if (order.getId() == selectedOrder.getId()) {
                order.markAsCancelled(); // ещё раз присваиваем статус для гарантии
            }
        }
        JsonStorageHelper.saveOrders(requireContext(), updatedOrders); // Сохраняем изменения
        goBackToPreviousFragment(); // Возвращаемся на предыдущий экран
    }

    private List<OrderInfo> getUpdatedOrdersList() {
        List<OrderInfo> orders = JsonStorageHelper.loadOrders(requireContext());
        for (OrderInfo order : orders) {
            if (order.getId() == selectedOrder.getId()) {
                order.status = selectedOrder.status;
            }
        }
        return orders;
    }

    private void goBackToPreviousFragment() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    // Переход к экрану редактирования заказа
    private void navigateToEditOrder() {
        EditOrderFragment editFragment = EditOrderFragment.newInstance(selectedOrder);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, editFragment)
                .addToBackStack(null)
                .commit();
    }

    // Диалог подтверждения удаления заказа
    private void confirmDeletion() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Подтверждение удаления")
                .setMessage("Вы уверены, что хотите удалить этот заказ?")
                .setPositiveButton("Удалить", (dialog, which) -> performDeletion())
                .setNegativeButton("Отмена", null)
                .show();
    }

    // Выполнить фактическое удаление заказа
    private void performDeletion() {
        List<OrderInfo> orders = JsonStorageHelper.loadOrders(requireContext());

        // Проверяем, что выбранный заказ существует и есть ID
        if (selectedOrder == null || selectedOrder.getId() <= 0L) {
            Toast.makeText(requireContext(), "Ошибка: невозможно определить заказ для удаления.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Удаляем только заказ с указанным ID
        boolean removed = orders.removeIf(order -> order.getId() == selectedOrder.getId());

        if (removed) {
            // Сохраняем обновлённую версию списка
            JsonStorageHelper.saveOrders(requireContext(), orders);
            Toast.makeText(requireContext(), "Заказ успешно удалён.", Toast.LENGTH_SHORT).show();
            goBackToPreviousFragment();
        } else {
            Toast.makeText(requireContext(), "Ошибка при удалении заказа.", Toast.LENGTH_SHORT).show();
        }
    }
}