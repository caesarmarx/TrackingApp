package hitec.com.proxy;

import com.google.gson.Gson;

import java.io.IOException;

import hitec.com.util.URLManager;
import hitec.com.vo.SendLocationRequestVO;
import hitec.com.vo.SendLocationResponseVO;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class SendLocationProxy extends BaseProxy {

    public SendLocationResponseVO run(String sender, String latitude, String longitude) throws IOException {
        SendLocationRequestVO requestVo = new SendLocationRequestVO();
        requestVo.sender = sender;
        requestVo.latitude = latitude;
        requestVo.longitude = longitude;

        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("sender", requestVo.sender);
        formBuilder.add("latitude", requestVo.latitude);
        formBuilder.add("longitude", requestVo.longitude);

        RequestBody formBody = formBuilder.build();

        String contentString = postPlain(URLManager.getSendLocationURL(), formBody);

        System.out.println(contentString);

        SendLocationResponseVO responseVo = new Gson().fromJson(contentString, SendLocationResponseVO.class);

        return responseVo;
    }
}
