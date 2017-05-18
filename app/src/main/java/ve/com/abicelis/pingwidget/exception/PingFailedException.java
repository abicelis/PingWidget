package ve.com.abicelis.pingwidget.exception;

import ve.com.abicelis.pingwidget.enums.PingFailureType;

/**
 * Created by abice on 18/5/2017.
 */

public class PingFailedException extends Exception {

    PingFailureType mPingFailureType;

    public PingFailedException(PingFailureType pingFailureType) {
        super(pingFailureType.name());
        mPingFailureType = pingFailureType;
    }
    public PingFailedException(PingFailureType pingFailureType, Throwable cause) {
        super(pingFailureType.name(), cause);
        mPingFailureType = pingFailureType;
    }

    public PingFailureType getPingFailureType() {
        return mPingFailureType;
    }
}
