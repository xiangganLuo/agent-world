package com.aworld.system.dal.mysql.mail;

import com.aworld.framework.common.pojo.PageResult;
import com.aworld.framework.mybatis.core.mapper.BaseMapperX;
import com.aworld.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.aworld.system.controller.admin.mail.vo.account.MailAccountPageReqVO;
import com.aworld.system.dal.dataobject.mail.MailAccountDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MailAccountMapper extends BaseMapperX<MailAccountDO> {

    default PageResult<MailAccountDO> selectPage(MailAccountPageReqVO pageReqVO) {
        return selectPage(pageReqVO, new LambdaQueryWrapperX<MailAccountDO>()
                .likeIfPresent(MailAccountDO::getMail, pageReqVO.getMail())
                .likeIfPresent(MailAccountDO::getUsername , pageReqVO.getUsername())
                .orderByDesc(MailAccountDO::getId));
    }

}
