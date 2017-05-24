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

import ve.com.abicelis.pingwidget.enums.PingFailureType;
import ve.com.abicelis.pingwidget.enums.PingIconState;
import ve.com.abicelis.pingwidget.exception.PingFailedException;
import ve.com.abicelis.pingwidget.model.PingWidgetData;
import ve.com.abicelis.pingwidget.util.RemoteViewsUtil;
import ve.com.abicelis.pingwidget.util.SharedPreferencesHelper;

import static ve.com.abicelis.pingwidget.enums.PingIconState.PING_BAD;
import static ve.com.abicelis.pingwidget.enums.PingIconState.PING_OK;
import static ve.com.abicelis.pingwidget.enums.PingIconState.PING_SENT;

/**
 * Created by abice on 7/2/2017.
 */

class PingAsyncTask extends AsyncTask<String, Object, Integer> {

    //CONST
    private static final int MILLIS = 1000;
    private static final String TAG = PingAsyncTask.class.getSimpleName();

    //DATA
    private Context mAppContext;
    private AppWidgetManager mAppWidgetManager;
    private int mWidgetId;

    public PingAsyncTask(Context appContext, int widgetId) {

        mAppContext = appContext;
        mWidgetId = widgetId;
        mAppWidgetManager = AppWidgetManager.getInstance(appContext);
    }



    @Override
    protected Integer doInBackground(String... strings) {
        while (!isCancelled()) {
            PingWidgetData data = SharedPreferencesHelper.readPingWidgetData(mAppContext, mWidgetId);

            if(data == null)
                cancel(true);

            try {
                publishProgress(PING_SENT);


                //float pingDelay = ping(strings[0]);
                float pingDelay = ping(data.getAddress());
                Log.d(TAG, "PING SUCCESS (" + pingDelay + "ms)");

                publishProgress(PING_OK, pingDelay);

            }catch (PingFailedException e) {
                Log.d(TAG, "PING FAILED. FailureType=" + e.getPingFailureType().name());
                e.printStackTrace();
                publishProgress(PING_BAD, e.getPingFailureType().getErrorId());
            }

            try {
                Thread.sleep(data.getPingInterval().getValue()*MILLIS);
            } catch (InterruptedException e) {
                return -1;
            }

        }
        return -1;
    }


    private float ping(String url) throws PingFailedException {
        String str;

        //TODO: Figure this out: reader.read(buffer) takes way too long, solution is using grep to minimize output but it gives
        //TODO: permission errors; "java.io.IOException: Error running exec(). Command: [bash, -c, /system/bin/ping -c 1 -W 1 www.google.com] Working Directory: null Environment: null"
//        String[] pingCommand = {
//                "/bin/sh",
//                "-c",
//                "/system/bin/ping -c 1 -W 1 " + url
//        };
        //Process process =  Runtime.getRuntime().exec(pingCommand);
        //Process process =  Runtime.getRuntime().exec(String.format(Locale.getDefault(), "/system/bin/ping -c 1 -W 1 %1$s  | grep 'icmp_seq'", url));

        try {
            Process process =  Runtime.getRuntime().exec(String.format(Locale.getDefault(), "/system/bin/ping -c 1 -W 1 %1$s", url));
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

//            InetAddress address0 = InetAddress.getByName("www.google.com");
//            InetAddress address = InetAddress.getByName("http://www.google.com");
//            InetAddress address2 = InetAddress.getByName("http://google.com");
//
//            String str0 = address0.getHostAddress();
//            String str1 = address.getHostAddress();
//            String str2 = address2.getHostAddress();
//
//            Log.d(TAG, "Str1 = " + str1 + " str2=" + str2);


            int i;
            char[] buffer = new char[4096];

            StringBuilder output = new StringBuilder();
            while ((i = reader.read(buffer)) > 0)
                output.append(buffer, 0, i);
            reader.close();

            str = output.toString();
        } catch (IOException e) {
            throw new PingFailedException(PingFailureType.COULD_NOT_PING);
        }

        //Ping process done, checking result
        Log.d(TAG, "PING str:" + str);

        //Try to get ping time, if ping was successful
        try {
            str = str.substring(str.indexOf("time=")+5, str.indexOf("ms")-1);
            return Float.parseFloat(str);
        } catch (Exception e) { /* Do nothing */}

        //Try to figure out ping error type
        if(PingFailureType.DESTINATION_PORT_UNREACHABLE.checkPingString(str))
            throw new PingFailedException(PingFailureType.DESTINATION_PORT_UNREACHABLE);

        if(PingFailureType.DESTINATION_HOST_UNREACHABLE.checkPingString(str))
            throw new PingFailedException(PingFailureType.DESTINATION_HOST_UNREACHABLE);

        //TODO: Other errors...


        throw new PingFailedException(PingFailureType.UNKNOWN_ERROR);
    }


    @Override
    protected void onProgressUpdate(Object... values) {

        //Get widget data, check if exists
        PingWidgetData data = SharedPreferencesHelper.readPingWidgetData(mAppContext.getApplicationContext(), mWidgetId);
        if(data == null) {
            //Widget was probably destroyed while running, kill this AsyncTask.
            cancel(true);
            Log.d(TAG, "Widget " + mWidgetId + " gone.. Canceling PingAsyncTask");
            return;
        }

        //Get remote views
        RemoteViews views = RemoteViewsUtil.getRemoteViews(mAppContext, data.getWidgetLayoutType());

        //Update ping icon
        RemoteViewsUtil.updatePingIcon(views, (PingIconState) values[0]);

        //If update is PING_SENT, update widget views and leave
        if(values[0] == PingIconState.PING_SENT) {
            AppWidgetManager.getInstance(mAppContext).updateAppWidget(mWidgetId, views);
            return;
        }

        //Update is PING_OK or PING_BAD, Update chart:

        //Add latest ping value
        data.getPingTimes().addLast((float)values[1]);
        while(data.getPingTimes().size() > data.getMaxPings().getValue()) {
            data.getPingTimes().removeFirst();
        }

        //Save widget data
        SharedPreferencesHelper.writePingWidgetData(mAppContext.getApplicationContext(), mWidgetId, data);

        //Redraw widget and chart
        RemoteViewsUtil.redrawWidget(mAppContext, views, data.getPingTimes(), data.getMaxPings().getValue(), data.getTheme().getChartColor(), data.showChartLines());

        //Update widget views
        AppWidgetManager.getInstance(mAppContext).updateAppWidget(mWidgetId, views);

    }

}
