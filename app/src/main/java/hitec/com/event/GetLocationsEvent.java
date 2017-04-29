package hitec.com.event;

import hitec.com.vo.GetLocationsResponseVO;

public class GetLocationsEvent {
    private GetLocationsResponseVO responseVo;

    public GetLocationsEvent(GetLocationsResponseVO responseVo) {
        this.responseVo = responseVo;
    }

    public GetLocationsResponseVO getResponse() {
        return responseVo;
    }
}
