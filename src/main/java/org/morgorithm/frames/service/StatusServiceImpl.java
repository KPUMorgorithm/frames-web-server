package org.morgorithm.frames.service;

import com.google.common.collect.Lists;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.IteratorUtils;
import org.apache.tomcat.jni.Local;
import org.morgorithm.frames.dto.*;
import org.morgorithm.frames.entity.Facility;
import org.morgorithm.frames.entity.Member;
import org.morgorithm.frames.entity.QStatus;
import org.morgorithm.frames.entity.Status;
import org.morgorithm.frames.repository.StatusRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.function.Function;

@Service
@Log4j2
@RequiredArgsConstructor
public class StatusServiceImpl implements StatusService{

    static final int BUILDING_NUM=10;
    private final StatusRepository statusRepository;

    @Override
    public RealTimeStatusDTO getFacilityStatus() {
        RealTimeStatusDTO realTimeStatusDTO = new RealTimeStatusDTO();
        int[] in = new int[BUILDING_NUM];
        int[] out = new int[BUILDING_NUM];
        String[] bName=new String[BUILDING_NUM];
        int iidx = 0;
        int oidx = 0;
        int total=0;
        Boolean s = true;
        List<Object[]> result = statusRepository.getFacilityInInfo();
        Iterable<Status> statusResult=statusRepository.findAll(getStatusSearch());
        List<Status> statusList= Lists.newArrayList(statusResult);

        //**********dummy test data
        /*DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ran=LocalDateTime.parse("2021-03-16 12:30:22",formatter);


        Member m=Member.builder().mno(5L).build();
        Facility f= Facility.builder().bno(2L).building("building"+2L).build();
        Status s1=Status.builder().member(m).facility(f).state(true).temperature(36.6).build();
        s1.setRegDate(ran);
        statusList.add(s1);


        Member m2=Member.builder().mno(6L).build();
        Facility f2= Facility.builder().bno(3L).building("building"+3L).build();
        Status s2=Status.builder().member(m2).facility(f2).state(false).temperature(37.3).build();
        s2.setRegDate(ran);
        statusList.add(s2);

        Member m3=Member.builder().mno(10L).build();
        Facility f3= Facility.builder().bno(5L).building("building"+5L).build();

        Status s3=Status.builder().member(m3).facility(f3).state(true).temperature(36.1).build();
        s3.setRegDate(ran);
        statusList.add(s3);*/
        //************dummy test data

        System.out.println("test");
        for (int i = 0; i < result.size(); i++) {
            if (s){
                in[iidx] = ((Long) (result.get(i)[1])).intValue();
                Facility facility = (Facility)result.get(i)[0];
                bName[iidx++]=facility.getBuilding();
            }
            else
                out[oidx++] = ((Long) (result.get(i)[1])).intValue();
            total+=((Long) (result.get(i)[1])).intValue();
            s = !s;
        }
        realTimeStatusDTO = realTimeStatusDTO.builder().in(in).out(out).total(total).bName(bName).statusList(statusList).build();

        return realTimeStatusDTO;
    }
    private BooleanBuilder getStatusSearch(){
        BooleanBuilder booleanBuilder=new BooleanBuilder();
        BooleanBuilder conditionBuilder=new BooleanBuilder();
        QStatus qStatus = QStatus.status;

        //브라우저가 3초마다 업데이트되기 때문에 3초로 설정해 둔다.
        LocalDateTime latestUpdate=LocalDateTime.now().minusSeconds(3L);
        conditionBuilder.and(qStatus.regDate.between(latestUpdate,LocalDateTime.now()));
        return conditionBuilder;

    }
    @Override
    public PageResultDTO<StatusDTO, Status> getStatusList(PageRequestDTO requestDTO){
        Pageable pageable=requestDTO.getPageable(Sort.by("regDate").descending());
        BooleanBuilder booleanBuilder=getSearch(requestDTO);

        Page<Status> result=statusRepository.findAll(booleanBuilder,pageable);

        Function<Status, StatusDTO> fn=(entity->statusEntityToDto(entity));

        return new PageResultDTO<>(result, fn);
    }

    private BooleanBuilder getSearch(PageRequestDTO requestDTO){

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


        List<Long> bnoList= new ArrayList<>();


        //데이터 유효성 10일로 설정
        LocalDateTime now=LocalDateTime.now();

        LocalDateTime dataDue=LocalDateTime.now().minusDays(10L);


        System.out.println("now:"+now);

        System.out.println("minus one:"+dataDue);


        String type = requestDTO.getType();

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        QStatus qStatus = QStatus.status;

        String keyword = requestDTO.getKeyword();

        BooleanExpression expression = qStatus.statusnum.gt(0L);// gno > 0 조건만 생성

        booleanBuilder.and(expression);

        //검색 조건을 작성하기
        BooleanBuilder conditionBuilder = new BooleanBuilder();


        if(type == null || type.trim().length() == 0){ //검색 조건이 없는 경우
            //데이터 유효성 10일
            conditionBuilder.and(qStatus.regDate.between(dataDue,now));
            booleanBuilder.and(conditionBuilder);
            return booleanBuilder;
        }

       /* System.out.println("####################");
        System.out.println("facility num:"+Long.valueOf(requestDTO.getKeyword()));
        System.out.println("####################");*/

        System.out.println("####################");
        System.out.println("type String:"+requestDTO.getType());
        System.out.println("####################");




        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime from=null;
        LocalDateTime to=null;

        if(requestDTO.getFrom().length()!=0)
            from = LocalDateTime.parse(requestDTO.getFrom(), formatter);
        if(requestDTO.getTo().length()!=0)
             to = LocalDateTime.parse(requestDTO.getTo(), formatter);


        /*
        System.out.println("####################");
        System.out.println("parse from:"+from);
        System.out.println("####################");

        System.out.println("####################");
        System.out.println("parse to:"+to);
        System.out.println("####################");
*/


        //d->date
        if(type.contains("d") && requestDTO.getFrom().length()!=0 && requestDTO.getTo().length()!=0){
            conditionBuilder.and(qStatus.regDate.between(from,to));
            System.out.println("####################");
            System.out.println("type from:"+requestDTO.getFrom());
            System.out.println("####################");

            System.out.println("####################");
            System.out.println("type to:"+requestDTO.getTo());
            System.out.println("####################");
        }
        //b->bno
        if(type.contains("b") && requestDTO.getKeyword().length()!=0){
            conditionBuilder.and(qStatus.facility.bno.eq(Long.valueOf(requestDTO.getKeyword())));
        }
        if(type.contains("m") && requestDTO.getMno().length()!=0){
            List<Object> result=statusRepository.getMemberFacility(Long.valueOf(requestDTO.getMno()));
            BooleanBuilder buildingCondition=new BooleanBuilder();
            for(Object a:result){
                //확진자가 다녀갔던 건물들을 리스트에 넣는다.
                buildingCondition.or(qStatus.facility.bno.eq(((Facility) a).getBno()));
                bnoList.add(((Facility) a).getBno());
                System.out.println("mno bno!!!:"+((Facility) a).getBno());
            }
            conditionBuilder.and(buildingCondition);
            conditionBuilder.and(qStatus.member.mno.eq(Long.valueOf(requestDTO.getMno())));
        }
        if(type.contains("c") && requestDTO.getCloseContact()!=null && requestDTO.getCloseContact().length()!=0){
            List<Object> result2=statusRepository.getMemberFacility(Long.valueOf(requestDTO.getMno()));
            for(Object a:result2){
                //확진자가 다녀갔던 건물들을 리스트에 넣는다.
                bnoList.add(((Facility) a).getBno());
                System.out.println("mno bno!!!:"+((Facility) a).getBno());
            }




            System.out.println("test:"+requestDTO.getCloseContact());
            List<Object> result=statusRepository.getRegDate(Long.valueOf(requestDTO.getMno()));

            StringTokenizer st = new StringTokenizer(requestDTO.getCloseContact(), ":"); // ':'는 구분문자

            int count = st.countTokens(); // 구분 문자(:)로 구분된 문자열(토큰)의 개수를 count
            int idx=0;

            String min = st.nextToken(); // 각 토큰 출력
            String sec=st.nextToken();
            System.out.println("min: "+min+"sec: "+sec);
            BooleanBuilder connectCondition=new BooleanBuilder();
            for(Object a:result){
                BooleanBuilder closeContactCondition=new BooleanBuilder();
                LocalDateTime tempFrom;
                LocalDateTime tempTo;
                tempFrom=((LocalDateTime) a).minusMinutes(Long.valueOf(min)).minusSeconds(Long.valueOf(sec));

                tempTo=((LocalDateTime) a).plusMinutes(Long.valueOf(min)).plusSeconds(Long.valueOf(sec));
                /*System.out.println("확진자  Original Time: "+((LocalDateTime) a).toString());
                System.out.println("확진자  minus Time: "+tempFrom);
                System.out.println("확진자  plus Time: "+tempTo);*/
                closeContactCondition.and(qStatus.facility.bno.eq(bnoList.get(idx++)));
                closeContactCondition.and(qStatus.regDate.between(tempFrom,tempTo));
                connectCondition.or(closeContactCondition);
            }
            conditionBuilder.and(connectCondition);
        }



        //모든 조건 통합
        booleanBuilder.and(conditionBuilder);

        return booleanBuilder;
    }
}
