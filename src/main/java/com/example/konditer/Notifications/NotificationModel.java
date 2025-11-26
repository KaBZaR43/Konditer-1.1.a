package com.example.konditer.Notifications;

import org.json.JSONException;
import org.json.JSONObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class NotificationModel {
    private long id;
    private String title;
    private String body;
    private LocalDateTime timestamp;
    private boolean isRead;

    // Новые поля для передачи параметров заказа
    private String orderName;
    private LocalDateTime deadline;
    private double cost;
    private String clientName;

    // Конструктор с параметрами заказа
    public NotificationModel(long id, String title, String body, LocalDateTime timestamp, boolean isRead,
                             String orderName, LocalDateTime deadline, double cost, String clientName) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.timestamp = timestamp;
        this.isRead = isRead;
        this.orderName = orderName;
        this.deadline = deadline;
        this.cost = cost;
        this.clientName = clientName;
    }

    // Методы для десериализации из JSON
    public static NotificationModel fromJson(JSONObject jsonObject) throws JSONException {
        try {
            return new NotificationModel(
                    jsonObject.getLong("id"),                     // ID
                    jsonObject.getString("title"),                // Заголовок
                    jsonObject.getString("body"),                 // Тело уведомления
                    LocalDateTime.parse(jsonObject.getString("timestamp"), DateTimeFormatter.ISO_DATE_TIME),
                    jsonObject.getBoolean("isRead"),              // Прочитано или нет
                    jsonObject.getString("orderName"),            // Название заказа
                    LocalDateTime.parse(jsonObject.getString("deadline"), DateTimeFormatter.ISO_DATE_TIME),
                    jsonObject.getDouble("cost"),                 // Стоимость
                    jsonObject.getString("clientName")            // Имя клиента
            );
        } catch (JSONException | DateTimeParseException e) {
            throw new JSONException("Ошибка при обработке JSON-объекта: " + e.getMessage());
        }
    }

    // Методы для сериализации в JSON
    public JSONObject toJson() {
        try {
            return new JSONObject()
                    .put("id", this.id)
                    .put("title", this.title)
                    .put("body", this.body)
                    .put("timestamp", this.timestamp.format(DateTimeFormatter.ISO_DATE_TIME))
                    .put("isRead", this.isRead)
                    .put("orderName", this.orderName)
                    .put("deadline", this.deadline.format(DateTimeFormatter.ISO_DATE_TIME))
                    .put("cost", this.cost)
                    .put("clientName", this.clientName);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Методы equals и hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NotificationModel)) return false;
        NotificationModel that = (NotificationModel) o;
        return id == that.id && isRead == that.isRead && cost == that.cost && title.equals(that.title) && body.equals(that.body) && timestamp.equals(that.timestamp) && orderName.equals(that.orderName) && deadline.equals(that.deadline) && clientName.equals(that.clientName);
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(id);
        result = 31 * result + title.hashCode();
        result = 31 * result + body.hashCode();
        result = 31 * result + timestamp.hashCode();
        result = 31 * result + Boolean.hashCode(isRead);
        result = 31 * result + orderName.hashCode();
        result = 31 * result + deadline.hashCode();
        result = 31 * result + Double.hashCode(cost);
        result = 31 * result + clientName.hashCode();
        return result;
    }

    // Getters и setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setIsRead(boolean read) {
        isRead = read;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
}