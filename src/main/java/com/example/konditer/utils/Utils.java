package com.example.konditer.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    /**
     * Парсит строку формата "dd.MM.yyyy" в объект Date.
     *
     * @param dateString Строка даты
     * @return Экземпляр Date
     */
    public static Date parseDate(String dateString) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        return format.parse(dateString);
    }
}