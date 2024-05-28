package com.inmaytide.orbit.uaa.service.account;

import com.inmaytide.orbit.uaa.consts.UserAssociationCategory;
import com.inmaytide.orbit.uaa.domain.account.UserAssociation;

import java.util.List;
import java.util.Map;

/**
 * @author inmaytide
 * @since 2024/2/23
 */
public interface UserAssociationService {

    List<UserAssociation> findByUserAndCategory(Long user, UserAssociationCategory category);

    Map<Long, Map<UserAssociationCategory, List<UserAssociation>>> findByUsers(List<Long> userIds);

}
