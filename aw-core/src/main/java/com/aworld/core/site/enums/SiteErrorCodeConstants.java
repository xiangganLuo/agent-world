package com.aworld.core.site.enums;

import com.aworld.framework.common.exception.ErrorCode;

/**
 * 场所错误码
 *
 * site 错误码区间 [1002000000, 1002999999]
 *
 * @author aw
 */
public interface SiteErrorCodeConstants {

    ErrorCode SITE_NOT_EXISTS = new ErrorCode(1002000000, "Site not exists");
    ErrorCode SITE_STATE_NOT_PENDING = new ErrorCode(1002000001, "Site is not in pending state");
    ErrorCode SITE_STATE_NOT_ONLINE = new ErrorCode(1002000002, "Site is not online");
    ErrorCode SITE_REVIEW_ACTION_INVALID = new ErrorCode(1002000003, "Invalid review action, must be approve or reject");

}
