package hitec.com.event;

import hitec.com.vo.GetUsersResponseVO;

public class GetUsersEvent {
    private GetUsersResponseVO responseVo;

    public GetUsersEvent(GetUsersResponseVO responseVo) {
        this.responseVo = responseVo;
    }

    public GetUsersResponseVO getResponse() {
        return responseVo;
    }
}
