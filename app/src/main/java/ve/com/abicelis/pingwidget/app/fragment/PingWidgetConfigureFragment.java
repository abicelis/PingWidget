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
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.takisoft.fix.support.v7.preference.EditTextPreference;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

import ve.com.abicelis.pingwidget.R;
import ve.com.abicelis.pingwidget.app.activity.PingWidgetConfigureActivity;
import ve.com.abicelis.pingwidget.app.widget.PingWidgetProvider;
import ve.com.abicelis.pingwidget.model.PingWidgetData;
import ve.com.abicelis.pingwidget.util.SharedPreferencesHelper;


/**
 * Created by abice on 9/2/2017.
 */

public class PingWidgetConfigureFragment extends PreferenceFragmentCompat {

    private EditTextPreference mAddress;
    private ListPreference mInterval;
    private Preference mColor;
    private int mWidgetColor = -1;

    @Override
    public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.ping_widget_configure_preferences);

        mAddress = (EditTextPreference) findPreference("widget_configure_address");
        mAddress.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Toast.makeText(getActivity(), "TODO: Check if valid IP", Toast.LENGTH_SHORT).show();
                //TODO: Check if valid IP
                return true;
            }
        });
        mInterval = (ListPreference) findPreference("widget_configure_interval");
        mColor =  findPreference("widget_configure_color");
        mColor.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                new ColorChooserDialog.Builder((PingWidgetConfigureActivity) getActivity(), R.string.fragment_widget_configure_color_palette)
                        .doneButton(R.string.md_done_label)  // changes label of the done button
                        .cancelButton(R.string.md_cancel_label)  // changes label of the cancel button
                        .backButton(R.string.md_back_label)  // changes label of the back button
                        .dynamicButtonColor(true)  // defaults to true, false will disable changing action buttons' color to currently selected color
                        .show();
                return false;
            }
        });


        Preference enable = findPreference("widget_configure_enable");
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

                    //TODO: Cant seem to figure out how to change background color without overwriting background drawable (killing the rounded corners)
                    //views.setInt(R.id.widget_background, "setColorFilter", Color.WHITE);
                    //views.setInt(R.id.widget_background, "setBackgroundTint", Color.GREEN);
                   // views.setInt(R.id.widget_background, "setBackgroundColor", mWidgetColor);
                    views.setInt(R.id.widget_layout, "setBackgroundColor", mWidgetColor);
                    //And if your color has transparency call remoteViews.setInt(R.id.backgroundimage, "setImageAlpha", Color.alpha(color); if SDK>=16; else remoteViews.setInt(R.id.backgroundimage, "setAlpha", Color.alpha(color);

                    //Save PingWidgetData in SharedPreferences()
                    savePingWidgetData(widgetId, mAddress.getText(), Integer.parseInt(mInterval.getValue()));



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
        if(mAddress.getText() == null) {
            Toast.makeText(getActivity(), getResources().getString(R.string.fragment_widget_configure_err_address), Toast.LENGTH_SHORT).show();
            return false;
        }

        if(mInterval.getValue() == null) {
            Toast.makeText(getActivity(), getResources().getString(R.string.fragment_widget_configure_err_interval), Toast.LENGTH_SHORT).show();
            return false;
        }

        if(mWidgetColor == -1) {
            Toast.makeText(getActivity(), getResources().getString(R.string.fragment_widget_configure_err_color), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void savePingWidgetData(int widgetId, String address, int pingInterval) {
        PingWidgetData data = new PingWidgetData(address, pingInterval, mWidgetColor);
        SharedPreferencesHelper.writePingWidgetData(getContext().getApplicationContext(), widgetId, data);
    }

    //Method called from parent activity
    public void setWidgetColor( int color) {
        mWidgetColor = color;
    }
}