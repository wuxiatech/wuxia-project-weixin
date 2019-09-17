package cn.wuxia.project.weixin.core.user;

import cn.wuxia.project.weixin.WxUserContext;
import cn.wuxia.wechat.oauth.bean.AuthUserInfoBean;

public interface WxUserService {
    public WxUser findByOpenid(String openid);
    public WxUser findByUnionid(String unionid);
    public WxUser registerUser(AuthUserInfoBean authInfo, RegisterParameter parameter);
    public void afterOauth(WxUser wxUser, WxUserContext wxUserContext);
}
