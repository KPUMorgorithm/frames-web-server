package org.morgorithm.frames.repository;

import org.junit.jupiter.api.Test;
import org.morgorithm.frames.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

@SpringBootTest
public class MemberRepositoryTests {
    @Autowired
    private MemberRepository memberRepository;

    @Commit
    @Transactional
    @Test
    public void insertMembers(){
        IntStream.rangeClosed(1,100).forEach(i->{
            int count = (int)(Math. random()*9000)+1000;
            Member member=Member.builder().name("User"+i)
                    .phone("010"+"1111"+count).build();
            memberRepository.save(member);

        });
    }
}
