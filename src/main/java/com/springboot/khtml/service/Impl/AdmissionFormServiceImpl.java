package com.springboot.khtml.service.Impl;

import com.springboot.khtml.dao.AdmissionDao;
import com.springboot.khtml.dto.CommonResponse;
import com.springboot.khtml.dto.admissionFormDto.AdmissionFormDto;
import com.springboot.khtml.dto.centerDto.ResponseCenterDto;
import com.springboot.khtml.dto.signDto.ResultDto;
import com.springboot.khtml.entity.AdmissionForm;
import com.springboot.khtml.entity.Center;
import com.springboot.khtml.entity.User;
import com.springboot.khtml.jwt.JwtProvider;
import com.springboot.khtml.repository.CenterRepository;
import com.springboot.khtml.repository.UserRepository;
import com.springboot.khtml.service.AdmissionFormService;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class AdmissionFormServiceImpl implements AdmissionFormService {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final CenterRepository centerRepository;
    private final AdmissionDao admissionDao;

    @Override
    public ResponseCenterDto getCenterDetail(Long id,HttpServletRequest request) {
        Center center = centerRepository.getById(id);

        ResponseCenterDto responseCenterDto = new ResponseCenterDto();
        responseCenterDto.setCenter_name(center.getCenter_name());
        responseCenterDto.setRegion(center.getRegion());
        responseCenterDto.setDistrict(center.getDistrict());
        responseCenterDto.setContact_number(center.getContact_number());
        responseCenterDto.setDesignated_beds(center.getDesignated_beds());
        responseCenterDto.setId(center.getId());

        request.getSession().setAttribute("partialCenter", center);

        return responseCenterDto;
    }

    @Override
    public ResultDto saveAdmission(AdmissionFormDto admissionFormDto, HttpServletRequest request) {
        String info = jwtProvider.getUsername(request.getHeader("X-AUTH-TOKEN"));
        User user = userRepository.getByEmail(info);

        Center center = (Center)request.getSession().getAttribute("partialCenter");
        ResultDto resultDto = new ResultDto();

        if(user != null ){
        AdmissionForm admissionForm = new AdmissionForm();
        admissionForm.setRehabilitationHistory(admissionFormDto.getRehabilitationHistory());
        admissionForm.setGuardianName(admissionFormDto.getGuardianName());
        admissionForm.setPhoneNum(admissionFormDto.getPhoneNum());
        admissionForm.setHealthInfo(admissionFormDto.getHealthInfo());
        admissionForm.setOtherRequests(admissionFormDto.getOtherRequests());
        admissionForm.setCenter(center);
        admissionForm.setUser(user);
        admissionDao.save(admissionForm);
        setSuccess(resultDto);

        }else{
            setFail(resultDto);
        }
        return resultDto;
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
