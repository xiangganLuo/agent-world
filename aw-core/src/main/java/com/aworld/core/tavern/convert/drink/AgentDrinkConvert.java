package com.aworld.core.tavern.convert.drink;

import com.aworld.core.tavern.controller.agent.vo.drink.DrinkRespVO;
import com.aworld.core.tavern.dal.dataobject.DrinkDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AgentDrinkConvert {

    AgentDrinkConvert INSTANCE = Mappers.getMapper(AgentDrinkConvert.class);

    DrinkRespVO convert(DrinkDO bean);

    List<DrinkRespVO> convertList(List<DrinkDO> list);

}
