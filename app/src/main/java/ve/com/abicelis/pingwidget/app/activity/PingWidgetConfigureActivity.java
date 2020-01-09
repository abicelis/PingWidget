package ve.com.abicelis.pingwidget.app.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import ve.com.abicelis.pingwidget.R;
import ve.com.abicelis.pingwidget.app.fragment.PingWidgetConfigureFragment;
import ve.com.abicelis.pingwidget.util.Constants;

/**
 * Created by abice on 8/2/2017.
 */

public class PingWidgetConfigureActivity extends AppCompatActivity {

    //Const
    private static final String TAG = PingWidgetConfigureActivity.class.getSimpleName();

    //UI
    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping_widget_configure);

        setUpToolbar();

        Log.d(TAG, "onCreate(). Action =" + getIntent().getAction());

        if (savedInstanceState == null) {
            PingWidgetConfigureFragment fragment = new PingWidgetConfigureFragment();
            Bundle bundle = getIntent().getExtras();
            bundle.putString(Constants.ACTION, getIntent().getAction());
            fragment.setArguments(bundle);                             //Pass the fragment, the intent with the widgetID and the ACTION!

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
                if (Constants.ACTION_WIDGET_CONFIGURE.equals(getIntent().getAction()))
                    fragment.handleWidgetCreation();
                else
                    fragment.handleWidgetReconfigure();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        //If we're configuring a PingWidget
        if (Constants.ACTION_WIDGET_CONFIGURE.equals(getIntent().getAction())) {

            //Set result RESULT_CANCELED so the widget is never added
            setResult(RESULT_CANCELED);
        }

        super.onBackPressed();
    }

}