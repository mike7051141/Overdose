package com.springboot.khtml.controller;

import com.springboot.khtml.dto.myPageDto.RequestMyPageDto;
import com.springboot.khtml.dto.signDto.ResultDto;
import com.springboot.khtml.service.MyPageService;
import io.swagger.annotations.ApiImplicitParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1")
@Slf4j
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageService myPageService;

    @PutMapping("/update")
    @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    public ResponseEntity<ResultDto> updateUser(@RequestBody RequestMyPageDto requestMyPageDto, HttpServletRequest servletRequest) {
        ResultDto resultDto = myPageService.updateUser(requestMyPageDto, servletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(resultDto);
    }

}