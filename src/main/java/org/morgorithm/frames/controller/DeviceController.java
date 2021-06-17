package org.morgorithm.frames.controller;

import lombok.RequiredArgsConstructor;
import org.morgorithm.frames.service.DeviceService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
@RequestMapping("/device")
@RequiredArgsConstructor
public class DeviceController {
    private DeviceService deviceService;

    @PostMapping(value = "/facility", produces = "text/plain")
    public String addFacility(@RequestParam String deviceId, @RequestParam String facilityName, @RequestParam Boolean state) {
        return "" + deviceService.addFacility(UUID.fromString(deviceId), facilityName, state).getBno();
    }

    @PostMapping(value = "/device", produces = "text/plain")
    public String addDevice(@RequestParam String deviceId, @RequestParam Long bno, @RequestParam Boolean state) {
        return "" + deviceService.addDevice(UUID.fromString(deviceId), bno, state).getBno();
    }
}
