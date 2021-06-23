package org.morgorithm.frames.service;

import lombok.Builder;
import org.modelmapper.ModelMapper;
import org.morgorithm.frames.configuration.ModelMapperUtil;
import org.morgorithm.frames.dto.*;
import org.morgorithm.frames.entity.Status;

import java.util.List;

public interface StatusService {
    ModelMapper modelMapper = ModelMapperUtil.getModelMapper();

    RealTimeStatusDTO getFacilityStatus();

    PageResultDTO<StatusDTO, Status> getStatusList(PageRequestDTO requestDTO);

    TrackerInfoDTO getMapInfo(TrackerInfoDTO trackerInfoDTO);

    void sendSms(PageRequestDTO requestDTO);

    EventDTO getEventInfo();

    List<Status> getDangerStatus();

    List<Status> getWarningStatus();

    List<Status> getNormalStatus();

    List<Status> getTotalStatus();

    List<StatusServiceImpl.Data> getList(Long mno);

    default Status statusDtoToEntity(StatusDTO dto) {

        Status entity = modelMapper.map(dto, Status.class);
        /*Status entity=Status.builder()
                .statusnum(dto.getStatusnum())
                .member(dto.getMember())
                .temperature(dto.getTemperature())
                .state(dto.getState())
                .facility(dto.getFacility())
                .build();*/
        return entity;
    }

    default StatusDTO statusEntityToDto(Status entity) {

        StatusDTO dto = modelMapper.map(entity, StatusDTO.class);
        /*StatusDTO dto=StatusDTO.builder()
                .statusnum(entity.getStatusnum())
                .member(entity.getMember())
                .temperature(entity.getTemperature())
                .state(entity.getState())
                .regDate(entity.getRegDate())
                .facility(entity.getFacility())
                .build();*/
        return dto;
    }
}
