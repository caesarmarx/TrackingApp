package hitec.com.event;

import hitec.com.vo.SendAdminNotificationResponseVO;

public class SendAdminNotificationEvent {
    private SendAdminNotificationResponseVO responseVo;

    public SendAdminNotificationEvent(SendAdminNotificationResponseVO responseVo) {
        this.responseVo = responseVo;
    }

    public SendAdminNotificationResponseVO getResponse() {
        return responseVo;
    }
}
