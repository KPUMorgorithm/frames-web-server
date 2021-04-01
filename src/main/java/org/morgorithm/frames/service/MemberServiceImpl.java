package org.morgorithm.frames.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.morgorithm.frames.dto.MemberDTO;
import org.morgorithm.frames.dto.PageRequestDTO;
import org.morgorithm.frames.dto.PageResultDTO;
import org.morgorithm.frames.entity.Member;
import org.morgorithm.frames.entity.MemberImage;
import org.morgorithm.frames.entity.QMember;
import org.morgorithm.frames.entity.Status;
import org.morgorithm.frames.repository.MemberImageRepository;
import org.morgorithm.frames.repository.MemberRepository;
import org.morgorithm.frames.repository.StatusRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;

@SuppressWarnings("unchecked")
@Service
@Log4j2
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final MemberImageRepository memberImageRepository;
    private final StatusRepository statusRepository;
    @Override
    public void remove(Long mno) {

        //member은 foreign 키로 memberimage와 status가 참조하고 있기 때문에 먼저 지워주고 member을 마지막에 지워야 함
        List<Object[]> result = memberRepository.getMemberWithAll(mno);
        List<Object[]> statusResult=memberRepository.getMemberWithStatus(mno);

        Member member=(Member)result.get(0)[0];

        result.forEach(arr->{
            MemberImage memberImage=(MemberImage)arr[1];
            memberImageRepository.delete(memberImage);
        });

        statusResult.forEach(arr->{
            Status status=(Status)arr[1];
            if(status!=null)
             statusRepository.delete(status);
        });


        memberRepository.delete(member);


        //Auto-Increment 재정렬
        memberRepository.setSafeUpdate();
        memberRepository.initialCnt();
        memberRepository.reorderKeyId();

    }
    @Override
    public MemberDTO read(Long mno) {

        List<Object[]> result = memberRepository.getMemberWithAll(mno);

        Member member=(Member)result.get(0)[0];

        List<MemberImage> memberImageList=new ArrayList<>();


        result.forEach(arr->{
            MemberImage memberImage=(MemberImage)arr[1];
            memberImageList.add(memberImage);
        });
        Long imgCnt=(Long)result.get(0)[2];
        return entitiesToDTO(member,memberImageList,imgCnt);
    }

    @Override
    public void modify(MemberDTO dto) {

        //업데이트 하는 항목은 '제목', '내용'
       // System.out.println("test ID*************************************::"+dto.getMno());
        Optional<Member> result = memberRepository.findById(dto.getMno());
        if(result.isPresent()){

            Member entity = result.get();

            entity.changeName(dto.getName());
            entity.changePhone(dto.getPhone());

            memberRepository.save(entity);

        }
    }

    @Transactional
    @Override
    public Long register(MemberDTO memberDTO) {
        Map<String, Object> entityMap=dtoToEntity(memberDTO);

        Member member=(Member)entityMap.get("member");
        List<MemberImage> movieImageList=(List<MemberImage>)entityMap.get("imgList");
        memberRepository.save(member);
        movieImageList.forEach(movieImage -> {
            memberImageRepository.save(movieImage);
        });

        //Auto-Increment 재정렬
        memberRepository.setSafeUpdate();
        memberRepository.initialCnt();
        memberRepository.reorderKeyId();
        return member.getMno();
    }

    @Override
    public MemberDTO getMember(Long mno) {
        List<Object[]> result=memberRepository.getMemberWithAll(mno);

        //get(x)에서 x번째 row이다
        //그래서 get(0)은 일단 첫 번째 row이고 거기에 [x]의 x는 그 행의 x번 째 값을 의미
        //잘 모르겠으면 test에서 해당 query돌려보기
        Member member=(Member)result.get(0)[0]; //Member 엔티티는 가장 앞에 존재-모든 Row가 동일한 값

        List<MemberImage> memberImageList=new ArrayList<>(); //영화의 이미지 개수만큼 MovieImage 객체 필요

        //Query로 얻어낸 MovieImage를 하나씩 빼서 movieImageList에 담는다.
        result.forEach(arr->{
            MemberImage memberImage=(MemberImage)arr[1];
            memberImageList.add(memberImage);
        });
        Long imgCnt=(Long)result.get(0)[2];
        return entitiesToDTO(member,memberImageList,imgCnt);
    }
    @Override
    public PageResultDTO<MemberDTO, Object[]> getList(PageRequestDTO requestDTO) {

        Pageable pageable = requestDTO.getPageable(Sort.by("mno").descending());


        Page<Object[]> result = memberRepository.getListPage(pageable);

        //arr로 entitiesToDTO 함수에 파라미터를 각각 넘겨주는 것
        Function<Object[], MemberDTO> fn = (arr -> entitiesToDTO(
                (Member)arr[0],
                (List<MemberImage>)(Arrays.asList((MemberImage)arr[1])),
                (Long)arr[2])
        );



        return new PageResultDTO<>(result,fn);
    }

    public PageResultDTO<MemberDTO, Member> getMemberList(PageRequestDTO requestDTO){
        Pageable pageable=requestDTO.getPageable(Sort.by("mno").descending());
        BooleanBuilder booleanBuilder=getSearch(requestDTO);

        Page<Member> result=memberRepository.findAll(booleanBuilder,pageable);

        Function<Member, MemberDTO> fn=(entity->memberEntityToDto(entity));

        return new PageResultDTO<>(result, fn);
    }
    private BooleanBuilder getSearch(PageRequestDTO requestDTO){

        String type = requestDTO.getType();

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        QMember qMember = QMember.member;

        String keyword = requestDTO.getKeyword();

        BooleanExpression expression = qMember.mno.gt(0L);// gno > 0 조건만 생성

        booleanBuilder.and(expression);

        if(type == null || type.trim().length() == 0){ //검색 조건이 없는 경우
            return booleanBuilder;
        }


        //검색 조건을 작성하기
        BooleanBuilder conditionBuilder = new BooleanBuilder();

        if(type.contains("n")){
            conditionBuilder.or(qMember.name.contains(keyword));
        }
        if(type.contains("p")){
            conditionBuilder.or(qMember.phone.contains(keyword));
        }

        //모든 조건 통합
        booleanBuilder.and(conditionBuilder);

        return booleanBuilder;
    }


}
