package hitec.com.event;

import hitec.com.vo.GetUserMessagesResponseVO;

public class GetUserMessagesEvent {
    private GetUserMessagesResponseVO responseVo;

    public GetUserMessagesEvent(GetUserMessagesResponseVO responseVo) {
        this.responseVo = responseVo;
    }

    public GetUserMessagesResponseVO getResponse() {
        return responseVo;
    }
}
