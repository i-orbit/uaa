package com.inmaytide.orbit.uaa.service.user;

import com.inmaytide.orbit.commons.domain.dto.result.AffectedResult;
import com.inmaytide.orbit.uaa.domain.user.UserAssociationCategory;
import com.inmaytide.orbit.uaa.domain.user.User;
import com.inmaytide.orbit.uaa.domain.user.UserAssociation;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author inmaytide
 * @since 2023/6/29
 */
public interface UserAssociationService {

    AffectedResult deleteByUsers(UserAssociationCategory category, Collection<Long> users);

    AffectedResult deleteByUser(UserAssociationCategory category, Long user);

    AffectedResult deleteByUser(Long user);

    AffectedResult deleteByUsers(Collection<Long> users);

    void persistForUser(User user);

    default Map<UserAssociationCategory, List<UserAssociation>> findByUser(Long user) {
        return findByUsers(Collections.singleton(user)).getOrDefault(user, Collections.emptyMap());
    }

    Map<Long, Map<UserAssociationCategory, List<UserAssociation>>> findByUsers(Collection<Long> users);

    default List<UserAssociation> findByUser(UserAssociationCategory category, Long user) {
        return findByUsers(category, Collections.singleton(user)).getOrDefault(user, Collections.emptyList());
    }

    Map<Long, List<UserAssociation>> findByUsers(UserAssociationCategory category, Collection<Long> user);

}
