package org.morgorithm.frames.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.morgorithm.frames.dto.PageRequestDTO;
import org.morgorithm.frames.entity.Status;
import org.morgorithm.frames.projection.AccessSet;
import org.morgorithm.frames.repository.StatusRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
@AllArgsConstructor
public class AccessSetService {
    private StatusRepository statusRepository;

    // 1. 멤버의 시설 입장, 퇴장 기간 로그 (멤버id, 시설id, 입장로그번호, 퇴장로그번호, 입장시간, 퇴장시간)
    public List<AccessSet> getAllAccessSet(PageRequestDTO requestDTO) {
        if (requestDTO == null) {
            return statusRepository.getAllAccessSet();
        }
        List<AccessSet> result = null;
        if (requestDTO.getMno() != null && requestDTO.getMno().length() > 0) {
            if (requestDTO.getFrom() != null && requestDTO.getFrom().length() > 0
                    && requestDTO.getTo() != null && requestDTO.getTo().length() > 0) {
                if (requestDTO.getKeyword() != null && requestDTO.getKeyword().length() > 0) {
                    result = statusRepository.getAccessSetByMemberIdAndFacilityId(Long.parseLong(requestDTO.getMno()), Long.parseLong(requestDTO.getKeyword()), requestDTO.getFrom(), requestDTO.getTo());
                } else {
                    result = statusRepository.getAccessSetByMemberId(Long.parseLong(requestDTO.getMno()), requestDTO.getFrom(), requestDTO.getTo());
                }
            } else {
                if (requestDTO.getKeyword() != null && requestDTO.getKeyword().length() > 0) {
                    result = statusRepository.getAccessSetByMemberIdAndFacilityId(Long.parseLong(requestDTO.getMno()), Long.parseLong(requestDTO.getKeyword()));
                } else {
                    result = statusRepository.getAccessSetByMemberId(Long.parseLong(requestDTO.getMno()));
                }
            }
        } else {
            if (requestDTO.getFrom() != null && requestDTO.getFrom().length() > 0
                    && requestDTO.getTo() != null && requestDTO.getTo().length() > 0) {
                if (requestDTO.getKeyword() != null && requestDTO.getKeyword().length() > 0) {
                    result = statusRepository.getAccessSetByFacilityId(Long.parseLong(requestDTO.getKeyword()), requestDTO.getFrom(), requestDTO.getTo());
                } else {
                    result = statusRepository.getAllAccessSet(requestDTO.getFrom(), requestDTO.getTo());
                }
            } else {
                if (requestDTO.getKeyword() != null && requestDTO.getKeyword().length() > 0) {
                    result = statusRepository.getAccessSetByFacilityId(Long.parseLong(requestDTO.getKeyword()));
                } else {
                    result = statusRepository.getAllAccessSet();
                }
            }
        }
        return result;
    }

    // 2. 같은 공간에 있었던 멤버 목록 질의
    public List<Status> getStatusOverlapped(AccessSet accessSet) {
        List<Status> list = statusRepository.findAllByRegDateBetweenAndFacilityBno(accessSet.getTimeEnter(), accessSet.getTimeLeave(), accessSet.getFacilityId());
        return list;
    }

    // 3. 같은 공간에 있었던 시간 (초 단위) 질의
}

