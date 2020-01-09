package ve.com.abicelis.pingwidget.model;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import java.util.LinkedList;

import ve.com.abicelis.pingwidget.enums.MaxPingsPreferenceType;
import ve.com.abicelis.pingwidget.enums.PingIntervalPreferenceType;
import ve.com.abicelis.pingwidget.enums.WidgetLayoutType;
import ve.com.abicelis.pingwidget.enums.WidgetTheme;

/**
 * Created by abice on 9/2/2017.
 */

public class PingWidgetData {

    private String mAddress;
    private PingIntervalPreferenceType mPingInterval;
    private MaxPingsPreferenceType mMaxPings;
    private boolean mChartLines;
    private boolean mUseDarkTheme;
    private WidgetTheme mTheme;
    private LinkedList<Float> mPingTimes;

    private WidgetLayoutType mWidgetLayoutType;
    private boolean isRunning;


    public PingWidgetData(@NonNull String address, @NonNull PingIntervalPreferenceType pingInterval, @NonNull MaxPingsPreferenceType maxPings, @NonNull boolean showChartLines, @NonNull boolean useDarkTheme, @NonNull WidgetTheme theme) {
        mAddress = address;
        mPingInterval = pingInterval;
        mMaxPings = maxPings;
        mChartLines = showChartLines;
        mUseDarkTheme = useDarkTheme;
        mTheme = theme;

        mPingTimes = new LinkedList<>();
        isRunning = false;
        mWidgetLayoutType = WidgetLayoutType.TALL;    //Default layout
    }


    public String getAddress() {
        return mAddress;
    }
    public void setAddress(@NonNull String mAddress) {
        this.mAddress = mAddress;
    }


    public PingIntervalPreferenceType getPingInterval() {
        return mPingInterval;
    }
    public void setPingInterval(@NonNull PingIntervalPreferenceType pingInterval) {
        this.mPingInterval = pingInterval;
    }


    public MaxPingsPreferenceType getMaxPings() {
        return mMaxPings;
    }
    public void setMaxPings(@NonNull MaxPingsPreferenceType mMaxPings) {
        this.mMaxPings = mMaxPings;
    }


    public boolean showChartLines() {
        return mChartLines;
    }
    public void setShowChartLines(@NonNull boolean in) {
        mChartLines = in;
    }


    public WidgetLayoutType getWidgetLayoutType() {
        return mWidgetLayoutType;
    }
    public void setWidgetLayoutType(@NonNull WidgetLayoutType widgetLayoutType) {
        this.mWidgetLayoutType = widgetLayoutType;
    }

    public boolean useDarkTheme() {
        return mUseDarkTheme;
    }
    public void setUseDarkTheme(@NonNull boolean useDarkTheme) {
        this.mUseDarkTheme = useDarkTheme;
    }


    public WidgetTheme getTheme() {
        return mTheme;
    }
    public void setTheme(@NonNull WidgetTheme theme) {
        this.mTheme = theme;
    }


    public LinkedList<Float> getPingTimes() {
        return mPingTimes;
    }
    public void setPingTimes(@NonNull LinkedList<Float> mPingTimes) {
        this.mPingTimes = mPingTimes;
    }


    public void toggleRunning() {
        isRunning = !isRunning;
    }
    public boolean isRunning() {
        return isRunning;
    }


    @Override
    public String toString() {
        String str = "";
        str += "Address: " + (mAddress != null ? mAddress : "NULL");
        str += ". PingInterval: " + (mPingInterval != null ? mPingInterval.name() : "NULL");
        str += ". MaxPings: " + (mMaxPings != null ? mMaxPings.name() : "NULL");
        str += ". WidgetLayout: " + (mWidgetLayoutType != null ? mWidgetLayoutType.name() : "NULL");
        str += ". Theme: " + (mTheme != null ? mTheme.name() : "NULL");
        str += ". DarkTheme: " +  mUseDarkTheme;
        str += ". ChartLines: " +  mChartLines;
        str += ". isRunning?: " +  isRunning;

        return str;
    }
}
