package com.springboot.khtml.service;

import com.springboot.khtml.dto.admissionFormDto.AdmissionFormDto;
import com.springboot.khtml.dto.centerDto.ResponseCenterDto;
import com.springboot.khtml.dto.signDto.ResultDto;

import javax.servlet.http.HttpServletRequest;

public interface AdmissionFormService {
    ResultDto saveAdmission ( AdmissionFormDto admissionFormDto, HttpServletRequest request);
    ResponseCenterDto getCenterDetail(Long id,HttpServletRequest request);
}
