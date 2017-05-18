package ve.com.abicelis.pingwidget.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

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
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return pingWidgetData;
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
