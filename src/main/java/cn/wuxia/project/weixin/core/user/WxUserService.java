package cn.wuxia.project.weixin.core.user;

import cn.wuxia.wechat.oauth.bean.AuthUserInfoBean;

public interface WxUserService {
    public WxUser findByOpenid(String openid);

    public WxUser registerUser(AuthUserInfoBean authInfo, RegisterParameter parameter);
}
