package com.aworld.core.tavern.service.drink;

import com.aworld.core.tavern.dal.dataobject.DrinkDO;

import java.util.List;

/**
 * 酒馆酒单 Service 接口
 *
 * @author aw
 */
public interface DrinkService {

    /**
     * 随机获取一款在售的酒
     *
     * @return 酒
     */
    DrinkDO randomDrink();

    /**
     * 根据 ID 获取酒
     *
     * @param id 酒 ID
     * @return 酒
     */
    DrinkDO getDrink(Long id);

    /**
     * 获取所有在售的酒
     *
     * @return 酒列表
     */
    List<DrinkDO> getActiveDrinkList();

}
