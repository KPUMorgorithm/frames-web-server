package org.morgorithm.frames.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.morgorithm.frames.dto.PageRequestDTO;
import org.morgorithm.frames.entity.Facility;
import org.morgorithm.frames.entity.Status;
import org.morgorithm.frames.service.StatusService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/events")
@Controller
@Log4j2
public class EventController {
    private final StatusService statusService;

    @GetMapping("/list")
    public String register(PageRequestDTO pageRequestDTO,Model model){
        model.addAttribute("result", statusService.getEventInfo());

        model.addAttribute("result2", statusService.getStatusList(pageRequestDTO));

        model.addAttribute("dangerList", statusService.getDangerStatus());
        model.addAttribute("warningList", statusService.getWarningStatus());
        model.addAttribute("normalList", statusService.getNormalStatus());
        model.addAttribute("totalList", statusService.getTotalStatus());
        List<Status> s=statusService.getDangerStatus();
       s.get(0).getStatusnum();
        return "events/list";
    }

}
