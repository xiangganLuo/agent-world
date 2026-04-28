package com.aworld.framework.common.util.sql;

/**
 * SQL 安全工具类
 * <p>
 * 提供 SQL 相关的安全防护功能，包括 LIKE 特殊字符转义等
 *
 * @author aw
 */
public final class SqlUtils {

    /**
     * 转义 LIKE 特殊字符
     * <p>
     * 防止用户输入 %、_、\ 等字符导致 LIKE 查询语义错误或潜在的安全问题
     * <p>
     * 注意：MyBatis-Plus 的 like() 方法本身会使用参数化查询防止 SQL 注入，
     * 但特殊字符仍可能导致 LIKE 语义混淆，因此需要额外转义
     *
     * @param value 原始字符串
     * @return 转义后的字符串，如果输入为 null 则返回 null
     */
    public static String escapeLikeSpecialChars(String value) {
        if (value == null) {
            return null;
        }
        // 转义顺序很重要：先转义 \，再转义其他字符
        return value.replace("\\", "\\\\")
                    .replace("%", "\\%")
                    .replace("_", "\\_");
    }

    private SqlUtils() {
        // 防止实例化
    }
}
