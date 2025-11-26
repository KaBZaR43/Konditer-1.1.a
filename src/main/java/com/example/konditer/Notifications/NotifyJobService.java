package com.example.konditer.Notifications;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.util.Log;

import com.example.konditer.db.JsonStorageHelper;
import com.example.konditer.models.OrderInfo;
import com.example.konditer.models.OrderStatus;
import com.example.konditer.utils.Utils;

import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

public class NotifyJobService extends JobService {

    private OrderNotificationsTracker tracker = new OrderNotificationsTracker(); // Глобальный трекер уведомлений

    @Override
    public boolean onStartJob(JobParameters params) {
        checkOrderDeadlines(this); // Передаем контекст
        jobFinished(params, false);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        // Освобождаем ресурсы или производим очистку при завершении задания
        return false; // Верните true, если хотите повторить выполнение задания
    }

    private void checkOrderDeadlines(Context context) {
        List<OrderInfo> orders = fetchOrders(); // Методы получения заказов перенесены внутрь службы
        LocalDateTime now = LocalDateTime.now();
        for (OrderInfo order : orders) {
            LocalDateTime deadline;
            try {
                // Используем старый метод парсинга и конвертируем результат в LocalDateTime
                Date legacyDate = Utils.parseDate(order.getDeliveryDate());
                ZonedDateTime zdt = legacyDate.toInstant().atZone(ZonedDateTime.now().getZone());
                deadline = zdt.toLocalDateTime();
            } catch (ParseException e) {
                Log.e("NotifyJobService", "Ошибка при парсинге даты", e);
                continue; // Переходим к следующей итерации
            }

            Duration duration = Duration.between(now, deadline);
            if (
                    duration.toHours() <= 72 && // Срок ближе, чем через 3 суток
                            !tracker.wasAlreadySent(order.getId()) && // Ещё не отправлялось уведомление
                            order.getStatus() != OrderStatus.Выполнен // Проверяем, что заказ ещё не завершён
            ) {
                NotificationModel newNotification = new NotificationModel( // Объявляем переменную newNotification
                        System.currentTimeMillis(), // уникальный ID
                        "Срок сдачи заказа: " + order.getOrderName(), // Заголовок уведомления
                        "Срок сдачи: " + deadline.toString() + "\nСтоимость: " + order.getPrice() + "\nКлиент: " + order.getClientFullName(), // Детальное описание
                        LocalDateTime.now(), false, // Timestamp и статус прочтения
                        order.getOrderName(), deadline, order.getPrice(), order.getClientFullName()
                );

                NotificationSender.sendNotification(context, newNotification); // Отправляем уведомление
                tracker.markAsSent(order.getId()); // Отмечаем заказ как отправленный

                List<NotificationModel> notifications = NotificationStorageHelper.loadNotifications(context);
                notifications.add(newNotification); // Добавляем новое уведомление в список
                NotificationStorageHelper.saveNotifications(context, notifications); // Сохраняем уведомления
            }
        }
    }

    private List<OrderInfo> fetchOrders() {
        // Получаем список заказов из хранилища
        return JsonStorageHelper.loadOrders(this);
    }
}