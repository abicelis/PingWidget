package ve.com.abicelis.pingwidget.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by abice on 10/2/2017.
 */

public class ScreenReceiver extends BroadcastReceiver {
    public static final String SCREEN_STATE = "SCREEN_STATE";
    public static IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, PingWidgetUpdateService.class);
        i.putExtra(SCREEN_STATE, intent.getAction());
        context.startService(i);
    }

}