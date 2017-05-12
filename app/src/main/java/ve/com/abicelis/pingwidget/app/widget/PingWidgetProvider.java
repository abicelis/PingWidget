package ve.com.abicelis.pingwidget.app.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import ve.com.abicelis.pingwidget.enums.WidgetLayoutType;
import ve.com.abicelis.pingwidget.enums.WidgetTheme;
import ve.com.abicelis.pingwidget.model.PingWidgetData;
import ve.com.abicelis.pingwidget.service.PingWidgetUpdateService;
import ve.com.abicelis.pingwidget.R;
import ve.com.abicelis.pingwidget.util.RemoteViewsUtil;
import ve.com.abicelis.pingwidget.util.SharedPreferencesHelper;
import ve.com.abicelis.pingwidget.util.Util;

/**
 * Created by abice on 6/2/2017.
 */

public class PingWidgetProvider extends AppWidgetProvider {

    //CONST
    private static final String TAG = PingWidgetProvider.class.getSimpleName();
    public static String PING_WIDGET_TOGGLE = "PING_WIDGET_TOGGLE";

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.d(TAG, "onDeleted()");

        //Delete widgets from SharedPreferences
        for(int appWidgetId :appWidgetIds)
            SharedPreferencesHelper.deletePingWidgetData(context.getApplicationContext(), appWidgetId);

        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate()");


        for (int widgetId : appWidgetIds) {
            Log.d(TAG, "onUpdate() Processing ID= " + widgetId);

            //Get widget data from SharedPreferences
            PingWidgetData data = SharedPreferencesHelper.readPingWidgetData(context.getApplicationContext(), widgetId);
            Log.d(TAG, "onUpdate() Current widget data: " + (data != null ? data.toString() : "null"));

            //Check if widget has data (has been configured on PingWidgetConfigureFragment!)
            if(data != null) {

                //Get AppWidgetManager and RemoteViews
                RemoteViews views = RemoteViewsUtil.getRemoteViews(context, data.getWidgetLayoutType());

                //Update the widget's views
                RemoteViewsUtil.initWidgetViews(context, widgetId, views, data);
                RemoteViewsUtil.updatePlayPause(views, data.isRunning());


                //Register an Intent so that onClicks on the widget are received by PingWidgetProvider.onReceive()
                //Create an Intent, set PING_WIDGET_TOGGLE action to it, put EXTRA_APPWIDGET_ID as extra
                Util.registerWidgetStartPauseOnClickListener(context, widgetId, views);



                //Finally, update the widget
                appWidgetManager.updateAppWidget(widgetId, views);
            }
        }


//        for (int widgetId : appWidgetIds) {
//
//            //Add widget to mServiceRunning map!
//            mServiceRunning.put(widgetId, false);
//
//            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
//
//
//            //Register an Intent so that onClicks are received by PingWidgetProvider.onReceive()
//
//            //Create an Intent, set PING_WIDGET_TOGGLE action to it, put EXTRA_APPWIDGET_ID as extra
//            Intent clickIntent = new Intent(context, PingWidgetProvider.class);
//            clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
//            clickIntent.setAction(PING_WIDGET_TOGGLE);
//
//            //Construct a PendingIntent using the Intent above
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, widgetId, clickIntent, 0);
//
//            //Register pendingIntent in RemoteViews onClick
//            remoteViews.setOnClickPendingIntent(R.id.widget_start_pause, pendingIntent);
//
//            //Update widget
//            appWidgetManager.updateAppWidget(widgetId, remoteViews);
//        }


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

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        Log.d(TAG, "onAppWidgetOptionsChanged() appWidgetId=" + appWidgetId);

        //int OPTION_APPWIDGET_MAX_HEIGHT = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, -1);
        //int OPTION_APPWIDGET_MAX_WIDTH = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, -1);
        //int OPTION_APPWIDGET_MIN_WIDTH = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, -1);
        int OPTION_APPWIDGET_MIN_HEIGHT = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, -1);
        Log.d(TAG, "onAppWidgetOptionsChanged() Changed widget size! New OPTION_APPWIDGET_MIN_HEIGHT = " + OPTION_APPWIDGET_MIN_HEIGHT);

        //Update SharedPreferenceData
        PingWidgetData data = SharedPreferencesHelper.readPingWidgetData(context, appWidgetId);
        data.setWidgetLayoutType(WidgetLayoutType.getWidgetLayoutTypeByHeight(OPTION_APPWIDGET_MIN_HEIGHT));
        SharedPreferencesHelper.writePingWidgetData(context, appWidgetId, data);

        //Get new RemoteViews layout and update widget
        RemoteViews views = RemoteViewsUtil.getRemoteViews(context, data.getWidgetLayoutType());
        RemoteViewsUtil.initWidgetViews(context, appWidgetId, views, data);
        RemoteViewsUtil.updatePlayPause(views, data.isRunning());

        //Register an Intent so that onClicks on the widget are received by PingWidgetProvider.onReceive()
        //Create an Intent, set PING_WIDGET_TOGGLE action to it, put EXTRA_APPWIDGET_ID as extra
        Util.registerWidgetStartPauseOnClickListener(context, appWidgetId, views);

        AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, views);

        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onEnabled(Context context) {
        Log.d(TAG, "onEnabled()");
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        Log.d(TAG, "onDisabled()");
        super.onDisabled(context);
    }

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        Log.d(TAG, "onRestored()");
        super.onRestored(context, oldWidgetIds, newWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive() Action: " + intent.getAction());


        //R.id.widget_start_pause was clicked
        if(PING_WIDGET_TOGGLE.equals(intent.getAction())) {
            Log.d(TAG, "onReceive() attending PING_WIDGET_TOGGLE");

            //Get extras from intent, namely, the widgetId
            Bundle extras = intent.getExtras();
            int widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            Log.d(TAG, "onReceive(), widgetId=" + widgetId);

            if (widgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                //Get widget data from SharedPreferences
                PingWidgetData currentWidget = SharedPreferencesHelper.readPingWidgetData(context.getApplicationContext(), widgetId);

                if (currentWidget != null) {
                    //Toggle isRunning(), write new running state into SharedPreferences
                    currentWidget.toggleRunning();
                    SharedPreferencesHelper.writePingWidgetData(context.getApplicationContext(), widgetId, currentWidget);

                    // Notify PingWidgetUpdateService about the change (start/pause) ping
                    Intent serviceIntent = new Intent(context.getApplicationContext(), PingWidgetUpdateService.class);
                    serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
                    context.startService(serviceIntent);

                    //Get remote views and update
                    RemoteViews views = RemoteViewsUtil.getRemoteViews(context, currentWidget.getWidgetLayoutType());

                    //Update Play/Pause icon
                    RemoteViewsUtil.updatePlayPause(views, currentWidget.isRunning());
                    Log.d(TAG, (currentWidget.isRunning() ? "onReceive(), Sent START to service" : "onReceive(), Sent STOP to service") );


                    //Update widget
                    AppWidgetManager.getInstance(context).updateAppWidget(widgetId, views);
                }
            }
        }

        super.onReceive(context, intent);
    }
}
