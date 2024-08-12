package com.springboot.khtml.controller;

import com.springboot.khtml.dto.admissionFormDto.AdmissionFormDto;
import com.springboot.khtml.dto.centerDto.ResponseCenterDto;
import com.springboot.khtml.dto.signDto.ResultDto;
import com.springboot.khtml.service.AdmissionFormService;
import io.swagger.annotations.ApiImplicitParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
@RestController
@RequestMapping("/api/admission-form")
@RequiredArgsConstructor
public class AdmissionFormController {

    private final AdmissionFormService admissionFormService;

    @PostMapping("/get-center-detail")
    @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    public ResponseEntity<ResponseCenterDto> getCenterDetail(Long id, HttpServletRequest request){
        ResponseCenterDto responseCenterDto = admissionFormService.getCenterDetail(id,request);
        return ResponseEntity.status(HttpStatus.OK).body(responseCenterDto);
    }

    @PostMapping("/save")
    @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    public ResponseEntity<ResultDto> saveAdmission(@RequestBody AdmissionFormDto admissionFormDto, HttpServletRequest request){
        ResultDto resultDto = admissionFormService.saveAdmission(admissionFormDto,request);
        return ResponseEntity.status(HttpStatus.OK).body(resultDto);
    }
}
