package ve.com.abicelis.pingwidget.app.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v14.preference.SwitchPreference;
import android.support.v4.util.Pair;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.takisoft.fix.support.v7.preference.EditTextPreference;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

import java.util.Locale;

import ve.com.abicelis.pingwidget.R;
import ve.com.abicelis.pingwidget.app.preference.ThemePreference;
import ve.com.abicelis.pingwidget.enums.WidgetTheme;
import ve.com.abicelis.pingwidget.model.PingWidgetData;
import ve.com.abicelis.pingwidget.util.AddressValidator;
import ve.com.abicelis.pingwidget.util.SharedPreferencesHelper;
import ve.com.abicelis.pingwidget.util.Util;


/**
 * Created by abice on 9/2/2017.
 */

public class PingWidgetConfigureFragment extends PreferenceFragmentCompat {

    //UI
    private EditTextPreference mAddress;
    private ListPreference mInterval;
    private ListPreference mMaxPings;
    private SwitchPreference mShowChartLines;
    private ThemePreference mTheme;
    private Preference mAbout;
    private Preference mRate;
    private Preference mContact;


    @Override
    public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.ping_widget_configure_preferences);

        mAddress = (EditTextPreference) findPreference(getResources().getString(R.string.fragment_widget_configure_address_key));
        mAddress.setSummary(mAddress.getText());                            //If a value was previously set for a previous widget, keep that for next widget
        mAddress.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mAddress.setSummary((String) newValue);
                return true;
            }
        });
        mInterval = (ListPreference) findPreference(getResources().getString(R.string.fragment_widget_configure_interval_key));
        mMaxPings = (ListPreference) findPreference(getResources().getString(R.string.fragment_widget_configure_max_pings_key));
        mShowChartLines = (SwitchPreference) findPreference(getResources().getString(R.string.fragment_widget_show_chart_lines_key));
        mTheme = (ThemePreference) findPreference(getResources().getString(R.string.fragment_widget_configure_theme_key));
        mAbout = findPreference(getResources().getString(R.string.fragment_widget_configure_about_key));
        mAbout.setSummary(String.format(Locale.getDefault(), getResources().getString(R.string.fragment_widget_configure_about_summary), getAppVersionAndBuild(getActivity()).first));
        mRate = findPreference(getResources().getString(R.string.fragment_widget_configure_rate_key));
        mRate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent playStoreIntent = new Intent(Intent.ACTION_VIEW);
                playStoreIntent.setData(Uri.parse(getResources().getString(R.string.url_market)));
                startActivity(playStoreIntent);
                return true;
            }
        });
        mContact = findPreference(getResources().getString(R.string.fragment_widget_configure_contact_key));
        mContact.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",getResources().getString(R.string.address_email), null));
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
                return true;
            }
        });

    }

    private static Pair<String, Integer> getAppVersionAndBuild(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return new Pair<>(pInfo.versionName, pInfo.versionCode);
        } catch (Exception e) {
            return new Pair<>("", 0);
        }
    }

    @SuppressLint("DefaultLocale")
    private static boolean launchWebBrowser(Context context, String url) {
        try {
            url = url.toLowerCase();
            if (!url.startsWith("http://") || !url.startsWith("https://")) {
                url = "http://" + url;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent,
                    PackageManager.MATCH_DEFAULT_ONLY);
            if (null == resolveInfo) {
                Toast.makeText(context, "Could not find a Browser to open link", Toast.LENGTH_SHORT).show();
                return false;
            }
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            Toast.makeText(context, "Could not start web browser", Toast.LENGTH_SHORT).show();

            return false;
        }
    }

    private boolean checkValues() {
        AddressValidator validator = new AddressValidator();
        if(mAddress.getText() == null || !validator.validate(mAddress.getText())) {
            Toast.makeText(getActivity(), getResources().getString(R.string.fragment_widget_configure_err_address), Toast.LENGTH_SHORT).show();
            return false;
        }

        if(mInterval.getValue() == null) {
            Toast.makeText(getActivity(), getResources().getString(R.string.fragment_widget_configure_err_interval), Toast.LENGTH_SHORT).show();
            return false;
        }

        if(mMaxPings.getValue() == null) {
            Toast.makeText(getActivity(), getResources().getString(R.string.fragment_widget_configure_err_max_pings), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void savePingWidgetData(int widgetId, String address, int pingInterval, int maxPings, boolean showMaxMinAvgLines, String themeName) {
        PingWidgetData data = new PingWidgetData(address, pingInterval, maxPings, showMaxMinAvgLines, themeName);
        SharedPreferencesHelper.writePingWidgetData(getContext().getApplicationContext(), widgetId, data);
    }




    //Methods called from parent activity
    public void handleWidgetCreation() {

        if(!checkValues()) {
            return;
        }

        Bundle extras = getArguments();
        if (extras != null) {

            //Get the arguments (bundle) passed from PingWidgetConfigureActivity, which contains the widgetId
            int widgetId = getArguments().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

            //Get AppWidgetManager and RemoteViews
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getActivity());
            RemoteViews views = new RemoteViews(getActivity().getPackageName(), R.layout.widget_layout);

            //Update the widget's views
            views.setTextViewText(R.id.widget_host, mAddress.getText());
            views.setImageViewResource(R.id.widget_start_pause, android.R.drawable.ic_media_play);
            views.setInt(R.id.widget_layout_container_top, "setBackgroundResource", WidgetTheme.valueOf(mTheme.getSelectedTheme()).getDrawableBackgroundContainerTop());

            //Save PingWidgetData in SharedPreferences()
            savePingWidgetData(widgetId, mAddress.getText(), Integer.parseInt(mInterval.getValue()), Integer.parseInt(mMaxPings.getValue()), mShowChartLines.isChecked(), mTheme.getSelectedTheme());


            //Register an Intent so that onClicks on the widget are received by PingWidgetProvider.onReceive()
            //Create an Intent, set PING_WIDGET_TOGGLE action to it, put EXTRA_APPWIDGET_ID as extra
            Util.registerWidgetStartPauseOnClickListener(getActivity(), widgetId, views);


            //Finally, update the widget
            appWidgetManager.updateAppWidget(widgetId, views);

            //Send widget an intent with RESULT_OK so it know its been configured correctly
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            getActivity().setResult(Activity.RESULT_OK, resultValue);
            getActivity().finish();

        } else {
            Toast.makeText(getActivity(), "Error! No WidgetID passed to Activity", Toast.LENGTH_SHORT).show();
        }

    }

}
