package hitec.com.proxy;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import hitec.com.util.URLManager;
import hitec.com.vo.GetUserMessagesRequestVO;
import hitec.com.vo.GetUserMessagesResponseVO;
import hitec.com.vo.GetUsersRequestVO;
import hitec.com.vo.GetUsersResponseVO;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class GetUserMessagesProxy extends BaseProxy {

    public GetUserMessagesResponseVO run(String username) throws IOException {
        GetUserMessagesRequestVO requestVo = new GetUserMessagesRequestVO();
        requestVo.username = username;

        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("username", requestVo.username);

        RequestBody formBody = formBuilder.build();

        String contentString = postPlain(URLManager.getUserMessagesURL(), formBody);

        System.out.println(contentString);

        GetUserMessagesResponseVO responseVo = new GetUserMessagesResponseVO();

        try {
            JSONObject json = new JSONObject(contentString);
            responseVo.success = json.getInt("success");
            responseVo.error_code = json.getInt("error_code");
            responseVo.messages = json.getString("messages");
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        return responseVo;
    }
}
