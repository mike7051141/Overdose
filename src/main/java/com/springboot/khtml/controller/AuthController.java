package com.springboot.khtml.controller;

import com.springboot.khtml.dto.signDto.SignInResultDto;
import com.springboot.khtml.service.AuthService;
import com.springboot.khtml.service.Impl.AuthServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth/kakao")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @GetMapping("/callback")
    public ResponseEntity<?> kakaoCallback(@RequestParam String code) {
        log.info("Received Kakao authorization code: {}", code);
        // 카카오에서 발급받은 인가 코드를 사용하여 사용자 정보를 가져오고 JWT 토큰 생성
        return authService.getKakaoUserInfo(code);
    }

    @PostMapping("/kakao/signin")
    public SignInResultDto kakao_SignIn(@RequestParam String accessToken){
        log.info("[kakao-login] accessToken {}", accessToken);
        return authService.kakao_SignIn(accessToken);
    }
}
