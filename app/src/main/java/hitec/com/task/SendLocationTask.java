package hitec.com.task;

import android.os.AsyncTask;

import org.greenrobot.eventbus.EventBus;

import hitec.com.event.SendLocationEvent;
import hitec.com.event.SendNotificationEvent;
import hitec.com.proxy.SendLocationProxy;
import hitec.com.proxy.SendNotificationProxy;
import hitec.com.vo.SendLocationResponseVO;
import hitec.com.vo.SendNotificationResponseVO;

public class SendLocationTask extends AsyncTask<String, Void, SendLocationResponseVO> {

    private String sender;
    private String latitude;
    private String longitude;
    private String tracktime;

    public SendLocationTask() {
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected SendLocationResponseVO doInBackground(String... params) {
        SendLocationProxy simpleProxy = new SendLocationProxy();
        sender = params[0];
        latitude = params[1];
        longitude = params[2];
        tracktime = params[3];
        try {
            final SendLocationResponseVO responseVo = simpleProxy.run(sender, latitude, longitude, tracktime);

            return responseVo;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(SendLocationResponseVO responseVo) {
        EventBus.getDefault().post(new SendLocationEvent(responseVo));
    }
}