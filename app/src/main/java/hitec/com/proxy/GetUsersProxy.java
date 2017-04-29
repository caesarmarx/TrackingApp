package hitec.com.proxy;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import hitec.com.util.URLManager;
import hitec.com.vo.GetUsersRequestVO;
import hitec.com.vo.GetUsersResponseVO;
import hitec.com.vo.RegisterTokenRequestVO;
import hitec.com.vo.RegisterTokenResponseVO;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class GetUsersProxy extends BaseProxy {

    public GetUsersResponseVO run(String username, String customerID, String usertype) throws IOException {
        GetUsersRequestVO requestVo = new GetUsersRequestVO();
        requestVo.username = username;
        requestVo.customerID = customerID;
        requestVo.usertype = usertype;

        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("username", requestVo.username);
        formBuilder.add("customer_id", requestVo.customerID);
        formBuilder.add("usertype", requestVo.usertype);

        RequestBody formBody = formBuilder.build();

        String contentString = postPlain(URLManager.getUsersURL(), formBody);

        System.out.println(contentString);

        GetUsersResponseVO responseVo = new GetUsersResponseVO();

        try {
            JSONObject json = new JSONObject(contentString);
            responseVo.success = json.getInt("success");
            responseVo.error_code = json.getInt("error_code");
            responseVo.users = json.getString("users");
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        return responseVo;
    }
}
