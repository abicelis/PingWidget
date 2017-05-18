package ve.com.abicelis.pingwidget.enums;

import android.content.Context;

import java.util.Locale;

import ve.com.abicelis.pingwidget.R;

/**
 * Created by abice on 3/5/2017.
 */

public enum MaxPingsPreferenceType {
    MAX_PINGS_5(5),
    MAX_PINGS_10(10),
    MAX_PINGS_15(15),
    MAX_PINGS_20(20),
    MAX_PINGS_25(25),
    MAX_PINGS_30(30),
    MAX_PINGS_40(40),
    MAX_PINGS_50(50),
    MAX_PINGS_75(75),
    MAX_PINGS_80(80),
    MAX_PINGS_100(100)
    ;

    private int mPings;

    MaxPingsPreferenceType(int pings) {
        mPings = pings;
    }

    public int getValue() {
        return mPings;
    }


    public String getEntry(Context context) {
        return String.format(Locale.getDefault(), context.getResources().getString(R.string.enum_preference_max_ping_format), this.getValue());
    }

    public String getEntryValue() {
        return this.name();
    }


    public static CharSequence[] getEntryValues() {
        CharSequence entries[] = new CharSequence[MaxPingsPreferenceType.values().length];

        for (int i = 0; i < MaxPingsPreferenceType.values().length; i++) {
            entries[i] = MaxPingsPreferenceType.values()[i].name();
        }
        return entries;
    }

    public static CharSequence[] getEntries(Context context) {
        CharSequence entries[] = new CharSequence[MaxPingsPreferenceType.values().length];
        String format = context.getResources().getString(R.string.enum_preference_max_ping_format);

        for (int i = 0; i < MaxPingsPreferenceType.values().length; i++) {
            entries[i] = String.format(Locale.getDefault(), format, MaxPingsPreferenceType.values()[i].getValue());
        }
        return entries;
    }

    public static boolean isValidEntry(String entryString) {
        try {
            MaxPingsPreferenceType.valueOf(entryString);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
