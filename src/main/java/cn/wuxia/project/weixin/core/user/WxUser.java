package cn.wuxia.project.weixin.core.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WxUser implements Serializable {
    private String openid;
    private String unionid;
    private String uid;
    private String nickName;
    private String mobile;
}
