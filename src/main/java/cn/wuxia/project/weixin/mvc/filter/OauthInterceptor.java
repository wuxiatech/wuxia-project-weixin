package cn.wuxia.project.weixin.mvc.filter;

import cn.wuxia.common.spring.SpringContextHolder;
import cn.wuxia.common.util.*;
import cn.wuxia.project.basic.core.conf.enums.PointActionEnum;
import cn.wuxia.project.basic.core.conf.support.DTools;
import cn.wuxia.project.basic.support.ApplicationPropertiesUtil;
import cn.wuxia.project.basic.support.DConstants;
import cn.wuxia.project.basic.support.LogIt;
import cn.wuxia.project.common.security.UserContext;
import cn.wuxia.project.common.security.UserContextUtil;
import cn.wuxia.project.weixin.WxUserContext;
import cn.wuxia.project.weixin.WxUserContextUtil;
import cn.wuxia.project.weixin.api.WxAccountUtil;
import cn.wuxia.project.weixin.core.user.RegisterParameter;
import cn.wuxia.project.weixin.core.user.WxUser;
import cn.wuxia.project.weixin.core.user.WxUserService;
import cn.wuxia.wechat.Account;
import cn.wuxia.wechat.WeChatException;
import cn.wuxia.wechat.oauth.bean.AuthUserInfoBean;
import cn.wuxia.wechat.oauth.bean.OAuthTokeVo;
import cn.wuxia.wechat.oauth.util.LoginUtil;
import cn.wuxia.wechat.open.util.ProxyLoginUtil;
import cn.wuxia.wechat.open.util.ThirdBaseUtil;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * 拦截器
 *
 * @author songlin.li
 */
public class OauthInterceptor implements HandlerInterceptor, InitializingBean {
    private static Logger logger = LoggerFactory.getLogger(OauthInterceptor.class);

    private String oauthuri;

//    @Autowired
//    private SecurityRolePermissionsService rolePermissionsService;


    @Value("${system.type:''}")
    private String system;

    /**
     * preHandle方法是进行处理器拦截用的，顾名思义，该方法将在Controller处理之前进行调用，SpringMVC中的Interceptor拦截器是链式的，可以同时存在
     * 多个Interceptor，然后SpringMVC会根据声明的前后顺序一个接一个的执行，而且所有的Interceptor中的preHandle方法都会在
     * Controller方法调用之前调用。SpringMVC的这种Interceptor链式结构也是可以进行中断的，这种中断方式是令preHandle的返
     * 回值为false，当preHandle的返回值为false的时候整个请求就结束了。
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        system = ApplicationPropertiesUtil.getValue("system.type");
        logger.info("{}请求来源：{}  请求地址：{} ；忽略参数", system, BrowserUtils.checkBrowse(request), uri);
        /**
         * 暂所有经过微信的非api请求都需要微信授权登录
         */
        boolean isContinue = true;
        long starttime = System.currentTimeMillis();
        if (BrowserUtils.isWeiXin(request)) {
            //获取该url需要的权限
//            Collection<ConfigAttribute> atts = Lists.newArrayList();
            // if uri matchs  need login uri
//            List<ResourcesDto> resources = rolePermissionsService.findResourcesByRoleName(LoginResourceType.NOT_NEED_WX_LOGIN.name());
//            if (ListUtil.isNotEmpty(resources)) {
//                for (ResourcesDto dburi : resources) {
//                    RequestMatcher pathMatcher = new AntPathRequestMatcher(dburi.getUri());
//                    if (pathMatcher.matches(request) && dburi.getSystemType().equals(system)) {
//                        logger.info("{} {}跳过登录", system, uri);
//                        return isContinue;
//                    }
//                }
//            }
            /**
             * 打点打开链接
             */
            LogIt.action(PointActionEnum.open_invitation_register_url);
            /**
             * 如果是微信则注册
             */
            isContinue = oauth(request, response);
        }
        long endtime = System.currentTimeMillis();
        logger.info("current oauth cost time:{}ms", endtime - starttime);
        return isContinue;
    }

    /**
     * 这个方法只会在当前这个Interceptor的preHandle方法返回值为true的时候才会执行。postHandle是进行处理器拦截用的，它的执行时间是在处理器进行处理之
     * 后，也就是在Controller的方法调用之后执行，但是它会在DispatcherServlet进行视图的渲染之前执行，也就是说在这个方法中你可以对ModelAndView进行操
     * 作。这个方法的链式结构跟正常访问的方向是相反的，也就是说先声明的Interceptor拦截器该方法反而会后调用，这跟Struts2里面的拦截器的执行过程有点像，
     * 只是Struts2里面的intercept方法中要手动的调用ActionInvocation的invoke方法，Struts2中调用ActionInvocation的invoke方法就是调用下一个Interceptor
     * 或者是调用action，然后要在Interceptor之前调用的内容都写在调用invoke之前，要在Interceptor之后调用的内容都写在调用invoke方法之后。
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }

    /**
     * 从cas/WechatLoginController 传过来的授权获取用户信息的参数
     */
    public final String WX_CODE = "wxcode";

    public final String PARAM_WX_APPID = "wxappid";

    public final String PARAM_WX_OPEN_APPID = "wxopenappid";

    public final String PARAM_CALLBACK = "callback";

    public final String PARAM_SCOPE = "scope";

    public final String OPENID_COOKIE = "_openid";

    protected String getWxAppid(HttpServletRequest request) {
        String wxappid = DTools.dic(DConstants.getPlatform());
        logger.info("appid:{}", wxappid);
        request.setAttribute("appid", wxappid);
        return wxappid;
    }

    /**
     * 授权登录
     *
     * @return
     * @throws IOException
     * @author songlin.li
     */
    public boolean oauth(HttpServletRequest request, HttpServletResponse response) throws IOException, WeChatException {
        final String uri = request.getScheme() + "://" + request.getServerName()
                + (request.getServerPort() == 80 ? "" : ":" + request.getServerPort()) + request.getRequestURI();
        String openId = null;
        if (StringUtil.indexOf(request.getServerName(), "127.0.0.1") == 0 || StringUtil.indexOf(request.getServerName(), "localhost") == 0
                || StringUtil.indexOf(request.getServerName(), "192.168.") == 0) {
            /**
             *
             * 每次读取文件消耗性能(注：生产不可有该文件)
             */
            Properties testProp = PropertiesUtils.loadProperties("classpath:test.properties");
            openId = testProp.getProperty("openid");
        }
        /**
         * 为了解决每次登录的问题, 不安全的做法
         */
//        Cookie openidCookie = ServletUtils.getCookie(request, getPlatform(request) + OPENID_COOKIE);
//        if (openidCookie != null) {
//            openId = openidCookie.getValue();
//        }
        WxUserContext userContext = null;
        if (openId == null) {
            userContext = WxUserContextUtil.getUserContext();
            logger.debug("用户信息：{}", userContext);
        }
        //为空考虑是否使用测试账号
        if (userContext != null) {
            logger.debug("当前用户{}已登录，跳过授权验证; openid={}", userContext.getName(), userContext.getOpenid());
            return true;
        }
        String wxcode = request.getHeader(WX_CODE);
        if (StringUtil.isBlank(wxcode)) {
            wxcode = request.getParameter(WX_CODE);
            logger.debug("--------parameter wxcode :  {}", wxcode);
            request.setAttribute(WX_CODE, null);
        }
        OAuthTokeVo oauthToken = null;
        Account account = WxAccountUtil.getByAppid(getWxAppid(request));

        //授权登录之后获取用户的基础信息
        if (StringUtil.isBlank(openId) && StringUtil.isNotBlank(wxcode)) {
            logger.debug("-------------通过微信授权，cas返回的授权code为：{}", wxcode);
            try {
                if (account.isAuthorizedToThird()) {
                    oauthToken = ProxyLoginUtil.authUser(account, wxcode);
                } else {
                    oauthToken = LoginUtil.authUser(account, wxcode);
                }

                logger.debug("-------------根据微信授权code获取用户信息：{}", oauthToken);
                openId = oauthToken.getOpenId();

            } catch (WeChatException e) {
                /**
                 * 可能wxcode为自带参数值（比如过期刷新），需要忽略处理
                 * FIXED
                 */
                logger.warn("可能是刷新旧页面导致wxcode为过期的，尝试去掉此参数");
                return false;
            }
        }
        logger.info("+++++++++++++++++++++微信openId：" + openId);

        //只有微信客户端时方执行授权登录(附件条件为openid为空， 初始授权)
        //code 为空避免拉取用户信息方法报错无法获取openid导致循环重定向
        if (StringUtil.isBlank(openId) && StringUtil.isBlank(wxcode)) {
            //微信重定向连接
            final StringBuffer callback = new StringBuffer(uri)
                    .append((StringUtil.isBlank(request.getQueryString()) ? "" : "?" + request.getQueryString()));

            //如果openId是空，则向CAS请求授权登录（静默授权）
            logger.info("尝试微信静默授权当前URL：{}", callback);
            StringBuffer oauthurl = new StringBuffer(oauthuri).append("?").append(PARAM_WX_APPID).append("=").append(account.getAppid()).append("&")
                    .append(PARAM_CALLBACK).append("=").append(EncodeUtils.base64Encode(callback.toString().getBytes()));
            if (account.isAuthorizedToThird()) {
                oauthurl.append("&").append(PARAM_WX_OPEN_APPID).append("=").append(ThirdBaseUtil.OPEN_APPID);
            }
            logger.debug("访问：" + oauthurl.toString());
            response.sendRedirect(oauthurl.toString());
            return false;
        }
        logger.info("---------------------微信openId：" + openId);
        /**
         * 到达此步，openid不可为空，如为空要么微信请求问题要么就是本地测试，暂忽略不再继续代码执行
         */
        if (StringUtil.isBlank(openId)) {
            logger.error("unionId不可为空，如为空要么微信请求问题要么就是本地测试，暂忽略不再继续代码执行");
            return true;
        }
        WxUser user = getUserService().findByOpenid(openId);
        //判断用户是否存在
        if (user == null) {
            logger.info("用户不存在，则需要注册用户");
            /*
             * 当用户不存在，则需要注册用户
             * 此时判断是否为静默授权登录（
             *          oauthToken的scope属性为snsapi_base时表示是静默授权登录,
             *          为snsapi_userinfo时表示是弹出授权页面登录，可以拿到昵称、性别、所在地等信息）
             */
            //如果oauthToken为空（不管为何），都将进行授权登录，授权后oauthToken则不能为空，否则授权有问题
            logger.info("当前微信授权处理模式是：{}", oauthToken == null ? "" : oauthToken.getScope());
            if (oauthToken == null || StringUtil.equals("snsapi_base", oauthToken.getScope())) {
                //微信重定向连接
                final StringBuffer callback = new StringBuffer(uri);
                if (StringUtil.isNotBlank(request.getQueryString())) {
                    Map<String, Object> param = ServletUtils.getUrlParams(request.getQueryString());
                    if (MapUtil.isNotEmpty(param) && param.containsKey(WX_CODE)) {
                        param.remove(WX_CODE);
                        if (MapUtil.isNotEmpty(param)) {
                            logger.debug("尝试微信非静默授权当前URL（获取）参数为:{} ", param.toString());
                            String queryString = ServletUtils.getUrlParamsByMap(param);
                            //微信重定向连接
                            callback.append("?").append(queryString);
                            logger.info("尝试微信非静默授权当前URL（获取）：{}", callback.toString());
                        }
                    }
                }
                //向微信请求授权登录（弹出授权页面）
                StringBuffer oauthurl = new StringBuffer(getOauthuri()).append("?").append(PARAM_WX_APPID).append("=").append(account.getAppid())
                        .append("&").append(PARAM_CALLBACK).append("=").append(EncodeUtils.base64Encode(callback.toString().getBytes())).append("&")
                        .append(PARAM_SCOPE).append("=").append("true");
                if (account.isAuthorizedToThird()) {
                    oauthurl.append("&").append(PARAM_WX_OPEN_APPID).append("=").append(ThirdBaseUtil.OPEN_APPID);
                }
                logger.debug("非静默访问：{}", oauthurl);
                response.sendRedirect(oauthurl.toString());
                return false;
            }
            RegisterParameter parameter = new RegisterParameter();
            parameter.setRegisterFrom(uri);
            try {
                UserAgent userAgent = ServletUtils.getUserAgent(request);
                parameter.setDeviceInfo(userAgent.getOperatingSystem().getName());
                parameter.setClientInfo(userAgent.getBrowser().getName());
                parameter.setWechatInfo(BrowserUtils.getWeiXinVersion(request));
            } catch (Exception e) {
            }
            //根据用户的openId注册游客账号
            AuthUserInfoBean authInfo = null;
            if (account.isAuthorizedToThird()) {
                authInfo = ProxyLoginUtil.getAuthUserInfo(oauthToken);
            } else {
                authInfo = LoginUtil.getAuthUserInfo(oauthToken);
            }
            authInfo.setAppid(account.getAppid());
            user = getUserService().registerUser(authInfo, parameter);

            /**
             * 授权之后
             */
            LogIt.action(PointActionEnum.oauth_invitation_register_url);
        }

        /**
         * 用户已存在
         */
        if (null != user) {
            logger.debug("用户信息{}", ToStringBuilder.reflectionToString(user));
//
//            WxUserContext wxUserContext = new WxUserContext();
//            wxUserContext.setWxaccount(account);
//            wxUserContext.setId("" + user.getId());
//            wxUserContext.setMobile(user.getMobile());
//            wxUserContext.setName(user.getNickName());
//            wxUserContext.setHeadImg(user.getAvatar());
//
//            userContext = wxUserContext;
//
//
//            logger.info("当前用户的类型{}，userId:{}", userContext.getClass().getName(), userContext.getId());
//            userContext.setOpenid(openId);
//            userContext.setUnionid(unionId);
//            /**
//             * 赋权
//             */
////            final List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
////            grantedAuthorities.add(new SimpleGrantedAuthority(LoginResourceType.NEED_WX_LOGIN.name()));
////            userContext.setAuthorities(grantedAuthorities);
//            UserContextUtil.saveUserContext(userContext);
//
//            ServletUtils.setCookie(response, getPlatform(request) + OPENID_COOKIE, openId, "", 8 * 60 * 60);
//
            WxUserContext wxUserContext = new WxUserContext(user.getUid(), user.getNickName(), user.getMobile());
            wxUserContext.setWxaccount(account);
            wxUserContext.setOpenid(user.getOpenid());
            wxUserContext.setUnionid(user.getUnionid());
            WxUserContextUtil.saveUserContext(wxUserContext);
//            ServletUtils.setCookie(response, getPlatform(request) + OPENID_COOKIE, openId, "", 8 * 60 * 60);
        }


        //继续处理当前请求
        return true;
    }


    private WxUserService getUserService() {
        return SpringContextHolder.getBean(WxUserService.class);
    }


    /**
     * oauthurl keyname
     */
    public final String CAS_CTX = "wechat_oauth_url";


    public String getOauthuri() {
        return oauthuri;
    }

    public void setOauthuri(String oauthuri) {
        this.oauthuri = oauthuri;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        if (StringUtil.isBlank(oauthuri)) {
            /**
             * 默认配置
             */
            oauthuri = ApplicationPropertiesUtil.getPropertiesValue(CAS_CTX);
            if (StringUtil.isBlank(oauthuri)) {
                logger.warn("字典缺少code={}或者application.properties缺少{}的数据, 请新增", CAS_CTX, CAS_CTX);
            }
        } else if (StringUtil.isNotBlank(oauthuri) && !StringUtil.startsWithIgnoreCase(oauthuri, "http")) {
            oauthuri = DTools.dic(oauthuri);
            if (StringUtil.isBlank(oauthuri)) {
                logger.warn("字典缺少code={}的数据, 请检查配置", oauthuri);
            }
        }
    }
}
