package ve.com.abicelis.pingwidget.model;

import java.util.LinkedList;

/**
 * Created by abice on 9/2/2017.
 */

public class PingWidgetData {

    private String mAddress;
    private int mPingInterval;
    private int mColor;
    private int mMaxPings;
    private LinkedList<Float> mPingTimes;

    private boolean isRunning;


    public PingWidgetData(String address, int pingInterval, int color, int maxPings) {
        mAddress = address;
        mPingInterval = pingInterval;
        mColor = color;
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


    public int getColor() {
        return mColor;
    }

    public void setColor(int mColor) {
        this.mColor = mColor;
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
