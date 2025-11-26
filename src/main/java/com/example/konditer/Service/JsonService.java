package com.example.konditer.Service;

import android.content.Context;
import com.example.konditer.models.OrderInfo;
import com.example.konditer.models.OrderStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class JsonService {

    public String serializeOrders(Context context, List<OrderInfo> orders) {
        JSONArray jsonArray = new JSONArray();
        for (OrderInfo order : orders) {
            JSONObject jsonObject = new JSONObject();
            try {
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
                jsonObject.put("id", order.getId());
                // Включаем статус заказа в сериализацию
                jsonObject.put("status", order.getStatus().name());

                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray.toString();
    }


    public List<OrderInfo> deserializeOrders(Context context, String jsonData) {
        List<OrderInfo> result = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(jsonData);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                OrderInfo order = new OrderInfo(
                        object.getString("clientFullName"),
                        object.getString("orderName"),
                        object.getString("deliveryDate"),
                        object.getString("phoneNumber"),
                        object.getDouble("weight"),
                        object.getDouble("price"),
                        object.getDouble("discountPercentage"),
                        object.getString("socialNetwork"),
                        object.getString("decorationDescription"),
                        extractListFromJsonArray(object.getJSONArray("biscuits")),
                        extractListFromJsonArray(object.getJSONArray("fillings")),
                        extractListFromJsonArray(object.getJSONArray("creams")),
                        object.getString("notes"),
                        object.getString("history"),
                        object.getLong("id")
                );

                // Установите статус заказа при десериализации
                order.setStatus(OrderStatus.valueOf(object.getString("status")));
                result.add(order);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    private List<String> extractListFromJsonArray(JSONArray jsonArray) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                list.add(jsonArray.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}