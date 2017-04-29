package hitec.com.task;

import android.os.AsyncTask;

import org.greenrobot.eventbus.EventBus;

import hitec.com.event.SendNotificationEvent;
import hitec.com.proxy.SendNotificationProxy;
import hitec.com.vo.RegisterTokenResponseVO;
import hitec.com.vo.SendNotificationResponseVO;

public class SendNotificationTask extends AsyncTask<String, Void, SendNotificationResponseVO> {

    private String sender;
    private String receiver;
    private String message;
    private String imageFile;

    public SendNotificationTask() {
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected SendNotificationResponseVO doInBackground(String... params) {
        SendNotificationProxy simpleProxy = new SendNotificationProxy();
        sender = params[0];
        receiver = params[1];
        message = params[2];
        imageFile = params[3];
        try {
            final SendNotificationResponseVO responseVo = simpleProxy.run(sender, receiver, message, imageFile);

            return responseVo;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(SendNotificationResponseVO responseVo) {
        EventBus.getDefault().post(new SendNotificationEvent(responseVo));
    }
}