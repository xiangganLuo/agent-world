package com.aworld.core.agent.convert;

import com.aworld.core.agent.controller.admin.vo.AgentAdminRespVO;
import com.aworld.core.agent.controller.agent.vo.agent.AgentRespVO;
import com.aworld.core.agent.dal.dataobject.AgentDO;
import com.aworld.core.agent.enums.AgentStatusEnum;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Agent 转换器
 *
 * @author aw
 */
@Mapper
public interface AgentConvert {

    AgentConvert INSTANCE = Mappers.getMapper(AgentConvert.class);

    AgentRespVO convert(AgentDO bean);

    /**
     * 转换为管理后台响应 VO（含 API Key 脱敏）
     */
    default AgentAdminRespVO convertAdmin(AgentDO bean) {
        if (bean == null) {
            return null;
        }
        AgentAdminRespVO vo = new AgentAdminRespVO();
        vo.setId(bean.getId());
        vo.setUsername(bean.getUsername());
        vo.setNickname(bean.getNickname());
        vo.setAvatarUrl(bean.getAvatarUrl());
        vo.setBio(bean.getBio());
        // API Key 脱敏处理
        if (bean.getApiKey() != null && bean.getApiKey().length() > 20) {
            vo.setApiKey(bean.getApiKey().substring(0, 15) + "..." + 
                        bean.getApiKey().substring(bean.getApiKey().length() - 5));
        } else {
            vo.setApiKey(bean.getApiKey());
        }
        vo.setIsActive(bean.getIsActive());
        vo.setStatus(AgentStatusEnum.fromIsActive(bean.getIsActive()).getCode());
        vo.setCreateTime(bean.getCreateTime());
        vo.setUpdateTime(bean.getUpdateTime());
        return vo;
    }

}
