package com.springboot.khtml.service.Impl;

import com.springboot.khtml.dto.CommonResponse;
import com.springboot.khtml.dto.myPageDto.RequestMyPageDto;
import com.springboot.khtml.dto.signDto.ResultDto;
import com.springboot.khtml.entity.User;
import com.springboot.khtml.jwt.JwtProvider;
import com.springboot.khtml.repository.UserRepository;
import com.springboot.khtml.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MyPageServiceImpl implements MyPageService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ResultDto updateUser(RequestMyPageDto requestMyPageDto, HttpServletRequest request) {
        ResultDto resultDto = new ResultDto();
        String info = jwtProvider.getUsername(request.getHeader("X-AUTH-TOKEN"));
        User user = userRepository.getByEmail(info);
        if (user != null) {

            user.setUserName(requestMyPageDto.getUserName());
            user.setNickName(requestMyPageDto.getNickName());
            user.setAddress(requestMyPageDto.getAddress());
            user.setGender(requestMyPageDto.getGender());
            user.setPassword(passwordEncoder.encode(requestMyPageDto.getPassword()));
            user.setPasswordCheck(passwordEncoder.encode(requestMyPageDto.getPasswordCheck()));
            user.setUpdate_At(LocalDateTime.now());

            if (requestMyPageDto.getPassword().equals(requestMyPageDto.getPasswordCheck())) {
                userRepository.save(user);
                resultDto.setDetailMessage("회원정보 수정 완료!");
                setSuccess(resultDto);
            }else {
                resultDto.setDetailMessage("비밀번호가 일치하지 않습니다.");
                setFail(resultDto);
            }
            return resultDto;

        }return resultDto;
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

