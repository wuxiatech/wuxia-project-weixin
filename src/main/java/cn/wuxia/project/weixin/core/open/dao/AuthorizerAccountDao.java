/*
* Created on :2014年11月11日
* Author     :wuwenhao
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 wuxia.tech All right reserved.
*/
package cn.wuxia.project.weixin.core.open.dao;

import java.util.List;

import cn.wuxia.project.basic.core.common.BaseCommonDao;
import cn.wuxia.project.weixin.core.open.entity.AuthorizerAccount;
import org.springframework.stereotype.Component;

@Component
public class AuthorizerAccountDao extends BaseCommonDao<AuthorizerAccount, String> {

    /**
     * 反查询
     * @author songlin
     * @param serverName
     * @return
     */
    public List<AuthorizerAccount> findByDomain(String serverName) {
        String sql = "select * from wx_authorizer_account where FIND_IN_SET(?, authorization_domain) > 0";
        return query(sql, AuthorizerAccount.class, serverName);
    }

    /**
     * 根据授权的得到的appid查找
     * @param appid
     * @return
     */
    public List<AuthorizerAccount> findLikeByGateway(String appid) {
        String sql = "select * from wx_authorizer_account where  LOCATE(?, yunkefu_Gateway) > 0";
        return query(sql, AuthorizerAccount.class, appid);
    }

}
