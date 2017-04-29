package hitec.com.proxy;

import com.google.gson.Gson;

import java.io.IOException;

import hitec.com.util.URLManager;
import hitec.com.vo.RegisterTokenRequestVO;
import hitec.com.vo.RegisterTokenResponseVO;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class RegisterProxy extends BaseProxy {

    public static final int FAIL_DB_ERROR = 1;
    public static final int FAIL_INCORRECT_USER = 2;
    public static final int FAIL_INCORRECT_PWD = 3;
    public static final int FAIL_INVALID_REQ = 4;

    public RegisterTokenResponseVO run(String username, String customerID, String password, String deviceToken) throws IOException {
        RegisterTokenRequestVO requestVo = new RegisterTokenRequestVO();
        requestVo.username = username;
        requestVo.customerID = customerID;
        requestVo.password = password;
        requestVo.deviceToken = deviceToken;

        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("username", requestVo.username);
        formBuilder.add("customer_id", requestVo.customerID);;
        formBuilder.add("password", requestVo.password);
        formBuilder.add("token", requestVo.deviceToken);


        RequestBody formBody = formBuilder.build();

        String contentString = postPlain(URLManager.getRegisterURL(), formBody);

        System.out.println(contentString);

        RegisterTokenResponseVO responseVo = new Gson().fromJson(contentString, RegisterTokenResponseVO.class);

        return responseVo;
    }
}
