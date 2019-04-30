/*
* Created on :2018年2月6日
* Author     :songlin
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 wuxia.gd.cn All right reserved.
*/
package cn.wuxia.project.weixin.message;

public class SendRemindMessageException extends Exception {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = -8925944466280674002L;
    public SendRemindMessageException(String msg) {
        super(msg);
    }

    public SendRemindMessageException(String message, Throwable e) {
        super(message, e);
    }

    public SendRemindMessageException(Throwable e) {
        super(e);
    }

}
