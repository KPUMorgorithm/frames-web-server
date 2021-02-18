package org.morgorithm.frames.service;

import org.morgorithm.frames.dto.MemberDTO;
import org.morgorithm.frames.entity.Member;

public interface MemberService {

    Long register(MemberDTO memberDTO);



    default MemberDTO entitiesToDTO(Member member){
        MemberDTO memberDTO=MemberDTO.builder()
                .mno(member.getMno())
                .name(member.getName())
                .phone(member.getPhone())
                .build();
        return memberDTO;
    }
    default Member dtoToEntity(MemberDTO memberDTO){
        Member member=Member.builder()
                .mno(memberDTO.getMno())
                .name(memberDTO.getName())
                .phone(memberDTO.getPhone())
                .build();
        return member;
    }
}
