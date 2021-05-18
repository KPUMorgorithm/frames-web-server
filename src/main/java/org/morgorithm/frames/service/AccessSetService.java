package org.morgorithm.frames.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
    public List<AccessSet> getAllAccessSet() {
        return statusRepository.getAllAccessSet();
    }

    public List<AccessSet> getAccessSetByMemberId(Long memberId) {
        return statusRepository.getAccessSetByMemberId(memberId);
    }

    // 2. 같은 공간에 있었던 멤버 목록 질의
//    public List<Status> getStatusOverlapped(AccessSet accessSet) {
//        return statusRepository.getStatusOverlapped(accessSet);
//    }

    // 3. 같은 공간에 있었던 시간 (초 단위) 질의
}

