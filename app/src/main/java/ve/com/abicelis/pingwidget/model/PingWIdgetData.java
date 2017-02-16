package ve.com.abicelis.pingwidget.model;

import java.util.LinkedList;
import java.util.Locale;

/**
 * Created by abice on 9/2/2017.
 */

public class PingWidgetData {

    private String mAddress;
    private int mPingInterval;
    private int mMaxPings;
    private String mThemeName;
    private LinkedList<Float> mPingTimes;

    private boolean isRunning;


    public PingWidgetData(String address, int pingInterval, int maxPings, String themeName) {
        mAddress = address;
        mPingInterval = pingInterval;
        mMaxPings = maxPings;
        mThemeName = themeName;

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


    public int getMaxPings() {
        return mMaxPings;
    }

    public void setMaxPings(int mMaxPings) {
        this.mMaxPings = mMaxPings;
    }


    public String getThemeName() {
        return mThemeName;
    }

    public void setThemeName(String mThemeName) {
        this.mThemeName = mThemeName;
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
        return String.format(Locale.getDefault(),  "isRunning?: %1$b. Address: %2$s. PingInterval: %3$d. MaxPings: %4$d. Theme: %5$s.", isRunning, mAddress, mPingInterval, mMaxPings, mThemeName);
    }
}
