package com.library.common;

/**
 * 系统常量
 */
public class Constants {
    
    /** 用户角色 */
    public static class Role {
        public static final int USER = 0;
        public static final int ADMIN = 1;
    }
    
    /** 用户状态 */
    public static class UserStatus {
        public static final int DISABLED = 0;
        public static final int ENABLED = 1;
    }
    
    /** 借阅状态 */
    public static class BorrowStatus {
        public static final int BORROWING = 0;
        public static final int RETURNED = 1;
        public static final int OVERDUE = 2;
        
        public static String getText(int status) {
            return switch (status) {
                case BORROWING -> "借阅中";
                case RETURNED -> "已归还";
                case OVERDUE -> "已逾期";
                default -> "未知";
            };
        }
    }
    
    /** Token相关 */
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
}
