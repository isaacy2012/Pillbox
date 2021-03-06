package com.innerCat.pillBox.widgets;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;

import com.innerCat.pillBox.R;
import com.innerCat.pillBox.activities.MainActivity;
import com.innerCat.pillBox.factories.DatabaseFactory;
import com.innerCat.pillBox.factories.SharedPreferencesFactory;
import com.innerCat.pillBox.objects.Item;
import com.innerCat.pillBox.room.Database;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Implementation of App Widget functionality.
 */
public class HomeWidgetProvider extends AppWidgetProvider {

    public static final String DECREMENT = "com.innerCat.PillBox.DECREMENT";
    private static final String MIDNIGHT_UPDATE = "com.innerCat.PillBox.SCHEDULED_UPDATE";


    /**
     * Broadcast an update to all the widgets
     * @param context the context from which the update should be broadcast
     *
     */
    public static void broadcastUpdate( Context context ) {
        Intent intent = new Intent(context, HomeWidgetProvider.class);
        //update intent
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        //ids of widgets
        int[] ids = AppWidgetManager.getInstance(context.getApplicationContext()).
                getAppWidgetIds(new ComponentName(context.getApplicationContext(), HomeWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }


    @Override
    public void onUpdate( Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds ) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            setRemoteAdapter(context, views);

            views.setEmptyView(R.id.widgetGridView, R.id.emptyGridViewLayout);

            // Create an Intent to launch MainActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.emptyGridViewLayout, pendingIntent);

            // Create an intent to decrement
            Intent decrementIntent = new Intent(context, HomeWidgetProvider.class);
            decrementIntent.setAction(DECREMENT);
            PendingIntent decrementPendingIntent = PendingIntent.getBroadcast(context,
                    0, decrementIntent, 0);
            views.setPendingIntentTemplate(R.id.widgetGridView, decrementPendingIntent );

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widgetGridView);
            appWidgetManager.updateAppWidget(appWidgetId, views);

        }
        scheduleNextUpdate(context);
    }

    private static void scheduleNextUpdate(Context context) {
        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // Substitute AppWidget for whatever you named your AppWidgetProvider subclass
        Intent intent = new Intent(context, HomeWidgetProvider.class);
        intent.setAction(MIDNIGHT_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        // Get a calendar instance for midnight tomorrow.
        Calendar midnight = Calendar.getInstance();
        midnight.set(Calendar.HOUR_OF_DAY, 0);
        midnight.set(Calendar.MINUTE, 0);
        // Schedule one second after midnight, to be sure we are in the right day next time this
        // method is called.  Otherwise, we risk calling onUpdate multiple times within a few
        // milliseconds
        midnight.set(Calendar.SECOND, 1);
        midnight.set(Calendar.MILLISECOND, 0);
        midnight.add(Calendar.DAY_OF_YEAR, 1);

        //set the alarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, midnight.getTimeInMillis(), pendingIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case DECREMENT:

                //Tell sharedPreferences to update on next app launch
                SharedPreferences sharedPreferences = SharedPreferencesFactory.getSP(context);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("widgetUpdate", true);
                editor.apply();

                Database database = DatabaseFactory.create(context);

                //Passed info from WidgetService.java
                int id = intent.getIntExtra("id", -1);

                //ROOM Threads
                ExecutorService executor = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());
                executor.execute(() -> {
                    //Background work here
                    Item item = database.getDao().getItem(id);
                    item.decrementStock();
                    database.getDao().update(item);

                    handler.post(() -> {
                        //UI Thread work here
                        //notify the widget that there is an update
                        HomeWidgetProvider.broadcastUpdate(context);
                    });
                });
                break;
            case MIDNIGHT_UPDATE:
                AppWidgetManager manager = AppWidgetManager.getInstance(context);
                int[] ids = manager.getAppWidgetIds(new ComponentName(context, HomeWidgetProvider.class));
                onUpdate(context, manager, ids);
                break;
        }
        //Show information
        super.onReceive(context, intent);
    }

    @Override
    public void onEnabled( Context context ) {
        // Enter relevant functionality for when the first widget is created
        HomeWidgetProvider.broadcastUpdate(context);
    }


    @Override
    public void onDisabled( Context context ) {
        // Enter relevant functionality for when the last widget is disabled
    }

    /**
     * Set the remote adapter
     * @param context the context from which to create the intent
     * @param views the remoteViews to set the remoteAdapter of
     */
    private static void setRemoteAdapter( Context context, @NonNull final RemoteViews views ) {
        views.setRemoteAdapter(R.id.widgetGridView,
                new Intent(context, WidgetService.class));
    }
}