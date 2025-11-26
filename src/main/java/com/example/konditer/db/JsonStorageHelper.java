package com.example.konditer.db;

import android.content.Context;
import android.util.Log;
import com.example.konditer.models.OrderInfo;
import com.example.konditer.models.OrderStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class JsonStorageHelper {

    private static final String TAG = "JsonStorageHelper";
    private static final String ORDERS_FILE_NAME = "orders.json";

    /**
     * Загружает все заказы из JSON-файла.
     * Если файл не найден или произошла ошибка, возвращает пустой список.
     */
    public static List<OrderInfo> loadOrders(Context context) {
        try {
            InputStream fis = context.openFileInput(ORDERS_FILE_NAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();

            JSONArray jsonArray = new JSONArray(sb.toString());
            List<OrderInfo> orders = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // Безопасно извлекаем поле weight, защищаясь от отсутствия поля
                double weight = jsonObject.optDouble("weight", 0.0);

                // СОХРАНЯЕМ STATUS
                OrderInfo order = new OrderInfo(
                        jsonObject.getString("clientFullName"),
                        jsonObject.getString("orderName"),
                        jsonObject.getString("deliveryDate"),
                        jsonObject.getString("phoneNumber"),
                        weight,
                        jsonObject.getDouble("price"),
                        jsonObject.getDouble("discountPercentage"),
                        jsonObject.getString("socialNetwork"),
                        jsonObject.getString("decorationDescription"),
                        extractListFromJsonArray(jsonObject.getJSONArray("biscuits")),
                        extractListFromJsonArray(jsonObject.getJSONArray("fillings")),
                        extractListFromJsonArray(jsonObject.getJSONArray("creams")),
                        jsonObject.optString("notes"),
                        jsonObject.optString("history"),
                        jsonObject.getLong("id")
                );

                // ЗАДАЕМ STATUS
                order.setStatus(OrderStatus.valueOf(jsonObject.getString("status")));

                orders.add(order);
            }
            return orders;
        } catch (FileNotFoundException fnfe) {
            Log.w(TAG, "Файл '" + ORDERS_FILE_NAME + "' не найден. Будет использован пустой список заказов.");
            return new ArrayList<>();
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при загрузке данных из JSON: " + e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Сохраняет список заказов в JSON-файл.
     *
     * @return True, если данные успешно записаны, иначе False.
     */
    public static boolean saveOrders(Context context, List<OrderInfo> orders) {
        JSONArray jsonArray = new JSONArray();
        for (OrderInfo order : orders) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", order.getId());
                jsonObject.put("clientFullName", order.getClientFullName());
                jsonObject.put("orderName", order.getOrderName());
                jsonObject.put("deliveryDate", order.getDeliveryDate());
                jsonObject.put("phoneNumber", order.getPhoneNumber());
                jsonObject.put("weight", order.getWeight());
                jsonObject.put("price", order.getPrice());
                jsonObject.put("discountPercentage", order.getDiscountPercentage());
                jsonObject.put("socialNetwork", order.getSocialNetwork());
                jsonObject.put("decorationDescription", order.getDecorationDescription());
                jsonObject.put("biscuits", new JSONArray(order.getBiscuits()));
                jsonObject.put("fillings", new JSONArray(order.getFillings()));
                jsonObject.put("creams", new JSONArray(order.getCreams()));
                jsonObject.put("notes", order.getNotes());
                jsonObject.put("history", order.history);

                // ДОБАВИЛИ STATUS
                jsonObject.put("status", order.getStatus().name());

                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                Log.e(TAG, "Ошибка при создании JSON-объекта: " + e.getMessage(), e);
                return false;
            }
        }

        // Остальная логика сохранения данных
        try {
            OutputStream fos = context.openFileOutput(ORDERS_FILE_NAME, Context.MODE_PRIVATE);
            fos.write(jsonArray.toString().getBytes(StandardCharsets.UTF_8));
            fos.flush();
            fos.close();
            Log.i(TAG, "Заказы успешно сохранены в файл '" + ORDERS_FILE_NAME + "'");
            return true; // Успех
        } catch (IOException ioe) {
            Log.e(TAG, "Ошибка при записи в файл '" + ORDERS_FILE_NAME + "': " + ioe.getMessage(), ioe);
            return false; // Ошибка
        }
    }

    /**
     * Преобразует JSONArray в List<String>.
     */
    private static List<String> extractListFromJsonArray(JSONArray jsonArray) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                list.add(jsonArray.getString(i));
            } catch (JSONException ignored) {} // Игнорируем исключение, если индекс выходит за пределы
        }
        return list;
    }
}