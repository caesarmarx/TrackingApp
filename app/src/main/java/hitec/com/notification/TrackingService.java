package hitec.com.notification;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import hitec.com.event.GetRecentStatusEvent;
import hitec.com.event.SendLocationEvent;
import hitec.com.proxy.BaseProxy;
import hitec.com.proxy.SendLocationProxy;
import hitec.com.task.SendLocationTask;
import hitec.com.ui.HomeActivity;
import hitec.com.ui.MainActivity;
import hitec.com.util.DateUtil;
import hitec.com.util.MyNotificationManager;
import hitec.com.util.SharedPrefManager;
import hitec.com.util.TrackGPS;
import hitec.com.vo.GetRecentStatusResponseVO;
import hitec.com.vo.SendLocationResponseVO;

public class TrackingService extends Service {
    private Context ctx;
    private static Timer timer = new Timer();

    public TrackingService() {
        super();
    }

    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;

    public void onCreate()
    {
        super.onCreate();
        ctx = this;
        startService();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onSendLocationEvent(SendLocationEvent event) {
        SendLocationResponseVO responseVO = event.getResponse();
        if(responseVO != null && responseVO.success == SendLocationProxy.RESPONSE_SUCCESS) {
            Log.v("SendLocation", "Success");
            SharedPrefManager.getInstance(ctx).saveSentFlag(true);
        } else {
            Log.v("SendLocation", "Failed");
            SharedPrefManager.getInstance(ctx).saveSentFlag(false);
        }
    }

    private void startService()
    {
        try {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 300000, 0, mLocationListener);
            if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300000, 0, mLocationListener);
            EventBus.getDefault().register(this);
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    private void sendLocation(double latitude, double longitude) {
        String sender = SharedPrefManager.getInstance(getApplicationContext()).getUsername();

        SendLocationTask task = new SendLocationTask();
        task.execute(sender, String.valueOf(latitude), String.valueOf(longitude));
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
            try {
                Log.v("Latitude", String.valueOf(location.getLatitude()));
                Log.v("Longitude", String.valueOf(location.getLongitude()));

                SharedPrefManager.getInstance(ctx).saveLatitude(String.valueOf(location.getLatitude()));
                SharedPrefManager.getInstance(ctx).saveLongitude(String.valueOf(location.getLongitude()));
                SharedPrefManager.getInstance(ctx).saveTrackingTime(DateUtil.getCurDateTime());
                SharedPrefManager.getInstance(ctx).saveSentFlag(false);

                sendLocation(location.getLatitude(), location.getLongitude());

            } catch (SecurityException ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
}
