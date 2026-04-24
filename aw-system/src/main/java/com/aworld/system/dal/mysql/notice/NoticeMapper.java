package com.aworld.system.dal.mysql.notice;

import com.aworld.framework.common.pojo.PageResult;
import com.aworld.framework.mybatis.core.mapper.BaseMapperX;
import com.aworld.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.aworld.system.controller.admin.notice.vo.NoticePageReqVO;
import com.aworld.system.dal.dataobject.notice.NoticeDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoticeMapper extends BaseMapperX<NoticeDO> {

    default PageResult<NoticeDO> selectPage(NoticePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<NoticeDO>()
                .likeIfPresent(NoticeDO::getTitle, reqVO.getTitle())
                .eqIfPresent(NoticeDO::getStatus, reqVO.getStatus())
                .orderByDesc(NoticeDO::getId));
    }

}
