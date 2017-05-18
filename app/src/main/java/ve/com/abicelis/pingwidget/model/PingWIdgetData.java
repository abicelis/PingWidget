package ve.com.abicelis.pingwidget.model;

import android.support.annotation.LayoutRes;

import java.util.LinkedList;
import java.util.Locale;

import ve.com.abicelis.pingwidget.R;
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


    public PingWidgetData(String address, PingIntervalPreferenceType pingInterval, MaxPingsPreferenceType maxPings, boolean showChartLines, boolean useDarkTheme, WidgetTheme theme) {
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
    public void setAddress(String mAddress) {
        this.mAddress = mAddress;
    }


    public PingIntervalPreferenceType getPingInterval() {
        return mPingInterval;
    }
    public void setPingInterval(PingIntervalPreferenceType pingInterval) {
        this.mPingInterval = pingInterval;
    }


    public MaxPingsPreferenceType getMaxPings() {
        return mMaxPings;
    }
    public void setMaxPings(MaxPingsPreferenceType mMaxPings) {
        this.mMaxPings = mMaxPings;
    }


    public boolean showChartLines() {
        return mChartLines;
    }
    public void setShowChartLines(boolean in) {
        mChartLines = in;
    }


    public WidgetLayoutType getWidgetLayoutType() {
        return mWidgetLayoutType;
    }
    public void setWidgetLayoutType(WidgetLayoutType widgetLayoutType) {
        this.mWidgetLayoutType = widgetLayoutType;
    }

    public boolean useDarkTheme() {
        return mUseDarkTheme;
    }
    public void setUseDarkTheme(boolean useDarkTheme) {
        this.mUseDarkTheme = useDarkTheme;
    }


    public WidgetTheme getTheme() {
        return mTheme;
    }
    public void setTheme(WidgetTheme theme) {
        this.mTheme = theme;
    }


    public LinkedList<Float> getPingTimes() {
        return mPingTimes;
    }
    public void setPingTimes(LinkedList<Float> mPingTimes) {
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
        return String.format(Locale.getDefault(),  "Address: %1$s. PingInterval: %2$s. MaxPings: %3$s. Theme: %4$s. DarkTheme: %5$s. ChartLines: %6$s. isRunning?: %7$b. ",
                mAddress, mPingInterval.getEntryValue(), mMaxPings.getEntryValue(), mTheme.name(), mUseDarkTheme, mChartLines, isRunning);
    }
}
