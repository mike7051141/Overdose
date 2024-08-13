package com.springboot.khtml.service;

import com.springboot.khtml.dto.myPageDto.RequestMyPageDto;
import com.springboot.khtml.dto.signDto.ResultDto;

import javax.servlet.http.HttpServletRequest;

public interface MyPageService {
    ResultDto updateUser(RequestMyPageDto requestMyPageDto, HttpServletRequest servletRequest);
}
