package com.springboot.khtml.service.Impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.khtml.dao.AuthDao;
import com.springboot.khtml.dto.CommonResponse;
import com.springboot.khtml.dto.signDto.KakaoResponseDto;
import com.springboot.khtml.dto.signDto.ResultDto;
import com.springboot.khtml.dto.signDto.SignInResultDto;
import com.springboot.khtml.entity.User;
import com.springboot.khtml.jwt.JwtProvider;
import com.springboot.khtml.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtProvider jwtProvider;
    private final AuthDao authDao;

    @Value("${kakao.userinfo.url}")
    private String kakaoUserInfoUrl;

    @Override
    public ResponseEntity<?> getKakaoUserInfo(String accessToken) {
        log.info("[Kakao login] Authenticate user using access token");

        try {
            // 1. 엑세스 토큰을 사용하여 로그인 처리 및 JWT 생성
            SignInResultDto signInResultDto = kakao_SignIn(accessToken);

            // 2. JWT를 포함한 결과를 JSON으로 반환
            return ResponseEntity.ok(signInResultDto);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to authenticate using Kakao access token");
        }
    }

    private KakaoResponseDto getInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();
        HttpHeaders headers = new HttpHeaders();

        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        HttpEntity<?> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(kakaoUserInfoUrl, HttpMethod.POST, entity, String.class);

        try {
            Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {});
            Map<String, Object> kakaoAccount = (Map<String, Object>) responseMap.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

            KakaoResponseDto responseDto = KakaoResponseDto.builder()
                    .userName((String) kakaoAccount.get("name"))
                    .phoneNumber((String) kakaoAccount.get("phone_number"))
                    .email((String) kakaoAccount.get("email"))
                    .gender((String) kakaoAccount.get("gender"))
                    .profileUrl((String) profile.get("profile_image_url"))
                    .build();

            return responseDto;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public SignInResultDto kakao_SignIn(String accessToken) {
        KakaoResponseDto kakaoUserInfoResponse = getInfo(accessToken);

        SignInResultDto signInResultDto = new SignInResultDto();
        if (kakaoUserInfoResponse == null) {
            setFail(signInResultDto);
            throw new RuntimeException("Failed to get Kakao user info");
        }

        User user = authDao.kakaoUserFind(kakaoUserInfoResponse.getEmail());

        if (user == null) {
            User newUser = User.builder()
                    .email(kakaoUserInfoResponse.getEmail())
                    .userName(kakaoUserInfoResponse.getUserName())
                    .phoneNumber(kakaoUserInfoResponse.getPhoneNumber())
                    .gender(kakaoUserInfoResponse.getGender())
                    .profileUrl(kakaoUserInfoResponse.getProfileUrl())
                    .create_At(LocalDateTime.now())
                    .roles(List.of("ROLE_USER")) // roles 필드 설정
                    .update_At(LocalDateTime.now())
                    .build();

            authDao.KakaoUserSave(newUser);
            user = newUser;
            setSuccess(signInResultDto);
            signInResultDto.setDetailMessage("회원가입 완료.");
        }

        signInResultDto.setToken(jwtProvider.createToken(user.getEmail(), List.of("ROLE_USER")));
        setSuccess(signInResultDto);
        signInResultDto.setDetailMessage("로그인 성공.");
        log.info("[SignIn] SignInResultDto: {}", signInResultDto);

        return signInResultDto;
    }

    private void setSuccess(ResultDto resultDto) {
        resultDto.setSuccess(true);
        resultDto.setCode(CommonResponse.SUCCESS.getCode());
        resultDto.setMsg(CommonResponse.SUCCESS.getMsg());
    }

    private void setFail(ResultDto resultDto) {
        resultDto.setSuccess(false);
        resultDto.setCode(CommonResponse.Fail.getCode());
        resultDto.setMsg(CommonResponse.Fail.getMsg());
    }
}
