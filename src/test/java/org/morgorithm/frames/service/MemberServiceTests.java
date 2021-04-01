package org.morgorithm.frames.service;

import org.junit.jupiter.api.Test;
import org.morgorithm.frames.dto.MemberDTO;
import org.morgorithm.frames.dto.PageRequestDTO;
import org.morgorithm.frames.dto.PageResultDTO;

import org.morgorithm.frames.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class MemberServiceTests {
    @Autowired
    private MemberService service;


    @Test
    public void testList(){
        PageRequestDTO pageRequestDTO=PageRequestDTO.builder().page(1).size(10).build();
        PageResultDTO<MemberDTO, Object[]> resultDTO=service.getList(pageRequestDTO);
        System.out.println("-----------------------------------------");
        System.out.println("PREV: "+resultDTO.isPrev());
        System.out.println("NEXT: "+resultDTO.isNext());
        System.out.println("TOTAL: "+resultDTO.getTotalPage());
        System.out.println("start: "+resultDTO.getStart());
        System.out.println("tempEnd: "+resultDTO.getEnd());
        System.out.println("page: "+resultDTO.getPage());
        System.out.println("-----------------------------------------");
        //이 dtoList는 우리가 함수형 인터페이스로 변환한 리스트
        for(MemberDTO memberDTO : resultDTO.getDtoList()){
            System.out.println(memberDTO);
        }

        resultDTO.getPageList().forEach(i->System.out.println(i));


    }

    @Test
    public void testSearch(){
        PageRequestDTO pageRequestDTO=PageRequestDTO.builder()
                .page(1)
                .size(10)
                .type("n")
                .keyword("user")
                .build();

        PageResultDTO<MemberDTO, Member> resultDTO=service.getMemberList(pageRequestDTO);
        System.out.println("PREV: "+resultDTO.isPrev());
        System.out.println("NEXT: "+resultDTO.isNext());
        System.out.println("TOTAL: "+resultDTO.getTotalPage());

        System.out.println("-----------------------------------");
        for(MemberDTO memberDTO:resultDTO.getDtoList()){
            System.out.println(memberDTO);
        }
        System.out.println("===================================");

       /*pageRequestDTO.setPage(2);
        resultDTO=service.getMemberList(pageRequestDTO);

        System.out.println("-----------------------------------");
        for(MemberDTO memberDTO:resultDTO.getDtoList()){
            System.out.println(memberDTO);
        }
        System.out.println("===================================");*/
        //   resultDTO.getPageList().forEach(i->System.out.println(i));

    }
}
