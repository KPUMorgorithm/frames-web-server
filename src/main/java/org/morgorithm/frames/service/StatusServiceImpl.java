package org.morgorithm.frames.service;

import com.google.common.collect.Lists;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.apache.commons.collections.IteratorUtils;
import org.apache.tomcat.jni.Local;
import org.json.simple.JSONObject;
import org.morgorithm.frames.dto.*;
import org.morgorithm.frames.entity.Facility;
import org.morgorithm.frames.entity.Member;
import org.morgorithm.frames.entity.QStatus;
import org.morgorithm.frames.entity.Status;
import org.morgorithm.frames.repository.FacilityRepository;
import org.morgorithm.frames.repository.MemberRepository;
import org.morgorithm.frames.repository.StatusRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

@Service
@Log4j2
@RequiredArgsConstructor
public class StatusServiceImpl implements StatusService {

    static final int BUILDING_NUM = 10;
    private final StatusRepository statusRepository;
    private final MemberRepository memberRepository;
    private final FacilityRepository facilityRepository;

    @Override
    public EventDTO getEventInfo(){
        EventDTO eventDTO=new EventDTO();
        PageRequestDTO requestDTO=new PageRequestDTO();
        Pageable pageable = requestDTO.getPageable(Sort.by("regDate").descending());


        //위험군 온도 숫자 초기화
        BooleanBuilder booleanBuilder1 = getDangerEventCondition();
        Page<Status> resultDanger = statusRepository.findAll(booleanBuilder1, pageable);
        eventDTO.setDangerScanNum((int)resultDanger.getTotalElements());

        //경고군 온도 숫자 초기화
        BooleanBuilder booleanBuilder2 =  getWarningEventCondition();
        Page<Status> resultWarning = statusRepository.findAll(booleanBuilder2, pageable);
        eventDTO.setWarningScanNum((int)resultWarning.getTotalElements());

        //정상군 온도 숫자 초기화
        BooleanBuilder booleanBuilder3 =  getNormalEventCondition();
        Page<Status> resultNormal = statusRepository.findAll(booleanBuilder3, pageable);
        eventDTO.setNormalScanNum((int)resultNormal.getTotalElements());

        //하루 전체 검사 숫자 초기화
        eventDTO.setTotalScanNum(eventDTO.getDangerScanNum()+eventDTO.getNormalScanNum()+eventDTO.getWarningScanNum());

        //전체 멤버 수
        eventDTO.setTotalMember(memberRepository.getMemberNum());

        //전체 건물 수
        eventDTO.setNumOfFacility(facilityRepository.getFacilityNum());

        //오늘 날짜
        String date=LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy MM dd"));
        eventDTO.setTodayDate(date);

        //오늘 캠퍼스 내에 입장한 사람들 수 초기화
        Iterable<Status> result = statusRepository.findAll(dataCollectTime());
        HashSet<Long> hashSet=new HashSet<>();
        for(Object a:result){
            hashSet.add(((Status)a).getMember().getMno());
        }
        eventDTO.setInMember(hashSet.size());


        /*System.out.println("####danger: "+eventDTO.getDangerScanNum());
        System.out.println("####warning: "+eventDTO.getWarningScanNum());
        System.out.println("####normal: "+eventDTO.getNormalScanNum());
        System.out.println("####total: "+eventDTO.getTotalScanNum());
        System.out.println("####InMember: "+eventDTO.getInMember());
        System.out.println("####Today's Date: "+eventDTO.getTodayDate());
        System.out.println("####Facility Num: "+eventDTO.getNumOfFacility());
        System.out.println("####Number of Member: "+eventDTO.getTotalMember());*/
        return eventDTO;
    }

    @Override
    public List<Status> getDangerStatus() {
        BooleanBuilder booleanBuilder=getDangerEventCondition();
        Iterable<Status> result=statusRepository.findAll(booleanBuilder);
        List<Status> dangerList = Lists.newArrayList(result);
        return dangerList;
    }

    @Override
    public List<Status> getWarningStatus() {
        BooleanBuilder booleanBuilder=getWarningEventCondition();
        Iterable<Status> result=statusRepository.findAll(booleanBuilder);
        List<Status> warningList = Lists.newArrayList(result);
        return warningList;
    }

    @Override
    public List<Status> getNormalStatus() {
        BooleanBuilder booleanBuilder=getNormalEventCondition();
        Iterable<Status> result=statusRepository.findAll(booleanBuilder);
        List<Status> normalList = Lists.newArrayList(result);
        return normalList;
    }
    public BooleanBuilder dataCollectTime(){
        //오늘 날짜
        LocalDateTime from=LocalDateTime.now().minusDays(1L).with(LocalTime.of(23,59));
        LocalDateTime to = LocalDateTime.now();
        QStatus qStatus = QStatus.status;



        //검색 조건을 작성하기
        BooleanBuilder conditionBuilder = new BooleanBuilder();

        //범위:오늘동안
        conditionBuilder.and(qStatus.regDate.between(from,to));
        return conditionBuilder;


    }
    @Override
    public List<Status> getTotalStatus() {
        //오늘 날짜
        LocalDateTime from=LocalDateTime.now().minusDays(1L).with(LocalTime.of(23,59));
        LocalDateTime to = LocalDateTime.now();
        BooleanBuilder booleanBuilder=new BooleanBuilder();
        QStatus qStatus = QStatus.status;


        BooleanExpression expression = qStatus.statusnum.gt(0L);// gno > 0 조건만 생성

        booleanBuilder.and(expression);

        //검색 조건을 작성하기
        BooleanBuilder conditionBuilder = new BooleanBuilder();

        //범위:오늘동안
        conditionBuilder.and(qStatus.regDate.between(from,to));


        booleanBuilder.and(conditionBuilder);
        Iterable<Status> result=statusRepository.findAll(booleanBuilder);
        List<Status> totalList = Lists.newArrayList(result);

        return totalList;
    }

    BooleanBuilder getDangerEventCondition(){

        //오늘 날짜
        LocalDateTime from=LocalDateTime.now().minusDays(1L).with(LocalTime.of(23,59));
        LocalDateTime to = LocalDateTime.now();



        BooleanBuilder booleanBuilder = new BooleanBuilder();

        QStatus qStatus = QStatus.status;


        BooleanExpression expression = qStatus.statusnum.gt(0L);// gno > 0 조건만 생성

        booleanBuilder.and(expression);

        //검색 조건을 작성하기
        BooleanBuilder conditionBuilder = new BooleanBuilder();

        //범위:오늘동안
        conditionBuilder.and(qStatus.regDate.between(from,to));
        //위험 온도 37.2
        conditionBuilder.and(qStatus.temperature.between(37.3,40));

        booleanBuilder.and(conditionBuilder);



        return booleanBuilder;
    }
    BooleanBuilder getNormalEventCondition(){

        //오늘 날짜
        LocalDateTime from=LocalDateTime.now().minusDays(1L).with(LocalTime.of(23,59));
        LocalDateTime to = LocalDateTime.now();



        BooleanBuilder booleanBuilder = new BooleanBuilder();

        QStatus qStatus = QStatus.status;


        BooleanExpression expression = qStatus.statusnum.gt(0L);// gno > 0 조건만 생성

        booleanBuilder.and(expression);

        //검색 조건을 작성하기
        BooleanBuilder conditionBuilder = new BooleanBuilder();

        //범위:오늘동안
        conditionBuilder.and(qStatus.regDate.between(from,to));
        //위험 온도 37.2
        conditionBuilder.and(qStatus.temperature.between(30,36.8));

        booleanBuilder.and(conditionBuilder);



        return booleanBuilder;
    }
    BooleanBuilder getWarningEventCondition(){

        //오늘 날짜
        LocalDateTime from=LocalDateTime.now().minusDays(1L).with(LocalTime.of(23,59));
        LocalDateTime to = LocalDateTime.now();



        BooleanBuilder booleanBuilder = new BooleanBuilder();

        QStatus qStatus = QStatus.status;


        BooleanExpression expression = qStatus.statusnum.gt(0L);// gno > 0 조건만 생성

        booleanBuilder.and(expression);

        //검색 조건을 작성하기
        BooleanBuilder conditionBuilder = new BooleanBuilder();

        //범위:오늘동안
        conditionBuilder.and(qStatus.regDate.between(from,to));
        //위험 온도 37.2
        conditionBuilder.and(qStatus.temperature.between(36.9,37.2));

        booleanBuilder.and(conditionBuilder);



        return booleanBuilder;
    }
    @Override
    public RealTimeStatusDTO getFacilityStatus() {
        RealTimeStatusDTO realTimeStatusDTO = new RealTimeStatusDTO();
        int[] in = new int[BUILDING_NUM];
        int[] out = new int[BUILDING_NUM];
        String[] bName = new String[BUILDING_NUM];
        //초기화
        for(int i=0;i<BUILDING_NUM;i++){
            in[i]=0;
            out[i]=0;
            bName[i]="building"+(i+1);
        }

        int total = 0;
        LocalDateTime startDatetime = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.of(0,0,0)); //어제 00:00:00
        LocalDateTime endDatetime = LocalDateTime.of(LocalDate.now(), LocalTime.of(23,59,59));
        List<Object[]> result = statusRepository.getFacilityInInfoOneDay(startDatetime,endDatetime);
        Iterable<Status> statusResult = statusRepository.findAll(getStatusSearch());
        List<Status> statusList = Lists.newArrayList(statusResult);
       // System.out.println("#####statusList########: "+statusList.toString());
       // System.out.println("#####result########: "+result.toString());
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

        System.out.println("test getFacilityStatus result.size():"+result.size());
        for (int i = 0; i < result.size(); i++) {
           // System.out.println("test getFacilityStatus inside for loop"+result.size());
            //bno를 index 삼어서 in과 out배열의 인덱스로 씀
            int idx=((Long) (result.get(i)[2])).intValue();
            /*
            System.out.println("index:"+(idx-1));
            System.out.println("bno:"+((Long) (result.get(i)[2])).intValue());
            System.out.println("result.get(i):"+Arrays.toString(result.get(i)));
            System.out.println("result state:"+(Boolean)result.get(i)[3]);*/
            if ((Boolean)result.get(i)[3]) {
                in[(idx-1)] = ((Long) (result.get(i)[1])).intValue();
                Facility facility = (Facility) result.get(i)[0];
                bName[(idx-1)] = facility.getBuilding();
            } else
                out[(idx-1)] = ((Long) (result.get(i)[1])).intValue();
            total += ((Long) (result.get(i)[1])).intValue();
        }
        realTimeStatusDTO = realTimeStatusDTO.builder().in(in).out(out).total(total).bName(bName).statusList(statusList).build();

        return realTimeStatusDTO;
    }

    private BooleanBuilder getStatusSearch() {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        BooleanBuilder conditionBuilder = new BooleanBuilder();
        QStatus qStatus = QStatus.status;

        //브라우저가 3초마다 업데이트되기 때문에 3초로 설정해 둔다.
        LocalDateTime latestUpdate = LocalDateTime.now().minusSeconds(3L);
        conditionBuilder.and(qStatus.regDate.between(latestUpdate, LocalDateTime.now()));
        return conditionBuilder;

    }

    @Override
    public PageResultDTO<StatusDTO, Status> getStatusList(PageRequestDTO requestDTO) {
        Pageable pageable = requestDTO.getPageable(Sort.by("regDate").descending());
        BooleanBuilder booleanBuilder = getSearch(requestDTO);

        Page<Status> result = statusRepository.findAll(booleanBuilder, pageable);

        Function<Status, StatusDTO> fn = (entity -> statusEntityToDto(entity));

        return new PageResultDTO<>(result, fn);
    }

    @Override
    public void sendSms(PageRequestDTO requestDTO) {
        List<String> confirmedPath=new ArrayList<>();
        List<String> contactPath=new ArrayList<>();
        HashSet<Long> mnos=new HashSet<>();



        //배열 초기화
        Pageable pageable = requestDTO.getPageable(Sort.by("regDate").descending());
        BooleanBuilder booleanBuilder = getSearch(requestDTO);
        Page<Status> result = statusRepository.findAll(booleanBuilder, pageable);
        for(Status p:result){
            if(p.getMember().getMno().equals(Long.valueOf(requestDTO.getMno()))){
               confirmedPath.add("건물: "+p.getFacility().getBuilding()+" 출입여부: "+(p.getState()?"입장":"퇴장")+" 시간: "+p.getRegDate());
            }else{
                contactPath.add("건물: "+p.getFacility().getBuilding()+" 출입여부: "+(p.getState()?"입장":"퇴장")+" 시간: "+p.getRegDate());
                mnos.add(p.getMember().getMno());
            }
        }
        for(Long m:mnos){
            System.out.println("MNOS:"+m);
        }
       /* for(String s:confirmedPath){
            System.out.println(s);
        }*/


        System.out.println("Sending SNS Message");
        String api_key = "NCSJBLIL70QSO6P9";
        //사이트에서 발급 받은 API KEY
        String api_secret = "4BDRU03ULDUDLOY9BZ7AZIOLXAALBSUW";
        // 사이트에서 발급 받은 API SECRET KEY
        Message coolsms = new Message(api_key, api_secret);

        HashMap<String, String> params = new HashMap<String, String>();


        params.put("from", "01096588541");



        params.put("type", "LMS");
        String s="";
        //여럿한테 보낼때
        for(Long mno:mnos){
            s+="한국산업기술대학교 코로나 비상대책본부에서 알려드립니다.\n" +
                    "교내에 확진자 발생에 의한 정보를 알려드리겠습니다.\n" +
                    "해당 문자 수신자께서는 확진자와 동선이 겹침을 알립니다.\n"+
                    "확진자 동선을 공개합니다.\n\n";
            s+="<확진자 동선>\n\n";
            //확진자 동선 적는 부분
            for(Status p:result){
                if(p.getMember().getMno().equals(Long.valueOf(requestDTO.getMno()))){
                    String dateTime=p.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    s+=     "건물: "+p.getFacility().getBuilding()+"\n"+
                            "시간: "+dateTime+"\n"+
                            "출입여부: "+(p.getState()?"입장\n\n":"퇴장\n\n");
                }
            }

            s+="<수신자 동선>\n\n";
            String phoneNum="";
            //수신자 동선 적는 부분
            for(Status p:result){
                if(mno==p.getMember().getMno()){
                    String dateTime=p.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    s+=     "건물: "+p.getFacility().getBuilding()+"\n"+
                            "시간: "+dateTime+"\n"+
                            "출입여부: "+(p.getState()?"입장\n\n":"퇴장\n\n");
                    phoneNum=p.getMember().getPhone();
                }
            }
            s+="임으로 확진자와 동선이 겹칩니다.\n"+
                    "가까운 보건소를 방문하여 검사를 받으시기 바랍니다.\n";

            System.out.println("phoneNum:"+phoneNum);
            params.put("text",s);
            params.put("app_version", "test app 1.2");

            //*******************
            //*******************
            //테스트 용 번호
            //*******************
            //*******************
            params.put("to", "01030588541");
            //*******************
            //*******************
            //테스트 용 번호
            //*******************
            //*******************

            //%%%%%%%%%%%%%%%%%%경계선%%%%%%%%%%%%%%%%%%%%%%%

            //*******************
            //*******************
            //실제 데모용
            //*******************
            //*******************
           // params.put("to", phoneNum);
            //*******************
            //*******************
            //실제 데모용
            //*******************
            //*******************



            try {
                JSONObject obj = (JSONObject) coolsms.send(params);
                System.out.println(obj.toString());

            }catch (CoolsmsException e){
                System.out.println(e.getMessage());
                System.out.println(e.getCode());
            }
            s="";
            phoneNum="";
        }



    }

    private BooleanBuilder getSearch(PageRequestDTO requestDTO) {

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


        List<Long> bnoList = new ArrayList<>();


        //데이터 유효성 10일로 설정
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime dataDue = LocalDateTime.now().minusDays(10L);


        System.out.println("now:" + now);

        System.out.println("minus one:" + dataDue);


        String type = requestDTO.getType();

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        QStatus qStatus = QStatus.status;

        String keyword = requestDTO.getKeyword();

        BooleanExpression expression = qStatus.statusnum.gt(0L);// gno > 0 조건만 생성

        booleanBuilder.and(expression);

        //검색 조건을 작성하기
        BooleanBuilder conditionBuilder = new BooleanBuilder();


        if (type == null || type.trim().length() == 0) { //검색 조건이 없는 경우
            //데이터 유효성 10일
            conditionBuilder.and(qStatus.regDate.between(dataDue, now));
            booleanBuilder.and(conditionBuilder);
            return booleanBuilder;
        }

       /* System.out.println("####################");
        System.out.println("facility num:"+Long.valueOf(requestDTO.getKeyword()));
        System.out.println("####################");*/

        System.out.println("####################");
        System.out.println("type String:" + requestDTO.getType());
        System.out.println("####################");


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime from = null;
        LocalDateTime to = null;

        if (requestDTO.getFrom().length() != 0)
            from = LocalDateTime.parse(requestDTO.getFrom(), formatter);
        if (requestDTO.getTo().length() != 0)
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
        if (type.contains("d") && requestDTO.getFrom().length() != 0 && requestDTO.getTo().length() != 0) {
            conditionBuilder.and(qStatus.regDate.between(from, to));
            System.out.println("####################");
            System.out.println("type from:" + requestDTO.getFrom());
            System.out.println("####################");

            System.out.println("####################");
            System.out.println("type to:" + requestDTO.getTo());
            System.out.println("####################");
        }
        //b->bno
        if (type.contains("b") && requestDTO.getKeyword().length() != 0) {
            conditionBuilder.and(qStatus.facility.bno.eq(Long.valueOf(requestDTO.getKeyword())));
        }
        if (type.contains("m") && requestDTO.getMno().length() != 0) {
            List<Object> result = statusRepository.getMemberFacility(Long.valueOf(requestDTO.getMno()));
            BooleanBuilder buildingCondition = new BooleanBuilder();
            for (Object a : result) {
                //확진자가 다녀갔던 건물들을 리스트에 넣는다.
                buildingCondition.or(qStatus.facility.bno.eq(((Facility) a).getBno()));
                bnoList.add(((Facility) a).getBno());
                System.out.println("mno bno!!!:" + ((Facility) a).getBno());
            }
            conditionBuilder.and(buildingCondition);
            conditionBuilder.and(qStatus.member.mno.eq(Long.valueOf(requestDTO.getMno())));
        }
        if (type.contains("c") && requestDTO.getCloseContact() != null && requestDTO.getCloseContact().length() != 0) {
            List<Object> result2 = statusRepository.getMemberFacility(Long.valueOf(requestDTO.getMno()));
            for (Object a : result2) {
                //확진자가 다녀갔던 건물들을 리스트에 넣는다.
                bnoList.add(((Facility) a).getBno());
                System.out.println("mno bno!!!:" + ((Facility) a).getBno());
            }


            System.out.println("test:" + requestDTO.getCloseContact());
            List<Object[]> result = statusRepository.getRegDateAndState(Long.valueOf(requestDTO.getMno()));


            StringTokenizer st = new StringTokenizer(requestDTO.getCloseContact(), ":"); // ':'는 구분문자

            int count = st.countTokens(); // 구분 문자(:)로 구분된 문자열(토큰)의 개수를 count
            int idx = 0;

            String min = st.nextToken(); // 각 토큰 출력
            String sec = st.nextToken();
            System.out.println("min: " + min + "sec: " + sec);
            BooleanBuilder connectCondition = new BooleanBuilder();
            //입장과 퇴장을 각각 조건을 해줘야 함으로 for문의 조건 2로 함

            for (Object[] a : result) {
                BooleanBuilder closeContactCondition1 = new BooleanBuilder();
                LocalDateTime tempFrom;
                LocalDateTime tempTo;
                tempFrom = ((LocalDateTime) a[0]).minusMinutes(Long.valueOf(min)).minusSeconds(Long.valueOf(sec));

                tempTo = ((LocalDateTime) a[0]).plusMinutes(Long.valueOf(min)).plusSeconds(Long.valueOf(sec));
                Boolean flag = ((Boolean) a[1]).booleanValue();
                System.out.println("********BooleanValue(): "+flag);
                System.out.println("확진자  Original Time: "+((LocalDateTime) a[0]).toString());
                System.out.println("확진자  minus Time: "+tempFrom);
                System.out.println("확진자  plus Time: "+tempTo);
                System.out.println("확진자  State: "+flag);
                //여기서는 flag가 true니까 확진자와 같이 입장한 사람들 질의
                closeContactCondition1.and(qStatus.facility.bno.eq(bnoList.get(idx++)));
                closeContactCondition1.and(qStatus.regDate.between(tempFrom, tempTo));
                closeContactCondition1.and(qStatus.state.eq(flag));
                connectCondition.or(closeContactCondition1);




            }




            conditionBuilder.and(connectCondition);
        }


        //모든 조건 통합
        booleanBuilder.and(conditionBuilder);

        return booleanBuilder;
    }
}
