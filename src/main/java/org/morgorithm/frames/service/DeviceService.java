package org.morgorithm.frames.service;

import org.morgorithm.frames.entity.Device;
import org.morgorithm.frames.entity.Facility;

import java.util.List;

public interface DeviceService {
    Facility addFacility(String deviceId, String facilityName, Boolean state);

    Facility addDevice(String deviceId, Long bno, Boolean state);

    List<Device> getDevicesByFacility(Long bno);
}
