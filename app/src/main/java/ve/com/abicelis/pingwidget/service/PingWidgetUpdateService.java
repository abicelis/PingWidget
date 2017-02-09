package ve.com.abicelis.pingwidget.service;


import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

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
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");

        //Get widgetId extra
        int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        boolean shouldRun = intent.getBooleanExtra(PingWidgetUpdateService.SHOULD_RUN, false);


        PingAsyncTask task = mAsyncTasks.get(widgetId);
        if(shouldRun) {
            if(task != null && !task.isCancelled()) {
                Log.d(TAG, "ERROR: Task already running when trying to create a new one! ID=" + widgetId);
                task.cancel(true);

            } else {
                //Create a new task and run it, also save it to mAsyncTasks using widgetId
                task = new PingAsyncTask(this.getApplicationContext(), widgetId);
                mAsyncTasks.put(widgetId, task);

                //Get stored ip from somewhere, preferenceSettings?, AppWidget Settings activity? A bundle?
                String ipToPing = "www.google.com";
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ipToPing);
            }
        } else {
            mAsyncTasks.remove(widgetId);

            if(task == null)
                Log.d(TAG, "ERROR: Could not find asyncTask to cancel! ID=" + widgetId);
            else if(!task.isCancelled())
                task.cancel(false);
        }




//        //Get the RemoteViews of Widget
//        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.widget_layout);
//
//        // Set the text
//        remoteViews.setTextViewText(R.id.widget_max_ping_value, String.valueOf(500) + ".0");
//
//        //Update widget
//        AppWidgetManager.getInstance(this.getApplicationContext()).updateAppWidget(mWidgetId, remoteViews);

//        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());
//        int[] allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
//
//        for (int widgetId : allWidgetIds) {
//
//
////            PingAsyncTask pingRunnable = new PingAsyncTask(this.getApplicationContext(), widgetId);
////            Thread pingThread = new Thread(pingRunnable);
////            pingThread.start();
////
////            try {
////                Thread.sleep(10000);
////            } catch (InterruptedException e) {
////
////            }
////            pingThread.interrupt();
//
//
//            RemoteViews remoteViews = new RemoteViews(this.getApplicationContext().getPackageName(), R.layout.widget_layout);
//
//            // Set the text
//            int number;
//            number = (new Random().nextInt(100));
//            remoteViews.setTextViewText(R.id.widget_max_ping_value, String.valueOf(number) + ".0");
//            number = (new Random().nextInt(100));
//            remoteViews.setTextViewText(R.id.widget_avg_ping_value, String.valueOf(number) + ".0");
//            number = (new Random().nextInt(100));
//            remoteViews.setTextViewText(R.id.widget_min_ping_value, String.valueOf(number) + ".0");
//
//
//
//
//            // Register an onClickListener
//            /*Intent clickIntent = new Intent(this.getApplicationContext(), PingWidgetProvider.class);
//
//            clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
//            clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);
//
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//            remoteViews.setOnClickPendingIntent(R.id.widget_start_pause, pendingIntent);*/
//
//
//            //Update widget
//            appWidgetManager.updateAppWidget(widgetId, remoteViews);
//        }
//
//        try {
//            Thread.sleep(100000);
//            //ping("8.8.8.8");
//        }catch (Exception e) {
//
//        }
//
//        //stopSelf();

        // If service get killed, after returning from here, restart
        return START_STICKY;
    }

}
