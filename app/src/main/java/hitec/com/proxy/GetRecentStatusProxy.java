package hitec.com.proxy;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import hitec.com.util.URLManager;
import hitec.com.vo.GetRecentStatusRequestVO;
import hitec.com.vo.GetRecentStatusResponseVO;
import hitec.com.vo.GetUserMessagesRequestVO;
import hitec.com.vo.GetUserMessagesResponseVO;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class GetRecentStatusProxy extends BaseProxy {

    public GetRecentStatusResponseVO run(String username) throws IOException {
        GetRecentStatusRequestVO requestVo = new GetRecentStatusRequestVO();
        requestVo.username = username;

        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("username", requestVo.username);

        RequestBody formBody = formBuilder.build();

        String contentString = postPlain(URLManager.getRecentStatusURL(), formBody);

        System.out.println(contentString);

        GetRecentStatusResponseVO responseVo = new GetRecentStatusResponseVO();

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
