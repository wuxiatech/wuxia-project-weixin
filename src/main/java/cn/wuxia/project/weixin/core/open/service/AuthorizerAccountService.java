
package cn.wuxia.project.weixin.core.open.service;

import cn.wuxia.project.weixin.core.open.entity.AuthorizerAccount;
import cn.wuxia.project.common.service.CommonService;

public interface AuthorizerAccountService extends CommonService<AuthorizerAccount, String> {

    /**
     * 取消授权
     * @author songlin.li
     * @param authorizerAppid
     */
    public boolean cancelAuthorizer(String authorizerAppid);

    /**
     * 获取授权公众号信息
     * @author songlin
     * @param authorizerAppid
     * @return
     */
    public AuthorizerAccount findAuthorizerByAppid(String authorizerAppid);

    /**
     * 获取授权公众号信息
     * @author songlin
     * @param authorizerAppid
     * @return
     */
    public AuthorizerAccount findAuthorizerByDomain(String serverName);

    /**
     * 公众平台授权给第三方平台后，添加进数据库
     * @author songlin.li
     * @modifiedBy songlin
     * @return
     */
    public AuthorizerAccount addAuthorizerAccount(String authCode, String authorizationDomain);


    /**
     * 根据授权客服授权appid查找
     * @param appid
     * @return
     */
    public AuthorizerAccount findAuthorizerByGateway(String appid);

}
