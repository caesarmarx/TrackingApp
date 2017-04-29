package hitec.com.event;

import hitec.com.vo.SendLocationResponseVO;

public class SendLocationEvent {
    private SendLocationResponseVO responseVo;

    public SendLocationEvent(SendLocationResponseVO responseVo) {
        this.responseVo = responseVo;
    }

    public SendLocationResponseVO getResponse() {
        return responseVo;
    }
}
