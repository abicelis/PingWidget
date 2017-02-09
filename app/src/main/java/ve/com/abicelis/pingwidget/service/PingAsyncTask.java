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

/**
 * Created by abice on 7/2/2017.
 */

class PingAsyncTask extends AsyncTask<String, Float, Integer> {

    //CONST
    private static final int MILLIS = 1000;
    private static int SLEEP_TIME = 1000;
    private static final String TAG = PingAsyncTask.class.getSimpleName();

    //DATA
    private Context mAppContext;
    private AppWidgetManager mAppWidgetManager;
    private int mWidgetId;

    public PingAsyncTask(Context appContext, int widgetId, int pingInterval) {
        Log.d(TAG, "constructor()");

        mAppContext = appContext;
        mWidgetId = widgetId;
        SLEEP_TIME = pingInterval * MILLIS;
        mAppWidgetManager = AppWidgetManager.getInstance(appContext);
    }



    @Override
    protected Integer doInBackground(String... strings) {
        while (!isCancelled()) {
            Log.d(TAG, "doInBackground() loop");


            try {
                float pingDelay = ping(strings[0]);
                publishProgress(pingDelay);

                Log.d(TAG, "PING =" + pingDelay);
            }catch (Exception e) {
                Log.d(TAG, "doInBackground() EX!");
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

        try {
            Process process = Runtime.getRuntime().exec(String.format(Locale.getDefault(), "/system/bin/ping -c 1 %1$s ", url));
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            int i;
            char[] buffer = new char[4096];
            StringBuilder output = new StringBuilder();
            while ((i = reader.read(buffer)) > 0)
                output.append(buffer, 0, i);
            reader.close();

            str = output.toString();
            str = str.substring(str.indexOf("time=")+5, str.indexOf("ms")-1);
            pingDelay =  Float.parseFloat(str);
        } catch (IOException e) {
            pingDelay = -1;
        }
        return pingDelay;
    }


    @Override
    protected void onProgressUpdate(Float... values) {
        Log.d(TAG, "onProgressUpdate(). Value = " + values[0]);

        updateWidget(values[0]);

    }

    private void updateWidget(Float pingDelay) {

        //Get the RemoteViews of Widget
        RemoteViews remoteViews = new RemoteViews(mAppContext.getPackageName(), R.layout.widget_layout);

        // Set the text
        remoteViews.setTextViewText(R.id.widget_max_ping_value, String.valueOf(pingDelay));

        //Update widget
        mAppWidgetManager.updateAppWidget(mWidgetId, remoteViews);
    }




}
