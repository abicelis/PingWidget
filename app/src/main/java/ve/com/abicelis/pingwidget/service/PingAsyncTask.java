package ve.com.abicelis.pingwidget.service;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.RemoteViews;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Locale;

import ve.com.abicelis.pingwidget.R;
import ve.com.abicelis.pingwidget.model.PingWidgetData;
import ve.com.abicelis.pingwidget.util.SharedPreferencesHelper;

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

    public PingAsyncTask(Context appContext, int widgetId, int pingInterval, int maxPings) {
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

                Log.d(TAG, "PING SUCCESS (" + pingDelay + "ms)");
                publishProgress(pingDelay);

            }catch (Exception e) {
                Log.d(TAG, "PING FAILED");
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

        Process process = Runtime.getRuntime().exec(String.format(Locale.getDefault(), "/system/bin/ping -c 1 -W 1 %1$s ", url));
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
        Log.d(TAG, "onProgressUpdate(). Value = " + values[0]);


        PingWidgetData data = SharedPreferencesHelper.readPingWidgetData(mAppContext.getApplicationContext(), mWidgetId);
        data.getPingTimes().addLast(values[0]);
        if(data.getPingTimes().size() > data.getMaxPings())
            data.getPingTimes().removeFirst();
        SharedPreferencesHelper.writePingWidgetData(mAppContext.getApplicationContext(), mWidgetId, data);


        updateWidget(data.getPingTimes(), data.getMaxPings(), data.getChartLineColor());

    }

    private void updateWidget(LinkedList<Float> values, int maxPings, int chartLineColor) {

        //Get the RemoteViews of Widget
        RemoteViews remoteViews = new RemoteViews(mAppContext.getPackageName(), R.layout.widget_layout);


        //Calculate min/max/avg
        float avg = 0.0f;
        float max = 0.0f;
        float min = Float.MAX_VALUE;

        if(values.size() > 0) {
            for (float val : values) {
                if(val != -1) {     //skip unsuccessful pings
                    avg += val;

                    if (val > max)
                        max = val;
                    if (val < min)
                        min = val;
                }

            }
            avg = avg/values.size();

            remoteViews.setTextViewText(R.id.widget_graph_max_ping_value, (max != 0f ? String.format(Locale.getDefault(), "%.1f", max) : ""));
            remoteViews.setTextViewText(R.id.widget_last_ping_value, (values.peekLast() != -1f ? String.format(Locale.getDefault(), "%.1f", values.peekLast()) : "ERR"));
            remoteViews.setTextViewText(R.id.widget_graph_max_ping_value, (max != 0f ? String.format(Locale.getDefault(), "%.1f", max) : "-"));
            remoteViews.setTextViewText(R.id.widget_max_ping_value, (max != 0f ? String.format(Locale.getDefault(), "%.1f", max) : "-"));
            remoteViews.setTextViewText(R.id.widget_min_ping_value, (min != Float.MAX_VALUE ? String.format(Locale.getDefault(), "%.1f", min) : "-"));
            remoteViews.setTextViewText(R.id.widget_avg_ping_value, (avg != 0f ? String.format(Locale.getDefault(), "%.1f", avg) : "-"));

            drawGraph(remoteViews, max, min, maxPings, values, chartLineColor);


        } else {
            remoteViews.setTextViewText(R.id.widget_graph_max_ping_value, "");
            remoteViews.setTextViewText(R.id.widget_last_ping_value, "-");
            remoteViews.setTextViewText(R.id.widget_max_ping_value, "-");
            remoteViews.setTextViewText(R.id.widget_min_ping_value, "-");
            remoteViews.setTextViewText(R.id.widget_avg_ping_value, "-");
        }

        //Update widget
        mAppWidgetManager.updateAppWidget(mWidgetId, remoteViews);
    }

    private void drawGraph(RemoteViews views, float max, float min, int maxValueCount, LinkedList<Float> values, int chartLineColor) {

        final int POINT_SIZE = 5;
        final int SQUARE_SIZE = 5;
        int canvasWidth = 500;
        int canvasHeight = 100;
        int chartPadding = 15;
        int chartWidth = canvasWidth - (2*chartPadding);
        int chartHeight = canvasHeight - (2*chartPadding);
        int chartStepX = chartWidth / (maxValueCount - 1);      //.   .   .   .   .   If maxValueCount = 5
                                                                //  ^   ^   ^   ^     There are 4 spaces in between
        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setStrokeWidth(1);
        textPaint.setColor(ContextCompat.getColor(mAppContext, R.color.graph_text_paint));

        Paint linesPaint = new Paint();
        linesPaint.setAntiAlias(true);
        linesPaint.setStyle(Paint.Style.STROKE);
        linesPaint.setStrokeWidth(4);
        //linesPaint.setColor(ContextCompat.getColor(mAppContext, R.color.graph_lines));
        linesPaint.setColor(chartLineColor);

        Paint pointsPaint = new Paint();
        pointsPaint.setAntiAlias(true);
        pointsPaint.setStyle(Paint.Style.STROKE);
        pointsPaint.setStrokeWidth(8);
        //pointsPaint.setColor(ContextCompat.getColor(mAppContext, R.color.graph_points));
        pointsPaint.setColor(chartLineColor);

        Paint pingFailedPaint = new Paint();
        pingFailedPaint.setAntiAlias(true);
        pingFailedPaint.setStyle(Paint.Style.STROKE);
        pingFailedPaint.setStrokeWidth(8);
        pingFailedPaint.setColor(ContextCompat.getColor(mAppContext, R.color.graph_ping_failed_paint));

        Bitmap bitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        //DrawBoder DEBUG!
        //canvas.drawLine(0, 0, 0, canvasHeight, p);
        //canvas.drawLine(0, canvasHeight, canvasWidth, canvasHeight, p);
        //canvas.drawLine(canvasWidth, canvasHeight, canvasWidth, 0, p);
        //canvas.drawLine(canvasWidth, 0, 0, 0, p);

        //Draw axis
        //canvas.drawLine(chartPadding, chartPadding, chartPadding, canvasHeight - chartPadding, textPaint);  //Y
        //canvas.drawText((max != 0f ? String.format(Locale.getDefault(), "%d", (int)max) : ""), 4, chartPadding, textPaint);

        int xPos = chartPadding + ((maxValueCount - values.size()) * chartStepX);
        int yPos = -1;
        int oldXPos = -1, oldYPos = -1;
        boolean pingFailedFlag = false;

        //Loop through all the values
        for (float value : values) {

            if(!pingOk(value)) {
                if(oldXPos == -1) {  //If Leftmost (oldest) ping is bad, init oldXpos to xPos, and yPos to 50% height
                    oldXPos = xPos;
                    yPos = chartPadding + chartHeight/2;
                }
                pingFailedFlag = true;  //set flag so next line draws red with pingFailedPaint
                xPos +=chartStepX;      //Advance xPos
                continue;
            }

            //Set old and current yPos vals
            oldYPos = yPos;
            yPos = (int) (chartPadding + calcY(value, max, 0, chartHeight));

            if(oldXPos != -1) {   //If not first point..
                if (pingFailedFlag) {
                    canvas.drawLine(oldXPos, (oldYPos != -1 ? oldYPos : 0), xPos, yPos, pingFailedPaint);
                    pingFailedFlag = false; //reset flag
                } else {
                    canvas.drawLine(oldXPos, oldYPos, xPos, yPos, linesPaint);
                }
            }

            //Draw point
            canvas.drawCircle(xPos, yPos, POINT_SIZE, pointsPaint);


            //Save oldXPos, move xPos
            oldXPos = xPos;
            xPos +=chartStepX;
        }

        views.setImageViewBitmap(R.id.widget_graph, bitmap);
    }

    private float calcY(float val, float max, float min, int chartHeight) {
        float aux;

        aux = (val-min) / (max-min);    //Get proportion (0-1) against min/max
        aux = 1 - aux;                  //Invert that proportion
        return (aux * chartHeight);     //Get height according to chartHeight
    }

    private boolean pingOk(float val){
        return val != -1f;  //if val == -1, ping was bad
    }

}
