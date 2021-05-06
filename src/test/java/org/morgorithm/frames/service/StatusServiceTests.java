package org.morgorithm.frames.service;


import com.querydsl.core.BooleanBuilder;
import net.nurigo.java_sdk.Coolsms;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;
//import org.morgorithm.frames.dto.PageRequestDTO;
import org.morgorithm.frames.dto.PageResultDTO;
import org.morgorithm.frames.dto.RealTimeStatusDTO;
import org.morgorithm.frames.dto.StatusDTO;
import org.morgorithm.frames.entity.QStatus;
import org.morgorithm.frames.entity.Status;
import org.morgorithm.frames.repository.FacilityRepository;
import org.morgorithm.frames.repository.MemberRepository;
import org.morgorithm.frames.repository.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@SpringBootTest
public class StatusServiceTests {

    @Autowired
    private StatusService service;
    @Autowired
    private StatusRepository statusRepository;


    @Test
    void testGetFacilityStatus() {
        RealTimeStatusDTO realTimeStatusDTO = service.getFacilityStatus();
        for (int x : realTimeStatusDTO.getIn()) {
            System.out.print(x + " ");
        }
        System.out.println();
        for (int x : realTimeStatusDTO.getOut()) {
            System.out.print(x + " ");
        }
        System.out.println();
        for (String str : realTimeStatusDTO.getBName()) {
            System.out.print(str + " ");
        }
    }

  /*  @Test
    public void testSearch(){
        PageRequestDTO pageRequestDTO=PageRequestDTO.builder()
                .page(1)
                .size(10)
                .type("d")
                .from("2021-01-02 00:00:00")
                .to("2021-06-04 12:22:10")
                //.keyword("3")
                .build();

        PageResultDTO<StatusDTO, Status> resultDTO=service.getStatusList(pageRequestDTO);

        System.out.println("PREV: "+resultDTO.isPrev());
        System.out.println("NEXT: "+resultDTO.isNext());
        //Total이 전체 페이지 수
        System.out.println("TOTAL: "+resultDTO.getTotalPage());
        int total=resultDTO.getTotalPage();
        for(int i=0;i<total;i++){
            System.out.println("-----------------------------------");
            pageRequestDTO.setPage(i+1);
            resultDTO= service.getStatusList(pageRequestDTO);
            for(StatusDTO statusDTO:resultDTO.getDtoList()){
                System.out.println(statusDTO);
            }
            System.out.println("-----------------------------------");
            System.out.println("");
        }


    }*/

    //문자 보낼때 주의사항 일단 확진자에게 문자가 가면 안 된다.
    //특정 밀접 접촉자에게 메시지를 보낼때 밀접 접촉 횟수만큼 보내지 않게 조심하기
    //특정 밀접 접촉자가 여러 번 겹쳤을 경우 그것을 리스트화해서 한 번에 보낸다.
    @Test
    public void testSend() {
        String api_key = "NCSJBLIL70QSO6P9";
        //사이트에서 발급 받은 API KEY
        String api_secret = "4BDRU03ULDUDLOY9BZ7AZIOLXAALBSUW";
        // 사이트에서 발급 받은 API SECRET KEY
        Message coolsms = new Message(api_key, api_secret);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("to", "01036461294");
        params.put("from", "01096588541");
        //사전에 사이트에서 번호를 인증하고 등록하여야 함
        params.put("type", "SMS");
        params.put("text", "");
        //메시지 내용
        params.put("app_version", "test app 1.2");
        try {
            JSONObject obj = (JSONObject) coolsms.send(params);
            System.out.println(obj.toString());
            //전송 결과 출력
        } catch (CoolsmsException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCode());
        }
    }

    @Test
    void testMethodPrint() {
        service.getEventInfo();
    }

    @Test
    void testGetMapInfo() {

        String date = "05/29/2021 00:00:00";


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");

        LocalDateTime dateTime = LocalDateTime.parse(date, formatter);

        System.out.println("date:" + dateTime);

    }

    @Test
    void testGetList() {

        Long latestStatusNum = null;
        class Pair implements Comparable<Pair> {

            private LocalDateTime ldt;
            private Double temperature;

            public Pair(LocalDateTime x, double y) {
                this.ldt = x;
                this.temperature = y;
            }

            public LocalDateTime getX() {
                return ldt;
            }

            public double getY() {
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

        List<Object[]> result = statusRepository.getMemberDailyTemperatureStatus(33L);
        HashMap<String, Double> dailyStatus = new HashMap<String, Double>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM-dd");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy MM-dd HH:mm:ss");
        String hhMMSS = " 00:00:00";
        List<Pair> pairList = new ArrayList<>();
        LocalDateTime stringToLdt;
        for (Object[] a : result) {

            if (a[0] != null) {
                String temp = ((LocalDateTime) a[0]).format(formatter);
                temp += hhMMSS;
                stringToLdt = LocalDateTime.parse(temp, formatter2);
                Pair pair = new Pair(stringToLdt, (double) a[1]);
                pairList.add(pair);
                // dailyStatus.put(temp.format(formatter), (double) a[1]);
            }
        }
        Collections.sort(pairList);
        for (Pair p : pairList) {
            System.out.println("date:" + p.getX() + ", temperature:" + p.getY());
        }
        //    dailyStatus.forEach((key, value)
        //            -> System.out.println("key: " + key + ", value: " + value));

    }
    @Test
    void doubleSortTest(){
        List<Object[]> result = statusRepository.getMemberDailyTemperatureStatus(33L);
        List<Double> list=new ArrayList<>();
        for (Object[] a : result) {

            if (a[0] != null) {
                list.add((double)a[1]);
                // dailyStatus.put(temp.format(formatter), (double) a[1]);
            }
        }
        Collections.sort(list);
        for(Double e:list){
            System.out.println(e+" ");
        }
    }

}
