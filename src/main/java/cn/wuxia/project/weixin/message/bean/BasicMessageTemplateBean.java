/*
* Created on :2018年2月12日
* Author     :songlin
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 wuxia.gd.cn All right reserved.
*/
package cn.wuxia.project.weixin.message.bean;

import java.util.LinkedList;

import cn.wuxia.common.exception.ValidateException;
import cn.wuxia.common.util.ListUtil;
import cn.wuxia.project.weixin.message.SendRemindMessageException;
import cn.wuxia.project.weixin.message.util.WechatTemplateMessageUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import org.hibernate.validator.constraints.NotBlank;

import cn.wuxia.project.common.bean.SimpleFieldProperty;
import cn.wuxia.common.entity.ValidationEntity;

public class BasicMessageTemplateBean extends ValidationEntity {

    /**
     *
     */
    @NotBlank
    private String appId;

    @NotBlank
    private String wxtemplateid;

    public String openid;

    public String mobile;

    /**
     *  标题
     */
    @NotBlank
    private String title;

    /**
     * content
     */
    private String content;

    private LinkedList<SimpleFieldProperty> properties;

    // 备注
    private String remark;

    /**
     * 详情页
     */
    private String detaillink;

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDetaillink() {
        return detaillink;
    }

    public void setDetaillink(String detaillink) {
        this.detaillink = detaillink;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getWxtemplateid() {
        return wxtemplateid;
    }

    public void setWxtemplateid(String wxtemplateid) {
        this.wxtemplateid = wxtemplateid;
    }

    public LinkedList<SimpleFieldProperty> getProperties() {
        return properties;
    }

    public void setProperties(LinkedList<SimpleFieldProperty> properties) {
        this.properties = properties;
    }

    public void addProperty(SimpleFieldProperty property) {
        if (ListUtil.isEmpty(this.properties)) {
            this.properties = Lists.newLinkedList();
        }
        this.properties.add(property);
    }

    @JsonIgnore
    public void send() throws SendRemindMessageException {
        try {
            WechatTemplateMessageUtil.sendRemind(this);
        } catch (ValidateException e) {
            throw new SendRemindMessageException(e);
        }
    }
}
