package org.morgorithm.frames.service;

import lombok.Builder;
import org.modelmapper.ModelMapper;
import org.morgorithm.frames.configuration.ModelMapperUtil;
import org.morgorithm.frames.dto.MemberDTO;
import org.morgorithm.frames.dto.MemberImageDTO;
import org.morgorithm.frames.dto.PageRequestDTO;
import org.morgorithm.frames.dto.PageResultDTO;
import org.morgorithm.frames.entity.Member;
import org.morgorithm.frames.entity.MemberImage;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface MemberService {
    @Builder.Default
    ModelMapper modelMapper= ModelMapperUtil.getModelMapper();
    PageResultDTO<MemberDTO, Object[]> getList(PageRequestDTO requestDTO);
    PageResultDTO<MemberDTO, Member> getMemberList(PageRequestDTO requestDTO);
    Long register(MemberDTO memberDTO);
    MemberDTO read(Long mno);
    void remove(Long gno);
    void modify(MemberDTO dto);

    public MemberDTO getMember(Long mno);


    default MemberDTO entitiesToDTO(Member member, List<MemberImage> memberImages, Long imgCnt){


        MemberDTO memberDTO=modelMapper.map(member,MemberDTO.class);

        List<MemberImageDTO> memberImageDTOList=memberImages.stream().map(memberImage->{
            return MemberImageDTO.builder().imgName(memberImage.getImgName())
                    .path(memberImage.getPath())
                    .uuid(memberImage.getUuid())
                    .build();
        }).collect(Collectors.toList());

        //위에 MoveiDTO 빌더에서 해줄 수도 있지만 moveiImageDTOList가 초기화가 되어야 되서
        //지금 setter를 이용해서 나머지를 초기화 해준 것임
        memberDTO.setImgCnt(imgCnt.intValue());
        memberDTO.setImageDTOList(memberImageDTOList);

        return memberDTO;
    }


    //여기에 멤버DTO가 들어올 것이다
    //즉 웹에서 정보가 들어옴
    default Map<String, Object> dtoToEntity(MemberDTO memberDTO){
        Map<String, Object> entityMap=new HashMap<>();

        Member member=modelMapper.map(memberDTO,Member.class);


        entityMap.put("member",member);


        List<MemberImageDTO> imageDTOList=memberDTO.getImageDTOList();
        if(imageDTOList!=null && imageDTOList.size()>0){
            List<MemberImage> memberImageList=imageDTOList.stream().map(movieImageDTO ->{
                MemberImage movieImage=MemberImage.builder()
                        .path(movieImageDTO.getPath())
                        .imgName(movieImageDTO.getImgName())
                        .uuid(movieImageDTO.getUuid())
                        .member(member)
                        .build();
                return movieImage;
            }).collect(Collectors.toList());
            entityMap.put("imgList",memberImageList);
        }
        return entityMap;
    }

    default Member memberDtoToEntity(MemberDTO dto){
        Member entity=modelMapper.map(dto,Member.class);
       /* Member entity=Member.builder()
                .mno(dto.getMno())
                .name(dto.getName())
                .phone(dto.getPhone())
                .build();*/
        return entity;
    }
    default MemberDTO memberEntityToDto(Member entity){
        MemberDTO dto=modelMapper.map(entity,MemberDTO.class);
        /*MemberDTO dto=MemberDTO.builder()
                .mno(entity.getMno())
                .name(entity.getName())
                .phone(entity.getPhone())
                .build();*/
        return dto;
    }

}
