package cn.wuxia.project.weixin.core.open.enums;

/**
 * 授权状态
 */
public enum AccountStatusEnum {
    //取消授权
    UNAUTHORIZED("unauthorized"),
    //已授权
    AUTHORIZED("authorized");

    private String status;


    private AccountStatusEnum(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

}
