package org.morgorithm.frames.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.morgorithm.frames.configuration.ModelMapperUtil;
import org.morgorithm.frames.dto.FacilityDTO;
import org.morgorithm.frames.repository.FacilityRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class FacilityServiceImpl implements FacilityService{
    static final int BUILDING_NUM = 10;
    private final FacilityRepository facilityRepository;
    @Override
    public String[] getFacilityNames() {
        String[] bname=new String[BUILDING_NUM];
        int fidx=0;
        List<Object[]> result=facilityRepository.getFacilityNames();
        for(Object[] a:result){
            bname[fidx++]=(String)a[0];
        }
        return bname;
    }

    @Override
    public List<FacilityDTO> getAllFacilities() {
        return facilityRepository.findAll().stream().map(facility -> ModelMapperUtil.getModelMapper().map(facility, FacilityDTO.class)).collect(Collectors.toList());
    }
}
