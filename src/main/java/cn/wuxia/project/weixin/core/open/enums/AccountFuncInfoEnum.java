package cn.wuxia.project.weixin.core.open.enums;

import jodd.util.StringUtil;

public enum AccountFuncInfoEnum {
    _1("消息管理权限"),
    _2("用户管理权限"),
    _3("帐号服务权限"),
    _4("网页服务权限"),
    _5("微信小店权限"),
    _6("微信多客服权限"),
    _7("群发与通知权限"),
    _8("微信卡券权限"),
    _9("微信扫一扫权限"),
    _10("微信连WIFI权限"),
    _11("素材管理权限"),
    _12("微信摇周边权限"),
    _13("微信门店权限"),
    _14("微信支付权限"),
    _15("自定义菜单权限"),
    _16("获取认证状态及信息"),
    _17("帐号管理权限（小程序）"),
    _18("开发管理与数据分析权限（小程序）"),
    _19("客服消息管理权限（小程序）"),
    _20("微信登录权限（小程序）"),
    _21("数据分析权限（小程序）"),
    _22("城市服务接口权限"),
    _23("广告管理权限"),
    _24("开放平台帐号管理权限"),
    _25("开放平台帐号管理权限（小程序）"),
    _26("微信电子发票权限");

    private String displayName;

    AccountFuncInfoEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }


    /**
     * @param funcInfo
     * @return
     */
    public static AccountFuncInfoEnum get(String funcInfo) {
        for (AccountFuncInfoEnum infoEnum : AccountFuncInfoEnum.values()) {
            if (StringUtil.equals(infoEnum.name(), "_" + funcInfo)) {
                return infoEnum;
            }
        }
        return null;
    }
}
