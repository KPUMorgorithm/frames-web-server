package org.morgorithm.frames.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.morgorithm.frames.dto.PageRequestDTO;
import org.morgorithm.frames.dto.PageResultDTO;
import org.morgorithm.frames.projection.AccessSet;
import org.morgorithm.frames.repository.MemberRepository;
import org.morgorithm.frames.service.AccessSetService;
import org.morgorithm.frames.service.FacilityService;
import org.morgorithm.frames.service.MemberService;
import org.morgorithm.frames.service.StatusService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        private final MemberService memberService;
        private final FacilityService facilityService;
        private final AccessSetService accessSetService;

    @GetMapping("/st")
    public String getStAnalysis(Model model, PageRequestDTO pageRequestDTO){
        List<AccessSet> accessSets = accessSetService.getAllAccessSet(pageRequestDTO);

        Pageable pageable = pageRequestDTO.getPageable(Sort.by("regDate"));
        final int start = (int)pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), accessSets.size());

        Page<AccessSet> page = new PageImpl<>(accessSets.subList(start, end), pageable, accessSets.size());
        PageResultDTO<AccessSet, AccessSet> pageResultDTO = new PageResultDTO<>(page, e -> e);
        model.addAttribute("members", memberService.getAllMembers());
        model.addAttribute("facilities", facilityService.getAllFacilities());
        model.addAttribute("accessSets", accessSets);
        model.addAttribute("result", pageResultDTO);
        model.addAttribute("pageRequestDTO", pageRequestDTO);
        return "st/list";
    }
}
