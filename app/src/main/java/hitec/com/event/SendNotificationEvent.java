package hitec.com.event;

import hitec.com.vo.SendNotificationResponseVO;

public class SendNotificationEvent {
    private SendNotificationResponseVO responseVo;

    public SendNotificationEvent(SendNotificationResponseVO responseVo) {
        this.responseVo = responseVo;
    }

    public SendNotificationResponseVO getResponse() {
        return responseVo;
    }
}
