package org.morgorithm.frames.repository;
/*
import org.junit.jupiter.api.Test;
import org.morgorithm.frames.entity.Facility;
import org.morgorithm.frames.entity.Member;
import org.morgorithm.frames.entity.MemberImage;
import org.morgorithm.frames.entity.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@SpringBootTest
public class MemberRepositoryTests {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberImageRepository memberImageRepository;
    @Autowired
    FacilityRepository facilityRepository;
    @Autowired
    StatusRepository statusRepository;

    @Commit
    @Transactional
    @Test
    public void insertMembers(){
        IntStream.rangeClosed(1,100).forEach(i->{
            int count = (int)(Math. random()*9000)+1000;
            Member member=Member.builder().name("User"+i)
                    .phone("010"+"1111"+count).build();
            memberRepository.save(member);
            int count2=(int)(Math.random()*5)+1;//1,2,3,4

            for(int j=0;j<count2;j++){
                MemberImage memberImage=MemberImage.builder()
                        .uuid(UUID.randomUUID().toString())
                        .member(member)
                        .imgName("test"+j+".jpg").build();
                memberImageRepository.save(memberImage);
            }

        });
    }

    @Test
    public void insertAllDataToDB(){


        IntStream.rangeClosed(1,100).forEach(i->{
            int count = (int)(Math. random()*9000)+1000;
            Member member=Member.builder().name("User"+i)
                    .phone("010"+"1111"+count).build();
            memberRepository.save(member);
            int count2=(int)(Math.random()*5)+1;//1,2,3,4

            for(int j=0;j<count2;j++) {
                MemberImage memberImage = MemberImage.builder()
                        .uuid(UUID.randomUUID().toString())
                        .member(member)
                        .imgName("test" + j + ".jpg").build();
                memberImageRepository.save(memberImage);
            }
        });

        IntStream.rangeClosed(1,10).forEach(j->{
            Facility facility=Facility.builder().building("building"+j).build();
            facilityRepository.save(facility);
        });

        IntStream.rangeClosed(1,200).forEach(i->{
            Long mno=(long)(Math.random()*100)+1;
            Long bno=((long)(Math.random()*10)+1);
            int inout=(int)(Math.random()*2)+1;
            Boolean state=inout==1?true:false;
            Facility facility= Facility.builder().bno(bno).building("building"+bno).build();

            Status status=Status.builder()
                    .state(state)
                    .facility(facility)
                    .member(Member.builder().mno(mno).build())
                    .build();
            statusRepository.save(status);
        });
    }

    @Test
    public void testGetMemberWithStatus(){
        List<Object[]> result=memberRepository.getMemberWithStatus(32L);
        for(Object[] a:result){
           System.out.println(Arrays.toString(a));
        }
    }
    @Test
    public void testGetMemberWithAll(){
        List<Object[]> result=memberRepository.getMemberWithAll(95L);
       // System.out.println(result);
        for(Object[] arr:result){
            System.out.println(Arrays.toString(arr));
        }
    }*/
   /* @Test
    public void testGetListPage(){
        PageRequest pageRequest= PageRequest.of(0,10, Sort.by(Sort.Direction.DESC,"mno"));
        Page<Object[]> result=memberRepository.getListPage(pageRequest);

        for(Object[] arr:result){
            System.out.println(Arrays.toString(arr));
        }
    }*/

/*

    @Test
    public void testReorderGeneratedType(){
        memberRepository.setSafeUpdate();
        memberRepository.initialCnt();
        memberRepository.reorderKeyId();
    }



}
*/