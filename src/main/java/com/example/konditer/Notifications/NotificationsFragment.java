package com.example.konditer.Notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.konditer.R;

import java.util.List;

public class NotificationsFragment extends Fragment {

    private RecyclerView rvNotifications;
    private NotificationsAdapter adapter;
    private NotificationStorageHelper storageHelper;

    public static NotificationsFragment newInstance(NotificationStorageHelper helper) {
        NotificationsFragment fragment = new NotificationsFragment();
        Bundle args = new Bundle();
        args.putSerializable("STORAGE_HELPER", helper); // Хранилище должно быть сериализуемым
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            storageHelper = (NotificationStorageHelper) getArguments().getSerializable("STORAGE_HELPER");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        rvNotifications = view.findViewById(R.id.rvNotifications);
        rvNotifications.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new NotificationsAdapter();
        rvNotifications.setAdapter(adapter);

        // Добавляем линии-разделители между элементами
        rvNotifications.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        // Получаем хранилище из аргументов
        if (getArguments() != null) {
            storageHelper = (NotificationStorageHelper) getArguments().getSerializable("STORAGE_HELPER");
        }

        // Загружаем уведомления из хранилища
        List<NotificationModel> notifications = storageHelper.loadNotifications(requireContext());
        adapter.submitList(notifications);

        return view;
    }
}