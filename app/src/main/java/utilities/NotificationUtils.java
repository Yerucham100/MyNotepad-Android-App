package utilities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.icu.util.ULocale;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;

import com.example.diacious.mynotepad.MainActivity;
import com.example.diacious.mynotepad.R;

import java.util.Random;

/**
 * Created by Akhihiero David(Yerucham) on 12/20/2017.
 */

public class NotificationUtils
{
    private static final int NOTE_NOTIFICATION_ID = 312;
    private static final int PENDING_INTENT_ID = 1312;
    public static final String NOTIFICATION_SHOULD_BE_CANCELLED = "cancel_notification";

    /**
     * Method to issue notification
     * @param context The calling activity context
     */
    public static void issueNotification(Context context)
    {
        String notificationMessage = getNotificationMessage(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                 .setColor(ContextCompat.getColor(context, PreferenceUtils.getThemePrimaryColor(context)))
                 .setSmallIcon(R.drawable.ic_small_notification_icon)
                 .setContentTitle(context.getString(R.string.notification_title))
                 .setContentText(notificationMessage)
                 .setLargeIcon(getLargeIcon(context))
                 .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationMessage))
                 .setContentIntent(getContentIntent(context))
                 .setDefaults(Notification.DEFAULT_SOUND)
                 .setAutoCancel(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            builder.setPriority(Notification.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NOTE_NOTIFICATION_ID, builder.build());
    }

    /**
     * Method to cancel all notifications
     * @param context The calling activity context
     */
    public static void cancelNotifications(Context context)
    {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    /**
     * Method to get the bitmap for the notification
     * @param context
     * @return The bitmap
     */
    private static Bitmap getLargeIcon(Context context)
    {
        Resources res = context.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(res, R.drawable.ic_notepad);

        return largeIcon;
    }

    /**
     * Method to create a pending intent with intent to the main activity
     * @param context
     * @return
     */
    private static PendingIntent getContentIntent(Context context)
    {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(NOTIFICATION_SHOULD_BE_CANCELLED, true);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                                                                PENDING_INTENT_ID,
                                                                intent,
                                                                PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    /**
     * Method to get a notification message
     * @param context
     * @return A notification message
     */
    private static String getNotificationMessage(Context context)
    {
        Random r = new Random();
        String[] generalNotificationMessages = {context.getString(R.string.notification_msg1),
                context.getString(R.string.notification_msg2),
                context.getString(R.string.notification_msg3),};
        //
        String[] morningNotificationMessages = {context.getString(R.string.notification_morning_msg1)};
        String[] afternoonNotificationMessages = {context.getString(R.string.notification_afternoon_msg1)};
        String[] eveningNotificationMessages = {context.getString(R.string.notification_evening_msg1)};
        String notificationMessage = null;

        if (DateUtils.isMorning())
            notificationMessage = r.nextInt(2) == 1? morningNotificationMessages[r
                    .nextInt(morningNotificationMessages.length)] :
                    generalNotificationMessages[r.nextInt(generalNotificationMessages.length)];
        else if (DateUtils.isAfternoon())
            notificationMessage = r.nextInt(2) == 1? afternoonNotificationMessages[r
                    .nextInt(afternoonNotificationMessages.length)] :
                    generalNotificationMessages[r.nextInt(generalNotificationMessages.length)];
        else
            notificationMessage = r.nextInt(2) == 1? eveningNotificationMessages[r
                    .nextInt(eveningNotificationMessages.length)] :
                    generalNotificationMessages[r.nextInt(generalNotificationMessages.length)];

        return notificationMessage;
    }

}
