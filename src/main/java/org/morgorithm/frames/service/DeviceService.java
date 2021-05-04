package org.morgorithm.frames.service;

import org.morgorithm.frames.entity.Device;
import org.morgorithm.frames.entity.Facility;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface DeviceService {
    Facility addFacility(UUID deviceId, String facilityName, Boolean state);
    Device addDevice(UUID deviceId, Long bno, Boolean state);
    List<Device> getDevicesByFacility(Long bno);
}
