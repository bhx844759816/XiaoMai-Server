package com.guangzhida.xiaomai.server.chat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.EasyUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;

/**
 * 通知栏管理类
 */
public class NotificationUtils {
    private static int NOTIFY_ID = 525; // start notification id
    public static final String CHANNEL_ID = "xiaomai_server_notification";
    private final static String TAG = "EaseNotifier";
    private final static String MSG_CH = "%s个联系人发来%s条消息";


    private static final long[] VIBRATION_PATTERN = new long[]{0, 180, 80, 120};

    private NotificationManager notificationManager;

    private HashSet<String> fromUsers = new HashSet<>();
    private int notificationNum = 0;

    private Context appContext;
    private String packageName;
    private String msg;
    private long lastNotifyTime;
    private Ringtone ringtone = null;
    private AudioManager audioManager;
    private Vibrator vibrator;
    private EaseNotificationInfoProvider notificationInfoProvider;

    public NotificationUtils(Context context) {
        appContext = context.getApplicationContext();
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "XiaoMaiChat", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setVibrationPattern(VIBRATION_PATTERN);
            channel.setShowBadge(true);
            //channel.canShowBadge(); //设置是否允许显示角标
            notificationManager.createNotificationChannel(channel);
        }
        packageName = appContext.getApplicationInfo().packageName;
        msg = MSG_CH;
        audioManager = (AudioManager) appContext.getSystemService(Context.AUDIO_SERVICE);
        vibrator = (Vibrator) appContext.getSystemService(Context.VIBRATOR_SERVICE);
    }

    /**
     * this function can be override
     */
    public void reset() {
        resetNotificationCount();
        cancelNotification();
    }

    private void resetNotificationCount() {
        notificationNum = 0;
        fromUsers.clear();
    }

    private void cancelNotification() {
        if (notificationManager != null)
            notificationManager.cancel(NOTIFY_ID);
    }

    /**
     * handle the new message
     * this function can be override
     *
     * @param message 消息体
     */
    public synchronized void notify(EMMessage message, int count) {
        // check if app running background
        if (!EasyUtils.isAppRunningForeground(appContext)) {
            EMLog.d(TAG, "app is running in background");
            notificationNum = count;
            fromUsers.add(message.getFrom());
            handleMessage(message);
        }
    }

    public synchronized void notify(List<EMMessage> messages) {
        if (!EasyUtils.isAppRunningForeground(appContext)) {
            EMLog.d(TAG, "app is running in background");
            for (EMMessage message : messages) {
                notificationNum++;
                fromUsers.add(message.getFrom());
            }
            handleMessage(messages.get(messages.size() - 1));
        }
    }

    public synchronized void notify(String content,String title) {
        if (!EasyUtils.isAppRunningForeground(appContext)) {
            try {
                NotificationCompat.Builder builder = generateBaseBuilder(content);
                builder.setContentTitle(title);
                Notification notification = builder.build();
                notificationManager.notify(NOTIFY_ID, notification);

                if (Build.VERSION.SDK_INT < 26) {
                    vibrateAndPlayTone();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




    /**
     * send it to notification bar
     * This can be override by subclass to provide customer implementation
     *
     * @param message 消息体
     */
    void handleMessage(EMMessage message) {
        try {
            int fromUsersNum = fromUsers.size();
            String notifyText = String.format(msg, fromUsersNum, notificationNum);
            NotificationCompat.Builder builder = generateBaseBuilder(notifyText);
            if (notificationInfoProvider != null) {
                String contentTitle = notificationInfoProvider.getTitle(message);
                if (contentTitle != null) {
                    builder.setContentTitle(contentTitle);
                }
                notifyText = notificationInfoProvider.getDisplayedText(message);
                if (notifyText != null) {
                    builder.setTicker(notifyText);
                }
                Intent i = notificationInfoProvider.getLaunchIntent(message);
                if (i != null) {
                    PendingIntent pendingIntent = PendingIntent.getActivity(appContext, NOTIFY_ID, i, PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(pendingIntent);
                }
                notifyText = notificationInfoProvider.getLatestText(message, fromUsersNum, notificationNum);
                if (notifyText != null) {
                    builder.setContentText(notifyText);
                }
                int smallIcon = notificationInfoProvider.getSmallIcon(message);
                if (smallIcon != 0) {
                    builder.setSmallIcon(smallIcon);
                }
                //设置显示的角标个数
                builder.setNumber(notificationNum);
            }
            Notification notification = builder.build();
            showBadge(notification);
            notificationManager.notify(NOTIFY_ID, notification);
            if (Build.VERSION.SDK_INT < 26) {
                vibrateAndPlayTone();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 展示应用角标
     *
     * @param notification
     */
    public void showBadge(Notification notification) {
        String manufacturer = Build.MANUFACTURER;
        //这个字符串可以自己定义,例如判断华为就填写huawei,魅族就填写meizu
        if ("xiaomi".equalsIgnoreCase(manufacturer)) {
            xiaomiBadge(notification);
        } else if ("huawei".equalsIgnoreCase(manufacturer)) {
            huaweiBadge();
        }
    }


    /**
     * 适配小米通知角标
     *
     * @param notification
     */
    private void xiaomiBadge(Notification notification) {
        try {
            Field field = notification.getClass().getDeclaredField("extraNotification");
            Object extraNotification = field.get(notification);
            Method method = extraNotification.getClass().getDeclaredMethod("setMessageCount", int
                    .class);
            method.invoke(extraNotification, notificationNum);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 适配华为角标
     */
    private void huaweiBadge() {
        try {
            Bundle bunlde = new Bundle();
            bunlde.putString("package", packageName); // com.test.badge is your package name
            bunlde.putString("class", "com.guangzhida.xiaomai.ui.MainActivity"); // com.test. badge.MainActivity is your apk main activity
            bunlde.putInt("badgenumber", notificationNum);
            appContext.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, bunlde);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Generate a base Notification#Builder, contains:
     * 1.Use the app icon as default icon
     * 2.Use the app name as default title
     * 3.This notification would be sent immediately
     * 4.Can be cancelled by user
     * 5.Would launch the default activity when be clicked
     *
     * @return
     */
    private NotificationCompat.Builder generateBaseBuilder(String content) {
        PackageManager pm = appContext.getPackageManager();
        String title = pm.getApplicationLabel(appContext.getApplicationInfo()).toString();
        Intent i = appContext.getPackageManager().getLaunchIntentForPackage(packageName);
        PendingIntent pendingIntent = PendingIntent.getActivity(appContext, NOTIFY_ID, i, PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Builder(appContext, CHANNEL_ID)
                .setSmallIcon(appContext.getApplicationInfo().icon)
                .setContentTitle(title)
                .setTicker(content)
                .setContentText(content)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_MAX)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
    }

    /**
     * vibrate and  play tone
     */
    public void vibrateAndPlayTone() {


        if (System.currentTimeMillis() - lastNotifyTime < 1000) {
            return;
        }

        try {
            lastNotifyTime = System.currentTimeMillis();
            if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
                EMLog.e(TAG, "in slient mode now");
                return;
            }
            vibrator.vibrate(VIBRATION_PATTERN, -1);
            if (ringtone == null) {
                Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                ringtone = RingtoneManager.getRingtone(appContext, notificationUri);
                if (ringtone == null) {
                    EMLog.d(TAG, "cant find ringtone at:" + notificationUri.getPath());
                    return;
                }
            }
            if (!ringtone.isPlaying()) {
                String vendor = Build.MANUFACTURER;
                ringtone.play();
                if (vendor != null && vendor.toLowerCase().contains("samsung")) {
                    Thread ctlThread = new Thread() {
                        public void run() {
                            try {
                                Thread.sleep(3000);
                                if (ringtone.isPlaying()) {
                                    ringtone.stop();
                                }
                            } catch (Exception e) {
                            }
                        }
                    };
                    ctlThread.run();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * set notification info Provider
     *
     * @param provider
     */
    public void setNotificationInfoProvider(EaseNotificationInfoProvider provider) {
        notificationInfoProvider = provider;
    }

    public interface EaseNotificationInfoProvider {
        /**
         * set the notification content, such as "you received a new image from xxx"
         *
         * @param message
         * @return null-will use the default text
         */
        String getDisplayedText(EMMessage message);

        /**
         * set the notification content: such as "you received 5 message from 2 contacts"
         *
         * @param message
         * @param fromUsersNum- number of message sender
         * @param messageNum    -number of messages
         * @return null-will use the default text
         */
        String getLatestText(EMMessage message, int fromUsersNum, int messageNum);

        /**
         * 设置notification标题
         *
         * @param message
         * @return null- will use the default text
         */
        String getTitle(EMMessage message);

        /**
         * set the small icon
         *
         * @param message
         * @return 0- will use the default icon
         */
        int getSmallIcon(EMMessage message);

        /**
         * set the intent when notification is pressed
         *
         * @param message
         * @return null- will use the default icon
         */
        Intent getLaunchIntent(EMMessage message);
    }
}
