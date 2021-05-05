package org.morgorithm.frames.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.morgorithm.frames.configuration.ModelMapperUtil;
import org.morgorithm.frames.dto.MemberDTO;
import org.morgorithm.frames.dto.MemberImageDTO;
import org.morgorithm.frames.dto.PageRequestDTO;
import org.morgorithm.frames.dto.PageResultDTO;
import org.morgorithm.frames.entity.Member;
import org.morgorithm.frames.entity.MemberImage;
import org.morgorithm.frames.entity.QMember;
import org.morgorithm.frames.entity.Status;
import org.morgorithm.frames.repository.MemberImageRepository;
import org.morgorithm.frames.repository.MemberRepository;
import org.morgorithm.frames.repository.StatusRepository;
import org.morgorithm.frames.util.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
@Service
@Log4j2
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final MemberImageRepository memberImageRepository;
    private final StatusRepository statusRepository;

    @Value("${org.zerock.upload.path}")
    private String uploadPath;

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
        memberRepository.initialAutoIncrementToTheLatest();

    }
    @Override
    public MemberDTO read(Long mno) {
        Member member = memberRepository.findById(mno).orElse(null);
        MemberDTO memberDTO = ModelMapperUtil.getModelMapper().map(member, MemberDTO.class);
        if (member != null) {
            List<MemberImage> memberImages = memberImageRepository.findByMemberMno(member.getMno());
            memberDTO.setImageDTOList(memberImages.stream()
                    .map(mi -> ModelMapperUtil.getModelMapper().map(mi, MemberImageDTO.class))
                    .collect(Collectors.toList()));
        }
        return memberDTO;
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

    @Override
    public Member registerWithoutImage(MemberDTO memberDTO) {
        Member member = Member.builder()
                .name(memberDTO.getName())
                .phone(memberDTO.getPhone())
                .build();
        return memberRepository.save(member);
    }

    @Override
    public void uploadAndSaveMemberImages(Member member, String imgurl[]) throws IOException {
        for (int i=0; i<imgurl.length; i++) {
            String url = imgurl[i];
//            System.out.println("1  --  "+url);
            byte bytes[] = FileUtils.urlToByte(url);
            String pathDetail = FileUtils.generatedImagePath(uploadPath);
            String ext = FileUtils.getFileExtension(url);
            String uuid = UUID.randomUUID().toString();
            String filename = i+"."+ext;
//            System.out.println(uploadPath);
//            System.out.println(pathDetail);
//            System.out.println(uploadPath + File.separator + pathDetail);
            File image = new File(uploadPath + File.separator + pathDetail, uuid+"_"+filename);
//            System.out.println(image.getAbsolutePath());
//            System.out.println(image.getPath());
            FileUtils.downloadFromByte(image.getAbsolutePath(), bytes);

            MemberImage memberImage = MemberImage.builder()
                    .member(member)
                    .path(pathDetail)
                    .imgName(filename)
                    .uuid(uuid)
                    .build();

            memberImageRepository.save(memberImage);
        }
    }

    @Transactional(rollbackFor = IOException.class)
    public Long register(MemberDTO memberDTO, String imgurl[]) throws IOException {
        Member member = registerWithoutImage(memberDTO);
        uploadAndSaveMemberImages(member, imgurl);

        memberRepository.setSafeUpdate();
        memberRepository.initialCnt();
        memberRepository.reorderKeyId();
        return member.getMno();
    }

    @Transactional
    @Override
    public Long register(MemberDTO memberDTO) {
        Member member = modelMapper.map(memberDTO, Member.class);
        memberRepository.save(member);

        memberDTO.getImageDTOList().stream().forEach(e -> {
            MemberImage memberImage = modelMapper.map(e, MemberImage.class);
            memberImageRepository.save(memberImage);
        });

        //Auto-Increment 재정렬
        memberRepository.setSafeUpdate();
        memberRepository.initialCnt();
        memberRepository.reorderKeyId();
        return member.getMno();
    }
//
//    @Override
//    public PageResultDTO<MemberDTO, Object[]> getList(PageRequestDTO requestDTO) {
//
//        Pageable pageable = requestDTO.getPageable(Sort.by("mno").descending());
//
//
//        Page<Object[]> result = memberRepository.getListPage(pageable);
//
//        //arr로 entitiesToDTO 함수에 파라미터를 각각 넘겨주는 것
//        Function<Object[], MemberDTO> fn = (arr -> entitiesToDTO(
//                (Member)arr[0],
//                (List<MemberImage>)(Arrays.asList((MemberImage)arr[1])),
//                (Long)arr[2])
//        );
//
//
//
//        return new PageResultDTO<>(result,fn);
//    }

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
