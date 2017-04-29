package hitec.com.task;

import android.os.AsyncTask;

import org.greenrobot.eventbus.EventBus;

import hitec.com.event.GetLocationsEvent;
import hitec.com.proxy.GetLocationsProxy;
import hitec.com.vo.GetLocationsResponseVO;

public class GetLocationsTask extends AsyncTask<String, Void, GetLocationsResponseVO> {

    private String username;
    private String date;

    public GetLocationsTask() {
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected GetLocationsResponseVO doInBackground(String... params) {
        GetLocationsProxy simpleProxy = new GetLocationsProxy();
        username = params[0];
        date = params[1];

        try {
            final GetLocationsResponseVO responseVo = simpleProxy.run(username, date);

            return responseVo;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(GetLocationsResponseVO responseVo) {
        EventBus.getDefault().post(new GetLocationsEvent(responseVo));
    }
}