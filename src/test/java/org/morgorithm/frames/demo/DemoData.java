package org.morgorithm.frames.demo;

import org.junit.jupiter.api.Test;
import org.morgorithm.frames.entity.Facility;
import org.morgorithm.frames.entity.Member;
import org.morgorithm.frames.entity.MemberImage;
import org.morgorithm.frames.entity.Status;
import org.morgorithm.frames.repository.MemberImageRepository;
import org.morgorithm.frames.repository.MemberRepository;
import org.morgorithm.frames.repository.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.IntStream;

@SpringBootTest
public class DemoData {
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberImageRepository memberImageRepository;


    /*
   데이터베이스에 테스트 데이터를 넣는 것이다. 무작위 mno를 추출해서 무작위 건물을 순서대로 in 하고 out하는 것을
   구현한다. out를 먼저하고 in를 먼저 할 수는 없다.
    */
    //********************참고 사항*****
    //Status 테이블에 테스트 데이터 넣을 때는 BaseEntity 클래스에서 @CreatedDate 주석 처리해야함!!
    // 이것은 만들어지는 시간을 기록하기 때문이다 여기서는 내가 임의로 랜덤 시간을 배정한다.
    //dashboard의 실시간 출입현황 테스트용으로 사용할 수 없음 왜냐면 저건 현재 시간 기준으로 polling하기 때문
    //30개의 더미 멤버들에 대해선 3일 동안의 데이터를 넣지만 3개의 유의미한 멤버에 대해서는 7일 동안의 데이터를 넣는다
    //이유는 멤버 상세정보에 들어갔을 때 온도 변화 그래프를 좀 더 데이터를 넣어주고 데모 때 보여주기 위함이다.
    @Test
    public void insertStatusData() {
        List<Status> totalData = new ArrayList<>();
        int cnt = (int) (memberRepository.count());
        Boolean arr[] = new Boolean[1000];
        int barr[] = new int[1000];
        Arrays.fill(arr, false);
        Arrays.fill(barr, 0);

        //각 member에 대해서 현재 날짜 기준으로 3일간 4~6건물을 랜덤 온도로 돌아다니게끔 넣는다.
        IntStream.rangeClosed(1, cnt-3).forEach(i -> {
            List<Status> data = new ArrayList<>();
            Long mno = Long.valueOf(i);
            Member member = Member.builder().mno(mno).build();

            Long bno = 0L;
            //한 멤버가 4~6개의 건물을 왔다갔다 한다.
            int min = 4;
            int max = 6;
            int randomBuilding = (int) Math.floor(Math.random() * (max - min + 1) + min);
            //현재로부터 각 멤버에 대해서 3일간의 더미 데이터를 넣는다.
            for (int k = 0; k <= 2; k++) {

                for (int j = 0; j < randomBuilding; j++) {
                    //LocalDatetime Random****************************
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

                    // Save current LocalDateTime into a variable
                    LocalDateTime localDateTime = LocalDateTime.now();

                    // Format LocalDateTime into a String variable and print
                    String formattedLocalDateTime = localDateTime.format(dateTimeFormatter);
                  //  System.out.println("Current Date: " + formattedLocalDateTime);

                    //Get random amount of days between -7~0

                    Random random = new Random();
                    int randomAmountOfDays = k;

                 //   System.out.println("Random amount of days: " + randomAmountOfDays);

                    //현재 시간 기준으로 3시간 전 후로 랜덤 시간 min, max가 있으면 nextInt(max+min+1)-min
                    int randomAmountOfHours = random.nextInt(7) - 3;
                  //  System.out.println("Random amount of hours: " + randomAmountOfHours);

                    //현재 시간 기준으로 60분 전후 min, max가 있으면 nextInt(max+min+1)-min
                    int randomAmountOfMinute = random.nextInt(121) - 60;
                 //   System.out.println("Random amount of minutes: " + randomAmountOfMinute);


                    // Add randomAmountOfDays to LocalDateTime variable we defined earlier and store it into a new variable
                    LocalDateTime futureLocalDateTime = localDateTime.minusDays(randomAmountOfDays).plusHours(randomAmountOfHours).plusMinutes(randomAmountOfMinute);


                    // Format new LocalDateTime variable into a String variable and print
                    String formattedFutureLocalDateTime = String.format(futureLocalDateTime.format(dateTimeFormatter));
                 //   System.out.println("Date " + randomAmountOfDays + " days in future: " + formattedFutureLocalDateTime);
                 //   System.out.println();
                    //******************************************************
                    if (arr[mno.intValue()] == false) {
                        bno = ((long) (Math.random() * 10) + 1);
                        barr[mno.intValue()] = bno.intValue();
                        arr[mno.intValue()] = true;
                    } else {
                        bno = Long.valueOf(barr[mno.intValue()]);
                        arr[mno.intValue()] = false;
                    }
                    Facility facility = Facility.builder().bno(bno).building("building" + bno).build();

                    //온도 데이터 생성
                    double rangeMin = 35;
                    double rangeMax = 37.3;
                    Random r = new Random();

                    double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble();


                    //모든 데이터 넣기
                    //온도는 소수 자리수 1자리만 출력하게 하고 Double클래스를 이용해서 String to Double 해줌
                    Status status = Status.builder().member(member).facility(facility).state(arr[mno.intValue()]).temperature(Double.valueOf(String.format("%.1f", +randomValue))).build();
                    status.setRegDate(futureLocalDateTime);

                    data.add(status);
                    totalData.addAll(data);
                }

            }

        });



        for (Status s : totalData) {
/*
            System.out.println("mno:" + s.getMember());
            System.out.println("building:" + s.getFacility());
            System.out.println("status:" + s.getState());
            System.out.println("temperature" + s.getTemperature());*/
            statusRepository.save(s);
        }
    }

    //30명의 더미 멤버와 조성욱, 유영균, 송인걸 3명멤버 등록 (총 33명 등록)
    @Commit
    @Transactional
    @Test
    public void insertMembers() {
        IntStream.rangeClosed(1, 30).forEach(i -> {
            int count = (int) (Math.random() * 9000) + 1000;
            Member member = Member.builder().name("User" + i)
                    .phone("010-" + "1111-" + count).build();
            memberRepository.save(member);
            int count2 = (int) (Math.random() * 5) + 1;//1,2,3,4

            for (int j = 0; j < count2; j++) {
                MemberImage memberImage = MemberImage.builder()
                        .uuid(UUID.randomUUID().toString())
                        .member(member)
                        .imgName("test" + j + ".jpg").build();
                memberImageRepository.save(memberImage);
            }

        });
        Member memberJo = Member.builder().name("조성욱").phone("010-4134-0626").build();
        Member memberYou = Member.builder().name("유영균").phone("010-3058-8541").build();
        Member memberSong = Member.builder().name("송인걸").phone("010-3831-7657").build();
        memberRepository.save(memberJo);
        memberRepository.save(memberYou);
        memberRepository.save(memberSong);

        MemberImage memberImageJo = MemberImage.builder()
                .uuid(UUID.randomUUID().toString())
                .member(memberJo)
                .imgName("test" + 31 + ".jpg").build();
        memberImageRepository.save(memberImageJo);
        MemberImage memberImageYou = MemberImage.builder()
                .uuid(UUID.randomUUID().toString())
                .member(memberJo)
                .imgName("test" + 31 + ".jpg").build();
        memberImageRepository.save(memberImageYou);

        MemberImage memberImageSong = MemberImage.builder()
                .uuid(UUID.randomUUID().toString())
                .member(memberJo)
                .imgName("test" + 31 + ".jpg").build();
        memberImageRepository.save(memberImageSong);
    }
    @Test
    void contactTestData(){
        Long bno = 0L;
        List<Long> bnoList=new ArrayList<>();
        int songMno=33;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime insertDate;
        Boolean arr[] = new Boolean[1000];
        int barr[] = new int[1000];
        Member member=Member.builder().mno(33L).build();
        Arrays.fill(arr, false);
        Arrays.fill(barr, 0);
        //온도 데이터 생성
        double rangeMin = 35;
        double rangeMax = 37.3;
        bnoList.add(4L);
        bnoList.add(4L);
        bnoList.add(7L);
        bnoList.add(7L);
        bnoList.add(6L);
        bnoList.add(6L);
        bnoList.add(10L);


        //송인걸 데이터 삽입
        //오늘 기준 경로는 D동->P동->G동->제2기숙사이며
        //온도는 랜덤으로 한다.
        //날짜는 오늘 (데이터를 삽입하는 날)

        for(int i=0;i<7;i++){
            insertDate=now.minusHours(1).minusMinutes(30+i);
            Random r = new Random();
            double temperature = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
            if (arr[songMno] == false) {
                bno = bnoList.get(i);
                barr[songMno] = bnoList.get(i).intValue();
                arr[songMno] = true;
            } else {
                bno = Long.valueOf(barr[songMno]);
                arr[songMno] = false;
            }
            Facility facility = Facility.builder().bno(bno).building("building" + bno).build();
            Status status = Status.builder().member(member).facility(facility).state(arr[songMno]).temperature(Double.valueOf(String.format("%.1f", +temperature))).build();

            status.setRegDate(insertDate);
            statusRepository.save(status);

        }

        //송인걸 데이터 삽입
        //오늘 기준 경로는 산융->D동->A동->TIP
        //온도는 랜덤으로 한다.
        //날짜는 오늘 (데이터를 삽입하는 날 전날)

        bnoList.removeAll(bnoList);
        bnoList.add(8L);
        bnoList.add(8L);
        bnoList.add(4L);
        bnoList.add(4L);
        bnoList.add(1L);
        bnoList.add(1L);
        bnoList.add(9L);
        bnoList.add(9L);
        bnoList.add(10L);
        Arrays.fill(arr, false);
        Arrays.fill(barr, 0);


        for(int i=0;i<9;i++){
            insertDate=now.minusHours(1).minusMinutes(30-i).minusDays(1);
            Random r = new Random();
            double temperature = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
            if (arr[songMno] == false) {
                bno = bnoList.get(i);
                barr[songMno] = bnoList.get(i).intValue();
                arr[songMno] = true;
            } else {
                bno = Long.valueOf(barr[songMno]);
                arr[songMno] = false;
            }
            Facility facility = Facility.builder().bno(bno).building("building" + bno).build();
            Status status = Status.builder().member(member).facility(facility).state(arr[songMno]).temperature(Double.valueOf(String.format("%.1f", +temperature))).build();
            status.setRegDate(insertDate);
            statusRepository.save(status);

        }



        //조성욱 데이터 삽입
        //접촉 경로는 데이터 삽입 기준날 P동
        //온도는 랜덤으로 한다.
        //날짜는 오늘 (데이터를 삽입하는 날 전날)
        //이건 오늘 P동에서 겹치게 한다.
        Member member2=Member.builder().mno(31L).build();
        bnoList.removeAll(bnoList);
        bnoList.add(7L);//P동
        bnoList.add(7L);
        bnoList.add(9L);//TIP
        bnoList.add(9L);
        bnoList.add(1L);//A동
        bnoList.add(1L);
        bnoList.add(5L);//E동
        bnoList.add(5L);
        Arrays.fill(arr, false);
        Arrays.fill(barr, 0);


        int joMno=31;
        for(int i=0;i<8;i++){
            insertDate=now.minusHours(1).minusMinutes(32-i);
            Random r = new Random();
            double temperature = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
            if (arr[joMno] == false) {
                bno = bnoList.get(i);
                barr[joMno] = bnoList.get(i).intValue();
                arr[joMno] = true;
            } else {
                bno = Long.valueOf(barr[joMno]);
                arr[joMno] = false;
                insertDate=insertDate.plusHours(1);
            }
            Facility facility = Facility.builder().bno(bno).building("building" + bno).build();
            Status status = Status.builder().member(member2).facility(facility).state(arr[joMno]).temperature(Double.valueOf(String.format("%.1f", +temperature))).build();
            status.setRegDate(insertDate);
            statusRepository.save(status);

        }


        //조성욱 데이터 삽입
        //온도는 랜덤으로 한다.
        //날짜는 오늘 (데이터를 삽입하는 날 전날)
        //이건 어제 TIP에서 겹치게 한다.
        bnoList.removeAll(bnoList);
        bnoList.add(9L);//TIP
        bnoList.add(9L);
        bnoList.add(1L);//A동
        bnoList.add(1L);
        bnoList.add(2L);//B동
        bnoList.add(2L);
        bnoList.add(3L);//C동
        bnoList.add(3L);
        Arrays.fill(arr, false);
        Arrays.fill(barr, 0);


        for(int i=0;i<8;i++){
            insertDate=now.minusHours(1).minusMinutes(24+i).minusDays(1);
            Random r = new Random();
            double temperature = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
            if (arr[joMno] == false) {
                bno = bnoList.get(i);
                barr[joMno] = bnoList.get(i).intValue();
                arr[joMno] = true;
            } else {
                bno = Long.valueOf(barr[joMno]);
                arr[joMno] = false;
                insertDate=insertDate.plusHours(1);
            }
            Facility facility = Facility.builder().bno(bno).building("building" + bno).build();
            Status status = Status.builder().member(member2).facility(facility).state(arr[joMno]).temperature(Double.valueOf(String.format("%.1f", +temperature))).build();
            status.setRegDate(insertDate);
            statusRepository.save(status);

        }



        //유영균 데이터 삽입
        //접촉 경로는 데이터 삽입 기준날 P동
        //온도는 랜덤으로 한다.
        //날짜는 오늘 (데이터를 삽입하는 날 전날)
        //이건 오늘 D동에서 겹치게 한다.
        Member member3=Member.builder().mno(32L).build();
        bnoList.removeAll(bnoList);
        bnoList.add(4L);//P동
        bnoList.add(4L);
        bnoList.add(1L);//TIP
        bnoList.add(1L);
        bnoList.add(10L);//A동
        bnoList.add(10L);
        bnoList.add(2L);//E동
        bnoList.add(2L);
        Arrays.fill(arr, false);
        Arrays.fill(barr, 0);


        int youMno=32;
        for(int i=0;i<8;i++){
            insertDate=now.minusHours(1).minusMinutes(30-i);
            Random r = new Random();
            double temperature = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
            if (arr[youMno] == false) {
                bno = bnoList.get(i);
                barr[youMno] = bnoList.get(i).intValue();
                arr[youMno] = true;
            } else {
                bno = Long.valueOf(barr[youMno]);
                arr[youMno] = false;
                insertDate=insertDate.plusHours(1);
            }
            Facility facility = Facility.builder().bno(bno).building("building" + bno).build();
            Status status = Status.builder().member(member3).facility(facility).state(arr[youMno]).temperature(Double.valueOf(String.format("%.1f", +temperature))).build();
            status.setRegDate(insertDate);
            statusRepository.save(status);

        }


        //유영균 데이터 삽입
        //온도는 랜덤으로 한다.
        //날짜는 오늘 (데이터를 삽입하는 날 전날)
        //이건 어제 A동에서 겹치게 한다.
        bnoList.removeAll(bnoList);
        bnoList.add(1L);//TIP
        bnoList.add(1L);
        bnoList.add(5L);//A동
        bnoList.add(5L);
        bnoList.add(3L);//B동
        bnoList.add(3L);
        bnoList.add(7L);//C동
        bnoList.add(7L);
        Arrays.fill(arr, false);
        Arrays.fill(barr, 0);


        for(int i=0;i<8;i++){
            insertDate=now.minusHours(1).minusMinutes(26+i).minusDays(1);
            Random r = new Random();
            double temperature = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
            if (arr[youMno] == false) {
                bno = bnoList.get(i);
                barr[youMno] = bnoList.get(i).intValue();
                arr[youMno] = true;
            } else {
                bno = Long.valueOf(barr[youMno]);
                arr[youMno] = false;
                insertDate=insertDate.plusHours(1);
            }
            Facility facility = Facility.builder().bno(bno).building("building" + bno).build();
            Status status = Status.builder().member(member3).facility(facility).state(arr[youMno]).temperature(Double.valueOf(String.format("%.1f", +temperature))).build();
            status.setRegDate(insertDate);
            statusRepository.save(status);

        }

    }


}




