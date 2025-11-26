package com.example.konditer.adapters;

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

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final List<OrderInfo> mOrders;
    private final Context context;
    private OnItemClickListener clickListener;
    private OnLongItemClickListener longClickListener;

    public Context getContext() {
        return context;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnLongItemClickListener {
        void onLongItemClick(int position);
    }

    public OrderAdapter(Context context, List<OrderInfo> orders) {
        this.context = context;
        this.mOrders = orders;
        if (mOrders == null || mOrders.isEmpty()) {
            Log.d("OrderAdapter", "Warning: Initialized with empty or null list");
        }
    }

    // Новый метод для обновления списка заказов
    public void updateOrders(List<OrderInfo> newOrders) {
        synchronized (this) {
            this.mOrders.clear();
            this.mOrders.addAll(newOrders);
            notifyDataSetChanged();
            Log.d("OrderAdapter", "Updated with " + mOrders.size() + " orders");
        }
    }

    // Метод для фильтрации активных заказов
    /*public void filterActiveOrders() {
        List<OrderInfo> activeOrders = new ArrayList<>();
        for (OrderInfo order : mOrders) {
            if (order.getStatus() == OrderStatus.Активный) {
                activeOrders.add(order);
            }
        }
        updateOrders(activeOrders);
        Log.d("Filtering", "Filtered to " + activeOrders.size() + " active orders");
    }

    // Метод для фильтрации выполненных и отменённых заказов
    /*public List<OrderInfo> getArchivedOrders() {
        List<OrderInfo> archivedOrders = new ArrayList<>();
        for (OrderInfo order : mOrders) {
            if (order.getStatus() == OrderStatus.Выполнен || order.getStatus() == OrderStatus.Отменен) {
                archivedOrders.add(order);
            }
        }
        return archivedOrders;
    }*/

    public List<OrderInfo> getCurrentList() {
        return mOrders;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new OrderViewHolder(v, clickListener, longClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderInfo order = mOrders.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return mOrders.size();
    }

    // Внутренний класс для удержания ViewHolder
    protected static class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private final TextView textViewOrderName;
        private final TextView clientName;
        private final TextView deliveryDate;
        private final TextView totalAmount;
        private final OnItemClickListener clickListener;
        private final OnLongItemClickListener longClickListener;

        public OrderViewHolder(@NonNull View itemView, OnItemClickListener clickListener, OnLongItemClickListener longClickListener) {
            super(itemView);
            this.clickListener = clickListener;
            this.longClickListener = longClickListener;
            textViewOrderName = itemView.findViewById(R.id.textViewOrderName);
            clientName = itemView.findViewById(R.id.client_name);
            deliveryDate = itemView.findViewById(R.id.textViewDeliveryDate);
            totalAmount = itemView.findViewById(R.id.textViewTotalAmount);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bind(OrderInfo order) {
            textViewOrderName.setText(order.orderName);
            clientName.setText(order.clientFullName);
            deliveryDate.setText("Срок сдачи: " + order.deliveryDate);
            totalAmount.setText("Сумма: ₽" + order.price);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onItemClick(getBindingAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (longClickListener != null) {
                longClickListener.onLongItemClick(getBindingAdapterPosition());
            }
            return true;
        }
    }
}