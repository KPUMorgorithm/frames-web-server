package org.morgorithm.frames.service;


import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;
import org.morgorithm.frames.dto.PageRequestDTO;
import org.morgorithm.frames.dto.PageResultDTO;
import org.morgorithm.frames.dto.RealTimeStatusDTO;
import org.morgorithm.frames.dto.StatusDTO;
import org.morgorithm.frames.entity.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

@SpringBootTest
public class StatusServiceTests {

    @Autowired
    private StatusService service;



    @Test
    void testGetFacilityStatus(){
       RealTimeStatusDTO realTimeStatusDTO =service.getFacilityStatus();
        for (int x : realTimeStatusDTO.getIn()) {
            System.out.print(x + " ");
        }
        System.out.println();
        for (int x : realTimeStatusDTO.getOut()) {
            System.out.print(x + " ");
        }
        System.out.println();
        for(String str: realTimeStatusDTO.getBName()){
            System.out.print(str+" ");
        }
    }

    @Test
    public void testSearch(){
        PageRequestDTO pageRequestDTO=PageRequestDTO.builder()
                .page(1)
                .size(10)
                .type("d")
                .from("2021-03-12 05:22:10")
                .to("2021-03-14 12:22:10")
                //.keyword("3")
                .build();

        PageResultDTO<StatusDTO, Status> resultDTO=service.getStatusList(pageRequestDTO);

        System.out.println("PREV: "+resultDTO.isPrev());
        System.out.println("NEXT: "+resultDTO.isNext());
        System.out.println("TOTAL: "+resultDTO.getTotalPage());

        System.out.println("-----------------------------------");
        for(StatusDTO statusDTO:resultDTO.getDtoList()){
            System.out.println(statusDTO);
        }
        System.out.println("===================================");

        pageRequestDTO.setPage(2);
        resultDTO= service.getStatusList(pageRequestDTO);
        System.out.println("-----------------------------------");
        for(StatusDTO statusDTO:resultDTO.getDtoList()){
            System.out.println(statusDTO);
        }
        System.out.println("===================================");

        pageRequestDTO.setPage(10);
        resultDTO= service.getStatusList(pageRequestDTO);
        System.out.println("-----------------------------------");
        for(StatusDTO statusDTO:resultDTO.getDtoList()){
            System.out.println(statusDTO);
        }
        System.out.println("===================================");

        pageRequestDTO.setPage(15);
        resultDTO= service.getStatusList(pageRequestDTO);
        resultDTO.setPage(15);
        System.out.println("-----------------------------------");
        for(StatusDTO statusDTO:resultDTO.getDtoList()){
            System.out.println(statusDTO);
        }
        System.out.println("===================================");


        pageRequestDTO.setPage(17);
        resultDTO= service.getStatusList(pageRequestDTO);
        resultDTO.setPage(17);
        System.out.println("-----------------------------------");
        for(StatusDTO statusDTO:resultDTO.getDtoList()){
            System.out.println(statusDTO);
        }
        System.out.println("===================================");
    }

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
        params.put("to", "01036461294"); params.put("from", "01096588541");
        //사전에 사이트에서 번호를 인증하고 등록하여야 함
        params.put("type", "SMS");
        params.put("text", "");
        //메시지 내용
        params.put("app_version", "test app 1.2");
        try {
            JSONObject obj = (JSONObject) coolsms.send(params);
            System.out.println(obj.toString());
            //전송 결과 출력
        }catch (CoolsmsException e){
            System.out.println(e.getMessage());
            System.out.println(e.getCode());
        }
    }



}
