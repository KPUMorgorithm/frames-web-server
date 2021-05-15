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
import org.springframework.transaction.annotation.Transactional;

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
    private int flag = 1;

    private Long latestStatusNum = 0L; // 마지막으로 읽어온 로그 넘버
    class Pair implements Comparable<Pair>{

        private LocalDateTime ldt;
        private double temperature;
        public Pair(LocalDateTime x, double y){
            this.ldt=x;
            this.temperature=y;
        }

        public LocalDateTime getX(){
            return ldt;
        }

        public double getY(){
            return temperature;
        }

        @Override
        public int compareTo(Pair o) {
            boolean isAfter= this.ldt.isAfter(o.getX());


            if(isAfter){
                return 1;
            }else if(this.ldt.isEqual(o.getX())){
                if(this.temperature>o.temperature)
                    return 1;
            }
            return -1;
        }
    }
    class Data implements Comparable<Data>{

        private String ldt;
        private double temperature;
        public Data(String x, double y){
            this.ldt=x;
            this.temperature=y;
        }

        public String getX(){
            return ldt;
        }

        public double getY(){
            return temperature;
        }

        @Override
        public int compareTo(Data o) {
            if(this.ldt.compareTo(o.ldt)>0?true:false){
                return 1;
            }else if(this.ldt.equals(o.ldt)){
                if(this.temperature>o.temperature)
                    return 1;
            }
            return -1;
        }
    }
    @Override
    public List<Data> getList(Long mno) {
        List<Object[]> result = statusRepository.getMemberDailyTemperatureStatus(mno);
        HashMap<String, Double> dailyStatus = new HashMap<String, Double>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM-dd");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy MM-dd HH:mm:ss");
        String hhMMSS=" 00:00:00";
        List<Pair> pairList=new ArrayList<>();
        List<Data> dataList=new ArrayList<>();
        LocalDateTime stringToLdt;
        for (Object[] a : result) {
           /* System.out.println(Arrays.toString(a));
            System.out.println("temperature"+a[1]);
            System.out.println("regDate:"+a[0]);*/
            if(a[0]!=null){
                String temp=((LocalDateTime) a[0]).format(formatter);
                temp+=hhMMSS;
                stringToLdt=LocalDateTime.parse(temp, formatter2);
                Pair pair=new Pair(stringToLdt,(double)a[1]);
                pairList.add(pair);
               // dailyStatus.put(temp.format(formatter), (double) a[1]);
            }
        }
        Collections.sort(pairList);
        for(Pair p:pairList){
          //  System.out.println("date:"+p.getX()+", temperature:"+p.getY());
            dailyStatus.put(p.getX().format(formatter), p.getY());
        }
/*
        dailyStatus.forEach((key, value)
                -> System.out.println("key: " + key + ", value: " + value));*/
        dailyStatus.forEach((key, value)
                -> dataList.add(new Data(key,value)));
        Collections.sort(dataList);
       /* for(Data p:dataList){
            System.out.println("date:"+p.getX()+", temperature:"+p.getY());

        }*/
        return dataList;
    }

    @Override
    public EventDTO getEventInfo() {
        EventDTO eventDTO = new EventDTO();
        PageRequestDTO requestDTO = new PageRequestDTO();
        Pageable pageable = requestDTO.getPageable(Sort.by("regDate").descending());


        //위험군 온도 숫자 초기화
        BooleanBuilder booleanBuilder1 = getDangerEventCondition();
        Page<Status> resultDanger = statusRepository.findAll(booleanBuilder1, pageable);
        eventDTO.setDangerScanNum((int) resultDanger.getTotalElements());

        //경고군 온도 숫자 초기화
        BooleanBuilder booleanBuilder2 = getWarningEventCondition();
        Page<Status> resultWarning = statusRepository.findAll(booleanBuilder2, pageable);
        eventDTO.setWarningScanNum((int) resultWarning.getTotalElements());

        //정상군 온도 숫자 초기화
        BooleanBuilder booleanBuilder3 = getNormalEventCondition();
        Page<Status> resultNormal = statusRepository.findAll(booleanBuilder3, pageable);

        eventDTO.setNormalScanNum((int) resultNormal.getTotalElements());

        //하루 전체 검사 숫자 초기화
        eventDTO.setTotalScanNum(eventDTO.getDangerScanNum() + eventDTO.getNormalScanNum() + eventDTO.getWarningScanNum());

        //전체 멤버 수
        eventDTO.setTotalMember(memberRepository.getMemberNum());

        //전체 시설 수
        eventDTO.setNumOfFacility(facilityRepository.getFacilityNum());

        //오늘 날짜
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy MM dd"));
        eventDTO.setTodayDate(date);

        //오늘 캠퍼스 내에 입장한 사람들 수 초기화
        Iterable<Status> result = statusRepository.findAll(dataCollectTime());
        HashSet<Long> hashSet = new HashSet<>();
        for (Object a : result) {
            hashSet.add(((Status) a).getMember().getMno());
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
        BooleanBuilder booleanBuilder = getDangerEventCondition();
        Iterable<Status> result = statusRepository.findAll(booleanBuilder);
        List<Status> dangerList = Lists.newArrayList(result);
        return dangerList;
    }

    @Override
    public List<Status> getWarningStatus() {
        BooleanBuilder booleanBuilder = getWarningEventCondition();
        Iterable<Status> result = statusRepository.findAll(booleanBuilder);
        List<Status> warningList = Lists.newArrayList(result);
        return warningList;
    }

    @Override
    public List<Status> getNormalStatus() {
        BooleanBuilder booleanBuilder = getNormalEventCondition();
        Iterable<Status> result = statusRepository.findAll(booleanBuilder);
        List<Status> normalList = Lists.newArrayList(result);
        return normalList;
    }

    public BooleanBuilder dataCollectTime() {
        //오늘 날짜
        LocalDateTime from = LocalDateTime.now().minusDays(1L).with(LocalTime.of(23, 59));
        LocalDateTime to = LocalDateTime.now();
        QStatus qStatus = QStatus.status;


        //검색 조건을 작성하기
        BooleanBuilder conditionBuilder = new BooleanBuilder();

        //범위:오늘동안
        conditionBuilder.and(qStatus.regDate.between(from, to));

        return conditionBuilder;


    }

    @Override
    public List<Status> getTotalStatus() {
        //오늘 날짜
        LocalDateTime from = LocalDateTime.now().minusDays(1L).with(LocalTime.of(23, 59));
        LocalDateTime to = LocalDateTime.now();
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QStatus qStatus = QStatus.status;


        BooleanExpression expression = qStatus.statusnum.gt(0L);// gno > 0 조건만 생성

        booleanBuilder.and(expression);

        //검색 조건을 작성하기
        BooleanBuilder conditionBuilder = new BooleanBuilder();

        //범위:오늘동안
        conditionBuilder.and(qStatus.regDate.between(from, to));


        booleanBuilder.and(conditionBuilder);
        Iterable<Status> result = statusRepository.findAll(booleanBuilder);
        List<Status> totalList = Lists.newArrayList(result);

        return totalList;
    }


    BooleanBuilder getDangerEventCondition() {

        //오늘 날짜
        LocalDateTime from = LocalDateTime.now().minusDays(1L).with(LocalTime.of(23, 59));
        LocalDateTime to = LocalDateTime.now();


        BooleanBuilder booleanBuilder = new BooleanBuilder();

        QStatus qStatus = QStatus.status;


        BooleanExpression expression = qStatus.statusnum.gt(0L);// gno > 0 조건만 생성

        booleanBuilder.and(expression);

        //검색 조건을 작성하기
        BooleanBuilder conditionBuilder = new BooleanBuilder();

        //범위:오늘동안
        conditionBuilder.and(qStatus.regDate.between(from, to));
        //위험 온도 37.2
        conditionBuilder.and(qStatus.temperature.between(37.3, 40));

        booleanBuilder.and(conditionBuilder);


        return booleanBuilder;
    }

    BooleanBuilder getNormalEventCondition() {

        //오늘 날짜
        LocalDateTime from = LocalDateTime.now().minusDays(1L).with(LocalTime.of(23, 59));
        LocalDateTime to = LocalDateTime.now();


        BooleanBuilder booleanBuilder = new BooleanBuilder();

        QStatus qStatus = QStatus.status;


        BooleanExpression expression = qStatus.statusnum.gt(0L);// gno > 0 조건만 생성

        booleanBuilder.and(expression);

        //검색 조건을 작성하기
        BooleanBuilder conditionBuilder = new BooleanBuilder();

        //범위:오늘동안
        conditionBuilder.and(qStatus.regDate.between(from, to));
        //위험 온도 37.2
        conditionBuilder.and(qStatus.temperature.between(30, 36.8));

        booleanBuilder.and(conditionBuilder);


        return booleanBuilder;
    }

    BooleanBuilder getWarningEventCondition() {

        //오늘 날짜
        LocalDateTime from = LocalDateTime.now().minusDays(1L).with(LocalTime.of(23, 59));
        LocalDateTime to = LocalDateTime.now();


        BooleanBuilder booleanBuilder = new BooleanBuilder();

        QStatus qStatus = QStatus.status;


        BooleanExpression expression = qStatus.statusnum.gt(0L);// gno > 0 조건만 생성

        booleanBuilder.and(expression);

        //검색 조건을 작성하기
        BooleanBuilder conditionBuilder = new BooleanBuilder();

        //범위:오늘동안
        conditionBuilder.and(qStatus.regDate.between(from, to));
        //위험 온도 37.2
        conditionBuilder.and(qStatus.temperature.between(36.9, 37.2));

        booleanBuilder.and(conditionBuilder);


        return booleanBuilder;
    }

    @Override
    public RealTimeStatusDTO getFacilityStatus() {
        int fidx = 0;
        List<Object[]> facilityNames = facilityRepository.getFacilityNames();
        RealTimeStatusDTO realTimeStatusDTO = new RealTimeStatusDTO();
        int[] in = new int[BUILDING_NUM];
        int[] out = new int[BUILDING_NUM];
        String[] bName = new String[BUILDING_NUM];

        //초기화
        for (int i = 0; i < BUILDING_NUM; i++) {
            in[i] = 0;
            out[i] = 0;
        }
        for (Object[] a : facilityNames) {
            bName[fidx++] = (String) a[0];
        }

        int total = 0;
        LocalDateTime startDatetime = LocalDateTime.now().minusDays(1L).with(LocalTime.of(23, 59));
        LocalDateTime endDatetime = LocalDateTime.now();
        List<Object[]> result = statusRepository.getFacilityInInfoOneDay(startDatetime, endDatetime);
        List<Status> statusList = statusRepository.findRecentStatusList(latestStatusNum); // 실시간 출입현황 가져오기
        if (statusList.size() > 0)
            latestStatusNum = statusList.get(0).getStatusnum();

        System.out.println("test getFacilityStatus result.size():" + result.size());
        System.out.println(statusList);

        for (int i = 0; i < result.size(); i++) {
            // System.out.println("test getFacilityStatus inside for loop"+result.size());
            //bno를 index 삼어서 in과 out배열의 인덱스로 씀
            int idx = ((Long) (result.get(i)[2])).intValue();
            /*
            System.out.println("index:"+(idx-1));
            System.out.println("bno:"+((Long) (result.get(i)[2])).intValue());
            System.out.println("result.get(i):"+Arrays.toString(result.get(i)));
            System.out.println("result state:"+(Boolean)result.get(i)[3]);*/
            if ((Boolean) result.get(i)[3]) {
                in[(idx - 1)] = ((Long) (result.get(i)[1])).intValue();
                Facility facility = (Facility) result.get(i)[0];
                bName[(idx - 1)] = facility.getBuilding();
            } else
                out[(idx - 1)] = ((Long) (result.get(i)[1])).intValue();
            total += ((Long) (result.get(i)[1])).intValue();
        }
        realTimeStatusDTO = realTimeStatusDTO.builder().in(in).out(out).total(total).bName(bName).statusList(statusList).build();
        return realTimeStatusDTO;
    }

    @Deprecated(forRemoval = true)
    private BooleanBuilder getStatusSearch() {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        BooleanBuilder conditionBuilder = new BooleanBuilder();
        QStatus qStatus = QStatus.status;
        Long infiniteStatusNum = 10000000L;
        Status status;

        if (latestStatusNum != null) {

            // status=statusRepository.findTopByOrderByStatusnumDesc();
            //  latestStatusNum=status.getStatusnum();
            System.out.println("latestStatusNum before:" + latestStatusNum);
            latestStatusNum += 1;
            flag = 0;
        }
        System.out.println("latestStatusNum after:" + latestStatusNum);


        conditionBuilder.and(qStatus.statusnum.between(latestStatusNum, infiniteStatusNum));

        status = statusRepository.findTopByOrderByStatusnumDesc();
        if (status != null) {
            latestStatusNum = status.getStatusnum();
            System.out.println("***********************************");
            System.out.println("print:" + status.toString());
            System.out.println("***********************************");
            System.out.println();
            System.out.println();
            System.out.println();
        }

        return conditionBuilder;

    }

    @Override
    public PageResultDTO<StatusDTO, Status> getStatusList(PageRequestDTO requestDTO) {
        Pageable pageable = requestDTO.getPageable(Sort.by("regDate").descending());
        //여기서는 from과 to와 관계 없이 모든 데이터를 가져와야 한다.
        //getSearch함수에서 from과 to가 null일 때 조건문이 발동하게 만든다.
//        LocalDateTime from=null;
//        LocalDateTime to=null;
//        BooleanBuilder booleanBuilder = getSearch(requestDTO,from,to);
        BooleanBuilder booleanBuilder = getSearch(requestDTO);

        Page<Status> result = statusRepository.findAll(booleanBuilder, pageable);


        Function<Status, StatusDTO> fn = (entity -> statusEntityToDto(entity));

        return new PageResultDTO<>(result, fn);
    }

    @Override
    public TrackerInfoDTO getMapInfo(TrackerInfoDTO trackerInfoDTO) {
        List<Long> bnos = new ArrayList<>();
        List<String> dates = new ArrayList<>();
        List<String> bName = new ArrayList<>();
        List<Long> bno = new ArrayList<>();
        List<Boolean> state=new ArrayList<>();
        int len;
        int bidx = 0;
        BooleanBuilder conditionBuilder = new BooleanBuilder();
        String hhmmss = " 00:00:00";
        QStatus qStatus = QStatus.status;
        String date = trackerInfoDTO.getDate();
        TrackerInfoDTO infoDTO = new TrackerInfoDTO();
        // MM/dd/yyyy 포맷에서 MM/dd/yyyy 00:00:00 더해줌
        List<Object[]> nameResult = facilityRepository.getFacilityNames();
        List<Object[]> bnoResult = facilityRepository.getFacilityBno();

        for (Object[] a : nameResult)
            bName.add((String) a[0]);
        for (Object[] a : bnoResult)
            bno.add((Long) a[0]);
        len=bName.size();
        if (date != null) {
            date += hhmmss;
            //LocalDateTime으로 파싱
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
            LocalDateTime from = LocalDateTime.parse(date, formatter);
            LocalDateTime to = from.with(LocalTime.of(23,59));
            // 하룻동안
            from = from.with(LocalTime.of(00, 00));
            System.out.println("from:"+from );
            System.out.println("to:"+to );
            conditionBuilder.and(qStatus.regDate.between(from, to));
            conditionBuilder.and(qStatus.member.mno.eq(trackerInfoDTO.getMno()));
            Iterable<Status> result = statusRepository.findAll(conditionBuilder);
            Iterable<Status> stateResult=statusRepository.findAll(conditionBuilder);
            List<Status> mapInfoList = Lists.newArrayList(result);
            List<Status> stateInfoList=Lists.newArrayList(stateResult);
          //  System.out.println("*********************");
          //  System.out.println("info:" + mapInfoList.toString());
            for(Status s:stateInfoList){
                state.add(s.getState());
            }
            for (Status s : mapInfoList) {
                bnos.add(s.getFacility().getBno());
                dates.add(s.getRegDate().format(DateTimeFormatter.ofPattern("yyyy MM-dd HH:mm:ss")));
                // System.out.println("bnos:"+s.getFacility().getBno());
                //  System.out.println("date:"+s.getRegDate().format(DateTimeFormatter.ofPattern("yyyy MM-dd HH:mm:ss")));
            }
            infoDTO.setTrackingBno(bnos);
            infoDTO.setDates(dates);
            len=mapInfoList.size();
            infoDTO.setEntranceStatus(state);
        }
        infoDTO.setLength(len);
        System.out.println("length:"+len);
        infoDTO.setBName(bName);
        infoDTO.setBno(bno);


        return infoDTO;
    }


    @Override
    public void sendSms(PageRequestDTO requestDTO) {
        List<String> confirmedPath = new ArrayList<>();
        List<String> contactPath = new ArrayList<>();
        List<Status> totalStatusList=new ArrayList<>();
        LocalDateTime from=null;
        LocalDateTime to=null;
        String hhMMSSFrom=" 00:00:00";
        String hhMMSSTo=" 23:59:59";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        HashSet<Long> mnos = new HashSet<>();
        int pageIdx=0;


        //배열 초기화
        requestDTO.setSize(100000);
        Pageable pageable = requestDTO.getPageable(Sort.by("regDate").descending());

        //확진자 메시지 보낼때 오늘을 포함한 최근 10일동안의 데이터를 훑는다.
        //LocalDateTime에서 String으로 변환하고 하루씩 범위를 설정해주게끔 만들고
        //다시 LocalDatetime으로 변환해서 between 범위로 쓴다.
        /*for(int i=9;i>=0;i--){
            from=LocalDateTime.now();
            to=LocalDateTime.now();
            from=from.minusDays(i);
            to=to.minusDays(i);
            String f=from.format(formatter);
            String t=to.format(formatter);
            f+=hhMMSSFrom;
            t+=hhMMSSTo;
            from=LocalDateTime.parse(f,formatter2);
            to=LocalDateTime.parse(t,formatter2);
            System.out.println("FROM:"+from.toString()+", TO"+to.toString());*/

//            BooleanBuilder booleanBuilder = getSearch(requestDTO,from,to);
            BooleanBuilder booleanBuilder = getSearch(requestDTO);
            Page<Status> result = statusRepository.findAll(booleanBuilder, pageable);
            for(Status p:result){
                totalStatusList.add(p);
            }
      //  }



        for(Status p: totalStatusList){
            System.out.println("시설: " + p.getFacility().getBuilding() + " 출입여부: " + (p.getState() ? "입장" : "퇴장") + " 시간: " + p.getRegDate());
        }
        //확진자 동선과 접촉자 동선을 confirmedPath와 contactPath로 분류
        for (Status p : totalStatusList) {
            if (p.getMember().getMno().equals(Long.valueOf(requestDTO.getMno()))) {
                confirmedPath.add("시설: " + p.getFacility().getBuilding() + " 출입여부: " + (p.getState() ? "입장" : "퇴장") + " 시간: " + p.getRegDate());
            } else {
                contactPath.add("시설: " + p.getFacility().getBuilding() + " 출입여부: " + (p.getState() ? "입장" : "퇴장") + " 시간: " + p.getRegDate());
                mnos.add(p.getMember().getMno());
            }
        }
        for (Long m : mnos) {
            System.out.println("MNOS:" + m);
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
        String s = "";
        //여럿한테 보낼때
        for (Long mno : mnos) {
            s += "한국산업기술대학교 코로나 비상대책본부에서 알려드립니다.\n" +
                    "교내에 확진자 발생에 의한 정보를 알려드리겠습니다.\n" +
                    "해당 문자 수신자께서는 확진자와 동선이 겹침을 알립니다.\n" +
                    "확진자 동선을 공개합니다.\n\n";
            s += "<확진자 동선>\n\n";
            //확진자 동선 적는 부분
            for (Status p : totalStatusList) {
                if (p.getMember().getMno().equals(Long.valueOf(requestDTO.getMno()))) {
                    String dateTime = p.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    s += "시설: " + p.getFacility().getBuilding() + "\n" +
                            "시간: " + dateTime + "\n" +
                            "출입여부: " + (p.getState() ? "입장\n\n" : "퇴장\n\n");
                }
            }

            s += "<수신자 동선>\n\n";
            String phoneNum = "";
            //수신자 동선 적는 부분
            for (Status p : totalStatusList) {
                if (mno == p.getMember().getMno()) {
                    String dateTime = p.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    s += "시설: " + p.getFacility().getBuilding() + "\n" +
                            "시간: " + dateTime + "\n" +
                            "출입여부: " + (p.getState() ? "입장\n\n" : "퇴장\n\n");
                    phoneNum = p.getMember().getPhone();
                }
            }
            s += "임으로 확진자와 동선이 겹칩니다.\n" +
                    "가까운 보건소를 방문하여 검사를 받으시기 바랍니다.\n";

            System.out.println("phoneNum:" + phoneNum);
            params.put("text", s);
            params.put("app_version", "test app 1.2");

            //*******************
            //*******************
            //테스트 용 번호
            //*******************
            //*******************

           // params.put("to", "01030588541");

            //params.put("to", "01030588541");

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
             params.put("to", phoneNum);
            //*******************
            //*******************
            //실제 데모용
            //*******************
            //*******************


            try {
                JSONObject obj = (JSONObject) coolsms.send(params);
                System.out.println(obj.toString());

            } catch (CoolsmsException e) {
                System.out.println(e.getMessage());
                System.out.println(e.getCode());
            }
            s = "";
            phoneNum = "";
        }


    }
    int countConfirmedDays(Long mno){
        List<Object> result2 = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        HashMap<String, String> distinct=new HashMap<>();
        int total;

        result2 = statusRepository.getAllRegDate(mno);

        for (Object a : result2) {
            LocalDateTime localDateTime=(LocalDateTime)a;
            String s=localDateTime.format(formatter);
            distinct.put(s,s);
        }
        total=distinct.size();
        //탐색해야할 날짜가 10일보다 작으면 total를 반환하고 그것을 초과하면 10일 반환
        return total<=10?total:10;
    }

    private BooleanBuilder getSearch(PageRequestDTO requestDTO) {

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        //데이터 유효성 10일로 설정
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dataDue = LocalDateTime.now().minusDays(10L);


        System.out.println("now:" + now);
        System.out.println("minus one:" + dataDue);

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        QStatus qStatus = QStatus.status;
        String keyword = requestDTO.getKeyword();

        BooleanExpression expression = qStatus.statusnum.gt(0L);// gno > 0 조건만 생성
        booleanBuilder.and(expression);

        //검색 조건을 작성하기
        BooleanBuilder conditionBuilder = new BooleanBuilder();

        if (requestDTO.isEmpty()) { //검색 조건이 없는 경우
            //데이터 유효성 10일
            conditionBuilder.and(qStatus.regDate.between(dataDue, now));
            booleanBuilder.and(conditionBuilder);
            return booleanBuilder;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime from = null;
        LocalDateTime to = null;

        if (requestDTO.getFrom() != null && requestDTO.getFrom().length() != 0)
            from = LocalDateTime.parse(requestDTO.getFrom(), formatter);
        if (requestDTO.getTo() != null && requestDTO.getTo().length() != 0)
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
        if (from != null && to != null) {
            conditionBuilder.and(qStatus.regDate.between(from, to));
        }

        if (requestDTO.getKeyword() != null && requestDTO.getKeyword().length() != 0) {
            conditionBuilder.and(qStatus.facility.bno.eq(Long.valueOf(requestDTO.getKeyword())));
        }
        if (requestDTO.getMno() != null && requestDTO.getMno().length() != 0) {
//            List<Object> result = statusRepository.getMemberFacility(Long.valueOf(requestDTO.getMno()));
            BooleanBuilder buildingCondition = new BooleanBuilder();
//            for (Object a : result) {
//                //확진자가 다녀갔던 시설들을 리스트에 넣는다.
//                buildingCondition.or(qStatus.facility.bno.eq(((Facility) a).getBno()));
//                bnoList.add(((Facility) a).getBno());
//                System.out.println("mno bno!!!:" + ((Facility) a).getBno());
//            }
            conditionBuilder.and(buildingCondition);
            conditionBuilder.and(qStatus.member.mno.eq(Long.valueOf(requestDTO.getMno())));


            if (requestDTO.getCloseContact() != null && requestDTO.getCloseContact().length() != 0) { // TODO
//                List<Long> bnoList = new ArrayList<>();
//                if (requestDTO.getKeyword() != null && requestDTO.getKeyword().length() != 0) {
//                    bnoList.add(Long.parseLong(requestDTO.getKeyword()));
//                }
//                else {
//                    List<Object> result2 = null;
//                    if (from != null & to != null) {
//                        result2 = statusRepository.getMemberFacility(Long.valueOf(requestDTO.getMno()), from, to); // from to 주어짐
//                    }
//                    else {
//                        result2 = statusRepository.getMemberFacility(Long.valueOf(requestDTO.getMno()));
//                    }
//                    for (Object a : result2) {
//                        //확진자가 다녀갔던 시설들을 리스트에 넣는다.
//                        bnoList.add(((Facility) a).getBno());
//                        System.out.println("mno bno!!!:" + ((Facility) a).getBno());
//                    }
//                }

                System.out.println("test:" + requestDTO.getCloseContact());
                List<Object[]> result = null;
                if (requestDTO.getKeyword() != null && requestDTO.getKeyword().length() != 0) { // bno 주어짐
                    if (from != null & to != null) {
                        result = statusRepository.getRegDateAndState(Long.valueOf(requestDTO.getMno()), Long.valueOf(requestDTO.getKeyword()), from, to); // from to 주어짐
                    }
                    else {
                        result = statusRepository.getRegDateAndState(Long.valueOf(requestDTO.getMno()), Long.valueOf(requestDTO.getKeyword()));
                    }
                }
                else { // bno 안주어짐
                    if (from != null & to != null) {
                        result = statusRepository.getRegDateAndState(Long.valueOf(requestDTO.getMno()), from, to); // from to 주어짐
                    }
                    else {
                        result = statusRepository.getRegDateAndState(Long.valueOf(requestDTO.getMno()));
                    }
                }

                StringTokenizer st = new StringTokenizer(requestDTO.getCloseContact(), ":"); // ':'는 구분문자

                String min = st.nextToken(); // 각 토큰 출력
                String sec = st.nextToken();
                BooleanBuilder connectCondition = new BooleanBuilder();
                //입장과 퇴장을 각각 조건을 해줘야 함으로 for문의 조건 2로 함

                System.out.println(result);
                for (Object[] a : result) {
                    BooleanBuilder closeContactCondition1 = new BooleanBuilder();
                    LocalDateTime tempFrom;
                    LocalDateTime tempTo;
                    LocalDateTime regDate = (LocalDateTime) a[0];
                    boolean state = (Boolean) a[1];
                    Long bno = (Long) a[2];
                    tempFrom = regDate.minusMinutes(Long.valueOf(min)).minusSeconds(Long.valueOf(sec));
                    tempTo = regDate.plusMinutes(Long.valueOf(min)).plusSeconds(Long.valueOf(sec));

                    System.out.println("*******");
                    System.out.println("확진자  Original Time: " + ((LocalDateTime) a[0]).toString());
                    System.out.println("확진자  minus Time: " + tempFrom);
                    System.out.println("확진자  plus Time: " + tempTo);
                    System.out.println("확진자  State: " + state);
                    //여기서는 flag가 true니까 확진자와 같이 입장한 사람들 질의
                    closeContactCondition1.and(qStatus.facility.bno.eq(bno));
                    closeContactCondition1.and(qStatus.regDate.between(tempFrom, tempTo));
                    closeContactCondition1.and(qStatus.state.eq(state));
                    connectCondition.or(closeContactCondition1);


                }

                conditionBuilder.or(connectCondition);
            }

        }

        //모든 조건 통합
        booleanBuilder.and(conditionBuilder);
        System.out.println(booleanBuilder.toString());

        return booleanBuilder;
    }
}
