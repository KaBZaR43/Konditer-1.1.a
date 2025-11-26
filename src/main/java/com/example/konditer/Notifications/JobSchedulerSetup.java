package com.example.konditer.Notifications;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import java.util.concurrent.TimeUnit;

public class JobSchedulerSetup {

    public static void setupJobScheduler(Context context) {
        ComponentName componentName = new ComponentName(context, NotifyJobService.class);
        JobInfo info = new JobInfo.Builder(1, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(TimeUnit.HOURS.toMillis(24)) // Каждые 24 часов
                .build();

        JobScheduler scheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.schedule(info);
    }
}