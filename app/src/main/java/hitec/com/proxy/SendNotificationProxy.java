package hitec.com.proxy;

import com.google.gson.Gson;

import java.io.IOException;

import hitec.com.util.URLManager;
import hitec.com.vo.RegisterTokenRequestVO;
import hitec.com.vo.RegisterTokenResponseVO;
import hitec.com.vo.SendNotificationRequestVO;
import hitec.com.vo.SendNotificationResponseVO;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class SendNotificationProxy extends BaseProxy {

    public SendNotificationResponseVO run(String sender, String receiver, String message, String imageFile) throws IOException {
        SendNotificationRequestVO requestVo = new SendNotificationRequestVO();
        requestVo.sender = sender;
        requestVo.receiver = receiver;
        requestVo.message = message;
        requestVo.imageFile = imageFile;

        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("sender", requestVo.sender);
        formBuilder.add("receiver", requestVo.receiver);;
        formBuilder.add("message", requestVo.message);
        formBuilder.add("image", requestVo.imageFile);

        RequestBody formBody = formBuilder.build();

        String contentString = postPlain(URLManager.getSendNotificationURL(), formBody);

        System.out.println(contentString);

        SendNotificationResponseVO responseVo = new Gson().fromJson(contentString, SendNotificationResponseVO.class);

        return responseVo;
    }
}
