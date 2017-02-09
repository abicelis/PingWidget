package ve.com.abicelis.pingwidget.app.activity;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.afollestad.materialdialogs.color.ColorChooserDialog;

import ve.com.abicelis.pingwidget.R;
import ve.com.abicelis.pingwidget.app.fragment.PingWidgetConfigureFragment;
import ve.com.abicelis.pingwidget.app.widget.PingWidgetProvider;

/**
 * Created by abice on 8/2/2017.
 */

public class PingWidgetConfigureActivity extends AppCompatActivity implements ColorChooserDialog.ColorCallback {

    private static String CONFIGURE_ACTION="android.appwidget.action.APPWIDGET_CONFIGURE";
    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping_widget_configure);

        setUpToolbar();

        if (savedInstanceState == null) {
            PingWidgetConfigureFragment fragment = new PingWidgetConfigureFragment();
            fragment.setArguments(getIntent().getExtras());                             //Pass the fragment, the intent with the widgetID!

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.activity_preference_fragment, fragment);
            ft.commit();
        }
    }

    private void setUpToolbar() {

        mToolbar = (Toolbar) findViewById(R.id.activity_preference_toolbar);
        mToolbar.setTitle(getResources().getString(R.string.fragment_widget_configure_name));

        //Set toolbar as actionbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            // Respond to the mToolbar's back/home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        //If we're configuring a PingWidget
        if (CONFIGURE_ACTION.equals(getIntent().getAction())) {

            Bundle extras=getIntent().getExtras();
            if (extras!=null) {

                //Get the widgetId from the intent's extras
                int widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

                //Send an intent with the same widgetId and RESULT_CANCELED so the widget is removed
                Intent cancelWidgetIntent = new Intent();
                cancelWidgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
                setResult(RESULT_CANCELED, cancelWidgetIntent);
                sendBroadcast(new Intent(this, PingWidgetProvider.class));
            }
        }

        super.onBackPressed();
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int selectedColor) {
        PingWidgetConfigureFragment fragment = (PingWidgetConfigureFragment) getSupportFragmentManager().findFragmentById(R.id.activity_preference_fragment);
        fragment.setWidgetColor(selectedColor);
    }

    @Override
    public void onColorChooserDismissed(@NonNull ColorChooserDialog dialog) {
    }
}