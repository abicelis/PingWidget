package ve.com.abicelis.pingwidget.app.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.HashMap;
import java.util.Map;

import ve.com.abicelis.pingwidget.service.PingWidgetUpdateService;
import ve.com.abicelis.pingwidget.R;

/**
 * Created by abice on 6/2/2017.
 */

public class PingWidgetProvider extends AppWidgetProvider {

    //CONST
    private static final String TAG = PingWidgetProvider.class.getSimpleName();
    public static String TOGGLE_PING_WIDGET = "TOGGLE_PING_WIDGET";

    //DATA
    private static Map<Integer,Boolean> mServiceRunning = new HashMap<>();

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        //Delete widgets from mServiceRunning Map
        for(int appWidgetId :appWidgetIds) {
            mServiceRunning.remove(appWidgetId);
        }
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate");

        checkPermissions();

        for (int widgetId : appWidgetIds) {

            //Add widget to mServiceRunning map!
            mServiceRunning.put(widgetId, false);

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);



            //Register an Intent so that onClicks are received by PingWidgetProvider.onReceive()

            //Create an Intent, set TOGGLE_PING_WIDGET action to it, put EXTRA_APPWIDGET_ID as extra
            Intent clickIntent = new Intent(context, PingWidgetProvider.class);
            clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            clickIntent.setAction(TOGGLE_PING_WIDGET);

            //Construct a PendingIntent using the Intent above
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, widgetId, clickIntent, 0);

            //Register pendingIntent in RemoteViews onClick
            remoteViews.setOnClickPendingIntent(R.id.widget_start_pause, pendingIntent);

            //Update widget
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }


//        // Get all ids
//        ComponentName thisWidget = new ComponentName(context, PingWidgetProvider.class);
//        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
//
//        // Build the intent to call the service
//        Intent intent = new Intent(context.getApplicationContext(), PingWidgetUpdateService.class);
//        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);
//
//        // Update the widgets via the service
//        context.startService(intent);





//        // Get all ids
//        ComponentName thisWidget = new ComponentName(context, PingWidgetProvider.class);
//        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
//        for (int widgetId : allWidgetIds) {
//
//            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
//
//            // Set the text
//            int number;
//            number = (new Random().nextInt(100));
//            remoteViews.setTextViewText(R.id.widget_max_ping_value, String.valueOf(number) + ".0");
//            number = (new Random().nextInt(100));
//            remoteViews.setTextViewText(R.id.widget_avg_ping_value, String.valueOf(number) + ".0");
//            number = (new Random().nextInt(100));
//            remoteViews.setTextViewText(R.id.widget_min_ping_value, String.valueOf(number) + ".0");
//
//
//
//
//            //Set its intent, on R.id.widget_start_pause ImageView
//            Intent intent = new Intent(context, PingWidgetProvider.class);
//            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
//            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//            // Register an onClickListener in the RemoteViews
//            remoteViews.setOnClickPendingIntent(R.id.widget_start_pause, pendingIntent);
//
//            //Update widget (Whose id is widgetId) with value changes and intents, etc
//            appWidgetManager.updateAppWidget(widgetId, remoteViews);
//        }
    }

    private void checkPermissions() {

    }


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive()");

        //R.id.widget_start_pause was clicked
        if(intent.getAction().equals(TOGGLE_PING_WIDGET)) {
            Log.d(TAG, "onReceive(), TOGGLE_PING_WIDGET");


            Bundle extras = intent.getExtras();
            int widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            Log.d(TAG, "onReceive(), widgetId=" + widgetId);


            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            //If map doesn't contain widget id, add it, set it to false.
            //This happens when app is reinstalled and there are existing widgets, I think.
            if(!mServiceRunning.containsKey(widgetId)) {
                mServiceRunning.put(widgetId, false);
            }
            mServiceRunning.put(widgetId, !mServiceRunning.get(widgetId));


            // Create a fresh intent
            Intent serviceIntent = new Intent(context.getApplicationContext(), PingWidgetUpdateService.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            serviceIntent.putExtra(PingWidgetUpdateService.SHOULD_RUN, mServiceRunning.get(widgetId));
            context.startService(serviceIntent);


            if(mServiceRunning.get(widgetId)) {
                remoteViews.setImageViewResource(R.id.widget_start_pause, android.R.drawable.ic_media_pause);
                Log.d(TAG, "onReceive(), Sent START to service");
            } else {
                remoteViews.setImageViewResource(R.id.widget_start_pause, android.R.drawable.ic_media_play);
                Log.d(TAG, "onReceive(), Sent STOP to service");
            }

//            if(mServiceRunning.get(widgetId)) {
//                context.stopService(serviceIntent);
//                remoteViews.setImageViewResource(R.id.widget_start_pause, android.R.drawable.ic_media_play);
//                Log.d(TAG, "onReceive(), Service stopped");
//            } else {
//                context.startService(serviceIntent);
//                remoteViews.setImageViewResource(R.id.widget_start_pause, android.R.drawable.ic_media_pause);
//                Log.d(TAG, "onReceive(), Service started");
//            }


            //Update widget
            ComponentName thisWidget = new ComponentName(context, PingWidgetProvider.class);

            AppWidgetManager.getInstance(context).updateAppWidget(widgetId, remoteViews);
        }

            super.onReceive(context, intent);
    }
}
