package ve.com.abicelis.pingwidget.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import ve.com.abicelis.pingwidget.enums.MaxPingsOnChartPreferenceType;
import ve.com.abicelis.pingwidget.enums.MaxPingsPreferenceType;
import ve.com.abicelis.pingwidget.enums.PingIntervalPreferenceType;
import ve.com.abicelis.pingwidget.enums.WidgetLayoutType;
import ve.com.abicelis.pingwidget.enums.WidgetTheme;
import ve.com.abicelis.pingwidget.model.PingWidgetData;

/**
 * Created by abice on 9/2/2017.
 */

public class SharedPreferencesHelper {

    private static final String PING_WIDGET_FILE = "PING_WIDGET_FILE";
    private static final String PING_WIDGET_DATA = "PING_WIDGET_DATA_ID_";


    public static void writePingWidgetData(Context context, int widgetId, PingWidgetData data) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        Gson gson = new Gson();
        editor.putString(PING_WIDGET_DATA + widgetId, gson.toJson(data));
        editor.commit();
    }

    public static PingWidgetData readPingWidgetData(Context context, int widgetId) {
        PingWidgetData pingWidgetData = null;
        try {
            Gson gson = new Gson();
            String json = getPreferences(context).getString(PING_WIDGET_DATA + widgetId, "");
            pingWidgetData = gson.fromJson(json, PingWidgetData.class);

            //Fix for widgets with old data.
            if(pingWidgetData != null)
                resetPingWidgetDataIfNull(context, widgetId, pingWidgetData);


        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return pingWidgetData;
    }

    private static void resetPingWidgetDataIfNull(Context context, int widgetId, PingWidgetData data) {
        boolean flag = false;
        if(data.getWidgetLayoutType() == null) {
            data.setWidgetLayoutType(WidgetLayoutType.TALL);
            flag = true;
        }
        if(data.getPingInterval() == null) {
            data.setPingInterval(PingIntervalPreferenceType.INTERVAL_1);
            flag = true;
        }
        if(data.getTheme() == null) {
            data.setTheme(WidgetTheme.BLUE_YELLOW);
            flag = true;
        }
        if(data.getMaxPings() == null) {
            data.setMaxPings(MaxPingsPreferenceType.MAX_PINGS_INFINITE);
            flag = true;
        }
        if(data.getMaxPingsOnChart() == null) {
            data.setMaxPingsOnChart(MaxPingsOnChartPreferenceType.MAX_PINGS_15);
            flag = true;
        }
        if(data.getAddress() == null || data.getAddress().isEmpty()) {
            data.setAddress("www.google.com");
            flag = true;
        }

        if(flag) {
            Toast.makeText(context, "Updating data of widget " + widgetId + " to new version", Toast.LENGTH_SHORT).show();
            writePingWidgetData(context, widgetId, data);
        }
    }

    public static boolean pingWidgetDataExists(Context context, int widgetId) {
        return getPreferences(context).contains(PING_WIDGET_DATA + widgetId);
    }

    public static void deletePingWidgetData(Context context, int widgetId) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.remove(PING_WIDGET_DATA + widgetId);
        editor.apply();
    }

    private static SharedPreferences getPreferences(Context context){
        return context.getSharedPreferences(PING_WIDGET_FILE, Context.MODE_PRIVATE);
    }
}
