package hitec.com.event;

import hitec.com.vo.SendLocationResponseVO;
import hitec.com.vo.SendOfflineLocationResponseVO;

public class SendOfflineLocationEvent {
    private SendOfflineLocationResponseVO responseVo;

    public SendOfflineLocationEvent(SendOfflineLocationResponseVO responseVo) {
        this.responseVo = responseVo;
    }

    public SendOfflineLocationResponseVO getResponse() {
        return responseVo;
    }
}
