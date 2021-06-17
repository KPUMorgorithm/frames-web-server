package org.morgorithm.frames.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.morgorithm.frames.dto.TrackerInfoDTO;
import org.morgorithm.frames.service.StatusService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/map")
@Controller
@Log4j2
public class TrackerController {
    private final StatusService statusService;

    @GetMapping("/tracker")
    public void register(TrackerInfoDTO trackerInfoDTO, Model model) {
        model.addAttribute("result", statusService.getMapInfo(trackerInfoDTO));
        model.addAttribute("bName", statusService.getMapInfo(trackerInfoDTO).getBName());
    }
}
