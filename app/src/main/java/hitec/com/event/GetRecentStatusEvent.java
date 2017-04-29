package hitec.com.event;

import hitec.com.vo.GetRecentStatusResponseVO;
import hitec.com.vo.GetUserMessagesResponseVO;

public class GetRecentStatusEvent {
    private GetRecentStatusResponseVO responseVo;

    public GetRecentStatusEvent(GetRecentStatusResponseVO responseVo) {
        this.responseVo = responseVo;
    }

    public GetRecentStatusResponseVO getResponse() {
        return responseVo;
    }
}
