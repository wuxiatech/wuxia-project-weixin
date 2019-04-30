package cn.wuxia.project.weixin;

import cn.wuxia.common.util.StringUtil;
import cn.wuxia.project.common.security.UserContextUtil;
import cn.wuxia.wechat.BasicAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public class WxUserContextUtil extends UserContextUtil {
    private static Logger logger = LoggerFactory.getLogger(WxUserContextUtil.class);

    public static void saveUserContext(WxUserContext uc) {
        UserContextUtil.saveUserContext(uc);
        WxUserContext cacheUserContext = cache.get(CURRENT_SESSION_WX_USER + uc.getOpenid(), WxUserContext.class);
        if (cacheUserContext != null) {
            cache.evict(CURRENT_SESSION_WX_USER + uc.getOpenid());
        }
        cache.put(CURRENT_SESSION_WX_USER + uc.getOpenid(), uc);

        logger.debug("登录{}", uc);
    }

    public static WxUserContext getUserContext() {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        if (ra == null) {
            logger.warn("非浏览器请求");
            return null;
        }
        WxUserContext uc = (WxUserContext) ra.getAttribute(CURRENT_SESSION_WX_USER, RequestAttributes.SCOPE_SESSION);
        /**
         * 解决存放session会存在跨系统之后角色不同步的问题
         */
        if (uc != null && StringUtil.isNotBlank(uc.getOpenid())) {
            WxUserContext cacheUserContext = cache.get(CURRENT_SESSION_WX_USER + uc.getOpenid(), WxUserContext.class);
            if (cacheUserContext != null) {
                logger.debug("cache获取{}", cacheUserContext);
                return cacheUserContext;
            }
        }
        return uc;
    }

    public static void removeUserContext() {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        if (ra == null) {
            logger.warn("非浏览器请求");
            return;
        }
        String openid = getOpenid();
        ra.removeAttribute(CURRENT_SESSION_WX_USER, RequestAttributes.SCOPE_SESSION);
        cache.evict(CURRENT_SESSION_WX_USER + openid);
        if (getUserContext() == null) {
            logger.info("清除成功");
        }
    }


    public static String getOpenid() {
        return getUserContext() == null ? "" : getUserContext().getOpenid();
    }

    public static String getUnionid() {
        return getUserContext() == null ? "" : getUserContext().getUnionid();
    }


    public static BasicAccount getAccount() {
        return getUserContext() == null ? null : getUserContext().getWxaccount();
    }

}
