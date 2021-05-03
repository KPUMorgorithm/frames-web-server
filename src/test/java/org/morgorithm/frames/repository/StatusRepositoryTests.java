package org.morgorithm.frames.repository;

import org.junit.jupiter.api.Test;
import org.morgorithm.frames.entity.Facility;
import org.morgorithm.frames.entity.Member;
import org.morgorithm.frames.entity.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.IntStream;


@SpringBootTest
public class StatusRepositoryTests {
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void testGetFacilityInInfo(){
        List<Object[]> result=statusRepository.getFacilityInInfo();

        for(Object[] a:result){
            System.out.println(Arrays.toString(a));
        }
    }

    /*
    데이터베이스에 테스트 데이터를 넣는 것이다. 무작위 mno를 추출해서 무작위 건물을 순서대로 in 하고 out하는 것을
    구현한다. out를 먼저하고 in를 먼저 할 수는 없다.
     */
    //********************참고 사항*****
    //Status 테이블에 테스트 데이터 넣을 때는 BaseEntity 클래스에서 @CreatedDate 주석 처리해야함!!
    // 이것은 만들어지는 시간을 기록하기 때문이다 여기서는 내가 임의로 랜덤 시간을 배정한다.
    //dashboard의 실시간 출입현황 테스트용으로 사용할 수 없음 왜냐면 저건 현재 시간 기준으로 polling하기 때문
    @Test
    public void insertStatusData(){
        List<Status> totalData=new ArrayList<>();
        int cnt=(int)(memberRepository.count());
        Boolean arr[]=new Boolean[1000];
        int barr[]=new int[1000];
        Arrays.fill(arr,false);
        Arrays.fill(barr,0);

        //memeber 의 수 곱하기 5만큼 status를 만들어냄
        IntStream.rangeClosed(1,cnt*5).forEach(i->{
            List<Status> data=new ArrayList<>();
            Long mno=Long.valueOf((int)(Math.random()*cnt)+1);
            Member member=Member.builder().mno(mno).build();

            Long bno=0L;

            if(arr[mno.intValue()]==false){
                bno=((long)(Math.random()*10)+1);
                barr[mno.intValue()]=bno.intValue();
                arr[mno.intValue()]=true;
            }else{
                bno=Long.valueOf(barr[mno.intValue()]);
                arr[mno.intValue()]=false;
            }
            Facility facility= Facility.builder().bno(bno).building("building"+bno).build();

            //온도 데이터 생성
            double rangeMin=35;
            double rangeMax=37.3;
            Random r = new Random();

                double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble();


            //LocalDatetime Random****************************
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

            // Save current LocalDateTime into a variable
            LocalDateTime localDateTime = LocalDateTime.now();

            // Format LocalDateTime into a String variable and print
            String formattedLocalDateTime = localDateTime.format(dateTimeFormatter);
            System.out.println("Current Date: " + formattedLocalDateTime);

            //Get random amount of days between -9~0
            Random random = new Random();
            int randomAmountOfDays = random.nextInt(10);

            System.out.println("Random amount of days: " + randomAmountOfDays);

            //현재 시간 기준으로 3시간 전 후로 랜덤 시간 min, max가 있으면 nextInt(max+min+1)-min
            int randomAmountOfHours=random.nextInt(7)-3;
            System.out.println("Random amount of hours: " + randomAmountOfHours);

            //현재 시간 기준으로 60분 전후 min, max가 있으면 nextInt(max+min+1)-min
            int randomAmountOfMinute=random.nextInt(121)-60;
            System.out.println("Random amount of minutes: " + randomAmountOfMinute);


            // Add randomAmountOfDays to LocalDateTime variable we defined earlier and store it into a new variable
            LocalDateTime futureLocalDateTime = localDateTime.minusDays(randomAmountOfDays).plusHours(randomAmountOfHours).plusMinutes(randomAmountOfMinute);


            // Format new LocalDateTime variable into a String variable and print
            String formattedFutureLocalDateTime = String.format(futureLocalDateTime.format(dateTimeFormatter));
            System.out.println("Date " + randomAmountOfDays + " days in future: " + formattedFutureLocalDateTime);
            System.out.println();
            //******************************************************

            //모든 데이터 넣기
            //온도는 소수 자리수 1자리만 출력하게 하고 Double클래스를 이용해서 String to Double 해줌
            Status status=Status.builder().member(member).facility(facility).state(arr[mno.intValue()]).temperature(Double.valueOf(String.format("%.1f",+randomValue))).build();
            status.setRegDate(futureLocalDateTime);

            data.add(status);
            totalData.addAll(data);

        });

        for(Status s:totalData){

            System.out.println("mno:"+s.getMember());
            System.out.println("building:"+s.getFacility());
            System.out.println("status:"+s.getState());
            System.out.println("temperature"+s.getTemperature());
            statusRepository.save(s);
        }
    }

    @Test
    void dummyData(){
        Member member=Member.builder().mno(5L).build();
        Facility facility= Facility.builder().bno(2L).building("building"+2L).build();
        Status status=Status.builder().member(member).facility(facility).state(true).build();
        statusRepository.save(status);
    }

    @Test
    void testGetMemberFacility(){
        List<Object> result=statusRepository.getMemberFacility(80L);
        for(Object a:result){
            System.out.println(a.toString());
            System.out.println("bno:"+((Facility) a).getBno());
        }

    }

    @Test
    void testGetRegtDate(){
        List<Object[]> result=statusRepository.getRegDateAndState(80L);
        for(Object a:result){
            System.out.println(a.toString());
            System.out.println("regDate:"+((LocalDateTime) a).toString());
        }
    }

    //dashboard의 실시간 출입현황 테스트용이다
    //
    @Test
    void testAddTestDataForRealTimeStatusUpdate() throws InterruptedException {
        int cnt=(int)(memberRepository.count());
        Long bno=0L;

        for(int i=0;i<100;i++){
            double rangeMin=34.5;
            double rangeMax=37.6;
            Random r = new Random();
            Boolean stat;
            int s=(int)Math.round( Math.random() );
            if(s==1)
                stat=true;
            else
                stat=false;
            double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
            LocalDateTime now=LocalDateTime.now();
            Long mno=Long.valueOf((int)(Math.random()*cnt)+1);
            bno=((long)(Math.random()*10)+1);
            Member member=Member.builder().mno(mno).build();
            Facility facility= Facility.builder().bno(bno).building("building"+bno).build();
            Status status=Status.builder().member(member).facility(facility).state(stat).temperature(Double.valueOf(String.format("%.1f",+randomValue))).build();
            status.setRegDate(now);
            statusRepository.save(status);
            Thread.sleep(3000);
            System.out.println("데이터 삽입");
        }

    }
    //dashboard의 실시간 출입현황 테스트용으로 사용할 수 없음 왜냐면 저건 현재 시간 기준으로 polling하기 때문
    @Test
    void testEventNowBooleanBuilderInsertDummyData(){
        List<Status> totalData=new ArrayList<>();
        int cnt=(int)(memberRepository.count());
        Boolean arr[]=new Boolean[1000];
        int barr[]=new int[1000];
        Arrays.fill(arr,false);
        Arrays.fill(barr,0);

        IntStream.rangeClosed(1,cnt*4).forEach(i->{
            List<Status> data=new ArrayList<>();
            Long mno=Long.valueOf((int)(Math.random()*cnt)+1);
            Member member=Member.builder().mno(mno).build();

            Long bno=0L;

            if(arr[mno.intValue()]==false){
                bno=((long)(Math.random()*10)+1);
                barr[mno.intValue()]=bno.intValue();
                arr[mno.intValue()]=true;
            }else{
                bno=Long.valueOf(barr[mno.intValue()]);
                arr[mno.intValue()]=false;
            }
            Facility facility= Facility.builder().bno(bno).building("building"+bno).build();

            //온도 데이터 생성
            double rangeMin=35;
            double rangeMax=37.3;
            Random r = new Random();

            double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble();


            //LocalDatetime Random****************************
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

            // Save current LocalDateTime into a variable
            LocalDateTime localDateTime = LocalDateTime.now();

            // Format LocalDateTime into a String variable and print
            String formattedLocalDateTime = localDateTime.format(dateTimeFormatter);
            System.out.println("Current Date: " + formattedLocalDateTime);

            //Get random amount of days 오늘 당일
            Random random = new Random();
            int randomAmountOfDays = random.nextInt(1);
            randomAmountOfDays*=-1;
            System.out.println("Random amount of days: " + randomAmountOfDays);

            //현재 시간 기준으로 7시간 전 후로 랜덤 시간 min, max가 있으면 nextInt(max+min+1)-min
            int randomAmountOfHours=random.nextInt(7)-7;
            System.out.println("Random amount of hours: " + randomAmountOfHours);

            //현재 시간 기준으로 60분 전후 min, max가 있으면 nextInt(max+min+1)-min
            int randomAmountOfMinute=random.nextInt(121)-60;
            System.out.println("Random amount of minutes: " + randomAmountOfMinute);


            // Add randomAmountOfDays to LocalDateTime variable we defined earlier and store it into a new variable
            LocalDateTime futureLocalDateTime = localDateTime.plusDays(randomAmountOfDays).plusHours(randomAmountOfHours).plusMinutes(randomAmountOfMinute);


            // Format new LocalDateTime variable into a String variable and print
            String formattedFutureLocalDateTime = String.format(futureLocalDateTime.format(dateTimeFormatter));
            System.out.println("Date " + randomAmountOfDays + " days in future: " + formattedFutureLocalDateTime);
            System.out.println();
            //******************************************************

            //모든 데이터 넣기
            //온도는 소수 자리수 1자리만 출력하게 하고 Double클래스를 이용해서 String to Double 해줌
            Status status=Status.builder().member(member).facility(facility).state(arr[mno.intValue()]).temperature(Double.valueOf(String.format("%.1f",+randomValue))).build();
            status.setRegDate(futureLocalDateTime);

            data.add(status);
            totalData.addAll(data);

        });

        for(Status s:totalData){

            System.out.println("mno:"+s.getMember());
            System.out.println("building:"+s.getFacility());
            System.out.println("status:"+s.getState());
            System.out.println("temperature"+s.getTemperature());
            statusRepository.save(s);

        }
    }
    @Test
    void getFacilityInInfoOneDay(){
        //오늘 날짜
        LocalDateTime startDatetime = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.of(0,0,0)); //어제 00:00:00
        LocalDateTime endDatetime = LocalDateTime.of(LocalDate.now(), LocalTime.of(23,59,59));
        System.out.println("######from: "+startDatetime);
        System.out.println("######to: "+endDatetime);
        List<Object[]> result=statusRepository.getFacilityInInfoOneDay(startDatetime,endDatetime);
        for(Object a[]:result){
            System.out.println(a.toString());
            System.out.println(Arrays.toString(a));
        }
    }

    //데이터 유효기간 2주 검사 더미 데이터 삽입
    @Test
    void makeDummyDataFromTweoWeeksBefore(){
        int cnt=(int)(memberRepository.count());
        Long bno=0L;

        for(int i=0;i<100;i++){
            double rangeMin=34.5;
            double rangeMax=37.6;
            Random r = new Random();
            Boolean stat;
            int s=(int)Math.round( Math.random() );
            if(s==1)
                stat=true;
            else
                stat=false;
            double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
            LocalDateTime now=LocalDateTime.now().minusDays(20L);
            Long mno=Long.valueOf((int)(Math.random()*cnt)+1);
            bno=((long)(Math.random()*10)+1);
            Member member=Member.builder().mno(mno).build();
            Facility facility= Facility.builder().bno(bno).building("building"+bno).build();
            Status status=Status.builder().member(member).facility(facility).state(stat).temperature(Double.valueOf(String.format("%.1f",+randomValue))).build();
            status.setRegDate(now);
            statusRepository.save(status);

        }
    }
    @Test
    void getLatestDateTest(){
        List<Object> result=statusRepository.getLatestDate();
        LocalDateTime localDateTime=(LocalDateTime)result.get(0);
        for(Object r:result){
            System.out.println("Date:"+r.toString());
            System.out.println("LocalDateTime:"+localDateTime);
        }
    }
    //LocalDateTime의 시간에서 1 밀리세컨드 빼주는 테스트
    @Test
    void testLocalDateTime(){
        List<Object> result = statusRepository.getLatestDate();
        LocalDateTime latestDateTime;
        latestDateTime = (LocalDateTime) result.get(0);
        String temp=latestDateTime.toString();

        String modifyTime="";
        int minusMillisecond;
        //s의 마지막 인덱스 character를 제외한 string 받아오기
        modifyTime=temp.substring(0,temp.length()-1);

        //minusMillisecond s의 마지막 인덱스에 해당하는character를 받아와서 int로 변환
        minusMillisecond=Integer.parseInt(String.valueOf(temp.charAt(temp.length()-1)));

        //1 깍아줌
        minusMillisecond--;

        modifyTime+=Integer.toString(minusMillisecond);

        latestDateTime=LocalDateTime.parse(modifyTime);
        System.out.println("test localdatetime version:"+latestDateTime.toString());

    }
    @Test
    void testGetMaxStatusNum(){
        Status result=statusRepository.findTopByOrderByStatusnumDesc();
        Long num=result.getStatusnum();
        System.out.println("result:"+result.toString());

    }

    @Test
    void testGetMemberDailyTemperatureStatus(){
        List<Object[]> result=statusRepository.getMemberDailyTemperatureStatus(1L);
        HashMap<String,Double> temperature=new HashMap<String, Double>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM-dd");


        for(Object[] a:result){
            System.out.println(Arrays.toString(a));
            System.out.println("temperature"+a[1]);
            System.out.println("regDate:"+a[0]);
            LocalDateTime temp=(LocalDateTime)a[0];
            System.out.println();
            temperature.put(temp.format(formatter),(double)a[1]);
        }
        temperature.forEach((key,value)
            ->System.out.println("key: "+key+", value: "+value));



    }
}
