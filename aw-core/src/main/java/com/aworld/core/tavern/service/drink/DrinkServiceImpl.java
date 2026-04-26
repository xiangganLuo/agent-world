package com.aworld.core.tavern.service.drink;

import com.aworld.core.tavern.dal.dataobject.DrinkDO;
import com.aworld.core.tavern.dal.mysql.DrinkMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;

/**
 * 酒馆酒单 Service 实现类
 *
 * @author aw
 */
@Service
@Validated
public class DrinkServiceImpl implements DrinkService {

    @Resource
    private DrinkMapper drinkMapper;

    private final Random random = new Random();

    @Override
    public DrinkDO randomDrink() {
        List<DrinkDO> list = getActiveDrinkList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(random.nextInt(list.size()));
    }

    @Override
    public DrinkDO getDrink(Long id) {
        return drinkMapper.selectById(id);
    }

    @Override
    public List<DrinkDO> getActiveDrinkList() {
        return drinkMapper.selectList(DrinkDO::getIsActive, true);
    }

}
