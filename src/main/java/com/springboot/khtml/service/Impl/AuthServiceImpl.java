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
import com.springboot.khtml.repository.UserRepository;
import com.springboot.khtml.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    @Value("${kakao.client.id}")
    private String clientKey;
    @Value("${kakao.redirect.url}")
    private String redirectUrl;
    @Value("${kakao.accesstoken.url}")
    private String kakaoAccessTokenUrl;
    @Value("${kakao.userinfo.url}")
    private String kakaoUserInfoUrl;

    private final UserRepository userRepository;
    private final JwtProvider jwtTokenProvider;

    @Override
    public ResponseEntity<?> getKakaoUserInfo(String authorizeCode) {
        log.info("[kakao login] issue a authorizecode");
        ObjectMapper objectMapper = new ObjectMapper(); // json 파싱해주는 객체
        RestTemplate restTemplate = new RestTemplate(); // client 연결 객체

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientKey);
        params.add("redirect_uri", redirectUrl);
        params.add("code", authorizeCode);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        try{
            ResponseEntity<String> response = restTemplate.exchange(
                    kakaoAccessTokenUrl,
                    HttpMethod.POST,
                    kakaoTokenRequest,
                    String.class
            );
            log.info("[kakao login] authorizecode issued successfully");
            Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {});
            String accessToken = (String) responseMap.get("access_token");
            KakaoResponseDto kakaoUserInfo = getInfo(accessToken);
            if (kakaoUserInfo == null) {
                return ResponseEntity.status(401).body("Invalid Kakao login");
            }
            // 사용자 정보가 있다면 이메일을 기준으로 DB에서 사용자 찾기
            User user = userRepository.findByEmail(kakaoUserInfo.getEmail());
            if (user == null) {
                // 새로운 사용자는 DB에 저장
                user = User.builder()
                        .email(kakaoUserInfo.getEmail())
                        .password("1") // 카카오 로그인에서는 비밀번호가 필요하지 않음
                        .roles(Collections.singletonList("ROLE_ADMIN")) // 기본 역할 설정
                        .build();
                userRepository.save(user);
                return ResponseEntity.ok(accessToken);
            } else { // JWT 토큰 생성
                String token = jwtTokenProvider.createToken(user.getEmail(), user.getRoles());
                return ResponseEntity.ok(new ResultDto(true, 0, "Success", token));
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }
    private KakaoResponseDto getInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        ObjectMapper mapper = new ObjectMapper();

        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();

        HttpEntity<?> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(kakaoUserInfoUrl, entity, String.class);

        try {
            Map<String, Object> responseMap = mapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {});
            Map<String, Object> kakaoAccount = (Map<String, Object>) responseMap.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

            // Gender 변환 로직 추가
            String gender = (String) kakaoAccount.get("gender");
            String displayGender;
            if ("female".equals(gender)) {
                displayGender = "여자";
            } else if ("male".equals(gender)) {
                displayGender = "남자";
            } else {
                displayGender = "기타"; // 기본값 설정
            }

            KakaoResponseDto requestSignUpDto = KakaoResponseDto.builder()
                    .email((String) kakaoAccount.get("email"))
                    .build();
            // User 엔티티를 생성하고 데이터베이스에 저장
            User user = userRepository.findByEmail(requestSignUpDto.getEmail());
            if (user == null) {
                user = User.builder()
                        .email(requestSignUpDto.getEmail())
                        .password("1") // 카카오 로그인에서는 비밀번호가 필요하지 않음
                        .roles(Collections.singletonList("ROLE_ADMIN")) // 기본 역할 설정
                        .build();
                userRepository.save(user);
            } else {
                // 기존 사용자의 정보 업데이트 (필요시)
                user.setEmail(requestSignUpDto.getEmail());
                userRepository.save(user);
            }

            return requestSignUpDto;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
