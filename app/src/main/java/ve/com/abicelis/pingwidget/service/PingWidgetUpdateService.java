package ve.com.abicelis.pingwidget.service;


import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import java.util.HashMap;
import java.util.Map;

import ve.com.abicelis.pingwidget.R;
import ve.com.abicelis.pingwidget.app.widget.PingWidgetProvider;
import ve.com.abicelis.pingwidget.model.PingWidgetData;
import ve.com.abicelis.pingwidget.util.RemoteViewsUtil;
import ve.com.abicelis.pingwidget.util.SharedPreferencesHelper;
import ve.com.abicelis.pingwidget.util.Util;

/**
 * Created by abice on 6/2/2017.
 */

public class PingWidgetUpdateService extends Service {
    private static String TAG = PingWidgetUpdateService.class.getSimpleName();
    private static Map<Integer,PingAsyncTask> mAsyncTasks = new HashMap<>();
    private static BroadcastReceiver mReceiver;



    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind()");
        return null;
    }



    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        super.onCreate();

        //Register ScreenReceiver to screen on/off broadcasts
        registerReceiver(mReceiver = new ScreenReceiver(), ScreenReceiver.filter);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");

        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");

        if(intent != null) {
            //Try to get extras
            String screenState = intent.getStringExtra(ScreenReceiver.SCREEN_STATE);
            int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

            //Check if intent is coming from ScreenReceiver
            if(screenState != null)
                handleScreenState(screenState);

            //Check if intent came from a Widget click
            else if (widgetId != AppWidgetManager.INVALID_APPWIDGET_ID)
                handleWidgetClick(widgetId);

            else
                Log.d(TAG, "Error: Got an unknown onStartCommand() in PingWidgetUpdateService");
        }

        // If service get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged() " + newConfig.toString());

        //Called when screen is rotated, phone charging state changes
        //The widget gets reset, so we need to reconfigure some things

        //Get AppWidgetManager
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());

        // Get all widget ids
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(getApplication(), PingWidgetProvider.class));

        for (int widgetId : allWidgetIds) {

            //Get widget data
            PingWidgetData data = SharedPreferencesHelper.readPingWidgetData(getApplicationContext(), widgetId);
            if(data != null) {

                //Get RemoteViews
                RemoteViews views = RemoteViewsUtil.getRemoteViews(getApplicationContext(), data.getWidgetLayoutType());

                //Update widget views
                RemoteViewsUtil.initWidgetViews(getApplicationContext(), views, data);

                //Register an Intent so that onClicks on the widget are received by PingWidgetProvider.onReceive()
                //Create an Intent, set PING_WIDGET_TOGGLE action to it, put EXTRA_APPWIDGET_ID as extra
                Util.registerWidgetStartPauseOnClickListener(getApplication(), widgetId, views);
                Util.registerWidgetReconfigureClickListener(getApplication(), widgetId, views);

                //Update the widget
                appWidgetManager.updateAppWidget(widgetId, views);
            }

        }
        super.onConfigurationChanged(newConfig);
    }

    private void handleScreenState(String screenState) {
        Log.d(TAG, "handleScreenState() " + screenState);

        //If screen has just been turned off, stop all threads and clear mAsyncTasks
        if(Intent.ACTION_SCREEN_OFF.equals(screenState)){
            Log.d(TAG, "handleScreenState() ACTION_SCREEN_OFF");

            for (Map.Entry<Integer, PingAsyncTask> entry : mAsyncTasks.entrySet()) {
                Log.d(TAG, "handleScreenState() Shutting down Widget " + entry.getKey());

                //Stop asyncTask
                if(entry == null || entry.getKey() == null || entry.getValue() == null)
                    Log.d(TAG, "ERROR: Could not find asyncTask to cancel! ID=" + entry.getKey());
                else if(!entry.getValue().isCancelled())
                    entry.getValue().cancel(false);


                //Get AppWidgetManager and RemoteViews
                PingWidgetData data = SharedPreferencesHelper.readPingWidgetData(getApplicationContext(), entry.getKey());

                if(data != null) {
                    RemoteViews views = RemoteViewsUtil.getRemoteViews(getApplicationContext(), data.getWidgetLayoutType());

                    //Change widget's start_pause icon to pause
                    views.setImageViewResource(R.id.widget_start_pause, android.R.drawable.ic_media_play);

                    //Finally, update the widget
                    AppWidgetManager.getInstance(getApplicationContext()).updateAppWidget(entry.getKey(), views);

                    //Toggle toggleRunning() so that it's isRunning() is false.
                    data.toggleRunning();
                    SharedPreferencesHelper.writePingWidgetData(getApplicationContext(), entry.getKey(), data);
                }
            }
            mAsyncTasks.clear();
        }
    }

    private void handleWidgetClick(int widgetId) {
        //Get widget data from SharedPreferences
        PingWidgetData currentWidget = SharedPreferencesHelper.readPingWidgetData(getApplicationContext(), widgetId);


        PingAsyncTask task = mAsyncTasks.get(widgetId);
        if(currentWidget.isRunning()) {
            if(task != null && !task.isCancelled()) {
                Log.d(TAG, "ERROR: Task already running when trying to create a new one! ID=" + widgetId);
                task.cancel(true);

            } else {

                //Set widget_press_start to GONE
                PingWidgetData data = SharedPreferencesHelper.readPingWidgetData(getApplicationContext(), widgetId);

                if(data != null) {
                    RemoteViews views = RemoteViewsUtil.getRemoteViews(getApplicationContext(), data.getWidgetLayoutType());

                    views.setViewVisibility(R.id.widget_press_start, View.GONE);
                    AppWidgetManager.getInstance(getApplicationContext()).updateAppWidget(widgetId, views);

                    //Create a new task and run it, also save it to mAsyncTasks using widgetId
                    task = new PingAsyncTask(this.getApplicationContext(), widgetId);
                    mAsyncTasks.put(widgetId, task);

                    //Get stored address from PreferenceSettings
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, currentWidget.getAddress());
                }
            }
        } else {
            mAsyncTasks.remove(widgetId);

            if(task == null)
                Log.d(TAG, "ERROR: Could not find asyncTask to cancel! ID=" + widgetId);
            else if(!task.isCancelled())
                task.cancel(false);
        }
    }

}
