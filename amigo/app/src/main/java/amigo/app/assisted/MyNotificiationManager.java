package amigo.app.assisted;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import amigo.app.R;

/**
 * This class is for managing what kind of the notification would be send
 * It used Singleton pattern to avoid sending too many notifications.
 */
public class MyNotificiationManager {

    private static MyNotificiationManager mInstance;
    private Context myContext;

    private MyNotificiationManager(Context context) {

        myContext = context;
    }

    public static synchronized MyNotificiationManager getInstance(Context context) {

        if (mInstance == null) {

            mInstance = new MyNotificiationManager(context);
        }

        return mInstance;
    }


    /**
     * This method is designed for display notification of a chat message.
     *
     * @param title
     * @param body
     */
    public void displayNotification(String title, String body) {

        //set notification bar
        NotificationCompat.Builder mBuilder = new NotificationCompat
                .Builder(myContext, Constants.CHANNEL_ID)
                .setSmallIcon(R.mipmap.amigo_launcher)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setContentText(body);

        Intent intent = chatting.createIntent(myContext);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(myContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification noti = mBuilder.build();
        noti.flags = Notification.FLAG_ONLY_ALERT_ONCE;

        mBuilder.setContentIntent(pendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) myContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (mNotificationManager != null) {

            mNotificationManager.notify(1, mBuilder.build());
        }


    }

    public void videoRequest(String title, String body) {

        //set notification bar
        NotificationCompat.Builder mBuilder = new NotificationCompat
                .Builder(this.myContext, Constants.CHANNEL_ID)
                .setSmallIcon(R.mipmap.amigo_launcher)
                .setContentTitle(title)
                .setContentText(body);

        Intent intent = VideoChat.createIntent(myContext);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(myContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification noti = mBuilder.build();
        noti.flags = Notification.FLAG_INSISTENT;

        mBuilder.setContentIntent(pendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) myContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (mNotificationManager != null) {

            mNotificationManager.notify(1, mBuilder.build());
        }


    }

    /**
     * This method would create a new notification when a new trip is ready for assisted person.
     *
     * @param title the title of notification
     * @param body  the message of notification
     */
    public void TripReady(String title, String body) {

        //set notification bar
        NotificationCompat.Builder mBuilder = new NotificationCompat
                .Builder(myContext, Constants.CHANNEL_ID)
                .setSmallIcon(R.mipmap.amigo_launcher)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setContentText(body);

        Intent intent = MyTripLists.newMyTripLists(myContext);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(myContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification noti = mBuilder.build();
        noti.flags = Notification.FLAG_ONLY_ALERT_ONCE;

        mBuilder.setContentIntent(pendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) myContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (mNotificationManager != null) {

            mNotificationManager.notify(1, mBuilder.build());
        }


    }

    /**
     * This method create notification when the assisted person is arrived at destination.
     *
     * @param title
     * @param body
     */
    public void personArrived(String title, String body) {

        //set notification bar
        NotificationCompat.Builder mBuilder = new NotificationCompat
                .Builder(myContext, Constants.CHANNEL_ID)
                .setSmallIcon(R.mipmap.amigo_launcher)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setContentText(body);

        Intent intent = MyTripLists.newMyTripLists(myContext);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(myContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification noti = mBuilder.build();
        noti.flags = Notification.FLAG_ONLY_ALERT_ONCE;

        mBuilder.setContentIntent(pendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) myContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (mNotificationManager != null) {

            mNotificationManager.notify(1, mBuilder.build());
        }


    }

}
