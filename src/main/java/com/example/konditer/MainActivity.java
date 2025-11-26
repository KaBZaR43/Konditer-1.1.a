package com.example.konditer;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.konditer.Notifications.JobSchedulerSetup;
import com.example.konditer.Notifications.NotificationModel;
import com.example.konditer.Notifications.NotificationSender;
import com.example.konditer.Notifications.NotificationsChannelCreator;
import com.example.konditer.activity.CreateOrderActivity;
import com.example.konditer.adapters.OrderAdapter;
import com.example.konditer.db.JsonStorageHelper;
import com.example.konditer.Notifications.NotificationStorageHelper;
import com.example.konditer.fragments.HistoryFragment;
import com.example.konditer.Notifications.NotificationsFragment;
import com.example.konditer.fragments.OrdersListFragment;
import com.example.konditer.fragments.StatisticsFragment;
import com.example.konditer.models.OrderInfo;
import com.google.android.material.navigation.NavigationView;

import java.time.LocalDateTime;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NotificationStorageHelper storageHelper;
    private RecyclerView recyclerView;
    private List<OrderInfo> mOrders;
    private List<OrderInfo> orders; // объявление переменной уровня активности

    // Определение приемника
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Обновляем список заказов
            List<OrderInfo> updatedOrders = JsonStorageHelper.loadOrders(MainActivity.this);
            updateOrdersList(updatedOrders);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NotificationsChannelCreator.createChannels(this);
        NotificationSender.requestPostNotificationsPermission(this);
        JobSchedulerSetup.setupJobScheduler(this);

        // Привязка к RecyclerView
        recyclerView = findViewById(R.id.orders_recyclerview);
        if (recyclerView == null) {
            throw new IllegalStateException("RecyclerView not found!");
        }

        // Настройка навигационного меню и прочего UI
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Подключение обработчика открытия бокового меню
        ImageButton drawerOpenBtn = findViewById(R.id.btn_drawer_open);
        drawerOpenBtn.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Загружаем данные о заказах
        loadOrders();

        // Показываем фрагмент списка заказов сразу при старте активности
        loadInitialFragment();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults); // Обязательная строка

        if (requestCode == NotificationSender.REQUEST_CODE_POST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Создали объект уведомления
                NotificationModel notification = new NotificationModel(
                        System.currentTimeMillis(), // уникальный ID
                        "Успешно", // Заголовок уведомления
                        "Разрешение на уведомления получено.", // Содержимое уведомления
                        LocalDateTime.now(), // Текущее время
                        false, // Изначально уведомление не прочитано
                        "", // Название заказа (оставляем пустым, если это системное уведомление)
                        LocalDateTime.now(), // Текущий срок сдачи (может быть текущий момент)
                        0.0, // Стоимость (неактуальна для системных уведомлений)
                        "" // Клиент (также неактуален)
                );

                // Отправляем уведомление
                NotificationSender.sendNotification(this, notification);
            } else {
                // Разрешение не дано, выводим сообщение пользователю
                Toast.makeText(this, "Необходимо разрешить уведомления для нормальной работы приложения.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void loadOrders() {
        mOrders = JsonStorageHelper.loadOrders(this);
    }

    private void loadInitialFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, new OrdersListFragment());
        transaction.commit();
    }

    // Метод для обновления списка заказов
    private void updateOrdersList(List<OrderInfo> orders) {
        // Берем адаптер и обновляем его
        OrderAdapter adapter = (OrderAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.updateOrders(orders);
        }
    }

    // Переход на создание нового заказа
    public void navigateToCreateOrderActivity(View view) {
        startActivity(new Intent(this, CreateOrderActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Регистрация приёмника с указанием флага RECEIVER_NOT_EXPORTED
        IntentFilter filter = new IntentFilter(Constants.ACTION_REFRESH_ORDERS);
        registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED);

        // Обновляем данные
        loadOrders();
        setupAdapterListeners();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Отписываемся от приёма событий
        unregisterReceiver(receiver);
    }

    // Метод для подключения слушателей в адаптере
    public void setupAdapterListeners() {
        OrderAdapter adapter = (OrderAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.setOnItemClickListener(this::onOrderClicked);
        }
    }

    // Новый метод для вызова детальных сведений при клике на заказ
    private void onOrderClicked(int position) {
        if (mOrders != null && !mOrders.isEmpty()) { // Проверка на null и пустоту
            OrderInfo clickedOrder = mOrders.get(position);
            showOrderDetails(clickedOrder);
        } else {
            Toast.makeText(this, "Нет заказов для отображения", Toast.LENGTH_SHORT).show();
        }
    }

    // Метод для отображения детальной информации о заказе
    @SuppressLint("SetTextI18n")
    private void showOrderDetails(OrderInfo order) {
        // Инфляция нашей кастомной разметки
        View dialogView = getLayoutInflater().inflate(R.layout.fragment_order_details, null);

        // Привязываем элементы интерфейса
        TextView tvClientName = dialogView.findViewById(R.id.textViewClientName);
        TextView tvOrderName = dialogView.findViewById(R.id.textViewOrderName);
        TextView tvDeliveryDate = dialogView.findViewById(R.id.textViewDeliveryDate);
        TextView tvWeight = dialogView.findViewById(R.id.textViewWeight);
        TextView tvTotalAmount = dialogView.findViewById(R.id.textViewTotalAmount);
        TextView tvDiscount = dialogView.findViewById(R.id.textViewDiscount);
        TextView tvBiscuits = dialogView.findViewById(R.id.textViewBiscuits);
        TextView tvFillings = dialogView.findViewById(R.id.textViewFillings);
        TextView tvCreams = dialogView.findViewById(R.id.textViewCreams);
        TextView tvSocialNetwork = dialogView.findViewById(R.id.textViewSocialNetwork);
        TextView tvStatus = dialogView.findViewById(R.id.textViewStatus);
        TextView tvNote = dialogView.findViewById(R.id.textViewNote);

        // Заполнение данных
        tvClientName.setText("Клиент: " + order.clientFullName);
        tvOrderName.setText("Название заказа: " + order.orderName);
        tvDeliveryDate.setText("Срок сдачи: " + order.deliveryDate);
        tvWeight.setText("Вес: " + order.weight);
        tvTotalAmount.setText("Общая сумма: ₽" + order.price);
        tvDiscount.setText("Скидка: " + order.discountPercentage + "%");
        tvBiscuits.setText("Использованные бисквиты:\n" + formatList(order.biscuits));
        tvFillings.setText("Использованные начинки:\n" + formatList(order.fillings));
        tvCreams.setText("Использованные кремы:\n" + formatList(order.creams));
        tvSocialNetwork.setText("Платформа связи: " + order.socialNetwork);
        tvStatus.setText("Статус заказа: " + order.getStatus().getLabel()); // Используем метод getLabel()
        tvNote.setText("Примечание: " + order.notes);

        // Создаем и показываем диалог
        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Закрыть", null)
                .show();
    }

    // Вспомогательный метод для форматирования списков
    private String formatList(List<String> items) {
        if (items == null || items.isEmpty()) {
            return "-"; // Можно выбрать любое подходящее значение
        }
        return String.join(", ", items);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_home) {          // Главная страница (Список заказов)
            displaySelectedScreen(R.id.menu_home);
        } else if (id == R.id.menu_history) { // История заказов
            displaySelectedScreen(R.id.menu_history);
        } else if (id == R.id.menu_statistics) { // Статистика
            displaySelectedScreen(R.id.menu_statistics);
        } else if (id == R.id.menu_notifications) { // Уведомления
            displaySelectedScreen(R.id.menu_notifications);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void displaySelectedScreen(int screenId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Удаляем ВСЕ активные фрагменты перед добавлением нового
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        // Первым этапом удаляем активные фрагменты
        Fragment activeFragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (activeFragment != null) {
            transaction.remove(activeFragment);
        }

        // Далее проверяем, какой фрагмент нужно загрузить исходя из идентификатора выбранного пункта меню
        if (screenId == R.id.menu_home) {              // Новый случай: переход на Главное окно (список заказов)
            transaction.add(R.id.fragment_container, new OrdersListFragment());
        } else if (screenId == R.id.menu_history) {    // Экран истории
            transaction.add(R.id.fragment_container, new HistoryFragment());
        } else if (screenId == R.id.menu_statistics) { // Экран статистики
            transaction.add(R.id.fragment_container, new StatisticsFragment());
        } else if (screenId == R.id.menu_notifications) {// Экран уведомлений
            transaction.add(R.id.fragment_container, NotificationsFragment.newInstance(storageHelper));
        }

        // Завершаем транзакцию с commitAllowingStateLoss, чтобы предотвратить ошибки состояния Activity
        transaction.commitAllowingStateLoss();
    }

    // Диалог сортировки
    public void showSortMenu(View v) {
        String[] options = {"По дате", "По стоимости", "По социальной сети"};
        new AlertDialog.Builder(this)
                .setTitle("Выберите способ сортировки")
                .setItems(options, (dialog, which) -> {
                    OrdersListFragment fragment = (OrdersListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    if (fragment != null) {
                        switch (which) {
                            case 0:
                                fragment.sortByDate();
                                break;
                            case 1:
                                fragment.sortByCost();
                                break;
                            case 2:
                                fragment.sortBySocialNetwork();
                                break;
                        }
                    }
                })
                .create()
                .show();
    }
}