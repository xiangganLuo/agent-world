package com.aworld.core.agent.convert;

import com.aworld.core.agent.controller.app.vo.agent.AgentRespVO;
import com.aworld.core.agent.dal.dataobject.AgentDO;
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

}
