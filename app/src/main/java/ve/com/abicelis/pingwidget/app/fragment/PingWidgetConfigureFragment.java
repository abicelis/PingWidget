package ve.com.abicelis.pingwidget.app.fragment;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.takisoft.fix.support.v7.preference.EditTextPreference;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

import ve.com.abicelis.pingwidget.R;
import ve.com.abicelis.pingwidget.app.activity.PingWidgetConfigureActivity;
import ve.com.abicelis.pingwidget.app.widget.PingWidgetProvider;
import ve.com.abicelis.pingwidget.model.PingWidgetData;
import ve.com.abicelis.pingwidget.util.AddressValidator;
import ve.com.abicelis.pingwidget.util.SharedPreferencesHelper;


/**
 * Created by abice on 9/2/2017.
 */

public class PingWidgetConfigureFragment extends PreferenceFragmentCompat {

    private EditTextPreference mAddress;
    private ListPreference mInterval;
    private Preference mBackgroundColor;
    private Preference mChartLineColor;
    private ListPreference mMaxPings;

    private int mWidgetBackgroundColor = -1;
    private int mWidgetChartLineColor = -1;

    @Override
    public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.ping_widget_configure_preferences);

        mAddress = (EditTextPreference) findPreference(getResources().getString(R.string.fragment_widget_configure_key_address));
        mAddress.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                //Toast.makeText(getActivity(), "TODO: Check if valid IP", Toast.LENGTH_SHORT).show();
                //TODO: Check if valid IP
                return true;
            }
        });
        mInterval = (ListPreference) findPreference(getResources().getString(R.string.fragment_widget_configure_key_interval));
        mBackgroundColor =  findPreference(getResources().getString(R.string.fragment_widget_configure_key_background_color));
        mBackgroundColor.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                new ColorChooserDialog.Builder((PingWidgetConfigureActivity) getActivity(), R.string.fragment_widget_configure_dialog_background_color)
                        .tag(getResources().getString(R.string.fragment_widget_configure_dialog_background_color))
                        .accentMode(true)
                        .doneButton(R.string.md_done_label)  // changes label of the done button
                        .cancelButton(R.string.md_cancel_label)  // changes label of the cancel button
                        .backButton(R.string.md_back_label)  // changes label of the back button
                        .dynamicButtonColor(true)  // defaults to true, false will disable changing action buttons' color to currently selected color
                        .show();
                return false;
            }
        });
        mWidgetBackgroundColor = Color.parseColor("#448AFF");
        mChartLineColor =  findPreference(getResources().getString(R.string.fragment_widget_configure_key_line_color));
        mChartLineColor.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                new ColorChooserDialog.Builder((PingWidgetConfigureActivity) getActivity(), R.string.fragment_widget_configure_dialog_chart_line_color)
                        .tag(getResources().getString(R.string.fragment_widget_configure_dialog_chart_line_color))
                        .accentMode(false)
                        .doneButton(R.string.md_done_label)  // changes label of the done button
                        .cancelButton(R.string.md_cancel_label)  // changes label of the cancel button
                        .backButton(R.string.md_back_label)  // changes label of the back button
                        .dynamicButtonColor(true)  // defaults to true, false will disable changing action buttons' color to currently selected color
                        .show();
                return false;
            }
        });
        mWidgetChartLineColor = Color.parseColor("#FFEB3B");
        mMaxPings = (ListPreference) findPreference(getResources().getString(R.string.fragment_widget_configure_key_max_pings));


        Preference enable = findPreference(getResources().getString(R.string.fragment_widget_configure_key_enable));
        enable.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                if(!checkValues()) {
                    return true;
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

                    //TODO: Cant seem to figure out how to change background color without overwriting background drawable (killing the rounded corners)
                    //Do this here and in PingWidgetProvider
                    //views.setInt(R.id.widget_background, "setColorFilter", Color.WHITE);
                    //views.setInt(R.id.widget_background, "setBackgroundTint", Color.GREEN);
                     views.setInt(R.id.widget_background, "setBackgroundColor", mWidgetBackgroundColor);
                    //views.setInt(R.id.widget_layout, "setBackgroundColor", mWidgetBackgroundColor);
                    //And if your color has transparency call remoteViews.setInt(R.id.backgroundimage, "setImageAlpha", Color.alpha(color); if SDK>=16; else remoteViews.setInt(R.id.backgroundimage, "setAlpha", Color.alpha(color);

                    //Save PingWidgetData in SharedPreferences()
                    savePingWidgetData(widgetId, mAddress.getText(), Integer.parseInt(mInterval.getValue()), Integer.parseInt(mMaxPings.getValue()));


                    //Register an Intent so that onClicks on the widget are received by PingWidgetProvider.onReceive()
                    //Create an Intent, set PING_WIDGET_TOGGLE action to it, put EXTRA_APPWIDGET_ID as extra
                    Intent clickIntent = new Intent(getActivity(), PingWidgetProvider.class);
                    clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
                    clickIntent.setAction(PingWidgetProvider.PING_WIDGET_TOGGLE);

                    //Construct a PendingIntent using the Intent above
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), widgetId, clickIntent, 0);

                    //Register pendingIntent in RemoteViews onClick
                    views.setOnClickPendingIntent(R.id.widget_start_pause, pendingIntent);



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

                //Return true, click was handled here
                return true;
            }
        });
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

        if(mWidgetBackgroundColor == -1) {
            Toast.makeText(getActivity(), getResources().getString(R.string.fragment_widget_configure_err_background_color), Toast.LENGTH_SHORT).show();
            return false;
        }

        if(mWidgetChartLineColor == -1) {
            Toast.makeText(getActivity(), getResources().getString(R.string.fragment_widget_configure_err_chart_line_color), Toast.LENGTH_SHORT).show();
            return false;
        }

        if(mMaxPings.getValue() == null) {
            Toast.makeText(getActivity(), getResources().getString(R.string.fragment_widget_configure_err_max_pings), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void savePingWidgetData(int widgetId, String address, int pingInterval, int maxPings) {
        PingWidgetData data = new PingWidgetData(address, pingInterval, mWidgetBackgroundColor, mWidgetChartLineColor, maxPings);
        SharedPreferencesHelper.writePingWidgetData(getContext().getApplicationContext(), widgetId, data);
    }

    //Method called from parent activity
    public void setWidgetBackgroundColor(int color) {
        mWidgetBackgroundColor = color;
    }
    public void setWidgetChartLineColor(int color) {
        mWidgetChartLineColor = color;
    }
}
