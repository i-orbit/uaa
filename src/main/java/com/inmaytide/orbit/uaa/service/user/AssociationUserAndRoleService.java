package com.inmaytide.orbit.uaa.service.user;

import com.inmaytide.orbit.commons.domain.dto.result.AffectedResult;
import com.inmaytide.orbit.uaa.domain.user.AssociationUserAndRole;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author inmaytide
 * @since 2023/6/29
 */
@Service
public interface AssociationUserAndRoleService {

    default AffectedResult deleteByUser(Long user) {
        return deleteByUsers(Collections.singleton(user));
    }

    AffectedResult deleteByUsers(Collection<Long> users);

    void save(Long user, Collection<AssociationUserAndRole> associations);

    default List<AssociationUserAndRole> findByUser(Long user) {
        return findByUsers(Collections.singleton(user)).getOrDefault(user, Collections.emptyList());
    }

    Map<Long, List<AssociationUserAndRole>> findByUsers(Collection<Long> users);

}
