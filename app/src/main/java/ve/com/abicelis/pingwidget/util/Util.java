package ve.com.abicelis.pingwidget.util;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.widget.RemoteViews;

import ve.com.abicelis.pingwidget.R;
import ve.com.abicelis.pingwidget.app.activity.PingWidgetConfigureActivity;
import ve.com.abicelis.pingwidget.app.widget.PingWidgetProvider;
import ve.com.abicelis.pingwidget.enums.PingIconState;
import ve.com.abicelis.pingwidget.model.PingWidgetData;
import ve.com.abicelis.pingwidget.service.PingWidgetUpdateService;

/**
 * Created by abice on 14/2/2017.
 */

public class Util {

    private static final String TAG = Util.class.getSimpleName();


    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }


    public static void registerWidgetStartPauseOnClickListener(Context context, int widgetId, RemoteViews views) {

        //Register an Intent so that onClicks on the widget are received by PingWidgetProvider.onReceive()
        //Create an Intent, set PING_WIDGET_TOGGLE action to it, put EXTRA_APPWIDGET_ID as extra
        Intent clickIntent = new Intent(context, PingWidgetProvider.class);
        clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        clickIntent.setAction(Constants.PING_WIDGET_TOGGLE);

        //Construct a PendingIntent using the Intent above
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, widgetId, clickIntent, 0);

        //Register pendingIntent in RemoteViews onClick
        views.setOnClickPendingIntent(R.id.widget_start_pause, pendingIntent);
    }


    public static void registerWidgetReconfigureClickListener(Context context, int widgetId, RemoteViews views) {

        //Register an Intent so that onClicks on the widget reconfigure button are received by PingWidgetConfigureActivity
        //Create an Intent, set ACTION_WIDGET_RECONFIGURE action to it, put EXTRA_APPWIDGET_ID as extra
        Intent reconfigureIntent = new Intent(context, PingWidgetConfigureActivity.class);
        reconfigureIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        reconfigureIntent.setAction(Constants.ACTION_WIDGET_RECONFIGURE);

        PendingIntent configPendingIntent = PendingIntent.getActivity(context, widgetId, reconfigureIntent, 0);
        views.setOnClickPendingIntent(R.id.widget_host, configPendingIntent);
        views.setOnClickPendingIntent(R.id.widget_app_name, configPendingIntent);
    }


    public static void handleWidgetToggle(Context context, PingWidgetData data, int widgetId) {
        //Toggle isRunning() and set ping count to zero, write new running state into SharedPreferences
        data.toggleRunning();
        data.setPingCount(0);
        SharedPreferencesHelper.writePingWidgetData(context.getApplicationContext(), widgetId, data);

        // Notify PingWidgetUpdateService about the change (start/pause) ping
        Intent serviceIntent = new Intent(context.getApplicationContext(), PingWidgetUpdateService.class);
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        ContextCompat.startForegroundService(context, serviceIntent);

        //Get remote views and update
        RemoteViews views = RemoteViewsUtil.getRemoteViews(context, data.getWidgetLayoutType());

        //Update Play/Pause icon
        RemoteViewsUtil.updatePlayPause(views, data.isRunning());
        if(!data.isRunning())
            RemoteViewsUtil.updatePingIcon(views, PingIconState.PING_HIDDEN);

        Log.d(TAG, (data.isRunning() ? "onReceive(), Sent START to service" : "onReceive(), Sent STOP to service") );


        //Update widget
        AppWidgetManager.getInstance(context).updateAppWidget(widgetId, views);
    }



}
