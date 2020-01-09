package ve.com.abicelis.pingwidget.util;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.widget.RemoteViews;

import ve.com.abicelis.pingwidget.R;
import ve.com.abicelis.pingwidget.app.activity.PingWidgetConfigureActivity;
import ve.com.abicelis.pingwidget.app.widget.PingWidgetProvider;

/**
 * Created by abice on 14/2/2017.
 */

public class Util {

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
        views.setOnClickPendingIntent(R.id.widget_reconfigure, configPendingIntent);
    }



}
