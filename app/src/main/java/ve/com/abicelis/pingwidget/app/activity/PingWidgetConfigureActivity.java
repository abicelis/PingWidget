package ve.com.abicelis.pingwidget.app.activity;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import ve.com.abicelis.pingwidget.R;
import ve.com.abicelis.pingwidget.app.fragment.PingWidgetConfigureFragment;

/**
 * Created by abice on 8/2/2017.
 */

public class PingWidgetConfigureActivity extends AppCompatActivity {

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_ping_widget_configure, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            // Respond to the mToolbar's back/home button
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_activity_widget_configure_done:
                PingWidgetConfigureFragment fragment = (PingWidgetConfigureFragment) getSupportFragmentManager().findFragmentById(R.id.activity_preference_fragment);
                fragment.handleWidgetCreation();
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
            }
        }

        super.onBackPressed();
    }

}