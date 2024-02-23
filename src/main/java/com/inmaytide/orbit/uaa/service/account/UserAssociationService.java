package com.inmaytide.orbit.uaa.service.account;

import com.inmaytide.orbit.uaa.consts.UserAssociationCategory;
import com.inmaytide.orbit.uaa.domain.account.UserAssociation;

import java.util.List;

/**
 * @author inmaytide
 * @since 2024/2/23
 */
public interface UserAssociationService {

    List<UserAssociation> findByUserAndCategory(Long user, UserAssociationCategory category);

}
