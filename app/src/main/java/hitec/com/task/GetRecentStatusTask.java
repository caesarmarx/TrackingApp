package hitec.com.task;

import android.os.AsyncTask;

import org.greenrobot.eventbus.EventBus;

import hitec.com.event.GetRecentStatusEvent;
import hitec.com.event.GetUserMessagesEvent;
import hitec.com.proxy.GetRecentStatusProxy;
import hitec.com.proxy.GetUserMessagesProxy;
import hitec.com.vo.GetRecentStatusResponseVO;
import hitec.com.vo.GetUserMessagesResponseVO;

public class GetRecentStatusTask extends AsyncTask<String, Void, GetRecentStatusResponseVO> {

    private String username;

    public GetRecentStatusTask() {
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected GetRecentStatusResponseVO doInBackground(String... params) {
        GetRecentStatusProxy simpleProxy = new GetRecentStatusProxy();
        username = params[0];

        try {
            final GetRecentStatusResponseVO responseVo = simpleProxy.run(username);

            return responseVo;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(GetRecentStatusResponseVO responseVo) {
        EventBus.getDefault().post(new GetRecentStatusEvent(responseVo));
    }
}