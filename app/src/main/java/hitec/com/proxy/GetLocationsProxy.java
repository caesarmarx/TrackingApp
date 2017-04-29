package hitec.com.proxy;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import hitec.com.util.URLManager;
import hitec.com.vo.GetLocationsRequestVO;
import hitec.com.vo.GetLocationsResponseVO;
import hitec.com.vo.GetUserMessagesRequestVO;
import hitec.com.vo.GetUserMessagesResponseVO;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class GetLocationsProxy extends BaseProxy {

    public GetLocationsResponseVO run(String username, String date) throws IOException {
        GetLocationsRequestVO requestVo = new GetLocationsRequestVO();
        requestVo.username = username;
        requestVo.date = date;

        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("username", requestVo.username);
        formBuilder.add("date", requestVo.date);

        RequestBody formBody = formBuilder.build();

        String contentString = postPlain(URLManager.getLocationsURL(), formBody);

        System.out.println(contentString);

        GetLocationsResponseVO responseVo = new GetLocationsResponseVO();

        try {
            JSONObject json = new JSONObject(contentString);
            responseVo.success = json.getInt("success");
            responseVo.error_code = json.getInt("error_code");
            responseVo.datas = json.getString("datas");
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        return responseVo;
    }
}
