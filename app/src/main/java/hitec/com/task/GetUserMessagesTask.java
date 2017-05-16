package hitec.com.task;

import android.os.AsyncTask;

import org.greenrobot.eventbus.EventBus;

import hitec.com.event.GetUserMessagesEvent;
import hitec.com.event.GetUsersEvent;
import hitec.com.proxy.GetUserMessagesProxy;
import hitec.com.proxy.GetUsersProxy;
import hitec.com.vo.GetUserMessagesResponseVO;
import hitec.com.vo.GetUsersResponseVO;

public class GetUserMessagesTask extends AsyncTask<String, Void, GetUserMessagesResponseVO> {

    private String username;

    public GetUserMessagesTask() {
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected GetUserMessagesResponseVO doInBackground(String... params) {
        GetUserMessagesProxy simpleProxy = new GetUserMessagesProxy();
        username = params[0];
        try {
            final GetUserMessagesResponseVO responseVo = simpleProxy.run(username);

            return responseVo;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(GetUserMessagesResponseVO responseVo) {
        EventBus.getDefault().post(new GetUserMessagesEvent(responseVo));
    }
}