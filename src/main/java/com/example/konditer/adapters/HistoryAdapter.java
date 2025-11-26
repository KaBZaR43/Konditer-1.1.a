package com.example.konditer.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.konditer.R;
import com.example.konditer.models.OrderInfo;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.OrderViewHolder> {

    private final List<OrderInfo> orders;
    private final Context context;
    private OnItemClickListener clickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public HistoryAdapter(List<OrderInfo> orders, Context context) {
        this.orders = orders;
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderInfo order = orders.get(position);
        holder.bind(order);

        // Обрабатываем событие клика по заказу
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick(position);
            }
            Log.d("HistoryAdapter", "Нажата карточка заказа №" + order.getId() + " на позиции " + position);
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewOrderName;
        private final TextView clientName;
        private final TextView deliveryDate;
        private final TextView totalAmount;
        private final TextView orderStatus; // Новое поле для статуса заказа

        public OrderViewHolder(View itemView) {
            super(itemView);
            textViewOrderName = itemView.findViewById(R.id.textViewOrderName);
            clientName = itemView.findViewById(R.id.client_name);
            deliveryDate = itemView.findViewById(R.id.textViewDeliveryDate);
            totalAmount = itemView.findViewById(R.id.textViewTotalAmount);
            orderStatus = itemView.findViewById(R.id.textViewStatus); // Присваиваем ссылку на поле статуса
        }

        @SuppressLint("SetTextI18n")
        public void bind(OrderInfo order) {
            if (textViewOrderName != null) {
                textViewOrderName.setText("Заказ: " + (order.getOrderName() != null ? order.getOrderName() : ""));
            }
            if (clientName != null) {
                clientName.setText("Клиент: " + (order.getClientFullName() != null ? order.getClientFullName() : ""));
            }
            if (deliveryDate != null) {
                deliveryDate.setText("Срок сдачи: " + (order.getDeliveryDate() != null ? order.getDeliveryDate() : ""));
            }
            if (totalAmount != null) {
                totalAmount.setText("Цена: " + (order.getPrice() != 0 ? order.getPrice() + " руб." : ""));
            }
            if (orderStatus != null) {
                orderStatus.setText("Статус: " + (order.getStatus() != null ? order.getStatus().name() : ""));
            }
        }
    }
}