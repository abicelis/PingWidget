package ve.com.abicelis.pingwidget.model;

import java.util.LinkedList;

/**
 * Created by abice on 9/2/2017.
 */

public class PingWidgetData {

    private String mAddress;
    private int mPingInterval;
    private int mBackgroundColor;
    private int mChartLineColor;
    private int mMaxPings;
    private LinkedList<Float> mPingTimes;

    private boolean isRunning;


    public PingWidgetData(String address, int pingInterval, int backgroundColor, int chartLineColor, int maxPings) {
        mAddress = address;
        mPingInterval = pingInterval;
        mBackgroundColor = backgroundColor;
        mChartLineColor = chartLineColor;
        mMaxPings = maxPings;

        mPingTimes = new LinkedList<>();
        isRunning = false;
    }


    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String mAddress) {
        this.mAddress = mAddress;
    }


    public int getPingInterval() {
        return mPingInterval;
    }

    public void setPingInterval(int pingInterval) {
        this.mPingInterval = pingInterval;
    }


    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public void setBackgroundColor(int mColor) {
        this.mBackgroundColor = mColor;
    }


    public int getChartLineColor() {
        return mChartLineColor;
    }

    public void setChartLineColor(int mColor) {
        this.mChartLineColor = mColor;
    }


    public int getMaxPings() {
        return mMaxPings;
    }

    public void setMaxPings(int mMaxPings) {
        this.mMaxPings = mMaxPings;
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

}
