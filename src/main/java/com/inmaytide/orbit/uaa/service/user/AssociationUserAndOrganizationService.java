package com.inmaytide.orbit.uaa.service.user;

import com.inmaytide.orbit.commons.domain.dto.result.AffectedResult;
import com.inmaytide.orbit.uaa.domain.user.AssociationUserAndOrganization;
import com.inmaytide.orbit.uaa.domain.user.AssociationUserAndPosition;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author inmaytide
 * @since 2023/6/29
 */
public interface AssociationUserAndOrganizationService {

    default AffectedResult deleteByUser(Long user) {
        return deleteByUsers(Collections.singleton(user));
    }

    AffectedResult deleteByUsers(Collection<Long> users);

    void save(Long user, Collection<AssociationUserAndOrganization> associations);

    default List<AssociationUserAndOrganization> findByUser(Long user) {
        return findByUsers(Collections.singleton(user)).getOrDefault(user, Collections.emptyList());
    }

    Map<Long, List<AssociationUserAndOrganization>> findByUsers(Collection<Long> users);

}
