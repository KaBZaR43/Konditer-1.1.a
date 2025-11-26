package com.example.konditer.Notifications;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class NotificationStorageHelper implements Serializable {
    private static final String TAG = "NotificationStorageHelper";
    private static final String FILE_NAME = "notifications.json";

    /**
     * Загрузка всех уведомлений из JSON-файла.
     */
    public static List<NotificationModel> loadNotifications(Context context) {
        try {
            InputStream inputStream = context.openFileInput(FILE_NAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();

            JSONArray jsonArray = new JSONArray(builder.toString());
            List<NotificationModel> notifications = new ArrayList<>(jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                NotificationModel notification = NotificationModel.fromJson(jsonObject);
                notifications.add(notification);
            }
            return notifications;
        } catch (FileNotFoundException fnfe) {
            Log.w(TAG, "Файл '" + FILE_NAME + "' не найден.");
            return new ArrayList<>();
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при загрузке данных из JSON.", e);
            return new ArrayList<>();
        }
    }

    /**
     * Сохранение списка уведомлений в JSON-файл.
     */
    public static void saveNotifications(Context context, List<NotificationModel> notifications) {
        JSONArray jsonArray = new JSONArray();
        for (NotificationModel notification : notifications) {
            jsonArray.put(notification.toJson());
        }
        try {
            OutputStream outputStream = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            outputStream.write(jsonArray.toString().getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            outputStream.close();
            Log.i(TAG, "Уведомления успешно сохранены в файл '" + FILE_NAME + "'");
        } catch (IOException e) {
            Log.e(TAG, "Ошибка при записи в файл '" + FILE_NAME + "'.", e);
        }
    }
}