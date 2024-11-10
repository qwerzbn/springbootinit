package com.zbn.springbootinit.constant;

/**
 * 用户常量
 *
 * @author <a href="https://github.com/qwerzbn">zbn</a>
 * @date 2024/06/23
 */
public interface UserConstant {

    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "user_login";

    //  region 权限

    /**
     * 默认角色
     */
    String DEFAULT_ROLE = "user";

    /**
     * 管理员角色
     */
    String ADMIN_ROLE = "admin";

    /**
     * 被封号
     */
    String BAN_ROLE = "ban";

    // endregion
}
