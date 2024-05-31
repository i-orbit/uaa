package com.inmaytide.orbit.uaa.api;

import com.inmaytide.orbit.uaa.service.account.PositionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author inmaytide
 * @since 2024/5/31
 */
@Tag(name = "岗位管理")
@RestController
@RequestMapping("/api/users")
public class PositionResource {

    private final PositionService positionService;

    public PositionResource(PositionService positionService) {
        this.positionService = positionService;
    }



}
