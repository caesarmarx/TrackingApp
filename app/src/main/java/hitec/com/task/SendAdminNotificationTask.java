package hitec.com.task;

import android.os.AsyncTask;

import org.greenrobot.eventbus.EventBus;

import hitec.com.event.SendAdminNotificationEvent;
import hitec.com.event.SendNotificationEvent;
import hitec.com.proxy.SendAdminNotificationProxy;
import hitec.com.proxy.SendNotificationProxy;
import hitec.com.vo.SendAdminNotificationResponseVO;
import hitec.com.vo.SendNotificationResponseVO;

public class SendAdminNotificationTask extends AsyncTask<String, Void, SendAdminNotificationResponseVO> {

    private String sender;
    private String customerID;
    private String message;
    private String imageFileName;

    public SendAdminNotificationTask() {
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected SendAdminNotificationResponseVO doInBackground(String... params) {
        SendAdminNotificationProxy simpleProxy = new SendAdminNotificationProxy();
        sender = params[0];
        customerID = params[1];
        message = params[2];
        imageFileName = params[3];
        try {
            final SendAdminNotificationResponseVO responseVo = simpleProxy.run(sender, customerID, message, imageFileName);

            return responseVo;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(SendAdminNotificationResponseVO responseVo) {
        EventBus.getDefault().post(new SendAdminNotificationEvent(responseVo));
    }
}