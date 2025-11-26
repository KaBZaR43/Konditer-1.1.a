package com.example.konditer.Notifications;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.konditer.MainActivity;
import com.example.konditer.R;
import com.example.konditer.Notifications.NotificationModel;

public class NotificationSender {

    public static void sendNotification(Context context, NotificationModel notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "order_channel")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Срок сдачи заказа: " + notification.getOrderName()) // Включаем название заказа
                .setContentText("Срок сдачи: " + notification.getDeadline().toString() +
                        "\nСтоимость: " + notification.getCost() +
                        "\nКлиент: " + notification.getClientName()) // Включаем остальные данные
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify((int) notification.getId(), builder.build());
    }

    public static void requestPostNotificationsPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activity.requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_POST_NOTIFICATION_PERMISSION);
        }
    }

    public static final int REQUEST_CODE_POST_NOTIFICATION_PERMISSION = 1001;
}