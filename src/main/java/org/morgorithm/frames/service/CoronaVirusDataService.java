package org.morgorithm.frames.service;

import lombok.Data;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@RequiredArgsConstructor
@Service
public class CoronaVirusDataService {

    private List<Integer> decideList;//날짜별 확진자 수
    private List<Integer> deathList;//날짜별 사망자 수
    private List<Integer> clearList;//날짜별 격리해제 수
    private List<String> dateList;//날짜별 문자열 배열
    private List<Integer> examList;
    private int totalConfirmed; //오늘까지 누적 총 확진자
    private int totalClear; //오늘까지 누적 총 격리해제 수

    public String getStartCreateDt(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }
    public  String getEndCreateDt(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime then = now.minusDays(11);
        return dtf.format(then);
    }
    public  void initialDateList(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd");
        LocalDateTime now = LocalDateTime.now();
        for(int i=9;i>=0;i--) {
            LocalDateTime date = now.minusDays(i);
            dateList.add(dtf.format(date));
        }
    }
    private String getTagValue(String tag, Element ele) {

        NodeList nodeList = ele.getElementsByTagName(tag).item(0).getChildNodes();


        Node nValue = (Node) nodeList.item(0);


        if(nValue == null) {

            return null;

        }

        return nValue.getNodeValue();

    }// getTagValue


    public void xmlApi(String url) {
        decideList=new ArrayList<>();
        deathList=new ArrayList<>();
        clearList=new ArrayList<>();
        dateList=new ArrayList<>();
        examList=new ArrayList<>();

        try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder  = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(url);



            //root tag

            doc.getDocumentElement().normalize();

          //  System.out.println("Root Element : "+doc.getDocumentElement().getNodeName());

            // parsing tag

            NodeList nodeList = doc.getElementsByTagName("item");

          //  System.out.println("파싱할 리스트 수 : "+ nodeList.getLength());
            boolean flag=true;
            for(int temp =0; temp<nodeList.getLength()-2; temp++) {

                Node nNode = nodeList.item(temp);

                Node nNodeTest=nodeList.item(temp+1);

                if(nNode.getNodeType()==Node.ELEMENT_NODE) {

                    Element element = (Element)nNode;

                    Element elementTest=(Element)nNodeTest;


                    //  System.out.println("examCnt: "+getTagValue("examCnt", element));
                    decideList.add(Integer.parseInt(getTagValue("decideCnt",element))-Integer.parseInt(getTagValue("decideCnt",elementTest)));
                    deathList.add(Integer.parseInt(getTagValue("deathCnt",element))-Integer.parseInt(getTagValue("deathCnt",elementTest)));
                    clearList.add(Integer.parseInt(getTagValue("clearCnt",element))-Integer.parseInt(getTagValue("clearCnt",elementTest)));
                    examList.add(Integer.parseInt(getTagValue("examCnt",element))-Integer.parseInt(getTagValue("examCnt",elementTest)));
                    if(flag){
                        totalConfirmed=Integer.parseInt(getTagValue("decideCnt",element));
                        totalClear=Integer.parseInt(getTagValue("clearCnt",element));
                        flag=false;
                    }
                }//if

            }//for


            initialDateList();
          //  getEachDayData();
        }catch(Exception e){
            System.out.println(e);
        }

        Collections.reverse(decideList);
        Collections.reverse(deathList);
        Collections.reverse(clearList);
        Collections.reverse(examList);

      /*  for(int j=0;j<10;j++){
            System.out.println("날짜:"+dateList.get(j)+" "+"확진자:"+decideList.get(j)+" "+"사망자:"+deathList.get(j)+" "+"격리해제:"+clearList.get(j));
        }
        for(int t:decideList){
            System.out.println("test:"+t);
        }*/

    }
    @PostConstruct
    @Scheduled(cron="* * 1 * * *")
    public void initialData() {
        try{
            StringBuilder urlBuilder = new StringBuilder("http://openapi.data.go.kr/openapi/service/rest/Covid19/getCovid19InfStateJson"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=P1wWeheZvhyl1CtYk8BFcVLbUl2ZJdoRsu1COpnHeAFmIaLOdBHSmEh1EDLZIWkBKa56UgtobT6nfpzu57d6AA%3D%3D"); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("ServiceKey","UTF-8") + "=" + URLEncoder.encode("P1wWeheZvhyl1CtYk8BFcVLbUl2ZJdoRsu1COpnHeAFmIaLOdBHSmEh1EDLZIWkBKa56UgtobT6nfpzu57d6AA%3D%3D", "UTF-8")); /*공공데이터포털에서 받은 인증키*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수*/
            urlBuilder.append("&" + URLEncoder.encode("startCreateDt","UTF-8") + "=" + URLEncoder.encode(getEndCreateDt(), "UTF-8")); /*검색할 생성일 범위의 시작*/
            urlBuilder.append("&" + URLEncoder.encode("endCreateDt","UTF-8") + "=" + URLEncoder.encode(getStartCreateDt(), "UTF-8")); /*검색할 생성일 범위의 종료*/
            URL url = new URL(urlBuilder.toString());
            xmlApi(url.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
          //  System.out.println("Response code: " + conn.getResponseCode());
            BufferedReader rd;
            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();
           // System.out.println(sb.toString());
        }catch(Exception e){
            System.out.println(e);
        }


    }



}