package org.morgorithm.frames.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.morgorithm.frames.dto.PageRequestDTO;
import org.morgorithm.frames.projection.AccessSet;
import org.morgorithm.frames.service.AccessSetService;
import org.morgorithm.frames.service.FacilityService;
import org.morgorithm.frames.service.StatusService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@Controller
@Log4j2
public class STAnalysisController {
        private final StatusService statusService;
        private final FacilityService facilityService;
        private final AccessSetService accessSetService;

    @GetMapping("/st")
    public String getStAnalysis(Model model
            , @RequestParam(value = "mno", required = false) Long mno){
        List<AccessSet> accessSets = null;
        accessSets = accessSetService.getAllAccessSet();
//        if (mno != null) accessSetService.getAccessSetByMemberId(mno);
//        else accessSets = accessSetService.getAllAccessSet();
        model.addAttribute("accessSets", accessSets);
        return "st/list";
    }
}
