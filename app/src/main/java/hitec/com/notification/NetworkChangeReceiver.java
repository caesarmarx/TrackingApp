package hitec.com.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import hitec.com.consts.NetworkStateConsts;
import hitec.com.task.SendLocationTask;
import hitec.com.util.NetworkUtil;
import hitec.com.util.SharedPrefManager;

/**
 * Created by Caesar on 4/25/2017.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int status = NetworkUtil.getConnectivityStatusString(context);
        if(status == NetworkStateConsts.NETWORK_WIFI_CONNECTED || status == NetworkStateConsts.NETWORK_MOBILE_DATA_CONNECTED) {
            String latitude = SharedPrefManager.getInstance(context).getLatitude();
            String longitude = SharedPrefManager.getInstance(context).getLongitude();
            String time = SharedPrefManager.getInstance(context).getTrackingTime();
            String sender = SharedPrefManager.getInstance(context).getUsername();

            boolean sentFlag = SharedPrefManager.getInstance(context).getSentFlag();
            if(!sentFlag) {
                SendLocationTask task = new SendLocationTask();
                task.execute(sender, latitude, longitude);
            }
        }
    }
}