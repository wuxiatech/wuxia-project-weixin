package cn.wuxia.project.weixin.core.user;

import java.io.Serializable;

public class RegisterParameter implements Serializable {

    private static final long serialVersionUID = 1038030557186571119L;
    String deviceInfo;

    String clientInfo;

    String wechatInfo;

    String registerFrom;

    public RegisterParameter() {
    }

    public RegisterParameter(String registerFrom) {
        this.registerFrom = registerFrom;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getClientInfo() {
        return clientInfo;
    }

    public void setClientInfo(String clientInfo) {
        this.clientInfo = clientInfo;
    }

    public String getWechatInfo() {
        return wechatInfo;
    }

    public void setWechatInfo(String wechatInfo) {
        this.wechatInfo = wechatInfo;
    }

    public String getRegisterFrom() {
        return registerFrom;
    }

    public void setRegisterFrom(String registerFrom) {
        this.registerFrom = registerFrom;
    }
}
