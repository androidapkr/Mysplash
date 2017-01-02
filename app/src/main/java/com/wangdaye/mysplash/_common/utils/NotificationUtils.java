package com.wangdaye.mysplash._common.utils;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.database.DownloadMissionEntity;
import com.wangdaye.mysplash._common.ui._basic.MysplashActivity;

/**
 * Snackbar utils.
 * */

public class NotificationUtils {
    // data
    private static final String NOTIFICATION_GROUP_KEY = "mysplash_download_result_notification";

    private static final String PREFERENCE_NOTIFICATION = "notification";
    private static final String KEY_NOTIFICATION_ID = "notification_id";
    private static final int NOTIFICATION_GROUP_SUMMARY_ID = 1001;

    /** <br> notification. */

    public static void sendDownloadPhotoSuccessNotification(Context c, DownloadMissionEntity entity) {
        NotificationManagerCompat.from(c)
                .notify(
                        getNotificationId(c),
                        buildSingleNotification(c, "Photo", entity.getRealTitle()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NotificationManagerCompat.from(c)
                    .notify(NOTIFICATION_GROUP_SUMMARY_ID, buildGroupSummaryNotification(c));
        }
    }

    public static void sendDownloadCollectionSuccessNotification(Context c, DownloadMissionEntity entity) {
        NotificationManagerCompat.from(c)
                .notify(
                        getNotificationId(c),
                        buildSingleNotification(
                                c, "Collection", entity.getRealTitle()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NotificationManagerCompat.from(c)
                    .notify(NOTIFICATION_GROUP_SUMMARY_ID, buildGroupSummaryNotification(c));
        }
    }

    private static Notification buildSingleNotification(Context c, String subText, String contentText) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(c)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(c.getString(R.string.feedback_download_success))
                .setSubText(subText)
                .setContentText(contentText);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setGroup(NOTIFICATION_GROUP_KEY);
        }
        return builder.build();
    }

    private static Notification buildGroupSummaryNotification(Context c) {
        return new NotificationCompat.Builder(c)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(c.getString(R.string.feedback_download_success))
                .setGroup(NOTIFICATION_GROUP_KEY)
                .setGroupSummary(true)
                .build();
    }

    private static int getNotificationId(Context c) {
        SharedPreferences sharedPreferences = c.getSharedPreferences(
                PREFERENCE_NOTIFICATION,
                Context.MODE_PRIVATE);
        int id = sharedPreferences.getInt(KEY_NOTIFICATION_ID, 1) + 1;
        if (id > NOTIFICATION_GROUP_SUMMARY_ID - 1) {
            id = 1;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_NOTIFICATION_ID, id);
        editor.apply();

        return id;
    }

    /** <br> snackbar & toast. */

    public static void showSnackbar(String content, int duration) {
        MysplashActivity a = Mysplash.getInstance().getTopActivity();
        if (Mysplash.getInstance().getActivityCount() > 0) {
            View container = a.provideSnackbarContainer();

            Snackbar snackbar = Snackbar
                    .make(container, content, duration);

            Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();

            TextView contentTxt = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);
            DisplayUtils.setTypeface(a, contentTxt);

            if (Mysplash.getInstance().isLightTheme()) {
                contentTxt.setTextColor(ContextCompat.getColor(a, R.color.colorTextContent_light));
                snackbarLayout.setBackgroundResource(R.color.colorRoot_light);
            } else {
                contentTxt.setTextColor(ContextCompat.getColor(a, R.color.colorTextContent_dark));
                snackbarLayout.setBackgroundResource(R.color.colorRoot_dark);
            }

            snackbar.show();
        }
    }

    public static void showActionSnackbar(String content, String action,
                                          int duration, View.OnClickListener l) {
        if (Mysplash.getInstance().getActivityCount() > 0) {
            MysplashActivity a = Mysplash.getInstance().getTopActivity();
            View container = a.provideSnackbarContainer();

            Snackbar snackbar = Snackbar
                    .make(container, content, duration)
                    .setAction(action, l);

            Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();

            TextView contentTxt = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);
            DisplayUtils.setTypeface(a, contentTxt);

            Button actionBtn = (Button) snackbarLayout.findViewById(R.id.snackbar_action);

            if (Mysplash.getInstance().isLightTheme()) {
                contentTxt.setTextColor(ContextCompat.getColor(a, R.color.colorTextContent_light));
                actionBtn.setTextColor(ContextCompat.getColor(a, R.color.colorTextTitle_light));
                snackbarLayout.setBackgroundResource(R.color.colorRoot_light);
            } else {
                contentTxt.setTextColor(ContextCompat.getColor(a, R.color.colorTextContent_dark));
                actionBtn.setTextColor(ContextCompat.getColor(a, R.color.colorTextTitle_dark));
                snackbarLayout.setBackgroundResource(R.color.colorRoot_dark);
            }

            snackbar.show();
        }
    }
}
