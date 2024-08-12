package com.springboot.khtml.dao.Impl;

import com.springboot.khtml.dao.AdmissionDao;
import com.springboot.khtml.entity.AdmissionForm;
import com.springboot.khtml.repository.AdmissionFormRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdmissionDaoImpl implements AdmissionDao {

    private final AdmissionFormRepository admissionFormRepository;
    @Override
    public void save(AdmissionForm admissionForm) {
        admissionFormRepository.save(admissionForm);
    }
}
