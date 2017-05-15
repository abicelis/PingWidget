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


                //Register an Intent so that onClicks on the widget are received by PingWidgetProvider.onReceive()
                //Create an Intent, set PING_WIDGET_TOGGLE action to it, put EXTRA_APPWIDGET_ID as extra
                Util.registerWidgetStartPauseOnClickListener(context, widgetId, views);



                //Finally, update the widget
                appWidgetManager.updateAppWidget(widgetId, views);
            }
        }
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
