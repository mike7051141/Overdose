package com.springboot.khtml.dto.admissionFormDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdmissionFormDto {
    private String guardianName;

    private String phoneNum;

    private String healthInfo;

    private String rehabilitationHistory;

    private String otherRequests;
}
