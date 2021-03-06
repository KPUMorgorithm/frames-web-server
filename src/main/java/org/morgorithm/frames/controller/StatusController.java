package org.morgorithm.frames.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.morgorithm.frames.dto.PageRequestDTO;
import org.morgorithm.frames.service.FacilityService;
import org.morgorithm.frames.service.MemberService;
import org.morgorithm.frames.service.StatusService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/status")
@Controller
@Log4j2
public class StatusController {
    private final StatusService statusService;
    private final MemberService memberService;
    private final FacilityService facilityService;

    @GetMapping("/list")
    public String register(PageRequestDTO pageRequestDTO, Model model) {
        PageRequestDTO pageRequestDTOClone = pageRequestDTO.toBuilder().build();
        if (pageRequestDTOClone.getFrom() != null && pageRequestDTOClone.getFrom().length() == 10) { // yyyy-mm-dd
            pageRequestDTOClone.setFrom(pageRequestDTOClone.getFrom() + " 00:00:00");
        }
        if (pageRequestDTOClone.getTo() != null && pageRequestDTOClone.getTo().length() == 10) { // yyyy-mm-dd
            pageRequestDTOClone.setTo(pageRequestDTOClone.getTo() + " 23:59:59");
        }
        model.addAttribute("result", statusService.getStatusList(pageRequestDTOClone));
        model.addAttribute("members", memberService.getAllMembers());
        model.addAttribute("facilities", facilityService.getAllFacilities());
        return "status/list";
    }

    @GetMapping("/sendSms")
    public String sendSns(PageRequestDTO pageRequestDTO) {

        statusService.sendSms(pageRequestDTO);
        return "redirect:/status/list";
    }
}
