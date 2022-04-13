package com.wuxin.blog.mode;

import com.wuxin.blog.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: wuxin001
 * @Date: 2022/02/07/0:49
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Blogger {
    private String nickname;
    private String motto;
    private String avatar;

    public static Blogger getBlogger(User user){
        Blogger blogger = new Blogger();
        blogger.setAvatar(user.getAvatar());
        blogger.setNickname(user.getNickname());
        blogger.setMotto(user.getMotto());
        return blogger;
    }

}
