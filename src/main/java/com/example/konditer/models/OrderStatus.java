package com.example.konditer.models;

/**
 * Типы статусов заказа
 */
public enum OrderStatus {
    Активный("Активный", "#FFD700"), // Желтый цвет (заказ в процессе выполнения)
    Выполнен("Выполнен", "#008000"), // Зеленый цвет (заказ выполнен)
    Отменен("Отменён", "#FF0000");    // Красный цвет (заказ отменён)

    private final String label;
    private final String color;

    /**
     * Конструктор с параметрами
     *
     * @param label Название статуса
     * @param color Цвет статуса (для отображения в UI)
     */
    OrderStatus(String label, String color) {
        this.label = label;
        this.color = color;
    }

    /**
     * Возвращает текстовую метку статуса
     *
     * @return Название статуса
     */
    public String getLabel() {
        return label;
    }

    /**
     * Возвращает цвет, ассоциированный со статусом
     *
     * @return HEX-код цвета
     */
    public String getColor() {
        return color;
    }
}