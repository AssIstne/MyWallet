package com.assistne.mywallet.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import com.assistne.mywallet.R;
import com.assistne.mywallet.activity.BillActivity;
import com.assistne.mywallet.activity.HomeActivity;
import com.assistne.mywallet.util.BillUtils;
import com.assistne.mywallet.util.GlobalUtils;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by assistne on 15/11/3.
 */
public class NotificationUtils {

    private static final int FLAG_NOTIFY = 1;
    public static void showNotification(Context context) {
        Log.d("", "is notify " + GlobalUtils.isNotify(context));
        if (!GlobalUtils.isNotify(context)) {
            dismissNotification(context);
            return;
        }
        NotificationManager notifyManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification.Builder builder = new Notification.Builder(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_add_bill);
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        float budget = GlobalUtils.getBudget(context);
        float unUsed = BillUtils.getBudgetUnused(context, calendar);
        float dayUsed = BillUtils.getDaySpend(context, calendar);
        remoteViews.setProgressBar(R.id.notification_progress, (int) budget, (int) (budget - unUsed), false);
        remoteViews.setTextViewText(R.id.notification_text_today, "今日￥" + GlobalUtils.formatPrice(dayUsed, true));
        remoteViews.setTextViewText(R.id.notification_text_unused, "剩余￥" + GlobalUtils.formatPrice(unUsed, true));
        Intent billIntent = new Intent(context, BillActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(BillActivity.class);
        stackBuilder.addNextIntent(billIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notification_btn, resultPendingIntent);

        builder.setSmallIcon(R.drawable.logo_small)
                .setContent(remoteViews)
                .setOngoing(true)
                .setAutoCancel(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setVisibility(Notification.VISIBILITY_SECRET);
        }
        TaskStackBuilder stackBuilder1 = TaskStackBuilder.create(context);
        stackBuilder1.addNextIntent(new Intent(context, HomeActivity.class));
        PendingIntent resultPendingIntent1 = stackBuilder1.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent1);
        notifyManager.notify(FLAG_NOTIFY, builder.build());

    }

    public static void dismissNotification(Context context) {
        NotificationManager notifyManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifyManager.cancel(FLAG_NOTIFY);
    }
}
