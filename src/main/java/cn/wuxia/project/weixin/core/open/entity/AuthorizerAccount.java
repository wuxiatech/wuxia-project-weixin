package cn.wuxia.project.weixin.core.open.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import cn.wuxia.project.weixin.core.open.enums.AccountServiceTypeInfoEnum;
import cn.wuxia.project.weixin.core.open.enums.AccountVerifyTypeInfoEnum;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;

import cn.wuxia.project.common.model.ModifyInfoEntity;

/**
 * 微信公众帐号
 * @author guwen
 *
 */
@Entity
@Table(name = "wx_authorizer_account")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Where(clause = ModifyInfoEntity.ISOBSOLETE_DATE_IS_NULL)
public class AuthorizerAccount extends ModifyInfoEntity {

    private static final long serialVersionUID = -2963595547748318582L;

    private String authorizerAppid;

    private String authorizerRefreshToken;

    private String funcInfo;

    private String status;

    private String miniProgramInfo;

    private String signature;

    private String nickName;

    private String headImg;

    private AccountServiceTypeInfoEnum serviceTypeInfo;

    private AccountVerifyTypeInfoEnum verifyTypeInfo;

    private String userName;

    private String principalName;

    private String businessInfo;

    private String alias;

    private String qrcodeUrl;

    private String yunkefuSecret;

    private String yunkefuToken;

    private String yunkefuGateway;

    /**
     * 授权域名
     */
    private String authorizationDomain;


    private String wxpayParner;

    private String wxpayApikey;

    @Column(name = "nick_name")
    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Column(name = "head_img")
    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type_info")
    public AccountServiceTypeInfoEnum getServiceTypeInfo() {
        return serviceTypeInfo;
    }

    public void setServiceTypeInfo(AccountServiceTypeInfoEnum serviceTypeInfo) {
        this.serviceTypeInfo = serviceTypeInfo;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "verify_type_info")
    public AccountVerifyTypeInfoEnum getVerifyTypeInfo() {
        return verifyTypeInfo;
    }

    public void setVerifyTypeInfo(AccountVerifyTypeInfoEnum verifyTypeInfo) {
        this.verifyTypeInfo = verifyTypeInfo;
    }

    @Column(name = "user_name")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Column(name = "alias")
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Column(name = "qrcode_url")
    public String getQrcodeUrl() {
        return qrcodeUrl;
    }

    public void setQrcodeUrl(String qrcodeUrl) {
        this.qrcodeUrl = qrcodeUrl;
    }

    @Column(name = "authorizer_appid")
    public String getAuthorizerAppid() {
        return authorizerAppid;
    }

    public void setAuthorizerAppid(String authorizerAppid) {
        this.authorizerAppid = authorizerAppid;
    }

    @Column(name = "authorizer_refresh_token")
    public String getAuthorizerRefreshToken() {
        return authorizerRefreshToken;
    }

    public void setAuthorizerRefreshToken(String authorizerRefreshToken) {
        this.authorizerRefreshToken = authorizerRefreshToken;
    }

    @Column(name = "func_info")
    public String getFuncInfo() {
        return funcInfo;
    }

    public void setFuncInfo(String funcInfo) {
        this.funcInfo = funcInfo;
    }

    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name = "miniprograminfo")
    public String getMiniProgramInfo() {
        return miniProgramInfo;
    }

    public void setMiniProgramInfo(String miniProgramInfo) {
        this.miniProgramInfo = miniProgramInfo;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Column(name = "principal_name")
    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    @Column(name = "business_info")
    public String getBusinessInfo() {
        return businessInfo;
    }

    public void setBusinessInfo(String businessInfo) {
        this.businessInfo = businessInfo;
    }

    @Column(name = "authorization_domain")
    public String getAuthorizationDomain() {
        return authorizationDomain;
    }

    public void setAuthorizationDomain(String authorizationDomain) {
        this.authorizationDomain = authorizationDomain;
    }

    @Column(name = "yunkefu_secret")
    public String getYunkefuSecret() {
        return yunkefuSecret;
    }

    public void setYunkefuSecret(String yunkefuSecret) {
        this.yunkefuSecret = yunkefuSecret;
    }

    @Column(name = "yunkefu_token")
    public String getYunkefuToken() {
        return yunkefuToken;
    }

    public void setYunkefuToken(String yunkefuToken) {
        this.yunkefuToken = yunkefuToken;
    }

    @Column(name = "yunkefu_gateway")
    public String getYunkefuGateway() {
        return yunkefuGateway;
    }

    public void setYunkefuGateway(String yunkefuGateway) {
        this.yunkefuGateway = yunkefuGateway;
    }

    @Column(name = "wxpay_parner")
    public String getWxpayParner() {
        return wxpayParner;
    }

    public void setWxpayParner(String wxpayParner) {
        this.wxpayParner = wxpayParner;
    }

    @Column(name = "wxpay_apikey")
    public String getWxpayApikey() {
        return wxpayApikey;
    }

    public void setWxpayApikey(String wxpayApikey) {
        this.wxpayApikey = wxpayApikey;
    }
}
