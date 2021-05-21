package org.morgorithm.frames.service;

import org.morgorithm.frames.dto.SmsDTO;
import org.morgorithm.frames.entity.Sms;

public interface SmsService {
    void saveSmsInfo(SmsDTO sms);
}
