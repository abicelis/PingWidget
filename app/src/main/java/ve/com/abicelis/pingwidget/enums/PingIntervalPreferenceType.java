package ve.com.abicelis.pingwidget.enums;

import android.content.Context;

import java.util.Locale;

import ve.com.abicelis.pingwidget.R;

/**
 * Created by abice on 3/5/2017.
 */

public enum PingIntervalPreferenceType {
    INTERVAL_1(1),
    INTERVAL_2(2),
    INTERVAL_3(3),
    INTERVAL_5(5),
    INTERVAL_10(10),
    INTERVAL_20(20),
    INTERVAL_30(30),
    INTERVAL_60(60)
    ;

    private int mInterval;

    PingIntervalPreferenceType(int interval) {
        mInterval = interval;
    }

    public int getInterval() {
        return mInterval;
    }


    public String getEntry(Context context) {
        String format = (this == PingIntervalPreferenceType.INTERVAL_1 ? context.getResources().getString(R.string.enum_preference_ping_interval_format_singular) : context.getResources().getString(R.string.enum_preference_ping_interval_format));
        return String.format(Locale.getDefault(), format, this.getInterval());
    }

    public String getEntryValue() {
        return this.name();
    }


    public static CharSequence[] getEntryValues() {
        CharSequence entries[] = new CharSequence[PingIntervalPreferenceType.values().length];

        for (int i = 0; i < PingIntervalPreferenceType.values().length; i++) {
            entries[i] = PingIntervalPreferenceType.values()[i].name();
        }
        return entries;
    }

    public static CharSequence[] getEntries(Context context) {
        CharSequence entries[] = new CharSequence[PingIntervalPreferenceType.values().length];

        for (int i = 0; i < PingIntervalPreferenceType.values().length; i++) {
            String format = (PingIntervalPreferenceType.values()[i] == PingIntervalPreferenceType.INTERVAL_1 ? context.getResources().getString(R.string.enum_preference_ping_interval_format_singular) : context.getResources().getString(R.string.enum_preference_ping_interval_format));

            entries[i] = String.format(Locale.getDefault(), format, PingIntervalPreferenceType.values()[i].getInterval());
        }
        return entries;
    }

    public static boolean isValidEntry(String entryString) {
        try {
            PingIntervalPreferenceType.valueOf(entryString);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
