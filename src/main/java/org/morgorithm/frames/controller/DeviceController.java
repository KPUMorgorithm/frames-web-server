package org.morgorithm.frames.controller;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.morgorithm.frames.entity.Device;
import org.morgorithm.frames.entity.Facility;
import org.morgorithm.frames.service.DeviceService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/device")
@RequiredArgsConstructor
public class DeviceController {
    private final DeviceService deviceService;

    @PostMapping(value = "/facility", produces = "text/plain")
    public String addFacility(@RequestParam String deviceId, @RequestParam String facilityName, @RequestParam Boolean state) {
        Facility facility = deviceService.addFacility(deviceId, facilityName, state);
        Long bno = facility.getBno();
        return "" + bno;
    }

    @PostMapping(value = "/device", produces = "text/plain")
    public String addDevice(@RequestParam String deviceId, @RequestParam Long bno, @RequestParam Boolean state) {
        Facility facility = deviceService.addDevice(deviceId, bno, state);
        String bname = facility.getBuilding();
        return "" + bname;
    }
}
