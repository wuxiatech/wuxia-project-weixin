package cn.wuxia.project.weixin.message.util;

import cn.wuxia.common.exception.ValidateException;
import cn.wuxia.common.util.ListUtil;
import cn.wuxia.common.util.StringUtil;
import cn.wuxia.project.weixin.api.WxAccountUtil;
import cn.wuxia.project.weixin.message.SendRemindMessageException;
import cn.wuxia.project.weixin.message.bean.BasicMessageTemplateBean;
import cn.wuxia.project.common.bean.SimpleFieldProperty;
import cn.wuxia.wechat.custom.bean.TemplateDataBean;
import cn.wuxia.wechat.custom.util.TemplateUtil;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class WechatTemplateMessageUtil {

    protected static final Logger logger = LoggerFactory.getLogger(WechatTemplateMessageUtil.class);

//    private static ZbusProducerHandler producerHandler = SpringContextHolder.getBean(ZbusProducerHandler.class);

    /**
     * 发送提醒
     *
     * @param message
     */
    public static void sendRemind(final BasicMessageTemplateBean message) throws SendRemindMessageException, ValidateException {
        message.validate();

        try {
            /**
             * 发送消息
             */

            if (StringUtil.isNotBlank(message.getOpenid())) {

                TemplateUtil.send(WxAccountUtil.getByAppid(message.getAppId()), message.getOpenid(), message.getWxtemplateid(),
                        message.getDetaillink(), templateMessageData(message));
                logger.info("微信消息发送完成,{}{}{}", message.getOpenid(), message.getTitle(), message.getContent());
            }
            if (StringUtil.isNotBlank(message.getMobile())) {
                logger.info(message.getContent());
//                ZbusProducer producer = producerHandler.create(message.getAppId() + "_ToCall");
//                //ToCallUtil.msg2call(message.getAppId(), message.getMobile(), message.getContent());
//
//                Map<String, String> m = Maps.newHashMap();
//                m.put("appid", message.getAppId());
//                m.put("mobile", message.getMobile());
//                m.put("message", message.getContent());
//                producer.tag("message2call").send(new ZbusMessage(JsonUtil.toJson(m)));
//                logger.info("电话消息发送完成,{}{}{}", message.getMobile(), message.getTitle(), message.getContent());
            }
        } catch (Exception e) {
            logger.error("send message to call error : " + e);
            throw new SendRemindMessageException("发送失败：", e);
        }
        logger.info("发送完成");
    }

    /**
     * 转换微信模板
     * @param message
     * @return
     */
    public static List<TemplateDataBean> templateMessageData(final BasicMessageTemplateBean message) {

        List<TemplateDataBean> dataList = Lists.newArrayList();
        //标题
        TemplateDataBean b = new TemplateDataBean();
        b.setName("first");
        b.setColor("#CC3333");
        b.setValue(message.getTitle());
        dataList.add(b);

        if (ListUtil.isNotEmpty(message.getProperties())) {
            int i = 1;
            for (SimpleFieldProperty fieldProperty : message.getProperties()) {
                logger.debug("配置：{}", fieldProperty);
                TemplateDataBean b1 = new TemplateDataBean();
                /**
                 * 如果不为空则复写
                 */

                b1.setName("keyword" + i++);

                b1.setColor("#777777");

                b1.setValue(fieldProperty.getStringValue());

                logger.debug("模板字段：{}", b1);
                dataList.add(b1);
            }
        }
        //备注
        TemplateDataBean b5 = new TemplateDataBean();
        b5.setName("remark");
        b5.setColor("#4D8AB3");
        b5.setValue(message.getRemark());
        dataList.add(b5);

        return dataList;
    }
}
