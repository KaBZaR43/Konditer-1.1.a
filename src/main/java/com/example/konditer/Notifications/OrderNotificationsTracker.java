package com.example.konditer.Notifications;

import java.util.HashSet;
import java.util.Set;

public class OrderNotificationsTracker {
    private Set<Long> sentNotifications = new HashSet<>();

    public synchronized void markAsSent(Long orderId) {
        sentNotifications.add(orderId);
    }

    public synchronized boolean wasAlreadySent(Long orderId) {
        return sentNotifications.contains(orderId);
    }
}
