package cn.wuxia.project.weixin.mvc.controller;

import cn.wuxia.common.spring.SpringContextHolder;
import cn.wuxia.common.util.StringUtil;
import cn.wuxia.project.basic.mvc.annotation.ApiAuthorized;
import cn.wuxia.project.basic.mvc.annotation.ApiAuthorizedType;
import cn.wuxia.project.basic.mvc.controller.BaseController;
import cn.wuxia.project.common.bean.CallbackBean;
import cn.wuxia.project.common.support.CacheConstants;
import cn.wuxia.project.common.support.CacheSupport;
import cn.wuxia.project.weixin.WxUserContext;
import cn.wuxia.project.weixin.WxUserContextUtil;
import cn.wuxia.project.weixin.api.WxAccountUtil;
import cn.wuxia.project.weixin.core.user.WxUser;
import cn.wuxia.project.weixin.core.user.WxUserService;
import cn.wuxia.wechat.Account;
import cn.wuxia.wechat.BasicAccount;
import cn.wuxia.wechat.WeChatException;
import cn.wuxia.wechat.js.util.AuthUtil;
import cn.wuxia.wechat.miniprogram.LoginAuthUtil;
import cn.wuxia.wechat.miniprogram.bean.AppLoginSession;
import cn.wuxia.wechat.open.util.ProxyJsAuthUtil;
import com.google.common.collect.Maps;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/auth/*")
public class WxAuthController extends BaseController {


    private WxUserService wxUserService;

    @RequestMapping("wechat/jsauth")
    @ResponseBody
    public Map<String, String> wechatAuth(String url) throws WeChatException {
        Assert.notNull(url, "url不能为空");
        Map<String, String> m = Maps.newHashMap();
        if (StringUtil.contains(url, "locahost") || StringUtil.contains(url, "127.0.0.1") || StringUtil.indexOf(url, "192.168.") > 0) {
            logger.info("==========={} 忽略js预授权==========", url);
            return m;
        }
        BasicAccount account = WxAccountUtil.getByAppid(getWxAppid());
        if (account.isAuthorizedToThird()) {
            m = ProxyJsAuthUtil.authentication(account, url);
        } else {
            m = AuthUtil.authentication(account, url);
        }
        logger.debug("url:{}", m.get("url"));
        m.remove("jsApiTicket");
        return m;
    }

    /**
     * 缓存用户的session_key
     */
    Cache sessionCache = CacheSupport.getCache(CacheConstants.CACHED_VALUE_30_MINUTES);

    public WxUserService getWxUserService() {
        if (wxUserService == null) {
            try {
                wxUserService = SpringContextHolder.getBean(WxUserService.class);
            } catch (Exception e) {
                logger.warn("", e.getMessage());
            }
        }
        return wxUserService;
    }

    @RequestMapping(value = {"/miniapp/login"}, method = RequestMethod.GET)
    @ApiAuthorized(type = ApiAuthorizedType.OPEN_TYPE)
    @ResponseBody
    public CallbackBean login(String code) {
        if (StringUtil.isBlank(code)) {
            return CallbackBean.notok("code不能为空");
        }
        Account account = WxAccountUtil.getByAppid(getWxAppid());

        try {
            AppLoginSession loginSession = LoginAuthUtil.getSession(account, code);
            Map<String, String> map = new HashMap<String, String>(4) {
                {
                    put("openid", loginSession.getOpenid());
                    put("sessionId", request.getSession().getId());
                }
            };
            /**
             * session_key 不能开放到小程序中，及第三方应用
             */
            if (sessionCache != null) {
                CacheSupport.set(sessionCache, "SESSIONKEY:" + loginSession.getOpenid(), loginSession);
            }
            if (getWxUserService() != null) {
                WxUser wxUser = getWxUserService().findByOpenid(loginSession.getOpenid());
                if (wxUser != null) {
                    WxUserContext wxUserContext = new WxUserContext(wxUser.getUid(), wxUser.getNickName(), wxUser.getMobile());
                    wxUserContext.setOpenid(wxUser.getOpenid());
                    wxUserContext.setUnionid(wxUser.getUnionid());
                    wxUserContext.setWxaccount(account);
                    WxUserContextUtil.saveUserContext(wxUserContext);
                    map.put("role", "");
                    map.put("binded", "true");
                }else{
                    map.put("role", "");
                    map.put("binded", "false");
                }
            } else {
                map.put("role", "");
                map.put("binded", "false");
            }
            return CallbackBean.okJsonResult(map, false);
        } catch (WeChatException | IOException e) {
            return CallbackBean.notok("获取openid异常");
        }
    }
}
