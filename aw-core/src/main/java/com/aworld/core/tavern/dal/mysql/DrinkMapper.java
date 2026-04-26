package com.aworld.core.tavern.dal.mysql;

import com.aworld.core.tavern.dal.dataobject.DrinkDO;
import com.aworld.framework.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

/**
 * 酒馆酒单 Mapper
 *
 * @author aw
 */
@Mapper
public interface DrinkMapper extends BaseMapperX<DrinkDO> {

    default DrinkDO selectByDrinkCode(String drinkCode) {
        return selectOne(DrinkDO::getDrinkCode, drinkCode);
    }

}
