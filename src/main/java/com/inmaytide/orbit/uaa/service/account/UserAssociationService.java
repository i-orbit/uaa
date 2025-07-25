package com.inmaytide.orbit.uaa.service.account;

import com.inmaytide.orbit.commons.constants.UserAssociationCategory;
import com.inmaytide.orbit.uaa.domain.account.User;
import com.inmaytide.orbit.uaa.domain.account.UserAssociation;

import java.util.List;
import java.util.Map;

/**
 * @author inmaytide
 * @since 2024/2/23
 */
public interface UserAssociationService {

    List<UserAssociation> findByUserAndCategory(String user, UserAssociationCategory category);

    Map<String, Map<UserAssociationCategory, List<UserAssociation>>> findByUsers(List<String> userIds);

    /**
     * 覆盖保存(先删后插)指定用户所有的关联信息
     */
    void persist(User user);

    /**
     * 抹除指定用户所有关联信息
     *
     * @param userId 指定用户唯一标识
     */
    void erase(String userId);

    /**
     * 抹除指定用户指定类型关联信息
     *
     * @param userId   指定用户唯一标识
     * @param category 指定类型
     */
    void erase(String userId, UserAssociationCategory category);

}
