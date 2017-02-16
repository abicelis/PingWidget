package ve.com.abicelis.pingwidget.app.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import ve.com.abicelis.pingwidget.enums.WidgetTheme;
import ve.com.abicelis.pingwidget.model.PingWidgetData;
import ve.com.abicelis.pingwidget.service.PingWidgetUpdateService;
import ve.com.abicelis.pingwidget.R;
import ve.com.abicelis.pingwidget.util.SharedPreferencesHelper;

/**
 * Created by abice on 6/2/2017.
 */

public class PingWidgetProvider extends AppWidgetProvider {

    //CONST
    private static final String TAG = PingWidgetProvider.class.getSimpleName();
    public static String PING_WIDGET_TOGGLE = "PING_WIDGET_TOGGLE";

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
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
            PingWidgetData currentWidget = SharedPreferencesHelper.readPingWidgetData(context.getApplicationContext(), widgetId);
            Log.d(TAG, "onUpdate() Current widget data " + currentWidget.toString());

            //Check if widget has data (has been configured on PingWidgetConfigureFragment!)
            if(currentWidget != null) {

                //Get AppWidgetManager and RemoteViews
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

                //Update the widget's views
                views.setTextViewText(R.id.widget_host, currentWidget.getAddress());
                views.setImageViewResource(R.id.widget_start_pause, android.R.drawable.ic_media_play);
                views.setViewVisibility(R.id.widget_press_start, View.VISIBLE);
                views.setInt(R.id.widget_layout_container_top, "setBackgroundResource", WidgetTheme.valueOf(currentWidget.getThemeName()).getDrawableBackgroundContainerTop());



                //Register an Intent so that onClicks on the widget are received by PingWidgetProvider.onReceive()
                //Create an Intent, set PING_WIDGET_TOGGLE action to it, put EXTRA_APPWIDGET_ID as extra
                Intent clickIntent = new Intent(context, PingWidgetProvider.class);
                clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
                clickIntent.setAction(PingWidgetProvider.PING_WIDGET_TOGGLE);

                //Construct a PendingIntent using the Intent above
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, widgetId, clickIntent, 0);

                //Register pendingIntent in RemoteViews onClick
                views.setOnClickPendingIntent(R.id.widget_start_pause, pendingIntent);


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
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive()");

        //R.id.widget_start_pause was clicked
        if(PING_WIDGET_TOGGLE.equals(intent.getAction())) {
            Log.d(TAG, "onReceive(), PING_WIDGET_TOGGLE");

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
                    RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

                    if (currentWidget.isRunning()) {
                        remoteViews.setImageViewResource(R.id.widget_start_pause, android.R.drawable.ic_media_pause);
                        Log.d(TAG, "onReceive(), Sent START to service");
                    } else {
                        remoteViews.setImageViewResource(R.id.widget_start_pause, android.R.drawable.ic_media_play);
                        Log.d(TAG, "onReceive(), Sent STOP to service");
                    }

                    //Update widget
                    AppWidgetManager.getInstance(context).updateAppWidget(widgetId, remoteViews);
                }
            }
        } else {
            Log.d(TAG, "onReceive(): GOT A WEIRD REQUEST HERE: " + intent.getAction());
        }
        super.onReceive(context, intent);
    }
}
