package org.morgorithm.frames.repository;

import org.junit.jupiter.api.Test;
import org.morgorithm.frames.entity.Facility;
import org.morgorithm.frames.entity.Member;
import org.morgorithm.frames.entity.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
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
    @Test
    public void insertStatusData(){
        List<Status> totalData=new ArrayList<>();
        int cnt=(int)(memberRepository.count());
        Boolean arr[]=new Boolean[1000];
        int barr[]=new int[1000];
        Arrays.fill(arr,false);
        Arrays.fill(barr,0);

        IntStream.rangeClosed(1,cnt*3).forEach(i->{
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

            //Get random amount of days between -3~0
            Random random = new Random();
            int randomAmountOfDays = random.nextInt(4);
            randomAmountOfDays*=-1;
            System.out.println("Random amount of days: " + randomAmountOfDays);

            //현재 시간 기준으로 3시간 전 후로 랜덤 시간 min, max가 있으면 nextInt(max+min+1)-min
            int randomAmountOfHours=random.nextInt(7)-3;
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
        List<Object> result=statusRepository.getRegDate(80L);
        for(Object a:result){
            System.out.println(a.toString());
            System.out.println("bno:"+((LocalDateTime) a).toString());
        }
    }

    @Test
    void testAddTestDataForRealTimeStatusUpdate() throws InterruptedException {
        int cnt=(int)(memberRepository.count());
        Long bno=0L;


        for(int i=0;i<100;i++){
            LocalDateTime now=LocalDateTime.now();
            Long mno=Long.valueOf((int)(Math.random()*cnt)+1);
            bno=((long)(Math.random()*10)+1);
            Member member=Member.builder().mno(mno).build();
            Facility facility= Facility.builder().bno(bno).building("building"+bno).build();
            Status status=Status.builder().member(member).facility(facility).state(true).temperature(36.6).build();
            status.setRegDate(now);
            statusRepository.save(status);
            Thread.sleep(3000);
        }

    }

}
