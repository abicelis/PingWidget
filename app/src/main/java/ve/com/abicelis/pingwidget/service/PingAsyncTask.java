package ve.com.abicelis.pingwidget.service;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

import ve.com.abicelis.pingwidget.R;
import ve.com.abicelis.pingwidget.enums.WidgetTheme;
import ve.com.abicelis.pingwidget.model.PingWidgetData;
import ve.com.abicelis.pingwidget.util.SharedPreferencesHelper;
import ve.com.abicelis.pingwidget.util.Util;

/**
 * Created by abice on 7/2/2017.
 */

class PingAsyncTask extends AsyncTask<String, Float, Integer> {

    //CONST
    private static final int MILLIS = 1000;
    private int SLEEP_TIME;
    private static final String TAG = PingAsyncTask.class.getSimpleName();

    //DATA
    private Context mAppContext;
    private AppWidgetManager mAppWidgetManager;
    private int mWidgetId;

    public PingAsyncTask(Context appContext, int widgetId, int pingInterval, int maxPings) {

        mAppContext = appContext;
        mWidgetId = widgetId;
        SLEEP_TIME = pingInterval * MILLIS;
        mAppWidgetManager = AppWidgetManager.getInstance(appContext);
    }



    @Override
    protected Integer doInBackground(String... strings) {
        while (!isCancelled()) {

            try {
                float pingDelay = ping(strings[0]);
                Log.d(TAG, "PING SUCCESS (" + pingDelay + "ms)");

                publishProgress(pingDelay);

            }catch (Exception e) {
                Log.d(TAG, "PING FAILED: " + e.getMessage());
                e.printStackTrace();
                publishProgress(-1f);

            }

            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                return -1;
            }

        }
        return -1;
    }


    private float ping(String url) throws IOException {
        String str;
        float pingDelay;


        //TODO: Figure this out: reader.read(buffer) takes way too long, solution is using grep to minimize output but it gives
        //TODO: permission errors; "java.io.IOException: Error running exec(). Command: [bash, -c, /system/bin/ping -c 1 -W 1 www.google.com] Working Directory: null Environment: null"
//        String[] pingCommand = {
//                "/bin/sh",
//                "-c",
//                "/system/bin/ping -c 1 -W 1 " + url
//        };
        //Process process =  Runtime.getRuntime().exec(pingCommand);
        //Process process =  Runtime.getRuntime().exec(String.format(Locale.getDefault(), "/system/bin/ping -c 1 -W 1 %1$s  | grep 'icmp_seq'", url));
        Process process =  Runtime.getRuntime().exec(String.format(Locale.getDefault(), "/system/bin/ping -c 1 -W 1 %1$s", url));

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        int i;
        char[] buffer = new char[4096];


        StringBuilder output = new StringBuilder();
        while ((i = reader.read(buffer)) > 0)
            output.append(buffer, 0, i);
        reader.close();

        str = output.toString();
        //Log.d(TAG, "PING str:" + str);

        str = str.substring(str.indexOf("time=")+5, str.indexOf("ms")-1);
        pingDelay =  Float.parseFloat(str);

        return pingDelay;
    }


    @Override
    protected void onProgressUpdate(Float... values) {
        PingWidgetData data = SharedPreferencesHelper.readPingWidgetData(mAppContext.getApplicationContext(), mWidgetId);

        if(data != null) {
            data.getPingTimes().addLast(values[0]);
            if(data.getPingTimes().size() > data.getMaxPings())
                data.getPingTimes().removeFirst();
            SharedPreferencesHelper.writePingWidgetData(mAppContext.getApplicationContext(), mWidgetId, data);

            RemoteViews views = new RemoteViews(mAppContext.getPackageName(), R.layout.widget_layout);
            Util.redrawWidgetChart(mAppContext, views, mWidgetId, data.getPingTimes(), data.getMaxPings(), WidgetTheme.valueOf(data.getThemeName()).getColorChart());
            AppWidgetManager.getInstance(mAppContext).updateAppWidget(mWidgetId, views);

        } else {
            //Widget was probably destroyed while running, kill this AsyncTask.
            cancel(true);
            Log.d(TAG, "Widget " + mWidgetId + " gone.. Canceling PingAsyncTask");
        }
    }


}
