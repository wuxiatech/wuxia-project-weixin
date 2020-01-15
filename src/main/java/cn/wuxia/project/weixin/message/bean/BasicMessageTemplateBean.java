/*
 * Created on :2018年2月12日
 * Author     :songlin
 * Change History
 * Version       Date         Author           Reason
 * <Ver.No>     <date>        <who modify>       <reason>
 * Copyright 2014-2020 wuxia.gd.cn All right reserved.
 */
package cn.wuxia.project.weixin.message.bean;

import cn.wuxia.common.exception.ValidateException;
import cn.wuxia.common.util.ListUtil;
import cn.wuxia.common.validator.ValidationEntity;
import cn.wuxia.project.common.bean.SimpleFieldProperty;
import cn.wuxia.project.weixin.message.SendRemindMessageException;
import cn.wuxia.project.weixin.message.util.WechatTemplateMessageUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import lombok.Data;
import javax.validation.constraints.NotBlank;

import java.util.LinkedList;

@Data
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
     * 标题
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
