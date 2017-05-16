package hitec.com.proxy;

import com.google.gson.Gson;

import java.io.IOException;

import hitec.com.util.URLManager;
import hitec.com.vo.SendLocationRequestVO;
import hitec.com.vo.SendLocationResponseVO;
import hitec.com.vo.SendOfflineLocationRequestVO;
import hitec.com.vo.SendOfflineLocationResponseVO;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class SendOfflineLocationProxy extends BaseProxy {

    public SendOfflineLocationResponseVO run(String id, String sender, String latitude, String longitude, String tracktime) throws IOException {
        SendOfflineLocationRequestVO requestVo = new SendOfflineLocationRequestVO();
        requestVo.id = id;
        requestVo.sender = sender;
        requestVo.latitude = latitude;
        requestVo.longitude = longitude;
        requestVo.time = tracktime;

        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("id", requestVo.id);
        formBuilder.add("sender", requestVo.sender);
        formBuilder.add("latitude", requestVo.latitude);
        formBuilder.add("longitude", requestVo.longitude);
        formBuilder.add("time", requestVo.time);

        RequestBody formBody = formBuilder.build();

        String contentString = postPlain(URLManager.getSendOfflineLocationURL(), formBody);

        System.out.println(contentString);

        SendOfflineLocationResponseVO responseVo = new Gson().fromJson(contentString, SendOfflineLocationResponseVO.class);

        return responseVo;
    }
}
