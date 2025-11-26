package com.example.konditer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.konditer.Constants;
import com.example.konditer.R;
import com.example.konditer.db.JsonStorageHelper;
import com.example.konditer.models.OrderInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateOrderActivity extends AppCompatActivity {

    private EditText inputClientName, inputOrderTitle, inputDeadline, inputPhoneNumber, inputWeight, editTextOrderPrice, inputDiscountPercentage, inputDescription, editTextNotes;
    private Spinner spinnerPlatform;
    private EditText inputBiscuits, inputFillings, inputCreams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);

        // Привязка элементов UI
        inputClientName = findViewById(R.id.input_client_name);
        inputOrderTitle = findViewById(R.id.input_order_title);
        inputDeadline = findViewById(R.id.input_deadline);
        inputPhoneNumber = findViewById(R.id.input_phone_number);
        inputWeight = findViewById(R.id.inputWeight);
        editTextOrderPrice = findViewById(R.id.editTextOrderPrice);
        inputDiscountPercentage = findViewById(R.id.input_discount_percentage);
        inputDescription = findViewById(R.id.input_description);
        editTextNotes = findViewById(R.id.editTextNotes);
        spinnerPlatform = findViewById(R.id.spinner_platform);

        // Новые поля для ингредиентов
        inputBiscuits = findViewById(R.id.input_biscuits);
        inputFillings = findViewById(R.id.input_fillings);
        inputCreams = findViewById(R.id.input_creams);

        // Инициализируем Spinner
        initializeSpinner();

        // Нажатие на кнопку "Сохранить заказ"
        findViewById(R.id.btn_save_order).setOnClickListener(v -> saveOrder());
    }

    // Инициализация Spinner'а для выбора платформы связи
    private void initializeSpinner() {
        // Массив платформ
        String[] platforms = {"VK", "Telegram", "Avito"};

        // Адаптер для Spinner'а
        ArrayAdapter<String> platformAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, platforms);
        platformAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Назначаем адаптер
        spinnerPlatform.setAdapter(platformAdapter);
    }

    // Обработка нажатия на кнопку "Сохранить заказ"
    private void saveOrder() {
        try {
            // Читаем введённые данные
            String clientFullName = inputClientName.getText().toString();
            String orderName = inputOrderTitle.getText().toString();
            String deadline = inputDeadline.getText().toString();
            String phoneNumber = inputPhoneNumber.getText().toString();
            String weight = inputWeight.getText().toString(); // Читаем вес

            // Проверка обязательных полей
            if (clientFullName.trim().isEmpty() || phoneNumber.trim().isEmpty()) {
                Toast.makeText(this, "Заполните все обязательные поля", Toast.LENGTH_SHORT).show();
                return;
            }

            // Проверка правильности номера телефона
            if (!validatePhone(phoneNumber)) {
                Toast.makeText(this, "Ошибка: неверный формат номера телефона.\nФормат: +7XXX XXX XX XX, 8XXX XXX XX XX или XXX XXX XX XX", Toast.LENGTH_SHORT).show();
                return;
            }

            // Проверка правильности даты
            if (!validateDate(deadline)) {
                Toast.makeText(this, "Ошибка: неверный формат даты. Должен быть ДД.ММ.ГГГГ", Toast.LENGTH_SHORT).show();
                return;
            }

            // Парсим цену с защитой от недопустимых значений
            double price;
            try {
                price = Double.parseDouble(editTextOrderPrice.getText().toString());
            } catch (NumberFormatException ex) {
                Toast.makeText(this, "Ошибка: некорректный формат цены", Toast.LENGTH_SHORT).show();
                return;
            }

            double parsedWeight;
            try {
                parsedWeight = Double.parseDouble(weight);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Ошибка: некорректный формат веса", Toast.LENGTH_SHORT).show();
                return;
            }

            double discountPercentage = Double.parseDouble(inputDiscountPercentage.getText().toString());
            String socialNetwork = spinnerPlatform.getSelectedItem().toString();
            String description = inputDescription.getText().toString();
            String notes = editTextNotes.getText().toString();

            // Получаем ингредиенты из полей ввода
            List<String> biscuits = convertCommaSeparatedToList(inputBiscuits.getText().toString());
            List<String> fillings = convertCommaSeparatedToList(inputFillings.getText().toString());
            List<String> creams = convertCommaSeparatedToList(inputCreams.getText().toString());

            OrderInfo newOrder = new OrderInfo(
                    clientFullName,
                    orderName,
                    deadline,
                    phoneNumber,
                    parsedWeight,
                    price,
                    discountPercentage,
                    socialNetwork,
                    description,
                    biscuits,
                    fillings,
                    creams,
                    notes,
                    "",
                    UUID.randomUUID().getMostSignificantBits()
            );

            // Получаем список существующих заказов и добавляем новый заказ
            List<OrderInfo> existingOrders = JsonStorageHelper.loadOrders(this);
            existingOrders.add(newOrder);

            // Сохраняем все заказы обратно в JSON
            JsonStorageHelper.saveOrders(this, existingOrders);

            // Отправляем сигнал для обновления списка заказов
            sendBroadcast(new Intent(Constants.ACTION_REFRESH_ORDERS));

            Toast.makeText(this, "Заказ успешно сохранён.", Toast.LENGTH_SHORT).show();
            finish(); // Закрываем активность
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка при обработке заказа: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Метод для проверки корректности номера телефона
    private boolean validatePhone(String phone) {
        // Регулярное выражение для формата российского номера телефона
        Pattern pattern = Pattern.compile("^(?:\\+7|8)?(\\d{3})(\\d{3})(\\d{2})(\\d{2})$");
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    // Метод для проверки корректности даты
    private boolean validateDate(String date) {
        // Регулярное выражение для формата даты ДД.ММ.ГГГГ
        Pattern pattern = Pattern.compile("^\\d{2}\\.\\d{2}\\.\\d{4}$");
        Matcher matcher = pattern.matcher(date);

        if (!matcher.matches()) {
            return false;
        }

        // Простая проверка существования даты
        try {
            // Пробуем разобрать дату согласно указанному формату
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            format.setLenient(false); // Выключаем снисхождение к ошибочным датам
            format.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    // Метод для перевода строки, разделённой запятыми, в список
    private List<String> convertCommaSeparatedToList(String commaSeparated) {
        return commaSeparated.isEmpty() ? new ArrayList<>() : Arrays.asList(commaSeparated.split("\\s*,\\s*"));
    }
}