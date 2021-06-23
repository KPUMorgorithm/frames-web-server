package org.morgorithm.frames.service;

import lombok.Builder;
import org.modelmapper.ModelMapper;
import org.morgorithm.frames.configuration.ModelMapperUtil;
import org.morgorithm.frames.dto.MemberDTO;
import org.morgorithm.frames.dto.PageRequestDTO;
import org.morgorithm.frames.dto.PageResultDTO;
import org.morgorithm.frames.entity.Member;

import java.io.IOException;
import java.util.List;

public interface MemberService {
    ModelMapper modelMapper = ModelMapperUtil.getModelMapper();

    //    PageResultDTO<MemberDTO, Object[]> getList(PageRequestDTO requestDTO);
    PageResultDTO<MemberDTO, Member> getMemberList(PageRequestDTO requestDTO);

    List<MemberDTO> getAllMembers();

    Long register(MemberDTO memberDTO);

    MemberDTO read(Long mno);

    void remove(Long gno);

    void modify(MemberDTO dto);

    Member registerWithoutImage(MemberDTO memberDTO);

    void uploadAndSaveMemberImages(Member member, String imgurl[]) throws IOException;

    Long register(MemberDTO memberDTO, String imgurl[]) throws IOException;

    void syncFaceClassificationServer() throws IOException;

    default MemberDTO memberEntityToDto(Member entity) {
        MemberDTO dto = modelMapper.map(entity, MemberDTO.class);
        /*MemberDTO dto=MemberDTO.builder()
                .mno(entity.getMno())
                .name(entity.getName())
                .phone(entity.getPhone())
                .build();*/
        return dto;
    }
//    default MemberDTO entitiesToDTO(Member member, List<MemberImage> memberImages, Long imgCnt){
//
//
//        MemberDTO memberDTO=modelMapper.map(member, MemberDTO.class);
//
//        if (memberImages != null) {
//            List<MemberImageDTO> memberImageDTOList=memberImages.stream().map(memberImage->{
//                return MemberImageDTO.builder().imgName(memberImage.getImgName())
//                        .path(memberImage.getPath())
//                        .uuid(memberImage.getUuid())
//                        .build();
//            }).collect(Collectors.toList());
//            memberDTO.setImageDTOList(memberImageDTOList);
//        }
//
//        memberDTO.setImgCnt(imgCnt.intValue());
//
//        return memberDTO;
//    }

    //여기에 멤버DTO가 들어올 것이다
    //즉 웹에서 정보가 들어옴
//    default Map<String, Object> dtoToEntity(MemberDTO memberDTO){
//        Map<String, Object> entityMap=new HashMap<>();
//
//        Member member=modelMapper.map(memberDTO,Member.class);
//
//
//        entityMap.put("member",member);
//
//
//        List<MemberImageDTO> imageDTOList=memberDTO.getImageDTOList();
//        if(imageDTOList!=null && imageDTOList.size()>0){
//            List<MemberImage> memberImageList=imageDTOList.stream().map(movieImageDTO ->{
//                MemberImage movieImage=MemberImage.builder()
//                        .path(movieImageDTO.getPath().replaceAll("\\\\", "/"))
//                        .imgName(movieImageDTO.getImgName())
//                        .uuid(movieImageDTO.getUuid())
//                        .member(member)
//                        .build();
//                return movieImage;
//            }).collect(Collectors.toList());
//            entityMap.put("imgList",memberImageList);
//        }
//        return entityMap;
//    }

//    default Member memberDtoToEntity(MemberDTO dto){
//        Member entity=modelMapper.map(dto,Member.class);
//       /* Member entity=Member.builder()
//                .mno(dto.getMno())
//                .name(dto.getName())
//                .phone(dto.getPhone())
//                .build();*/
//        return entity;
//    }
}
