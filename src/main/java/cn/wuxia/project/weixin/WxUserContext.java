package cn.wuxia.project.weixin;

import cn.wuxia.project.common.security.UserContext;
import cn.wuxia.wechat.BasicAccount;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Collection;

@Setter
@Getter
public class WxUserContext extends UserContext implements Serializable {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;


    String openid;

    String unionid;

    BasicAccount wxaccount;

    public WxUserContext(String id, String name, String mobile, String headImg, Collection<String> authorities) {
        super(id, name, mobile, headImg, authorities);
    }

    public WxUserContext(String id, String name, String mobile) {
        super(id, name, mobile);
    }


    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
