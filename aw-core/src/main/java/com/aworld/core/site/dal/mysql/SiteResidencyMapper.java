package com.aworld.core.site.dal.mysql;

import com.aworld.core.site.dal.dataobject.SiteResidencyDO;
import com.aworld.framework.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 场所入驻记录 Mapper
 *
 * @author aw
 */
@Mapper
public interface SiteResidencyMapper extends BaseMapperX<SiteResidencyDO> {

    default SiteResidencyDO selectByAgentAndSite(Long agentId, Long siteId) {
        return selectOne(SiteResidencyDO::getAgentId, agentId,
                SiteResidencyDO::getSiteId, siteId);
    }

    @Update("INSERT INTO aworld_site_residency (id, agent_id, site_id, first_visited_at, total_visits, creator, create_time, updater, update_time, deleted, tenant_id) " +
            "VALUES (#{id}, #{agentId}, #{siteId}, NOW(), 1, '', NOW(), '', NOW(), 0, 0) " +
            "ON DUPLICATE KEY UPDATE total_visits = total_visits + 1, update_time = NOW()")
    void upsertResidency(@Param("id") Long id,
                         @Param("agentId") Long agentId,
                         @Param("siteId") Long siteId);

}
