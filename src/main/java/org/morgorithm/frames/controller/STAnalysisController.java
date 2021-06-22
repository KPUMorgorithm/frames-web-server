package org.morgorithm.frames.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.morgorithm.frames.dto.AccessSetDTO;
import org.morgorithm.frames.dto.MemberDTO;
import org.morgorithm.frames.dto.PageRequestDTO;
import org.morgorithm.frames.dto.PageResultDTO;
import org.morgorithm.frames.entity.Member;
import org.morgorithm.frames.entity.Status;
import org.morgorithm.frames.projection.AccessSet;
import org.morgorithm.frames.service.AccessSetService;
import org.morgorithm.frames.service.FacilityService;
import org.morgorithm.frames.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
@RequestMapping("/st")
@Log4j2
public class STAnalysisController {
    private final MemberService memberService;
    private final FacilityService facilityService;
    private final AccessSetService accessSetService;

    @GetMapping("/list")
    public String getStAnalysis(Model model, PageRequestDTO pageRequestDTO) {
        List<AccessSet> accessSets = accessSetService.getAllAccessSet(pageRequestDTO);

        Pageable pageable = pageRequestDTO.getPageable(Sort.by("regDate"));
        final int start = (int) pageable.getOffset();
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

    @GetMapping("/overlapped")
    @ResponseBody
    public List<MemberDTO> overlappedMembers(AccessSetDTO accessSet) {
        System.out.println(accessSet.toString());
        List<Status> statusList = accessSetService.getStatusOverlapped(accessSet);
        List<MemberDTO> members = statusList.stream().map(e -> e.getMember()).distinct().filter(e -> e.getMno() != accessSet.getMemberId()).map(e -> memberService.memberEntityToDto(e)).collect(Collectors.toList());
        return members;
    }
}
