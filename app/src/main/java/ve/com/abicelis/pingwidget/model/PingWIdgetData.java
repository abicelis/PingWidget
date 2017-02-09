package ve.com.abicelis.pingwidget.model;

import java.util.List;

/**
 * Created by abice on 9/2/2017.
 */

public class PingWidgetData {

    private String mAddress;
    private int mPingInterval;
    private int mColor;
    private List<Float> mPingTimes;

    private boolean isRunning;


    public PingWidgetData(String address, int pingInterval, int color, List<Float> pingTimes) {
        mAddress = address;
        mPingInterval = pingInterval;
        mColor = color;
        mPingTimes = pingTimes;
        isRunning = false;
    }
    public PingWidgetData(String address, int pingInterval, int color) {
        this(address, pingInterval, color, null);
    }


    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String mAddress) {
        this.mAddress = mAddress;
    }


    public int getmPingInterval() {
        return mPingInterval;
    }

    public void setmPingInterval(int pingInterval) {
        this.mPingInterval = pingInterval;
    }


    public int getColor() {
        return mColor;
    }

    public void setColor(int mColor) {
        this.mColor = mColor;
    }


    public List<Float> getPingTimes() {
        return mPingTimes;
    }

    public void setPingTimes(List<Float> mPingTimes) {
        this.mPingTimes = mPingTimes;
    }


    public void toggleRunning() {
        isRunning = !isRunning;
    }

    public boolean isRunning() {
        return isRunning;
    }

}
