package com.example.konditer.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class OrderInfo implements Parcelable {

    @SerializedName("id")                   // Уникальный идентификатор заказа
    public long id;

    @SerializedName("creationDate")         // Дата создания заказа
    public Date creationDate;

    @SerializedName("clientFullName")       // Полное имя клиента
    public String clientFullName;

    @SerializedName("orderName")            // Название заказа
    public String orderName;

    @SerializedName("deliveryDate")         // Срок сдачи заказа
    public String deliveryDate;

    @SerializedName("phoneNumber")          // Контактный номер телефона
    public String phoneNumber;

    @SerializedName("price")                // Цена заказа
    public double price;

    @SerializedName("weight")               // Вес заказа
    public double weight;

    @SerializedName("discountPercentage")   // Процент скидки
    public double discountPercentage;

    @SerializedName("socialNetwork")        // Платформа связи с клиентом
    public String socialNetwork;

    @SerializedName("decorationDescription")// Описание украшений
    public String decorationDescription;

    @SerializedName("biscuits")             // Список использованных видов бисквитов
    public List<String> biscuits;

    @SerializedName("fillings")             // Список используемых начинок
    public List<String> fillings;

    @SerializedName("creams")               // Список используемых кремов
    public List<String> creams;

    @SerializedName("notes")                // Примечания к заказу
    public String notes;

    @SerializedName("history")              // История изменений заказа
    public String history;

    @SerializedName("status")               // Текущий статус заказа
    public OrderStatus status = OrderStatus.Активный;

    // Конструктор для восстановления из Parcel
    public OrderInfo(Parcel in) {
        creationDate = new Date(in.readLong()); // Важно преобразование!
        clientFullName = in.readString();
        orderName = in.readString();
        deliveryDate = in.readString();
        phoneNumber = in.readString();
        price = in.readDouble();
        weight = in.readDouble();
        discountPercentage = in.readDouble();
        socialNetwork = in.readString();
        decorationDescription = in.readString();
        biscuits = in.createStringArrayList();
        fillings = in.createStringArrayList();
        creams = in.createStringArrayList();
        notes = in.readString();
        history = in.readString(); // Новая история изменений
        id = in.readLong();
        status = OrderStatus.values()[in.readInt()];
    }

    // Основной конструктор
    public OrderInfo(String clientFullName, String orderName, String deliveryDate, String phoneNumber,
                     double weight, double price, double discountPercentage, String socialNetwork,
                     String decorationDescription, List<String> biscuits, List<String> fillings,
                     List<String> creams, String notes, String history, long id) {
        this.clientFullName = clientFullName;
        this.orderName = orderName;
        this.deliveryDate = deliveryDate;
        this.phoneNumber = phoneNumber;
        this.weight = weight;
        this.price = price;
        this.discountPercentage = discountPercentage;
        this.socialNetwork = socialNetwork;
        this.decorationDescription = decorationDescription;
        this.biscuits = biscuits;
        this.fillings = fillings;
        this.creams = creams;
        this.notes = notes;
        this.history = history;
        this.id = id;
    }

    // Реализация Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(creationDate.getTime()); // Запись в виде миллисекунд
        dest.writeString(clientFullName);
        dest.writeString(orderName);
        dest.writeString(deliveryDate);
        dest.writeString(phoneNumber);
        dest.writeDouble(weight);
        dest.writeDouble(price);
        dest.writeDouble(discountPercentage);
        dest.writeString(socialNetwork);
        dest.writeString(decorationDescription);
        dest.writeStringList(biscuits);
        dest.writeStringList(fillings);
        dest.writeStringList(creams);
        dest.writeString(notes);
        dest.writeString(history); // Новые данные
        dest.writeLong(id);
        dest.writeInt(status.ordinal()); // Индекс статуса
    }

    public int describeContents() {
        return 0;
    }

    // Статический конструктор для воссоздания объекта из Parcel
    public static final Parcelable.Creator<OrderInfo> CREATOR = new Parcelable.Creator<OrderInfo>() {
        @Override
        public OrderInfo createFromParcel(Parcel source) {
            return new OrderInfo(source);
        }

        @Override
        public OrderInfo[] newArray(int size) {
            return new OrderInfo[size];
        }
    };
    public void updateFrom(OrderInfo other) {
        this.clientFullName = other.clientFullName;
        this.orderName = other.orderName;
        this.deliveryDate = other.deliveryDate;
        this.weight = other.weight;
        this.price = other.price;
        this.discountPercentage = other.discountPercentage;
        this.socialNetwork = other.socialNetwork;
        this.notes = other.notes;
        this.decorationDescription = other.decorationDescription;
        this.biscuits = other.biscuits;      // обновить список бисквитов
        this.fillings = other.fillings;      // обновить список начинок
        this.creams = other.creams;         // обновить список кремов
        this.history = other.history;        // обновить историю изменений
    }

    // Геттеры и сеттеры для всех полей
    public long getId() {
        return id;
    }

    public String getClientFullName() {
        return clientFullName;
    }

    public String getOrderName() {
        return orderName;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public double getPrice() {
        return price;
    }

    public double getWeight() {
        return weight;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public String getSocialNetwork() {
        return socialNetwork;
    }

    public String getDecorationDescription() {
        return decorationDescription;
    }

    public List<String> getBiscuits() {
        return biscuits;
    }

    public List<String> getFillings() {
        return fillings;
    }

    public List<String> getCreams() {
        return creams;
    }

    public String getNotes() {
        return notes;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public OrderStatus getStatus() {
        return status;
    }

    // Метод для изменения статуса на "Выполненный"
    public void markAsCompleted() {
        this.status = OrderStatus.Выполнен;
        logChange("Заказ выполнен");
    }

    // Метод для изменения статуса на "Отменённый"
    public void markAsCancelled() {
        this.status = OrderStatus.Отменен;
        logChange("Заказ отменён");
    }

    // Регистрация изменения статуса в логах
    private void logChange(String message) {
        Log.i("OrderSystem", "ID=" + id + ": " + message);
    }

}