package com.inmaytide.orbit.uaa.service.permission;

import com.baomidou.mybatisplus.extension.service.IService;
import com.inmaytide.orbit.commons.business.BasicService;
import com.inmaytide.orbit.uaa.domain.permission.Role;
import com.inmaytide.orbit.uaa.domain.permission.RoleAssociation;
import com.inmaytide.orbit.uaa.domain.user.User;

import java.util.List;
import java.util.Map;

/**
 * @author inmaytide
 * @since 2023/5/19
 */
public interface RoleService extends BasicService<Role>, IService<Role> {

    List<String> findCodesByUser(User user);

    List<String> findCodesByIds(List<Long> ids);

    Map<Long, String> findNamesByIds(List<Long> ids);

    List<RoleAssociation> findAssociationsByCodes(List<String> roles);
}
