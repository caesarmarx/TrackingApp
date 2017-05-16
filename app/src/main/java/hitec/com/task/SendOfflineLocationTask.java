package hitec.com.task;

import android.os.AsyncTask;

import org.greenrobot.eventbus.EventBus;

import hitec.com.event.SendLocationEvent;
import hitec.com.event.SendOfflineLocationEvent;
import hitec.com.proxy.SendLocationProxy;
import hitec.com.proxy.SendOfflineLocationProxy;
import hitec.com.vo.SendLocationResponseVO;
import hitec.com.vo.SendOfflineLocationResponseVO;

public class SendOfflineLocationTask extends AsyncTask<String, Void, SendOfflineLocationResponseVO> {

    private String id;
    private String sender;
    private String latitude;
    private String longitude;
    private String tracktime;

    public SendOfflineLocationTask() {
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected SendOfflineLocationResponseVO doInBackground(String... params) {
        SendOfflineLocationProxy simpleProxy = new SendOfflineLocationProxy();
        id = params[0];
        sender = params[1];
        latitude = params[2];
        longitude = params[3];
        tracktime = params[4];
        try {
            final SendOfflineLocationResponseVO responseVo = simpleProxy.run(id, sender, latitude, longitude, tracktime);

            return responseVo;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(SendOfflineLocationResponseVO responseVo) {
        EventBus.getDefault().post(new SendOfflineLocationEvent(responseVo));
    }
}