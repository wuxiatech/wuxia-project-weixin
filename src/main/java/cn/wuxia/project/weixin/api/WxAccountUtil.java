/*
 * Created on :2016年3月31日
 * Author     :Administrator
 * Change History
 * Version       Date         Author           Reason
 * <Ver.No>     <date>        <who modify>       <reason>
 * Copyright 2014-2020 wuxia.tech All right reserved.
 */
package cn.wuxia.project.weixin.api;

import cn.wuxia.common.exception.AppServiceException;
import cn.wuxia.common.spring.SpringContextHolder;
import cn.wuxia.common.util.PropertiesUtils;
import cn.wuxia.common.util.StringUtil;
import cn.wuxia.project.weixin.core.open.entity.AuthorizerAccount;
import cn.wuxia.project.weixin.core.open.service.AuthorizerAccountService;
import cn.wuxia.wechat.Account;
import cn.wuxia.wechat.BasicAccount;
import cn.wuxia.wechat.PayAccount;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

public abstract class WxAccountUtil {
    static Logger logger = LoggerFactory.getLogger(WxAccountUtil.class);

    private static Map<String, Account> accounts = Maps.newHashMap(); // 公众号

    private static Properties properties = PropertiesUtils.loadProperties("classpath*:wechat.config.properties");

    static {
        Enumeration keys = properties.propertyNames();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement().toString();
            if (StringUtil.endsWith(key, ".APP_ID")) {
                String pre = StringUtil.substringBefore(key, ".APP_ID");
                Account account = getAccount(pre);
                for (WxAccountType type : WxAccountType.values()) {
                    if (StringUtil.endsWith(pre, type.toString())) {
                        account.setType(type.toString());
                    }
                }
                logger.info("初始化weixin {}：{}", pre, account);
                accounts.put(pre, account);
            }
        }
    }

    /**
     * 指定前缀
     *
     * @param pre
     * @return
     */
    public static Account getAccount(String pre) {
        Account account = accounts.get(pre);
        if (account == null) {
            account = new Account(properties.getProperty(pre + ".APP_ID"), properties.getProperty(pre + ".APP_SECRET"),
                    properties.getProperty(pre + ".TOKEN"), properties.getProperty(pre + ".MP_ID"));
        }
        return account;
    }

    /**
     * 指定前缀
     *
     * @param appid
     * @return
     */
    public static PayAccount getPayAccount(String appid) {
        logger.info("{}", accounts.size());
        for (Map.Entry<String, Account> set : accounts.entrySet()) {
            Account account = set.getValue();
            logger.info("value:{}", account);
            logger.info("key:{}", set.getKey());
            logger.info("parner:{}", properties.getProperty(set.getKey() + ".PARNER"));
            if (StringUtil.equals(account.getAppid(), appid) && StringUtil.isNotBlank(properties.getProperty(set.getKey() + ".PARNER"))
                    && StringUtil.isNotBlank(properties.getProperty(set.getKey() + ".API_KEY"))) {
                return new PayAccount(account, properties.getProperty(set.getKey() + ".PARNER"), properties.getProperty(set.getKey() + ".API_KEY"));
            }
        }
        if (getAuthorizerAccountService() == null) {
            return null;
        }
        AuthorizerAccount authorizerAccount = getAuthorizerAccountService().findAuthorizerByAppid(appid);
        if (authorizerAccount != null && StringUtil.isNotBlank(authorizerAccount.getWxpayParner())
                && StringUtil.isNotBlank(authorizerAccount.getWxpayApikey())) {
            BasicAccount account = new BasicAccount(authorizerAccount.getAuthorizerAppid(), authorizerAccount.getAuthorizerRefreshToken());
            return new PayAccount(new Account(account), authorizerAccount.getWxpayParner(), authorizerAccount.getWxpayApikey());
        }
        return null;
    }

    private static AuthorizerAccountService authorizerAccountService;

    private static AuthorizerAccountService getAuthorizerAccountService() {
        if (authorizerAccountService == null) {
            try {
                authorizerAccountService = SpringContextHolder.getBean(AuthorizerAccountService.class);
            } catch (IllegalStateException e) {
                logger.warn("", e);
            }
        }
        return authorizerAccountService;
    }


    /**
     * 根据appid查找Account
     *
     * @param appid
     * @return
     */
    public static Account getByAppid(String appid) {
        /**
         * 先查找本地配置是否有配置，否则再查表
         */
        for (Account account : accounts.values()) {
            if (StringUtil.equals(appid, account.getAppid())) {
                return account;
            }
        }
        if (getAuthorizerAccountService() == null) {
            return null;
        }
        AuthorizerAccount authorizerAccount = getAuthorizerAccountService().findAuthorizerByAppid(appid);
        if (authorizerAccount != null) {
            BasicAccount account = new BasicAccount(authorizerAccount.getAuthorizerAppid(), authorizerAccount.getAuthorizerRefreshToken());
            return new Account(account, authorizerAccount.getAuthorizerAppid(), authorizerAccount.getUserName());
        } else {
            throw new AppServiceException("无法根据appid：{}找到公众号信息！", appid);
        }
    }


    public static void main(String[] args) {
        System.out.println(getAccount("www"));
    }
}
