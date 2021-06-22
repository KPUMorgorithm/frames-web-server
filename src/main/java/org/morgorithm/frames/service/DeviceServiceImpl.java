package org.morgorithm.frames.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.morgorithm.frames.entity.Device;
import org.morgorithm.frames.entity.Facility;
import org.morgorithm.frames.repository.DeviceRepository;
import org.morgorithm.frames.repository.FacilityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {
    private final DeviceRepository deviceRepository;
    private final FacilityRepository facilityRepository;

    // 1. 시설 + 기기 등록
    @Transactional
    public Facility addFacility(String deviceId, String facilityName, Boolean state) {
        Facility facility = Facility.builder()
                .building(facilityName)
                .build();

        facility = facilityRepository.save(facility);

        // 2. 기기 등록 (입구라고 가정)
        Device device = Device.builder()
                .deviceId(deviceId)
                .bno(facility.getBno())
                .state(state)
                .build();

        device = deviceRepository.save(device);
        return facility;
    }

    // 3. 출구 기기 등록
    @Transactional
    public Facility addDevice(String deviceId, Long bno, Boolean state) {
        Device device = Device.builder()
                .deviceId(deviceId)
                .bno(bno)
                .state(state)
                .build();

        device = deviceRepository.save(device);
        Facility facility = facilityRepository.findById(bno).get();
        return facility;
    }

    @Transactional
    public List<Device> getDevicesByFacility(Long bno) {
        return deviceRepository.findByBno(bno);
    }
}
