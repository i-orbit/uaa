package com.inmaytide.orbit.uaa.service.account;

import com.inmaytide.orbit.commons.business.BasicService;
import com.inmaytide.orbit.commons.domain.dto.result.TreeNode;
import com.inmaytide.orbit.uaa.domain.account.Position;

import javax.swing.text.html.parser.Entity;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * @author inmaytide
 * @since 2024/5/28
 */
public interface PositionService extends BasicService<Position> {

    String SYMBOL = "POSITION";

    Map<Long, String> findNamesByIds(List<Long> ids);

    TreeSet<TreeNode<? extends Entity>> treeOfPositions();

}
