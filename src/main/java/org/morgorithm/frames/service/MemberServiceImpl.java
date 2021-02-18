package org.morgorithm.frames.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.morgorithm.frames.dto.MemberDTO;
import org.morgorithm.frames.entity.Member;
import org.morgorithm.frames.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public Long register(MemberDTO memberDTO) {
        Member member=dtoToEntity(memberDTO);
        memberRepository.save(member);
        return member.getMno();
    }
}
