package ve.com.abicelis.pingwidget.app.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import ve.com.abicelis.pingwidget.R;
import ve.com.abicelis.pingwidget.util.Util;

/**
 * Created by abice on 6/2/2017.
 */

public class HomeActivity extends AppCompatActivity {

    //UI
    @BindView(R.id.activity_home_version) TextView mVersion;
    @BindView(R.id.activity_home_ok_button) Button mOkButton;
    @BindView(R.id.activity_home_github) ImageView mGithub;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        mVersion.setText(String.format(Locale.getDefault(), getResources().getString(R.string.activity_home_version), Util.getAppVersionAndBuild(this).first));
        mGithub.setOnClickListener(v -> Util.launchWebBrowser(HomeActivity.this, getResources().getString(R.string.url_github)));
        mOkButton.setOnClickListener(v -> onBackPressed());
    }
}
