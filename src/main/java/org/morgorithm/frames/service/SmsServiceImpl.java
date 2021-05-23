package org.morgorithm.frames.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.morgorithm.frames.dto.SmsDTO;
import org.morgorithm.frames.entity.Sms;
import org.morgorithm.frames.repository.SmsRepository;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService{
    private final SmsRepository smsRepository;

    @Override
    public void saveSmsInfo(SmsDTO smsDTO) {
        Sms sms=Sms.builder().sender(smsDTO.getSender()).content(smsDTO.getContent()).receivedDate(smsDTO.getReceivedDate()).status(true).build();
        smsRepository.save(sms);
    }

   /* public void testInsert(String n, String j) {
        Test test=Test.builder().name(n).job(j).build();
        System.out.println("*******name:"+n+" job:"+j);
        testRepository.save(test);
    }*/
}
