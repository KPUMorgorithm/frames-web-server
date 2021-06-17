package org.morgorithm.frames.controller;

import lombok.RequiredArgsConstructor;
import org.morgorithm.frames.dto.SmsDTO;
import org.morgorithm.frames.service.SmsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class SmsController {
    private final SmsService smsService;

    @RequestMapping(value = "/sms", method = RequestMethod.POST, consumes = "application/json")
    public String getData(@RequestBody SmsDTO sender) {
        //   System.out.println("Name:"+Sms.getName()+" Job:"+student.getJob());
        //    tt.testInsert(student.getName(),student.getJob());
        //    return "Student Name is :" + student.getName() + " Job : " + student.getJob() ;
        SmsDTO smsDTO = sender;
        smsService.saveSmsInfo(smsDTO);
        return "sender :" + sender.getSender() + " content : " + sender.getContent() + " receivedDate: " + sender.getReceivedDate();
    }
}
