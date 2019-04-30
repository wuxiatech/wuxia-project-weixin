/*
* Created on :2014年11月11日
* Author     :wuwenhao
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 www.ibmall.cn All right reserved.
*/
package cn.wuxia.project.weixin.core.open.service.impl;

import java.util.List;
import java.util.Map;

import cn.wuxia.project.weixin.core.open.entity.AuthorizerAccount;
import cn.wuxia.project.weixin.core.open.enums.AccountServiceTypeInfoEnum;
import cn.wuxia.project.weixin.core.open.enums.AccountStatusEnum;
import cn.wuxia.project.weixin.core.open.enums.AccountVerifyTypeInfoEnum;
import cn.wuxia.project.weixin.core.open.dao.AuthorizerAccountDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import cn.wuxia.project.weixin.core.open.service.AuthorizerAccountService;
import cn.wuxia.project.common.dao.CommonDao;
import cn.wuxia.project.common.service.impl.CommonServiceImpl;
import cn.wuxia.project.common.support.CacheConstants;
import cn.wuxia.project.common.support.Constants;
import cn.wuxia.common.exception.AppServiceException;
import cn.wuxia.common.util.ListUtil;
import cn.wuxia.common.util.MapUtil;
import cn.wuxia.common.util.reflection.ConvertUtil;
import cn.wuxia.wechat.open.util.FullWebUtil;
import cn.wuxia.wechat.open.util.ProxyOAuthUtil;

@Service
@Transactional
public class AuthorizerAccountServiceImpl extends CommonServiceImpl<AuthorizerAccount, String> implements AuthorizerAccountService {

    @Autowired
    private AuthorizerAccountDao authorizerDao;

    @Override
    protected CommonDao<AuthorizerAccount, String> getCommonDao() {
        return authorizerDao;
    }

    /**
     * FIXME 清除特定分区的缓存
     */
    @Override
    @CacheEvict(value = CacheConstants.CACHED_VALUE_1_DAY, allEntries = true)
    public boolean cancelAuthorizer(String authorizerAppid) {
        //取消授权

        logger.info("取消授权");
        try {
            AuthorizerAccount account = authorizerDao.findUniqueBy("authorizerAppid", authorizerAppid);
            if (account != null) {
                account.setStatus(AccountStatusEnum.UNAUTHORIZED.getStatus());
                super.update(account);
            }
            return true;
        } catch (Exception e) {
            logger.error("", e);
            return false;
        }
    }

    /**
     * FIXME 清除特定分区的缓存
     */
    @Override
    @CacheEvict(value = CacheConstants.CACHED_VALUE_1_DAY, allEntries = true)
    public AuthorizerAccount addAuthorizerAccount(String authCode, String authorizationDomain) {
        Assert.hasText(authCode, "authCode 参数不正确");
        Assert.hasText(authorizationDomain, "authorizationDomain 参数不正确");
        //取公众号授权信息
        Map<String, Object> map = null;
        try {
            map = ProxyOAuthUtil.apiQueryAuth(authCode);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Map<String, Object> authorization_info = (Map<String, Object>) map.get("authorization_info");
        String authorizerAppid = (String) authorization_info.get("authorizer_appid");
        String authorizerRefreshToken = (String) authorization_info.get("authorizer_refresh_token");
        List<Map<String, Object>> func_info = (List<Map<String, Object>>) authorization_info.get("func_info");
        StringBuffer funcInfo = new StringBuffer("");
        for (Map<String, Object> item : func_info) {
            Map<String, Integer> funcscope_category = (Map<String, Integer>) item.get("funcscope_category");
            funcInfo.append(funcscope_category.get("id") + ",");
        }
        //取公众号账户信息
        Map<String, Object> infoMap;
        try {
            infoMap = ProxyOAuthUtil.apiGetAuthorizerInfo(authorizerAppid);
        } catch (Exception e) {
            throw new AppServiceException("", e);
        }
        Map<String, Object> authorizer_info = (Map<String, Object>) infoMap.get("authorizer_info");
        String nickName = (String) authorizer_info.get("nick_name");
        String headImg = (String) authorizer_info.get("head_img");
        String userName = (String) authorizer_info.get("user_name");
        String principalName = (String) authorizer_info.get("principal_name");
        String businessInfo = authorizer_info.get("business_info").toString();
        String alias = (String) authorizer_info.get("alias");
        String qrcodeUrl = (String) authorizer_info.get("qrcode_url");
        String signature = (String) authorizer_info.get("signature");
        Map miniProgramInfo = (Map) authorizer_info.get("MiniProgramInfo");
        Map<String, Integer> service_type_info = (Map<String, Integer>) authorizer_info.get("service_type_info");
        Integer serviceTypeInfo = service_type_info.get("id");
        Map<String, Integer> verify_type_info = (Map<String, Integer>) authorizer_info.get("verify_type_info");
        Integer verifyTypeInfo = verify_type_info.get("id");
        AuthorizerAccount account = authorizerDao.findUniqueBy("authorizerAppid", authorizerAppid);

        //存入数据库
        if (account == null) {
            account = new AuthorizerAccount();
        } else {
            //是否重新授权
            //            if (!StringUtil.equals(authorizationDomain, account.getAuthorizationDomain())) {
            //                result.put("success", false);
            //                result.put("msg", "绑定失败，公众号已绑定其他域名！");
            //                return result;
            //            }
        }
        account.setAuthorizerAppid(authorizerAppid);
        account.setAuthorizerRefreshToken(authorizerRefreshToken);
        account.setFuncInfo(funcInfo.toString());
        account.setStatus(AccountStatusEnum.AUTHORIZED.getStatus());
        account.setNickName(nickName);
        account.setHeadImg(headImg);
        account.setServiceTypeInfo(AccountServiceTypeInfoEnum.getByCode(serviceTypeInfo));
        account.setVerifyTypeInfo(AccountVerifyTypeInfoEnum.getByCode(verifyTypeInfo));
        account.setUserName(userName);
        account.setPrincipalName(principalName);
        account.setAlias(alias);
        account.setQrcodeUrl(qrcodeUrl);
        account.setBusinessInfo(businessInfo);
        account.setMiniProgramInfo(MapUtil.isNotEmpty(miniProgramInfo) ? miniProgramInfo.toString() : null);
        account.setAuthorizationDomain(authorizationDomain);
        account.setYunkefuSecret(account.getId());
        account.setYunkefuToken(account.getAlias());
        if (FullWebUtil.checkAccount(authorizerAppid, userName)) {
            logger.info("全网发布测试号请求授权");
            account.setCreatedBy("full web test");
        }

        super.save(account);
        //Cache cache =  CacheSupport.getCache(Constants.CACHED_VALUE_BASE);
        /**
         * classcn.daoming.basic.core.open.service.impl.AuthorizerAccountServiceImpl.findAuthorizerByAppidwxdc282971b0b8af0a
         */
        //cache.evict();
        return account;

    }

    /**
     * FIXME 将数据缓存到指定分区
     */
    @Cacheable(key = Constants.CACHED_KEY_DEFAULT + "+#authorizerAppid", value = CacheConstants.CACHED_VALUE_1_DAY)
    @Override
    public AuthorizerAccount findAuthorizerByAppid(String authorizerAppid) {
        AuthorizerAccount account = authorizerDao.findUniqueBy("authorizerAppid", authorizerAppid);
        if (account == null)
            logger.warn("找不到该公众号[appid={}]", authorizerAppid);
        return account;
    }

    /**
     * FIXME 将数据缓存到指定分区
     */
    @Cacheable(key = Constants.CACHED_KEY_DEFAULT + "+#serverName", value = CacheConstants.CACHED_VALUE_1_DAY)
    @Override
    public AuthorizerAccount findAuthorizerByDomain(String serverName) {
        List<AuthorizerAccount> accounts = authorizerDao.findByDomain(serverName);
        if (ListUtil.isEmpty(accounts)) {
            logger.info("无法根据域名{}查找到公众号", serverName);
            return null;
        }
        if (accounts.size() > 1) {
            logger.warn("存在多个公众号{}绑定域名：{}", ConvertUtil.convertElementPropertyToString(accounts, "authorizerAppid", ", "), serverName);
        }
        return accounts.get(0);
    }

    /**
     * FIXME 将数据缓存到指定分区
     */
    @Cacheable(key = Constants.CACHED_KEY_DEFAULT + "+#appid", value = CacheConstants.CACHED_VALUE_1_DAY)
    @Override
    public AuthorizerAccount findAuthorizerByGateway(String appid) {
        List<AuthorizerAccount> accounts = authorizerDao.findLikeByGateway(appid);
        if (ListUtil.isEmpty(accounts)) {
            logger.info("无法根据agent appid{}查找到公众号", appid);
            return null;
        }
        if (accounts.size() > 1) {
            logger.warn("存在多个公众号{}绑定域名：{}", ConvertUtil.convertElementPropertyToString(accounts, "authorizerAppid", ", "), appid);
        }
        return accounts.get(0);
    }
}
