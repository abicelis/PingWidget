package ve.com.abicelis.pingwidget.util;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.widget.RemoteViews;

import java.util.LinkedList;
import java.util.Locale;

import ve.com.abicelis.pingwidget.R;
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
        clickIntent.setAction(PingWidgetProvider.PING_WIDGET_TOGGLE);

        //Construct a PendingIntent using the Intent above
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, widgetId, clickIntent, 0);

        //Register pendingIntent in RemoteViews onClick
        views.setOnClickPendingIntent(R.id.widget_start_pause, pendingIntent);
    }


    public static void redrawWidgetChart(Context context, RemoteViews remoteViews, int widgetId, LinkedList<Float> values, int maxPings, int chartLineColor) {

        //Calculate min/max/avg
        float avg = 0.0f;
        float max = 0.0f;
        float min = Float.MAX_VALUE;
        float badPingCount = 0f;

        if(values.size() > 0) {
            for (float val : values) {
                if(val != -1) {     //skip unsuccessful pings
                    avg += val;

                    if (val > max)
                        max = val;
                    if (val < min)
                        min = val;
                }
                else
                    badPingCount++;

            }
            avg = avg/values.size();

            if(badPingCount == values.size()) {     //all pings are bad
                remoteViews.setTextViewText(R.id.widget_max_min_ping, context.getResources().getString(R.string.widget_max_min_err));
                remoteViews.setTextViewText(R.id.widget_last_ping, context.getResources().getString(R.string.widget_last_err));
                remoteViews.setTextViewText(R.id.widget_uptime_ping, context.getResources().getString(R.string.widget_uptime_err));
                remoteViews.setTextViewText(R.id.widget_avg_ping, context.getResources().getString(R.string.widget_avg_err));

            } else {                                //at least one ping is good
                float aux = badPingCount/values.size();
                int uptime = (badPingCount == 0 ? 100 : (int)((1-aux)*100) );

                String maxStr = (max != 0f ? String.format(Locale.getDefault(), "%.1f", max) : "-");
                String minStr = (min != Float.MAX_VALUE ? String.format(Locale.getDefault(), "%.1f", min) : "-");
                String lastStr = (values.peekLast() != -1f ? String.format(Locale.getDefault(), "%.1f", values.peekLast()) : "ERR");
                String avgStr = (avg != 0f ? String.format(Locale.getDefault(), "%.1f", avg) : "-");

                remoteViews.setTextViewText(R.id.widget_max_min_ping, String.format(Locale.getDefault(), context.getResources().getString(R.string.widget_max_min), maxStr, minStr));
                remoteViews.setTextViewText(R.id.widget_last_ping, String.format(Locale.getDefault(), context.getResources().getString(R.string.widget_last), lastStr));
                remoteViews.setTextViewText(R.id.widget_uptime_ping, String.format(Locale.getDefault(), context.getResources().getString(R.string.widget_uptime), uptime));
                remoteViews.setTextViewText(R.id.widget_avg_ping, String.format(Locale.getDefault(), context.getResources().getString(R.string.widget_avg), avgStr));

                drawChart(context, remoteViews, max, min, maxPings, values, chartLineColor);
            }

        } else {
            remoteViews.setTextViewText(R.id.widget_max_min_ping, "-");
            remoteViews.setTextViewText(R.id.widget_last_ping, "-");
            remoteViews.setTextViewText(R.id.widget_uptime_ping, "-");
            remoteViews.setTextViewText(R.id.widget_avg_ping, "-");
        }

    }

    private static void drawChart(Context context, RemoteViews views, float max, float min, int maxValueCount, LinkedList<Float> values, int chartLineColor) {

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
        textPaint.setColor(ContextCompat.getColor(context, R.color.chart_text_paint));

        Paint linesPaint = new Paint();
        linesPaint.setAntiAlias(true);
        linesPaint.setStyle(Paint.Style.STROKE);
        linesPaint.setStrokeWidth(4);
        linesPaint.setColor(ContextCompat.getColor(context, chartLineColor));

        Paint pointsPaint = new Paint();
        pointsPaint.setAntiAlias(true);
        pointsPaint.setStyle(Paint.Style.STROKE);
        pointsPaint.setStrokeWidth(8);
        pointsPaint.setColor(ContextCompat.getColor(context, chartLineColor));

        Paint pingFailedPaint = new Paint();
        pingFailedPaint.setAntiAlias(true);
        pingFailedPaint.setStyle(Paint.Style.STROKE);
        pingFailedPaint.setStrokeWidth(8);
        pingFailedPaint.setColor(ContextCompat.getColor(context, R.color.chart_ping_failed_paint));

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

        views.setImageViewBitmap(R.id.widget_chart, bitmap);
    }

    private static float calcY(float val, float max, float min, int chartHeight) {
        float aux;

        aux = (val-min) / (max-min);    //Get proportion (0-1) against min/max
        aux = 1 - aux;                  //Invert that proportion
        return (aux * chartHeight);     //Get height according to chartHeight
    }

    private static boolean pingOk(float val){
        return val != -1f;  //if val == -1, ping was bad
    }

}
