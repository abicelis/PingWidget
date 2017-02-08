package ve.com.abicelis.pingwidget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by abice on 6/2/2017.
 */

public class HomeActivity extends AppCompatActivity {

    //CONST
    public static final String GITHUB_URL = "github.com/abicelis/";
    public static final String WEBSITE_URL = "www.alejandrobicelis.com.ve";

    //UI
    @BindView(R.id.activity_home_version) TextView mVersion;
    @BindView(R.id.activity_home_author) TextView mAuthor;
    @BindView(R.id.activity_home_github_link) TextView mGithubLink;
    @BindView(R.id.activity_home_website_link) TextView mWebsiteLink;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);


        mVersion.setText(String.format(Locale.getDefault(), getResources().getString(R.string.activity_home_version), getAppVersionAndBuild(this).first));
        mAuthor.setText(String.format(Locale.getDefault(), getResources().getString(R.string.activity_home_author), Calendar.getInstance().get(Calendar.YEAR)));

        final AppCompatActivity thisActivity = this;
        mGithubLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchWebBrowser(thisActivity, GITHUB_URL);
            }
        });
        mWebsiteLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchWebBrowser(thisActivity, WEBSITE_URL);
            }
        });
    }




    public static Pair<String, Integer> getAppVersionAndBuild(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return new Pair<>(pInfo.versionName, pInfo.versionCode);
        } catch (Exception e) {
            return new Pair<>("", 0);
        }
    }

    @SuppressLint("DefaultLocale")
    public static boolean launchWebBrowser(Context context, String url) {
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



}
