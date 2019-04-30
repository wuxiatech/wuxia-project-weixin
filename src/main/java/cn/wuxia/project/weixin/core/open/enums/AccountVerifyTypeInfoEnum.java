package cn.wuxia.project.weixin.core.open.enums;

/**
 * 授权方认证类型
 */
public enum AccountVerifyTypeInfoEnum {
    /** 未认证 */
    type1(-1, "未认证"),
    /** 微信认证 */
    type2(0, "微信认证"),
    /** 新浪微博认证 */
    type3(1, "新浪微博认证"),

    /** 腾讯微博认证 */
    type4(2, "腾讯微博认证"),

    /** 已资质认证通过但还未通过名称认证 */
    type5(3, "已资质认证通过但还未通过名称认证"),

    /** 已资质认证通过、还未通过名称认证，但通过了新浪微博认证 */
    type6(4, "已资质认证通过、还未通过名称认证，但通过了新浪微博认证"),

    /** 资质认证通过、还未通过名称认证，但通过了腾讯微博认证 */
    type7(5, "资质认证通过、还未通过名称认证，但通过了腾讯微博认证");

    private Integer code;

    private String desc;

    private AccountVerifyTypeInfoEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;

    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static AccountVerifyTypeInfoEnum getByCode(Integer code) {
        for (AccountVerifyTypeInfoEnum item : AccountVerifyTypeInfoEnum.values()) {
            if (item.code == code) {
                return item;
            }
        }

        return null;
    }

}
