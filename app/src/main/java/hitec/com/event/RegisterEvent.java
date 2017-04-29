package hitec.com.event;

import hitec.com.vo.RegisterTokenResponseVO;

public class RegisterEvent {
    private RegisterTokenResponseVO responseVo;

    public RegisterEvent(RegisterTokenResponseVO responseVo) {
        this.responseVo = responseVo;
    }

    public RegisterTokenResponseVO getResponse() {
        return responseVo;
    }
}
