package org.morgorithm.frames.service;

import org.morgorithm.frames.dto.FacilityDTO;

import java.util.List;

public interface FacilityService {
    String[] getFacilityNames();
    List<FacilityDTO> getAllFacilities();
}
