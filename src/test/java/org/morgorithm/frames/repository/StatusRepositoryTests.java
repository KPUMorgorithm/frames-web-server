package org.morgorithm.frames.repository;

import org.junit.jupiter.api.Test;
import org.morgorithm.frames.entity.Facility;
import org.morgorithm.frames.entity.Member;
import org.morgorithm.frames.entity.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

@SpringBootTest
public class StatusRepositoryTests {
    @Autowired
    private StatusRepository statusRepository;

    @Test
    public void insertStatus(){
        IntStream.rangeClosed(1,200).forEach(i->{
            Long mno=(long)(Math.random()*100)+1;
            Long bno=((long)(Math.random()*10)+1);
            int inout=(int)(Math.random()*2)+1;
            int num=(int)(Math.random()*10)+1;
            Boolean state=inout==1?true:false;
            Facility facility= Facility.builder().bno(bno).building("building"+num).build();

            Status status=Status.builder()
                    .state(state)
                    .facility(facility)
                    .member(Member.builder().mno(mno).build())
                    .build();
            statusRepository.save(status);
        });
    }
}
