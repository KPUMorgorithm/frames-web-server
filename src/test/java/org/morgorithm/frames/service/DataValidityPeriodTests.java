package org.morgorithm.frames.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DataValidityPeriodTests {
    @Autowired
    DataValidityPeriod data;
    @Test
    void dataValidityPeriodTest(){
        data.dataValidityPeriod();
    }
}
