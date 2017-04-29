package hitec.com.proxy;

import java.io.IOException;

import hitec.com.ApplicationContext;
import hitec.com.util.CheckHandshake;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BaseProxy {

    public static int RESPONSE_SUCCESS = 1;
    public static OkHttpClient client;

    public String postPlain(String uri, RequestBody formBody) throws IOException {
        Request request =  new Request.Builder()
                .url(uri)
                .post(formBody)
                .build();

        String responseBody;

        // Write All request
        System.out.println("Request: " + ApplicationContext.HTTP_HOST + formBody);

        CheckHandshake handshake = new CheckHandshake();
        client = handshake.getClient();
        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        responseBody = response.body().string();

        // Write All resposne
        System.out.println("Response: " + responseBody);

        return responseBody;
    }
}