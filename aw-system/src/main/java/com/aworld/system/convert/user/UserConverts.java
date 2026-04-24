package com.aworld.system.convert.user;

import com.aworld.framework.common.util.collection.CollectionUtils;
import com.aworld.framework.common.util.object.BeanUtils;
import com.aworld.system.controller.admin.dept.vo.post.PostSimpleRespVO;
import com.aworld.system.controller.admin.permission.vo.role.RoleSimpleRespVO;
import com.aworld.system.controller.admin.user.vo.profile.UserProfileRespVO;
import com.aworld.system.controller.admin.user.vo.user.UserRespVO;
import com.aworld.system.controller.admin.user.vo.user.UserSimpleRespVO;
import com.aworld.system.dal.dataobject.dept.DeptDO;
import com.aworld.system.dal.dataobject.dept.PostDO;
import com.aworld.system.dal.dataobject.permission.RoleDO;
import com.aworld.system.dal.dataobject.user.AdminUserDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Mapper
public interface UserConverts {

    UserConverts INSTANCE = Mappers.getMapper(UserConverts.class);

    default List<UserRespVO> convertList(List<AdminUserDO> list, Map<Long, DeptDO> deptMap) {
        return CollectionUtils.convertList(list, user -> {
            // 获取用户的所有部门
            UserRespVO userVO = BeanUtils.toBean(user, UserRespVO.class);
            if (Objects.nonNull(user.getDeptId())) {
                // 构建部门名称（多个部门用逗号分隔）
                DeptDO dept = deptMap.get(user.getDeptId());
                userVO.setDeptName(dept.getName());
            }
            return userVO;
        });
    }

    default UserRespVO convert(AdminUserDO user,  Map<Long, DeptDO> deptMap) {
        UserRespVO userVO = BeanUtils.toBean(user, UserRespVO.class);
        if (Objects.nonNull(user.getDeptId())) {
            // 构建部门名称（多个部门用逗号分隔）
            DeptDO dept = deptMap.get(user.getDeptId());
            userVO.setDeptName(dept.getName());
        }
        return userVO;
    }

    default List<UserSimpleRespVO> convertSimpleList(List<AdminUserDO> list, Map<Long, DeptDO> deptMap) {
        return CollectionUtils.convertList(list, user -> {
            UserSimpleRespVO userVO = BeanUtils.toBean(user, UserSimpleRespVO.class);
            // 获取用户的所有部门
            if (Objects.nonNull(user.getDeptId())) {
                // 构建部门名称（多个部门用逗号分隔）
                DeptDO dept = deptMap.get(user.getDeptId());
                userVO.setDeptName(dept.getName());
            }
            return userVO;
        });
    }

    default UserProfileRespVO convert(AdminUserDO user, List<RoleDO> userRoles,
                                      List<DeptDO> userDepts, List<PostDO> posts) {
        UserProfileRespVO userVO = BeanUtils.toBean(user, UserProfileRespVO.class);
        userVO.setRoles(BeanUtils.toBean(userRoles, RoleSimpleRespVO.class));
        userVO.setPosts(BeanUtils.toBean(posts, PostSimpleRespVO.class));
        return userVO;
    }

}
