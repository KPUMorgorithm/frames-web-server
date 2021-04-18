package org.morgorithm.frames.service;

import com.google.common.collect.Lists;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.morgorithm.frames.entity.QStatus;
import org.morgorithm.frames.entity.Status;
import org.morgorithm.frames.repository.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class DataValidityPeriod {
    @Autowired
    StatusRepository statusRepository;
    @PostConstruct
    @Scheduled(cron="* * 1 * * *")
    void dataValidityPeriod(){
        //오늘 날짜
        LocalDateTime from=LocalDateTime.now().minusDays(14L);
        LocalDateTime to = LocalDateTime.now();
        BooleanBuilder booleanBuilder=new BooleanBuilder();
        QStatus qStatus = QStatus.status;



        //검색 조건을 작성하기
        BooleanBuilder conditionBuilder = new BooleanBuilder();

        //범위:오늘동안
        conditionBuilder.andNot(qStatus.regDate.between(from,to));


        booleanBuilder.and(conditionBuilder);
        Iterable<Status> result=statusRepository.findAll(booleanBuilder);
        statusRepository.deleteAll(result);

        //Auto-Increment 재정렬
        statusRepository.setSafeUpdate();
        statusRepository.initialCnt();
        statusRepository.reorderKeyId();
        statusRepository.initialAutoIncrementToTheLatest();

    }
}
