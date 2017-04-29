package hitec.com.task;

import android.os.AsyncTask;

import org.greenrobot.eventbus.EventBus;

import hitec.com.event.RegisterEvent;
import hitec.com.proxy.RegisterProxy;
import hitec.com.vo.RegisterTokenResponseVO;

public class RegisterTask extends AsyncTask<String, Void, RegisterTokenResponseVO> {

    private String username;
    private String customerId;
    private String password;
    private String token;

    public RegisterTask() {
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected RegisterTokenResponseVO doInBackground(String... params) {
        RegisterProxy simpleProxy = new RegisterProxy();
        username = params[0];
        customerId = params[1];
        password = params[2];
        token = params[3];
        try {
            final RegisterTokenResponseVO responseVo = simpleProxy.run(username, customerId, password, token);

            return responseVo;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(RegisterTokenResponseVO responseVo) {
        EventBus.getDefault().post(new RegisterEvent(responseVo));
    }
}