package com.inmaytide.orbit.uaa.api;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.vo.CaptchaResponse;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import com.inmaytide.orbit.commons.constants.Constants;
import com.inmaytide.orbit.commons.domain.Oauth2Token;
import com.inmaytide.orbit.commons.domain.dto.params.LoginParameters;
import com.inmaytide.orbit.commons.log.annotation.OperationLogging;
import com.inmaytide.orbit.commons.service.authorization.AuthorizationService;
import com.inmaytide.orbit.uaa.service.account.dto.CaptchaValidate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

/**
 * @author inmaytide
 * @since 2024/12/19
 */
@RestController
@Tag(name = "登录授权相关")
@RequestMapping("/api/")
public class AuthorizeResource {

    private final AuthorizationService authorizationService;

    private final ImageCaptchaApplication captchaApplication;

    public AuthorizeResource(AuthorizationService authorizationService, ImageCaptchaApplication captchaApplication) {
        this.authorizationService = authorizationService;
        this.captchaApplication = captchaApplication;
    }

    @GetMapping("captcha")
    @Operation(summary = "获取验证码")
    public CaptchaResponse<ImageCaptchaVO> getImageCaptcha() {
        return captchaApplication.generateCaptcha(CaptchaTypeConstant.SLIDER);
    }

    @PostMapping("/captcha/validate")
    @Operation(summary = "验证验证码")
    public Integer validate(@RequestBody CaptchaValidate body) {
        return captchaApplication.matching(body.getId(), body.getData()).getCode();
    }


    @OperationLogging(retainArguments = true)
    @PostMapping("/authorize/login")
    @Operation(summary = "系统登录")
    public Oauth2Token login(@RequestBody LoginParameters params, HttpServletResponse response, HttpServletRequest request) {
        return authorizationService.getToken(params)
                .doOnSuccess(token -> setTokenCookies(response, token))
                .block(Duration.ofSeconds(10));
    }

    private Cookie buildAccessTokenCookie(Oauth2Token token) {
        Cookie cookie = new Cookie(Constants.RequestParameters.ACCESS_TOKEN, token.getAccessToken());
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }

    private Cookie buildRefreshTokenCookie(Oauth2Token token) {
        Cookie cookie = new Cookie(Constants.RequestParameters.REFRESH_TOKEN, token.getRefreshToken());
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }

    protected void setTokenCookies(HttpServletResponse response, Oauth2Token token) {
        response.addCookie(buildAccessTokenCookie(token));
        response.addCookie(buildRefreshTokenCookie(token));
    }

}
