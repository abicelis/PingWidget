package ve.com.abicelis.pingwidget.app.preference;


import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.util.ArrayMap;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import ve.com.abicelis.pingwidget.R;
import ve.com.abicelis.pingwidget.enums.WidgetTheme;

/**
 * Created by abice on 13/2/2017.
 */

public class ThemePreference extends Preference {

    public ThemePreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThemePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayoutResource(R.layout.preference_theme);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return WidgetTheme.values()[0].name();
    }

    public String getSelectedTheme() {
        return getPersistedString(WidgetTheme.values()[0].name());
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        super.onSetInitialValue(restorePersistedValue, defaultValue);
        String value = restorePersistedValue ? getPersistedString(null): (String) defaultValue;

        if (TextUtils.isEmpty(value)) {
            value = WidgetTheme.values()[0].name();
        }
        setSummary(WidgetTheme.valueOf(value).getSummaryRes());
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        holder.itemView.setClickable(false);    // disable parent click

        //Loop through all the ThemeViews
        for(WidgetTheme widgetTheme : WidgetTheme.values()) {
            final String name = widgetTheme.name();
            final int themeViewId = widgetTheme.getThemeViewId();
            final int summaryRes = widgetTheme.getSummaryRes();

            View button = holder.findViewById(themeViewId);
            button.setClickable(true);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSummary(summaryRes);
                    persistString(name);
                }
            });
        }
    }

}