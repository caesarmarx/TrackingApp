package hitec.com.proxy;

import com.google.gson.Gson;

import java.io.IOException;

import hitec.com.util.URLManager;
import hitec.com.vo.SendAdminNotificationRequestVO;
import hitec.com.vo.SendAdminNotificationResponseVO;
import hitec.com.vo.SendNotificationRequestVO;
import hitec.com.vo.SendNotificationResponseVO;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class SendAdminNotificationProxy extends BaseProxy {

    public SendAdminNotificationResponseVO run(String sender, String customerID, String message, String imageFile) throws IOException {
        SendAdminNotificationRequestVO requestVo = new SendAdminNotificationRequestVO();
        requestVo.sender = sender;
        requestVo.customerID = customerID;
        requestVo.message = message;
        requestVo.imageFile = imageFile;

        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("sender", requestVo.sender);
        formBuilder.add("customer_id", requestVo.customerID);;
        formBuilder.add("message", requestVo.message);
        formBuilder.add("image", requestVo.imageFile);

        RequestBody formBody = formBuilder.build();

        String contentString = postPlain(URLManager.getSendAdminNotificationURL(), formBody);

        System.out.println(contentString);

        SendAdminNotificationResponseVO responseVo = new Gson().fromJson(contentString, SendAdminNotificationResponseVO.class);

        return responseVo;
    }
}
