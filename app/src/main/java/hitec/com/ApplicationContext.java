package hitec.com;

import android.content.Context;
import android.widget.Toast;

public class ApplicationContext {
//    public static final String HTTP_HOST = "http://192.168.2.113/tracking";
    public static final String HTTP_HOST = "http://cloud-pk.com/tracking";

    public static void showToastMessage(Context ctx, String message) {
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }
}
