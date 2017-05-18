package ve.com.abicelis.pingwidget.enums;

import android.content.Context;
import android.support.annotation.StringRes;

import java.security.InvalidParameterException;

import ve.com.abicelis.pingwidget.R;

/**
 * Created by abice on 18/5/2017.
 */

public enum PingFailureType {
    COULD_NOT_PING("", -1f, R.string.enum_ping_failure_type_could_not_ping),
    UNKNOWN_ERROR("", -2f, R.string.enum_ping_failure_type_unknown_error),
    DESTINATION_PORT_UNREACHABLE("Destination Port Unreachable", -3f, R.string.enum_ping_failure_type_destination_port_unreachable),
    DESTINATION_HOST_UNREACHABLE("Destination Host Unreachable", -4f, R.string.enum_ping_failure_type_destination_host_unreachable),
    ;

    private String mPattern;
    private float mErrorId;
    private int mErrorStringRes;

    PingFailureType(String pattern, float errorId, @StringRes int errorStringRes) {
        mPattern = pattern;
        mErrorId = errorId;
        mErrorStringRes = errorStringRes;
    }


    public boolean checkPingString(String str) {
        boolean asd = str.contains(this.mPattern);

        return str.contains(this.mPattern);
    }

    public float getErrorId() {
        return mErrorId;
    }

    public String getErrorString(Context context) {
        return context.getResources().getString(mErrorStringRes);
    }

    public static PingFailureType getFromErrorId(int errorId) {
        for (PingFailureType type : PingFailureType.values()) {
            if(errorId == type.getErrorId())
                return type;
        }
        throw new InvalidParameterException("Invalid errorId");
    }


}
