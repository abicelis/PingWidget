package ve.com.abicelis.pingwidget.service;


import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import java.util.HashMap;
import java.util.Map;

import ve.com.abicelis.pingwidget.R;
import ve.com.abicelis.pingwidget.model.PingWidgetData;
import ve.com.abicelis.pingwidget.util.SharedPreferencesHelper;

/**
 * Created by abice on 6/2/2017.
 */

public class PingWidgetUpdateService extends Service {
    private static String TAG = PingWidgetUpdateService.class.getSimpleName();
    public static String SHOULD_RUN = "SHOULD_RUN";
    private volatile boolean mThreadRunning = false;
    private static Map<Integer,PingAsyncTask> mAsyncTasks = new HashMap<>();



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        super.onCreate();

        //Register ScreenReceiver to screen on/off broadcasts
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        //filter.addAction(Intent.ACTION_SCREEN_ON);
        BroadcastReceiver mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");

        super.onDestroy();
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

    private void handleScreenState(String screenState) {
        Log.d(TAG, "handleScreenState()" + screenState);

        //If screen has just been turned off, stop all threads and clear mAsyncTasks
        if(Intent.ACTION_SCREEN_OFF.equals(screenState)){
            for (Map.Entry<Integer, PingAsyncTask> entry : mAsyncTasks.entrySet()) {

                //Stop asyncTask
                if(entry == null || entry.getKey() == null || entry.getValue() == null)
                    Log.d(TAG, "ERROR: Could not find asyncTask to cancel! ID=" + entry.getKey());
                else if(!entry.getValue().isCancelled())
                    entry.getValue().cancel(false);


                //Get AppWidgetManager and RemoteViews
                RemoteViews views = new RemoteViews(getApplication().getPackageName(), R.layout.widget_layout);
                //Change widget's start_pause icon to pause
                views.setImageViewResource(R.id.widget_start_pause, android.R.drawable.ic_media_play);

                //Finally, update the widget
                AppWidgetManager.getInstance(getApplicationContext()).updateAppWidget(entry.getKey(), views);

                //Get widget data, toggle toggleRunning() so that it's isRunning() is false.
                PingWidgetData currentWidget = SharedPreferencesHelper.readPingWidgetData(getApplicationContext(), entry.getKey());
                currentWidget.toggleRunning();
                SharedPreferencesHelper.writePingWidgetData(getApplicationContext(), entry.getKey(), currentWidget);


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
                RemoteViews views = new RemoteViews(getApplication().getPackageName(), R.layout.widget_layout);
                views.setViewVisibility(R.id.widget_press_start, View.GONE);
                AppWidgetManager.getInstance(getApplicationContext()).updateAppWidget(widgetId, views);

                //Create a new task and run it, also save it to mAsyncTasks using widgetId
                task = new PingAsyncTask(this.getApplicationContext(), widgetId, currentWidget.getPingInterval(), currentWidget.getMaxPings());
                mAsyncTasks.put(widgetId, task);

                //Get stored address from PreferenceSettings
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, currentWidget.getAddress());
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
