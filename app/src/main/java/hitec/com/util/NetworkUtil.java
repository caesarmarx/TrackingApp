package hitec.com.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import hitec.com.consts.NetworkStateConsts;

/**
 * Created by Caesar on 4/25/2017.
 */

public class NetworkUtil {

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return NetworkStateConsts.NETWORK_WIFI_CONNECTED;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return NetworkStateConsts.NETWORK_MOBILE_DATA_CONNECTED;
        }
        return NetworkStateConsts.NETWORK_NOT_CONNECTED;
    }

    public static int getConnectivityStatusString(Context context) {
        int conn = NetworkUtil.getConnectivityStatus(context);
        int status = 0;
        if (conn == NetworkStateConsts.NETWORK_WIFI_CONNECTED) {
            status = NetworkStateConsts.NETWORK_WIFI_CONNECTED;
        } else if (conn == NetworkStateConsts.NETWORK_MOBILE_DATA_CONNECTED) {
            status = NetworkStateConsts.NETWORK_MOBILE_DATA_CONNECTED;
        } else if (conn == NetworkStateConsts.NETWORK_NOT_CONNECTED) {
            status = NetworkStateConsts.NETWORK_NOT_CONNECTED;
        }
        return status;
    }
}
