package org.morgorithm.frames.repository;

import org.junit.jupiter.api.Test;
import org.morgorithm.frames.entity.Facility;
import org.morgorithm.frames.entity.Member;
import org.morgorithm.frames.entity.Sms;
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
public class SmsRepositoryTests {
    @Autowired
    private SmsRepository smsRepository;

    @Test
    void testGetAllList(){
        List<Object[]> result=smsRepository.getAllList();
        for (Object[] a : result) {
            System.out.println(Arrays.toString(a));
        }

    }
    @Test
    void t(){
        System.out.println("sdf");
    }
    @Test
    void testInsertData(){
        List<Sms> totalData=new ArrayList<>();
        int cnt=100;
        Boolean arr[]=new Boolean[1000];
        int barr[]=new int[1000];
        Arrays.fill(arr,false);
        Arrays.fill(barr,0);

        //memeber 의 수 곱하기 5만큼 status를 만들어냄
        IntStream.rangeClosed(1,cnt*2).forEach(i->{

            Long bno=0L;


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
            int count = (int)(Math. random()*9000)+1000;

            Sms s=Sms.builder().content("hello"+i).status(true).receivedDate(formattedFutureLocalDateTime).sender("010"+"1111"+String.valueOf(count)).build();

            totalData.add(s);
        });

        for(Sms s:totalData){

            System.out.println("Sender:"+s.getSender());
            System.out.println("Content:"+s.getContent());
            System.out.println("ReceivedDate:"+s.getReceivedDate());
            System.out.println("Status"+s.getStatus());
            smsRepository.save(s);
        }
    }
}
