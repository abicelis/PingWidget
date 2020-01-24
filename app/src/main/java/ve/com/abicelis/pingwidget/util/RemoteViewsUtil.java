package ve.com.abicelis.pingwidget.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.RemoteViews;

import java.security.InvalidParameterException;
import java.util.LinkedList;
import java.util.Locale;

import ve.com.abicelis.pingwidget.R;
import ve.com.abicelis.pingwidget.enums.PingFailureType;
import ve.com.abicelis.pingwidget.enums.PingIconState;
import ve.com.abicelis.pingwidget.enums.WidgetLayoutType;
import ve.com.abicelis.pingwidget.model.PingWidgetData;

/**
 * Created by abice on 10/5/2017.
 */

public class RemoteViewsUtil {

    /**
     * Determine appropriate view based on WidgetLayoutType.
     */
    public static RemoteViews getRemoteViews(@NonNull Context context, @NonNull WidgetLayoutType widgetLayoutType) {
        String packageName = context.getPackageName();
        int layout = widgetLayoutType.getLayout();
        RemoteViews rv = new RemoteViews(packageName, layout);
        return rv;
    }





    /*
     *  Drawing methods, remoteViews update
     */
   // public static void initWidgetViews(RemoteViews views, String address, WidgetTheme theme, WidgetLayoutType layoutType) {
    public static void initWidgetViews(Context context, RemoteViews views, PingWidgetData data) {
        views.setTextViewText(R.id.widget_host, data.getAddress());
        views.setImageViewResource(R.id.widget_start_pause, android.R.drawable.ic_media_play);
        views.setInt(R.id.widget_layout_container_top, "setBackgroundResource", data.getTheme().getDrawableBackgroundContainerTop(data.getWidgetLayoutType()));
        views.setInt(R.id.widget_layout_container, "setBackgroundResource", data.getTheme().getDrawableBackgroundContainer(data.getWidgetLayoutType(), data.useDarkTheme()));

        views.setTextColor(R.id.widget_press_start, ContextCompat.getColor(context, (data.useDarkTheme() ? R.color.text_white : R.color.text_dark_gray)));
        views.setTextColor(R.id.widget_avg_ping, ContextCompat.getColor(context, (data.useDarkTheme() ? R.color.text_white : R.color.text_dark_gray)));
        views.setTextColor(R.id.widget_last_ping, ContextCompat.getColor(context, (data.useDarkTheme() ? R.color.text_white : R.color.text_dark_gray)));
        views.setTextColor(R.id.widget_max_min_ping, ContextCompat.getColor(context, (data.useDarkTheme() ? R.color.text_white : R.color.text_dark_gray)));
        views.setTextColor(R.id.widget_uptime_ping, ContextCompat.getColor(context, (data.useDarkTheme() ? R.color.text_white : R.color.text_dark_gray)));

        views.setViewVisibility(R.id.widget_loading, View.GONE);

        //Never pinged?
        if(data.getPingTimes().size() == 0) {
            views.setViewVisibility(R.id.widget_press_start, View.VISIBLE);
            views.setImageViewResource(R.id.widget_start_pause, android.R.drawable.ic_media_play);
        } else {
            RemoteViewsUtil.redrawWidget(context, views, data.getPingTimes(), data.getMaxPingsOnChart().getValue(), data.getTheme().getChartColor(), data.showChartLines());
            views.setViewVisibility(R.id.widget_press_start, View.GONE);
            updatePlayPause(views, data.isRunning());
        }
    }

    public static void updatePingIcon(RemoteViews views, PingIconState state) {

        views.setViewVisibility(R.id.widget_ping_sent_icon, View.GONE);
        views.setViewVisibility(R.id.widget_ping_ok_icon, View.GONE);
        views.setViewVisibility(R.id.widget_ping_bad_icon, View.GONE);

        switch (state) {
            case PING_SENT:
                views.setViewVisibility(R.id.widget_ping_sent_icon, View.VISIBLE);
                //views.setInt(R.id.widget_ping_sent_icon, "setColorFilter", ContextCompat.getColor(context, R.color.ping_icon_sent));
                break;
            case PING_OK:
                views.setViewVisibility(R.id.widget_ping_ok_icon, View.VISIBLE);
                break;
            case PING_BAD:
                views.setViewVisibility(R.id.widget_ping_bad_icon, View.VISIBLE);
                break;
            case PING_HIDDEN:
            default:
                //Do nothing
                break;
        }

    }

    public static void updatePlayPause(RemoteViews views, boolean isRunning) {
        views.setImageViewResource(R.id.widget_start_pause, (isRunning ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play) );
    }






    public static void redrawWidget(Context context, RemoteViews remoteViews, LinkedList<Float> values, int maxPings, int chartLineColor, boolean showChartLines) {

        //Calculate min/max/avg
        float avg = 0.0f;
        float max = 0.0f;
        float min = Float.MAX_VALUE;
        float badPingCount = 0f;

        if(values.size() > 0) {
            for (float val : values) {
                if(val >= 0) {     //Skip unsuccessful pings
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
                remoteViews.setTextViewText(R.id.widget_last_ping, getLastStr(context, values.peekLast()));
                remoteViews.setTextViewText(R.id.widget_uptime_ping, context.getResources().getString(R.string.widget_uptime_err));
                remoteViews.setTextViewText(R.id.widget_avg_ping, context.getResources().getString(R.string.widget_avg_err));

            } else {                                //at least one ping is good
                float aux = badPingCount/values.size();
                int uptime = (badPingCount == 0 ? 100 : (int)((1-aux)*100) );

                String maxStr = (max != 0f ? String.format(Locale.getDefault(), "%.1f", max) : "-");
                String minStr = (min != Float.MAX_VALUE ? String.format(Locale.getDefault(), "%.1f", min) : "-");
                String lastStr = getLastStr(context, values.peekLast());
                String avgStr = (avg != 0f ? String.format(Locale.getDefault(), "%.1f", avg) : "-");

                remoteViews.setTextViewText(R.id.widget_max_min_ping, String.format(Locale.getDefault(), context.getResources().getString(R.string.widget_max_min), maxStr, minStr));
                remoteViews.setTextViewText(R.id.widget_last_ping, String.format(Locale.getDefault(), context.getResources().getString(R.string.widget_last), lastStr));
                remoteViews.setTextViewText(R.id.widget_uptime_ping, String.format(Locale.getDefault(), context.getResources().getString(R.string.widget_uptime), uptime));
                remoteViews.setTextViewText(R.id.widget_avg_ping, String.format(Locale.getDefault(), context.getResources().getString(R.string.widget_avg), avgStr));

                drawChart(context, remoteViews, max, avg, min, maxPings, values, chartLineColor, showChartLines);
            }

        } else {
            remoteViews.setTextViewText(R.id.widget_max_min_ping, "-");
            remoteViews.setTextViewText(R.id.widget_last_ping, "-");
            remoteViews.setTextViewText(R.id.widget_uptime_ping, "-");
            remoteViews.setTextViewText(R.id.widget_avg_ping, "-");
        }

    }

    private static void drawChart(Context context, RemoteViews views, float max, float avg, float min, int maxValueCount, LinkedList<Float> values, int chartLineColor, boolean showChartLines) {

        final int POINT_SIZE = 5;
        final int SQUARE_SIZE = 5;
        int canvasWidth = 625;  //Default 500
        int canvasHeight = 125; //Default 100
        int chartPadding = 15;
        int chartWidth = canvasWidth - (2*chartPadding);
        int chartHeight = canvasHeight - (2*chartPadding);
        int chartStepX = chartWidth / (maxValueCount - 1);      //.   .   .   .   .   If maxValueCount = 5
        //  ^   ^   ^   ^     There are 4 spaces in between
        Paint thinLinePaint = new Paint();
        thinLinePaint.setAntiAlias(true);
        thinLinePaint.setStyle(Paint.Style.STROKE);
        thinLinePaint.setStrokeWidth(2);

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

        //Draw avg line
        if(showChartLines) {
            thinLinePaint.setColor(ContextCompat.getColor(context, R.color.chart_avg_line_paint));
            int avgYPos = (int) (chartPadding + calcY(avg, max, 0, chartHeight));
            canvas.drawLine(chartPadding, avgYPos, chartPadding+chartWidth, avgYPos, thinLinePaint);

//            thinLinePaint.setColor(ContextCompat.getColor(context, R.color.chart_min_line_paint));
//            int minYPos = (int) (chartPadding + calcY(min, max, 0, chartHeight));
//            canvas.drawLine(chartPadding, minYPos, chartPadding+chartWidth, minYPos, thinLinePaint);
        }

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

    private static String getLastStr(Context context, float lastValue) {
        if(lastValue < 0) {
            try {
                return PingFailureType.getFromErrorId((int) lastValue).getErrorString(context);
            }catch (InvalidParameterException e) {
                return "ERR";
            }
        } else
            return String.format(Locale.getDefault(), "%.1f", lastValue);
    }

}
