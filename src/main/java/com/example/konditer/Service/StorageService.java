package com.example.konditer.Service;

import android.content.Context;
import android.os.Environment;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class StorageService {

    private static final String ORDERS_FILE_NAME = "orders.json";

    public void saveToFile(Context context, String data) {
        File dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(dir, ORDERS_FILE_NAME);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(data);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String loadFromFile(Context context) {
        File externalDir = Environment.getExternalStorageDirectory();
        File file = new File(externalDir, ORDERS_FILE_NAME);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}