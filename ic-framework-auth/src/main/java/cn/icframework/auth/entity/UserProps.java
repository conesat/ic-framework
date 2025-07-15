package cn.icframework.auth.entity;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户信息
 * @author hzl
 * @since 2021-06-18  13:38:00
 */
@Getter
@Setter
public class UserProps {
    /**
     * 用户id
     */
    private Object userId;
    /**
     * 用户名称
     */
    private Object username;
    /**
     * 用户类型
     */
    private String userType;
    /**
     * 是否超级管理员
     */
    private boolean su = false;
    /**
     * 请求对象
     */
    private HttpServletRequest request;
}
