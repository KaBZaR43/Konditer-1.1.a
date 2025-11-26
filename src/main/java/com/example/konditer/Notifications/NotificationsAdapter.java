package com.example.konditer.Notifications;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.konditer.R;

import java.time.format.DateTimeFormatter;

public class NotificationsAdapter extends ListAdapter<NotificationModel, NotificationsAdapter.NotificationHolder> {

    public NotificationsAdapter() {
        super(DIFF_CALLBACK);
    }

    static class NotificationHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvBody, tvOrderInfo, tvTimestamp;
        CheckBox cbRead;

        NotificationHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvOrderInfo = itemView.findViewById(R.id.tvOrderInfo); // Новая строка с информацией о заказе
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            cbRead = itemView.findViewById(R.id.cbRead);
        }

        void bind(NotificationModel model) {
            tvTitle.setText(model.getTitle());
            tvBody.setText(model.getBody());

            // Преобразовываем LocalDateTime в строку
            String formattedTimestamp = model.getTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            tvTimestamp.setText(formattedTimestamp);

            // Заполняем информацию о заказе и клиенте
            tvOrderInfo.setText("Заказ: " + model.getOrderName() + "\nКлиент: " + model.getClientName());

            cbRead.setChecked(model.isRead());
        }
    }

    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationHolder holder, int position) {
        holder.bind(getItem(position));
    }

    private static final DiffUtil.ItemCallback<NotificationModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<NotificationModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull NotificationModel oldItem, @NonNull NotificationModel newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull NotificationModel oldItem, @NonNull NotificationModel newItem) {
            return oldItem.equals(newItem);
        }
    };
}