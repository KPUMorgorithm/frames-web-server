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
import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService{
    private DeviceRepository deviceRepository;
    private FacilityRepository facilityRepository;

    // 1. 시설 + 기기 등록
    @Transactional
    public Facility addFacility(UUID deviceId, String facilityName, Boolean state) {
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
    public Device addDevice(UUID deviceId, Long bno, Boolean state) {
        Device device = Device.builder()
                .deviceId(deviceId)
                .bno(bno)
                .state(state)
                .build();

        device = deviceRepository.save(device);
        return device;
    }

    @Transactional
    public List<Device> getDevicesByFacility(Long bno) {
        return deviceRepository.findByBno(bno);
    }
}
