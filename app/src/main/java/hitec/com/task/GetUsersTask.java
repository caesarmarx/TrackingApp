package hitec.com.task;

import android.os.AsyncTask;

import org.greenrobot.eventbus.EventBus;

import hitec.com.event.GetUsersEvent;
import hitec.com.proxy.GetUsersProxy;
import hitec.com.vo.GetUsersResponseVO;

public class GetUsersTask extends AsyncTask<String, Void, GetUsersResponseVO> {

    private String username;
    private String customerId;
    private String usertype;

    public GetUsersTask() {
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected GetUsersResponseVO doInBackground(String... params) {
        GetUsersProxy simpleProxy = new GetUsersProxy();
        username = params[0];
        customerId = params[1];
        usertype = params[2];

        try {
            final GetUsersResponseVO responseVo = simpleProxy.run(username, customerId, usertype);

            return responseVo;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(GetUsersResponseVO responseVo) {
        EventBus.getDefault().post(new GetUsersEvent(responseVo));
    }
}