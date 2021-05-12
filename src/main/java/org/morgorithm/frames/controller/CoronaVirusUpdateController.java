package org.morgorithm.frames.controller;


import lombok.RequiredArgsConstructor;
import org.morgorithm.frames.service.CoronaVirusDataService;
import org.morgorithm.frames.service.StatusService;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Controller
public class CoronaVirusUpdateController {


    private final CoronaVirusDataService coronaVirusDataService;
    private final StatusService statusService;



    @GetMapping("/")
    public String showHomePage() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String getData(Model model){
        model.addAttribute("dates",coronaVirusDataService.getDateList());
        model.addAttribute("dailyDeath",coronaVirusDataService.getDeathList());
        model.addAttribute("increaseFromYesterday",coronaVirusDataService.getDecideList());
        model.addAttribute("dailyCure",coronaVirusDataService.getClearList());
        model.addAttribute("dailyExam",coronaVirusDataService.getExamList());
        model.addAttribute("totalConfirmed",coronaVirusDataService.getTotalConfirmed());
        model.addAttribute("totalClear",coronaVirusDataService.getTotalClear());
        model.addAttribute("statusDTO", statusService.getFacilityStatus());


        return "daily/dailyUpdate";
    }




}

