package com.neu.tms.utils;

import com.neu.tms.pojo.TUser;

/**
 * 会话管理器
 * 用于保存和获取当前登录用户的全局会话信息
 */
public class SessionManager {
    private static TUser currentUser;
    
    /**
     * 获取当前登录用户
     * @return 用户对象，未登录返回null
     */
    public static TUser getCurrentUser() {
        return currentUser;
    }
    
    /**
     * 设置当前登录用户（登录成功时调用）
     * @param user 用户对象
     */
    public static void setCurrentUser(TUser user) {
        currentUser = user;
    }
    
    /**
     * 获取当前登录用户名
     * @return 用户名，未登录返回"system"
     */
    public static String getCurrentUserName() {
        return currentUser != null ? currentUser.getUsername() : "system";
    }
    
    /**
     * 获取当前登录用户ID
     * @return 用户ID，未登录返回null
     */
    public static Integer getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : null;
    }
    
    /**
     * 清除会话（退出登录时调用）
     */
    public static void clearSession() {
        currentUser = null;
    }
    
    /**
     * 检查是否已登录
     * @return true-已登录，false-未登录
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}
