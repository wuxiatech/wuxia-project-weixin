package cn.wuxia.project.weixin.core.open.enums;

/**
 * 授权方公众号类型
 */
public enum AccountServiceTypeInfoEnum {
    /** 订阅号 */
    subscribe(0, "订阅号"),
    /** 由历史老帐号升级后的订阅号 */
    subscribeUp(1, "由历史老帐号升级后的订阅号"),
    /** 服务号 */
    service(2, "服务号");

    private Integer code;

    private String desc;

    private AccountServiceTypeInfoEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;

    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static AccountServiceTypeInfoEnum getByCode(Integer code) {
        for (AccountServiceTypeInfoEnum item : AccountServiceTypeInfoEnum.values()) {
            if (item.code == code) {
                return item;
            }
        }

        return null;
    }

}
